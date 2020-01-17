package org.jimsd.jimsradioapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class RatingFragment extends Fragment implements View.OnClickListener{
    ImageView btn_rate1,btn_rate2,btn_rate3,btn_rate4,btn_rate5;
    Button btn_submit_rating;
    EditText rating_name,rating_email;
    int rating;
    TextView rating_txt;
    RequestQueue requestQueue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rating, container, false);

        //rating buttons
        btn_rate1=(ImageView)v.findViewById(R.id.btn_1_star);
        btn_rate2=(ImageView)v.findViewById(R.id.btn_2_star);
        btn_rate3=(ImageView)v.findViewById(R.id.btn_3_star);
        btn_rate4=(ImageView)v.findViewById(R.id.btn_4_star);
        btn_rate5=(ImageView)v.findViewById(R.id.btn_5_star);
        btn_rate1.setOnClickListener(this);
        btn_rate2.setOnClickListener(this);
        btn_rate3.setOnClickListener(this);
        btn_rate4.setOnClickListener(this);
        btn_rate5.setOnClickListener(this);

        btn_rate1.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));
        btn_rate2.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));
        btn_rate3.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));
        btn_rate4.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));
        btn_rate5.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));

        btn_submit_rating=(Button)v.findViewById(R.id.btn_submit_rating);
        btn_submit_rating.setOnClickListener(this);

        rating_name=(EditText)v.findViewById(R.id.edt_add_rating_name);
        rating_email=(EditText)v.findViewById(R.id.edt_add_rating_email);

        rating_txt=(TextView)v.findViewById(R.id.txt_rating_count);

        rating=-1;

        return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_1_star:
                rating=1;
                rating_txt.setText("Your rating: "+rating+" stars");
                btn_rate1.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                btn_rate2.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));
                btn_rate3.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));
                btn_rate4.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));
                btn_rate5.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));
                break;
            case R.id.btn_2_star:
                rating=2;
                rating_txt.setText("Your rating: "+rating+" stars");
                btn_rate1.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                btn_rate2.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                btn_rate3.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));
                btn_rate4.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));
                btn_rate5.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));
                break;
            case R.id.btn_3_star:
                rating=3;
                rating_txt.setText("Your rating: "+rating+" stars");
                btn_rate1.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                btn_rate2.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                btn_rate3.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                btn_rate4.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));
                btn_rate5.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));
                break;
            case R.id.btn_4_star:
                rating=4;
                rating_txt.setText("Your rating: "+rating+" stars");
                btn_rate1.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                btn_rate2.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                btn_rate3.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                btn_rate4.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                btn_rate5.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));
                break;
            case R.id.btn_5_star:
                rating=5;
                rating_txt.setText("Your rating: "+rating+" stars");
                btn_rate1.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                btn_rate2.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                btn_rate3.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                btn_rate4.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                btn_rate5.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                break;
            case R.id.btn_submit_rating:
                submitRating(rating);
                break;
        }

    }

    private void submitRating(final int rating_count) {
        if(rating==-1){
            Toast.makeText(getActivity(), "Provide a rating first..", Toast.LENGTH_SHORT).show();
            return;
        }
        final String name=rating_name.getText().toString().trim();
        final String email=rating_email.getText().toString().trim();
        if(name.equals("")||email.equals("")){
            Toast.makeText(getActivity(), "Provide details..", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(getActivity(), "Invalid email...", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getActivity(),"Submitting a rating of "+rating+" stars",Toast.LENGTH_SHORT).show();
        String url = "https://jimsd.org/jimsradio/add_rating.php?";
        requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();
                        try {
                            if (rating_count <= 3) {
                                new AlertDialog.Builder(getActivity())
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setTitle("Didn't liked the app..?")
                                        .setMessage("Have suggestion for the developers..?" +
                                                "\n\nHelp us improve by providing valuable feedback")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                getActivity().getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.layout_frag_main, new SuggestionFragment()).commit();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                getActivity().getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.layout_frag_main, new RatingFragment()).commit();
                                            }

                                        })
                                        .show();
                            } else {
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.layout_frag_main, new RatingFragment()).commit();
                            }
                        }catch (Exception e){

                        }
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
                param.put("user_id", email);
                param.put("user_name", name);
                param.put("rating", ""+rating_count);
                return param;
            }
        };
        requestQueue.add(request);
    }
}
