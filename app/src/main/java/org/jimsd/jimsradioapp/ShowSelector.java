package org.jimsd.jimsradioapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
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

public class ShowSelector extends Fragment {
    ListView list_show;
    RequestQueue requestQueue;
    String arr_show[][];

    ArrayList<HashMap<String,String>> show_data;
    HashMap<String,String> item;
    SimpleAdapter simpleAdapter;

    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_show_selector, container, false);

        list_show=(ListView)v.findViewById(R.id.list_show);
        progressBar=(ProgressBar)v.findViewById(R.id.progbar_frag);

        show_data = new ArrayList<>();
        simpleAdapter = new SimpleAdapter(getActivity(), show_data,
                R.layout.layout_show,
                new String[] { "line1","line2" },
                new int[] {R.id.list_txt_show_name, R.id.list_txt_show_description});

        list_show.setAdapter(simpleAdapter);
        fetch_show();

        list_show.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {
                String val=arr_show[position][0];
                String show_name=arr_show[position][1];
                String details=arr_show[position][2];
                   ProgramSelector fragment=new ProgramSelector();
                Bundle bundle=new Bundle();
                bundle.putString("code",val);
                bundle.putString("show",show_name);
                bundle.putString("details",details);
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction=getActivity().
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.layout_frag_main,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        return v;
    }

    private void fetch_show(){
        String url="https://jimsd.org/jimsradio/fetch_show.php?";
        requestQueue= Volley.newRequestQueue(getActivity());
        StringRequest request =new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{

                            JSONArray json =new JSONArray(response);
                            JSONObject obj;
                            int size=json.length();
                            arr_show=new String[size][3];
                            for(int i=0;i<json.length();i++){
                                //adding to drawer code
                                obj=json.getJSONObject(i);
                                arr_show[i][0]=obj.getString("show_code");
                                arr_show[i][1]=obj.getString("show_name");
                                arr_show[i][2]=obj.getString("description");
                                item = new HashMap<String,String>();
                                item.put( "line1", arr_show[i][1]);
                                item.put( "line2", arr_show[i][2]);
                                show_data.add( item );
                            }
                            progressBar.setVisibility(View.GONE);
                            list_show.setVisibility(View.VISIBLE);
                        }catch(JSONException e) {
                            Toast.makeText(getActivity(),"No records present..",Toast.LENGTH_SHORT).show();
                        }catch(Exception e){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),"An Error has Occurred"+e,Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"Cannot connect to the server, check your Internet conection and try again...",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        error.printStackTrace();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                //param.put("code",code);
                return param;
            }
        };
        requestQueue.add(request);
    }

}