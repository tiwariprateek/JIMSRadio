package org.jimsd.jimsradioapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class ScheduleFragment extends Fragment {
    ProgressBar progressBar;
    ListView list_schedules;

    ArrayList<HashMap<String,String>> arrayList;
    HashMap<String,String> item;
    SimpleAdapter simpleAdapter;

    RequestQueue requestQueue;
    String [][] listvalue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_schedule, container, false);

        progressBar=(ProgressBar)v.findViewById(R.id.progbar_schedule_frag);

        list_schedules=(ListView)v.findViewById(R.id.list_schedule);
        arrayList=new ArrayList<>();
        simpleAdapter = new SimpleAdapter(getActivity(), arrayList,
                R.layout.layout_show,
                new String[] { "line1","line2" },
                new int[] {R.id.list_txt_show_name, R.id.list_txt_show_description});
        list_schedules.setAdapter(simpleAdapter);

        list_schedules.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item1=listvalue[position][0];
                String item2=listvalue[position][1];
                String item3=listvalue[position][2];
                //Toast.makeText(getActivity(),"Schedule for"+item1+": "+item2,Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Schedule")
                        .setMessage("A new program is being scheduled..." +
                                "\n\nProgram: "+item1+"\n\nDetails: "+item2+
                                "\n\nScheduled Date: "+item3)
                        .setPositiveButton("OK", null)
                        .show();

            }
        });
        loadSchedules();
        return v;
    }

    private void loadSchedules() {
        String url="https://jimsd.org/jimsradio/fetch_schedule.php?";
        requestQueue= Volley.newRequestQueue(getActivity());
        StringRequest request =new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray json =new JSONArray(response);
                            JSONObject obj;
                            listvalue=new String[json.length()][3];
                            for(int i=0;i<json.length();i++){
                                //adding to drawer code
                                obj=json.getJSONObject(i);
                                listvalue[i][0]=obj.getString("schedule_prog");
                                listvalue[i][1]=obj.getString("schedule_txt");
                                listvalue[i][2]=obj.getString("schedule_date");
                                item = new HashMap<String,String>();
                                item.put( "line1", listvalue[i][0]);
                                item.put( "line2", listvalue[i][1]);
                                arrayList.add( item );
                                simpleAdapter.notifyDataSetChanged();
                            }
                            progressBar.setVisibility(View.GONE);
                        }catch(JSONException e) {
                            Toast.makeText(getActivity(),"No records present..",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }catch(Exception e){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),"An Error has Occurred"+e,Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(),"Cannot connect to the server, check your Internet conection and try again...",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(),"Error\n"+error,Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
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
