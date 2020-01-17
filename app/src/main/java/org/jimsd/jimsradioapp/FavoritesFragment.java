package org.jimsd.jimsradioapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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

public class FavoritesFragment extends Fragment {
    ListView list_fav;
    ArrayList<HashMap<String,String>> prog_data;
    HashMap<String,String> item;
    SimpleAdapter simpleAdapter;
    ProgressBar progressBar;
    String arr_prog[][];
    int index;
    RequestQueue requestQueue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_favorites, container, false);
        list_fav=(ListView)v.findViewById(R.id.list_fav_prog);
        progressBar= (ProgressBar)v.findViewById(R.id.progbar_fav_frag);

        prog_data = new ArrayList<>();
        simpleAdapter = new SimpleAdapter(getActivity(), prog_data,
                R.layout.layout_program,
                new String[] { "line1","line2","line3" },
                new int[] {R.id.list_txt_prog_name, R.id.list_txt_prog_date,R.id.list_txt_prog_description});
        index=0;

        list_fav.setAdapter(simpleAdapter);

        loadFavorites();

        list_fav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        return v;
    }

    private void loadFavorites() {
        String req_txt="";
        arr_prog=new String[10][6];
        prog_data.clear();
        FavoritesHelper f= new FavoritesHelper(getActivity());
        Cursor cr=f.selectData();
        cr.moveToFirst();
        if(cr.getCount()>0){
            do{
                req_txt+=cr.getString(0)+"cx";
            }while(cr.moveToNext());
            req_txt=req_txt.substring(0, req_txt.length() - 1);
            getFavoriteData(req_txt);
            //Toast.makeText(getActivity(), req_txt, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(),"No records found..",Toast.LENGTH_SHORT).show();
        }

        //getFavoriteData("bpk1");
        //Toast.makeText(getActivity(),arr_prog[0][0],Toast.LENGTH_SHORT).show();
        //getFavoriteData("atm1");
        //Toast.makeText(getActivity(),arr_prog[1][0],Toast.LENGTH_SHORT).show();
        //getFavoriteData("jd1");
        //Toast.makeText(getActivity(),arr_prog[2][0],Toast.LENGTH_SHORT).show();

        progressBar.setVisibility(View.GONE);
        list_fav.setVisibility(View.VISIBLE);
    }

    private void getFavoriteData(final String prog_id){
        String url="https://jimsd.org/jimsradio/fetch_fav.php?";
        requestQueue= Volley.newRequestQueue(getActivity());
        StringRequest request =new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray json =new JSONArray(response);
                            JSONObject obj;

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
                                    item.put( "line2", "No record date registered..");
                                else
                                    item.put( "line2", "Date: "+ arr_prog[i][5]);

                                if(arr_prog[i][4].equals("null"))
                                    item.put( "line3", "No description registered..");
                                else
                                    item.put( "line3", "Description: "+arr_prog[i][4]);
                                prog_data.add( item );
                                simpleAdapter.notifyDataSetChanged();
                                index++;
                            }
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
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                param.put("code",prog_id);
                return param;
            }
        };
        requestQueue.add(request);
    }
}
