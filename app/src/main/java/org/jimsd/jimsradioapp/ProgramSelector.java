package org.jimsd.jimsradioapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProgramSelector extends Fragment {
    ListView list_prog;
    RequestQueue requestQueue;
    String arr_prog[][],code;
    //TextView txt_show_details;

    ArrayList<HashMap<String,String>> prog_data;
    HashMap<String,String> item;
    SimpleAdapter simpleAdapter;

    ArrayList sortList;
    ArrayAdapter sortAdapter;

    Spinner spinner;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_program_selector, container, false);

        list_prog=(ListView)v.findViewById(R.id.list_prog);
        progressBar=(ProgressBar)v.findViewById(R.id.progbar_prog_frag);
        spinner=(Spinner)v.findViewById(R.id.sp_sort);
        //txt_show_details=(TextView)v.findViewById(R.id.txt_show_details);

        sortList=new ArrayList();
        sortAdapter=new ArrayAdapter(getActivity(),android.R.layout.simple_dropdown_item_1line,sortList);
        spinner.setAdapter(sortAdapter);
        sortAdapter.add("Name (A-Z)");
        sortAdapter.add("Name (Z-A)");
        sortAdapter.add("Date");

        prog_data = new ArrayList<>();
        simpleAdapter = new SimpleAdapter(getActivity(), prog_data,
                R.layout.layout_program,
                new String[] { "line1","line2","line3" },
                new int[] {R.id.list_txt_prog_name, R.id.list_txt_prog_date,R.id.list_txt_prog_description});

        list_prog.setAdapter(simpleAdapter);

        //decide sorting (if any) to be performed
        code=getArguments().getString("code");
        String show_name=getArguments().getString("show");
        //String details=getArguments().getString("details");
        //txt_show_details.setText(details);
        //TextView textView=(TextView) getActivity().findViewById(R.id.txt_main_welcome);
        //textView.setText(show_name);
        fetch_prog(code,"name_a2z");

        list_prog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {
                Toast.makeText(getActivity(), "Now Playing: "+arr_prog[position][0], Toast.LENGTH_SHORT).show();

                PlayStream fragment=new PlayStream();
                Bundle bundle=new Bundle();
                bundle.putString("name",arr_prog[position][0]);
                bundle.putString("sauce",arr_prog[position][1]);
                bundle.putString("prog_name",arr_prog[position][2]);
                bundle.putString("audio_id",arr_prog[position][3]);
                bundle.putString("prog_desc",arr_prog[position][4]);
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction=getActivity().
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.layout_frag_main,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                switch(item){
                    case "Name (A-Z)":fetch_prog(code,"name_a2z");
                        progressBar.setVisibility(View.VISIBLE);
                        list_prog.setVisibility(View.INVISIBLE);
                        break;
                    case "Name (Z-A)":fetch_prog(code,"name_z2a");
                        progressBar.setVisibility(View.VISIBLE);
                        list_prog.setVisibility(View.INVISIBLE);
                        break;
                    case "Date"://fetch_prog(code,"date");
                        Toast.makeText(getActivity(),"To add for sort by date",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return v;
    }

    void fetch_prog(final String code,final String sort){
        String url="https://jimsd.org/jimsradio/fetch_program.php?";
        requestQueue= Volley.newRequestQueue(getActivity());
        StringRequest request =new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray json =new JSONArray(response);
                            JSONObject obj;
                            int size=json.length();
                            arr_prog=new String[size][6];
                            prog_data.clear();
                            for(int i=0;i<json.length();i++){

                                //adding to drawer code
                                obj=json.getJSONObject(i);
                                arr_prog[i][0]=obj.getString("name");
                                arr_prog[i][1]=obj.getString("link");
                                arr_prog[i][2]=obj.getString("show_name");
                                arr_prog[i][3]=obj.getString("audio_id");
                                arr_prog[i][4]=obj.getString("prog_desc");
                                arr_prog[i][5]=obj.getString("date");

                                item = new HashMap<String,String>();
                                item.put( "line1", arr_prog[i][0]);

                                if(arr_prog[i][5].equals("0000-00-00"))
                                    item.put( "line2", "Recorded On: ");
                                else
                                    item.put( "line2", "Recorded On: "+ arr_prog[i][5]);

                                if(arr_prog[i][4].equals("null"))
                                    item.put( "line3", "Info: ");
                                else
                                    item.put( "line3", "Info: "+arr_prog[i][4]);
                                prog_data.add( item );
                            }
                            progressBar.setVisibility(View.GONE);
                            list_prog.setVisibility(View.VISIBLE);
                            spinner.setVisibility(View.VISIBLE);
                        }catch(JSONException e) {
                            Toast.makeText(getActivity(),"No records present..",Toast.LENGTH_SHORT).show();
                        }catch(Exception e){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),"An error has occured.."+e,Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),"Cannot connect to the server, check your Internet conection and try again...",Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                param.put("code",code);
                param.put("sort",sort);
                return param;
            }
        };
        requestQueue.add(request);
    }
}
