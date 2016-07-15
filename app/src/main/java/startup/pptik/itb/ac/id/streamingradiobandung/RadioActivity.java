package startup.pptik.itb.ac.id.streamingradiobandung;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.commit451.nativestackblur.NativeStackBlur;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import startup.pptik.itb.ac.id.streamingradiobandung.connection.RadioApi;
import startup.pptik.itb.ac.id.streamingradiobandung.databases.GetData;
import startup.pptik.itb.ac.id.streamingradiobandung.databases.InsertData;
import startup.pptik.itb.ac.id.streamingradiobandung.models.RadioModel;
import startup.pptik.itb.ac.id.streamingradiobandung.service.StreamService;

public class RadioActivity extends AppCompatActivity {

    ImageView radioImage;
    RotateAnimation anim;
    ImageButton playBtn;
    boolean isPlay;
    Spinner spinner;
    TextView freqText, loadingText;
    ImageButton next, pref;

    int radioPos, channelSize;

    String[] radioNames, radioFreqs, radioUrls, radioThumbs;
    LinearLayout mainLayot;

    SharedPreferences prefs;
    public static String PREFS_IS_FIRST_TIME = "isFirstTime";
    Intent streamService;
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";
    String currFreq, currStation, currUrl, currPath;
    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        //STEP2: register the receiver
        if (activityReceiver != null) {
        //Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_ACTIVITY"
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
        //Map the intent filter to the receiver
            registerReceiver(activityReceiver, intentFilter);
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        streamService = new Intent(RadioActivity.this, StreamService.class);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        spinner = (Spinner) findViewById(R.id.spinnerList);
        mainLayot = (LinearLayout)findViewById(R.id.mainLayout);
        mainLayot.setVisibility(View.GONE);
        loadingText = (TextView)findViewById(R.id.loadingText);
        setSupportActionBar(toolbar);
        initControls();
        //------------- prefs

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        radioPos = prefs.getInt("Radio Position", 0);
        isPlay = prefs.getBoolean("isPlaying", false);
        Log.i("is Play : ", String.valueOf(isPlay));
        if(prefs.getBoolean(PREFS_IS_FIRST_TIME, false) == false){

            loadingText.setVisibility(View.VISIBLE);
            getAllData();

        }else {
            mainLayot.setVisibility(View.VISIBLE);
            GetData getData = new GetData();
            radioNames = getData.getRadioNameList(this);
            radioFreqs = getData.getRadioFreqList(this);
            radioUrls = getData.getRadioUrlList(this);
            radioThumbs = getData.getRadioThumbList(this);


            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, radioNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(radioPos);
            channelSize = radioNames.length;


        }


        radioImage = (ImageView)findViewById(R.id.radioImage);

        freqText = (TextView)findViewById(R.id.freqText);

        anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(9000);
        setNextPref();
        setFab();
        setPlayBtn();

        spinner .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                // TODO Auto-generated method stub
                Log.i("Radio Name", radioNames[pos]);
                radioPos = pos;
                freqText.setText(radioFreqs[pos]);
                currFreq = radioFreqs[pos];
                currStation = radioNames[pos];
                currUrl = radioUrls[pos];
                currPath = radioThumbs[pos];
                radioImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_image));
                URL newurl = null;
                try {
                    newurl = new URL(radioThumbs[pos]);
                    Log.i("URL : ", String.valueOf(newurl));
                //    Picasso.with(RadioActivity.this)
                //            .load(String.valueOf(newurl))
                //            .into(radioImage);
                    Picasso picasso = new Picasso.Builder(RadioActivity.this)
                            .listener(new Picasso.Listener() {
                                @Override
                                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                                    //Here your log
                                    radioImage.setImageDrawable(getResources().getDrawable(R.drawable.radio_image));
                                    Log.i("Error :", exception.getMessage());
                                }
                            })
                            .build();
                    picasso.load(newurl.toString())
                            .fit()
                            .into(radioImage);
                    Bitmap bm = NativeStackBlur.process(BitmapFactory.decodeResource(getResources(), R.drawable.myradio), 35);
                    ImageView img = (ImageView)findViewById(R.id.bg_img);
                    img.setImageBitmap(bm);
                } catch (MalformedURLException e) {
                    Log.e("Radio Activity", "Thumb not Found");
                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });


        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlay == false){
                    radioImage.startAnimation(anim);
                    playBtn.setImageDrawable(new IconicsDrawable(RadioActivity.this)
                            .icon(CommunityMaterial.Icon.cmd_pause_circle)
                            .color(Color.WHITE)
                            .sizeDp(34));
                    isPlay = true;
                //    sendBroadcast(currUrl, currPath, currStation);
                    streamService.putExtra("url", currUrl);
                    streamService.putExtra("path image", currPath);
                    streamService.putExtra("radio name", currStation);
                    startService(streamService);

                }else {

                    radioImage.clearAnimation();
                    playBtn.setImageDrawable(new IconicsDrawable(RadioActivity.this)
                            .icon(CommunityMaterial.Icon.cmd_play_circle)
                            .color(Color.WHITE)
                            .sizeDp(34));
                    isPlay = false;
                    stopService(streamService);
                }
            }
        });
    }

    private void setFab(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(view, "Powered by PPIK-ITB", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                Button btn = (Button)snackBarView.findViewById(android.support.design.R.id.snackbar_action);
                btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btn.setTextColor(getResources().getColor(R.color.colorBg));
                textView.setTextColor(Color.parseColor("#ffffff"));
                snackbar.setAction("More Apps", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                snackbar.show();

            }
        });

    }

    private void insertLocalDb(String radioName, String radioFreq, String radioUrl, String radioThumb, String city){
        InsertData.insertTBList(RadioActivity.this, radioName, radioFreq, radioUrl, radioThumb, city);
    }

    void getAllData(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://radio-bandung.lskk.ee.itb.ac.id/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RadioApi service = retrofit.create(RadioApi.class);
       // Call<RadioModel> mRadio = service.groupList();
        Call<List<RadioModel>> mRadio = service.groupList();
        mRadio.enqueue(new Callback<List<RadioModel>>() {
            @Override
            public void onResponse(Call<List<RadioModel>> call, Response<List<RadioModel>> response) {
                if(response.body().size() > 0){
                    Log.i("Test", response.message());
                    progressDialog.dismiss();
                    for(int i = 0; i < response.body().size(); i++){
                        insertLocalDb(response.body().get(i).name,
                                response.body().get(i).frekuensi,
                                response.body().get(i).urlStreamStereo,
                                response.body().get(i).pathlogo,
                                response.body().get(i).kota);
                    }

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(RadioActivity.this.PREFS_IS_FIRST_TIME, true);
                    editor.commit();

                    mainLayot.setVisibility(View.VISIBLE);
                    GetData getData = new GetData();
                    radioNames = getData.getRadioNameList(RadioActivity.this);
                    Log.i("Radio Activity", radioNames.toString());
                    radioFreqs = getData.getRadioFreqList(RadioActivity.this);
                    Log.i("Radio Activity", radioFreqs.toString());
                    radioUrls = getData.getRadioUrlList(RadioActivity.this);
                    Log.i("Radio Activity", radioUrls.toString());
                    radioThumbs = getData.getRadioThumbList(RadioActivity.this);
                    Log.i("Radio Activity", radioThumbs.toString());


                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(RadioActivity.this, R.layout.spinner_item, radioNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    loadingText.setVisibility(View.GONE);
                    spinner.setSelection(radioPos);
                    channelSize = radioNames.length;
                }
            }

            @Override
            public void onFailure(Call<List<RadioModel>> call, Throwable t) {
                Toast.makeText(RadioActivity.this, "Gagal memuat channel, periksa koneksi Internet Anda dan coba lagi", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void initControls()
    {
        try
        {
            volumeSeekbar = (SeekBar)findViewById(R.id.materialSeekBar);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setNextPref(){
        next = (ImageButton)findViewById(R.id.next);
        pref = (ImageButton)findViewById(R.id.pref);

        next.setImageDrawable(new IconicsDrawable(RadioActivity.this)
                .icon(CommunityMaterial.Icon.cmd_skip_next)
                .color(Color.WHITE)
                .sizeDp(18));

        pref.setImageDrawable(new IconicsDrawable(RadioActivity.this)
                .icon(CommunityMaterial.Icon.cmd_skip_previous)
                .color(Color.WHITE)
                .sizeDp(18));

        ImageView imgVolume = (ImageView)findViewById(R.id.imgVolume);
        imgVolume.setImageDrawable(new IconicsDrawable(RadioActivity.this)
                .icon(CommunityMaterial.Icon.cmd_volume_medium)
                .color(Color.WHITE)
                .sizeDp(24));

        pref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioPos > 0){
                    if(isPlay == true){
                        spinner.setSelection(radioPos - 1);
                        stopService(streamService);
                        streamService.putExtra("url", currUrl);
                        streamService.putExtra("path image", currPath);
                        streamService.putExtra("radio name", currStation);
                        startService(streamService);
                    }else {
                        spinner.setSelection(radioPos - 1);
                    }
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioPos < channelSize){
                    if(isPlay == true) {
                        spinner.setSelection(radioPos + 1);
                        stopService(streamService);
                        streamService.putExtra("url", currUrl);
                        streamService.putExtra("path image", currPath);
                        streamService.putExtra("radio name", currStation);
                        startService(streamService);
                    }else {
                        spinner.setSelection(radioPos + 1);
                    }
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //STEP3: Unregister the receiver
        unregisterReceiver(activityReceiver);
        SharedPreferences.Editor editor = prefs.edit();
        if(isPlay == true){
            editor.putBoolean("isPlaying", isPlay);
            editor.putInt("Radio Position", radioPos);
            editor.commit();
        }else {
            editor.putBoolean("isPlaying", isPlay);
            editor.putInt("Radio Position", radioPos);
            editor.commit();
        }

    }

    private void setPlayBtn(){
        playBtn = (ImageButton)findViewById(R.id.playBtn);
        if(isPlay == false) {
            playBtn.setImageDrawable(new IconicsDrawable(RadioActivity.this)
                    .icon(CommunityMaterial.Icon.cmd_play_circle)
                    .color(Color.WHITE)
                    .sizeDp(34));
        }else {
            playBtn.setImageDrawable(new IconicsDrawable(RadioActivity.this)
                    .icon(CommunityMaterial.Icon.cmd_pause_circle)
                    .color(Color.WHITE)
                    .sizeDp(34));
            radioImage.startAnimation(anim);
        }
    }

    //STEP1: Create a broadcast receiver
    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("status").toLowerCase().contains("offline")){
                radioImage.clearAnimation();
                stopService(streamService);
                isPlay = false;
                setPlayBtn();
            }
            freqText.setText(currFreq+ intent.getStringExtra("status"));
        }
    };
}

