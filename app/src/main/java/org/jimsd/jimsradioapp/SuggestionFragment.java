package org.jimsd.jimsradioapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SuggestionFragment extends Fragment {
    EditText edt_name,edt_email,edt_contact,edt_suggest;
    Button btn_submit_suggestion;
    RequestQueue requestQueue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_suggestion, container, false);

        edt_name=(EditText)v.findViewById(R.id.edt_add_suggest_name);
        edt_email=(EditText)v.findViewById(R.id.edt_add_suggest_email);
        edt_contact=(EditText)v.findViewById(R.id.edt_add_suggest_contact);
        edt_suggest=(EditText)v.findViewById(R.id.edt_add_suggest);
        btn_submit_suggestion=(Button)v.findViewById(R.id.btn_submit_suggest);
        btn_submit_suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSuggestion();
            }
        });

        return v;
    }

    private void submitSuggestion(){
        final String suggest_name =edt_name.getText().toString().trim();
        final String suggest_email =edt_email.getText().toString().trim();
        final String suggest_contact =edt_contact.getText().toString().trim();
        final String suggest_txt =edt_suggest.getText().toString().trim();
        if(suggest_name.equals("")||suggest_email.equals("")||suggest_contact.equals("")||suggest_txt.equals("")) {
            Toast.makeText(getActivity(), "Cannot leave fields empty..", Toast.LENGTH_SHORT).show();
            return;
        }
        if(suggest_contact.length()<8){
            Toast.makeText(getActivity(), "Contact number too short..", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(suggest_email).matches()){
            Toast.makeText(getActivity(), "Invalid email...", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getActivity(),"Submitting...",Toast.LENGTH_SHORT).show();
        String url = "https://jimsd.org/jimsradio/add_suggestion.php?";
        requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.layout_frag_main, new SuggestionFragment()).commit();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), "Cannot connect to the server, check your Internet conection and try again...", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(),""+error,Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("user_id", suggest_email);
                param.put("user_name", suggest_name);
                param.put("user_contact", suggest_contact);
                param.put("suggest", suggest_txt);
                return param;
            }
        };
        requestQueue.add(request);
    }
}
