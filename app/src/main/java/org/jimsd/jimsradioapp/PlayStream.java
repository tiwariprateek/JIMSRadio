package org.jimsd.jimsradioapp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.android.exoplayer2.mediacodec.MediaCodecInfo.TAG;

public class PlayStream extends Fragment {
    private Button btn_main;
    ImageView btn_favorites;

    String name,show_name,sauce,prog_code,prog_desc;
    TextView txt_showname,txt_progname,txt_progdesc,txt_detail,txt_views;

    RequestQueue requestQueue;

    private SeekBar seekPlayerProgress;
    private Handler handler;
    private ImageButton btnPlay;
    private TextView txtCurrentTime, txtEndTime;
    private boolean isPlaying = false;

    private static final String TAG = "MainActivity";
    private SimpleExoPlayer exoPlayer;
    private ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            Log.i(TAG,"onTimelineChanged");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.i(TAG,"onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.i(TAG,"onLoadingChanged");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.i(TAG,"onPlayerStateChanged: playWhenReady = "+String.valueOf(playWhenReady)
                    +" playbackState = "+playbackState);
            switch (playbackState){
                case ExoPlayer.STATE_ENDED:
                    Log.i(TAG,"Playback ended!");
                    //Stop playback and return to start position
                    setPlayPause(false);
                    exoPlayer.seekTo(0);
                    break;
                case ExoPlayer.STATE_READY:
                    Log.i(TAG,"ExoPlayer ready! pos: "+exoPlayer.getCurrentPosition()
                            +" max: "+stringForTime((int)exoPlayer.getDuration()));
                    setProgress();
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    Log.i(TAG,"Playback buffering!");
                    break;
                case ExoPlayer.STATE_IDLE:
                    Log.i(TAG,"ExoPlayer idle!");
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.i(TAG,"onPlaybackError: "+error.getMessage());
        }

        @Override
        public void onPositionDiscontinuity() {
            Log.i(TAG,"onPositionDiscontinuity");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_play_stream, container, false);
        btn_favorites =(ImageView) v.findViewById(R.id.btn_check_fav_stream);

        txt_showname = (TextView)v.findViewById(R.id.txt_showname);
        txt_progname = (TextView)v.findViewById(R.id.txt_progname);
        txt_progdesc = (TextView)v.findViewById(R.id.txt_desc);
        txt_detail = (TextView)v.findViewById(R.id.txt_detail);
        txt_views = (TextView)v.findViewById(R.id.txt_play_views);

        //exoplayer controls
        btnPlay = (ImageButton) v.findViewById(R.id.btnPlay);
        btnPlay.requestFocus();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlayPause(!isPlaying);
            }
        });
        txtCurrentTime = (TextView) v.findViewById(R.id.time_current);
        txtEndTime = (TextView) v.findViewById(R.id.player_end_time);
        seekPlayerProgress = (SeekBar) v.findViewById(R.id.mediacontroller_progress);

        name=getArguments().getString("name");;
        show_name=getArguments().getString("prog_name");
        sauce=getArguments().getString("sauce");
        prog_code=getArguments().getString("audio_id");
        prog_desc=getArguments().getString("prog_desc");
        txt_showname.setText(show_name);
        txt_progname.setText(name);
        if(prog_desc.equals("null")||prog_desc.equals(""))
            txt_progdesc.setText("Description : not provided");
        else
            txt_progdesc.setText("Description : "+prog_desc);

        FavoritesHelper fav_helper=new FavoritesHelper(getActivity());
        long flag=fav_helper.checkFavorite(prog_code);
        if(flag==1){
            btn_favorites.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
            Toast.makeText(getActivity(), "Added to favourites", Toast.LENGTH_SHORT).show();
        }
        else if(flag==-1){
            Toast.makeText(getContext(), "Error..", Toast.LENGTH_SHORT).show();
        }

        btn_favorites.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btn_favorites.setEnabled(false);
                FavoritesHelper fav_helper=new FavoritesHelper(getActivity());
                if(fav_helper.checkFavorite(prog_code)==0){
                    if(fav_helper.addFavorite(prog_code)==1){
                        btn_favorites.setImageDrawable(getResources().getDrawable(R.drawable.selected_fav));
                        Toast.makeText(getActivity(), "In favourites", Toast.LENGTH_SHORT).show();
                        btn_favorites.setEnabled(true);
                    }
                }
                else if(fav_helper.checkFavorite(prog_code)==1){
                    if(fav_helper.removeFavorite(prog_code)==0){
                        btn_favorites.setImageDrawable(getResources().getDrawable(R.drawable.default_fav));
                        Toast.makeText(getActivity(), "Not in favourites", Toast.LENGTH_SHORT).show();
                        btn_favorites.setEnabled(true);
                    }
                }
                else{
                    Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                    btn_favorites.setEnabled(true);
                }
            }
        });
        getGuests(prog_code);
        getViewCount(prog_code);

        prepareExoPlayerFromURL(Uri.parse(sauce));

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(exoPlayer!=null){
            exoPlayer.stop();
            exoPlayer=null;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(exoPlayer!=null){
            exoPlayer.stop();
            exoPlayer=null;
        }
    }

    private void getViewCount(final String code) {
        String url="https://jimsd.org/jimsradio/update_views.php?";
        requestQueue= Volley.newRequestQueue(getActivity());
        StringRequest request =new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray json =new JSONArray(response);
                            JSONObject obj;
                            if(json.length()>=1) {
                                obj = json.getJSONObject(0);
                                String views=obj.getString("views");
                                Toast.makeText(getActivity(), "Total Views:"+views, Toast.LENGTH_SHORT).show();
                                txt_views.setText("Total Views: "+views);
                                txt_views.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(getActivity(), "An error has occured..", Toast.LENGTH_SHORT).show();
                            }
                        }catch(JSONException e) {
                            Toast.makeText(getActivity(), "Error\n.."+e, Toast.LENGTH_SHORT).show();
                        }catch(Exception e){
                            Toast.makeText(getActivity(),"An error has occured..\n"+e,Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(),"Cannot connect to the server, check your Internet conection and try again...",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(),""+error,Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                param.put("audio_id",code);
                return param;
            }
        };
        requestQueue.add(request);
    }

    public void getGuests(final String code){
        String url="https://jimsd.org/jimsradio/fetch_prog_only_guest.php?";
        requestQueue= Volley.newRequestQueue(getActivity());
        StringRequest request =new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray json =new JSONArray(response);
                            JSONObject obj;
                            String detail=txt_detail.getText().toString().trim();
                            if(json.length()<=2) {
                                detail=detail+"\n\nGuests :";
                                for (int i = 0; i < json.length(); i++) {
                                    //adding to drawer code
                                    obj = json.getJSONObject(i);
                                    detail=detail+"\n"+(i+1)+" "+obj.getString("guest_name");
                                    detail=detail+"\nDescription : "+obj.getString("guest_desc");
                                }
                            }else{
                                Toast.makeText(getActivity(), "Invalid amount of entries..", Toast.LENGTH_SHORT).show();
                            }
                            txt_detail.setText(detail);
                            getAnchors(prog_code);
                        }catch(JSONException e) {
                            //Toast.makeText(getActivity(), "No valid guest records..", Toast.LENGTH_SHORT).show();
                        }catch(Exception e){
                            Toast.makeText(getActivity(),"An error has occured..\n"+e,Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(),"Cannot connect to the server, check your Internet conection and try again...",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(),""+error,Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                param.put("audio_id",code);
                return param;
            }
        };
        requestQueue.add(request);
    }

    public void getAnchors(final String code){
        String url="https://jimsd.org/jimsradio/fetch_prog_only_anchor.php?";
        requestQueue= Volley.newRequestQueue(getActivity());
        StringRequest request =new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray json =new JSONArray(response);
                            JSONObject obj;
                            String detail=txt_detail.getText().toString().trim();
                            if(json.length()<=2) {
                                detail=detail+"\n\nAnchors : ";
                                for (int i = 0; i < json.length(); i++) {
                                    //adding to drawer code
                                    obj = json.getJSONObject(i);
                                    detail=detail+"\n"+(i+1)+" "+obj.getString("anchor_name");
                                    detail=detail+"\nDescription : "+obj.getString("anchor_desc");
                                }
                                txt_detail.setText(detail);
                            }else{
                                Toast.makeText(getActivity(), "Invalid amount of entries..", Toast.LENGTH_SHORT).show();
                            }
                        }catch(JSONException e) {
                            //Toast.makeText(getActivity(), "No valid guest records..", Toast.LENGTH_SHORT).show();

                        }catch(Exception e){
                            Toast.makeText(getActivity(),"An error has occured..\n"+e,Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(),"Cannot connect to the server, check your Internet conection and try again...",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(),""+error,Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                param.put("audio_id",code);
                return param;
            }
        };
        requestQueue.add(request);
    }

    private void prepareExoPlayerFromURL(Uri uri){

        TrackSelector trackSelector = new DefaultTrackSelector();

        LoadControl loadControl = new DefaultLoadControl();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), "exoplayer2example"), null);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource audioSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
        exoPlayer.addListener(eventListener);

        exoPlayer.prepare(audioSource);
        initMediaControls();
    }

    private void initMediaControls() {
        initSeekBar();
    }

    private void setPlayPause(boolean play){
        isPlaying = play;
        exoPlayer.setPlayWhenReady(play);
        if(!isPlaying){
            btnPlay.setImageResource(android.R.drawable.ic_media_play);
        }else{
            setProgress();
            btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds =  timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void setProgress() {
        seekPlayerProgress.setProgress(0);
        seekPlayerProgress.setMax((int) exoPlayer.getDuration()/1000);
        txtCurrentTime.setText(stringForTime((int)exoPlayer.getCurrentPosition()));
        txtEndTime.setText(stringForTime((int)exoPlayer.getDuration()));

        if(handler == null)handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && isPlaying) {
                    seekPlayerProgress.setMax((int) exoPlayer.getDuration()/1000);
                    int mCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
                    seekPlayerProgress.setProgress(mCurrentPosition);
                    txtCurrentTime.setText(stringForTime((int)exoPlayer.getCurrentPosition()));
                    txtEndTime.setText(stringForTime((int)exoPlayer.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void initSeekBar() {
        seekPlayerProgress.requestFocus();

        seekPlayerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                exoPlayer.seekTo(progress*1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekPlayerProgress.setMax(0);
        seekPlayerProgress.setMax((int) exoPlayer.getDuration()/1000);
    }
}
