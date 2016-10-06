package org.pptik.radiostreaming.activity;


import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.nineoldandroids.view.ViewHelper;
import org.pptik.radiostreaming.R;
import org.pptik.radiostreaming.adapter.MainListAdapter;
import org.pptik.radiostreaming.service.RadioPlayService;
import org.pptik.radiostreaming.util.RadioOperationInfo;
import org.pptik.radiostreaming.view.DragLayout;
import org.pptik.radiostreaming.view.ExitDialog;
import org.pptik.radiostreaming.view.DragLayout.DragListener;

import io.vov.vitamio.Vitamio;

@SuppressLint("HandlerLeak")
public class MainActivity extends AppCompatActivity implements OnItemClickListener {
    private ListView MainMenuList;
    private ListView MainActivityList;
    private ImageView mImage;
    private DragLayout mDragLayout;
    private MainListAdapter mMainListAdapter;
    private RadioInfoChangeReceiver mReceiver = null;
    private ArrayList<String> mMainRadioName = new ArrayList<String>();
    private ArrayList<String> mMainRadioPath = new ArrayList<String>();
    private String RadioName;
    private String RadioPath;
    private boolean IsPlay = false;
    private String TAG = getClass().getSimpleName();
    private Toolbar toolbar;
    ImageView radioImage;
    RotateAnimation anim;
    ImageButton playBtn;
    boolean isPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(this);
        setContentView(R.layout.activity_main);
        initPlayerView();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_view_stream)
                .color(ContextCompat.getColor(this, R.color.colorLight))
                .sizeDp(24));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        MainMenuList = (ListView)findViewById(R.id.MainMenuList);
        MainActivityList = (ListView)findViewById(R.id.MainActivityList);
        initDragLayout();
        initMainMenu();
        initMainListInfo();
        mMainListAdapter = new MainListAdapter(this, mMainRadioName, mMainRadioPath);
        MainActivityList.setAdapter(mMainListAdapter);
        MainActivityList.setOnItemClickListener(this);
        initRadioService();
    }

    private void initPlayerView() {
        anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(9000);
        radioImage = (ImageView)findViewById(R.id.radioImage);
        playBtn = (ImageButton)findViewById(R.id.playBtn);
        playBtn.setImageDrawable(new IconicsDrawable(MainActivity.this)
                .icon(GoogleMaterial.Icon.gmd_play_circle_filled)
                .color(Color.WHITE)
                .sizeDp(34));
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlay == false) {
                    RadioPlay();
                }else {
                    RadioStop();
                }
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initReceiver();
    }
    
    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
        stopService(new Intent(MainActivity.this, RadioPlayService.class));
        if (mReceiver != null)
        {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            ExitDialog exitDialog = new ExitDialog(this, R.style.MyStyle);
            exitDialog.show();
            return false;
        }
        else
        {
            return super.onKeyDown(keyCode, event);
        }
        
    }


    
    private void initMainListInfo() {
        if (mMainRadioName != null)
        {
            mMainRadioName.removeAll(mMainRadioName);
        }
        if (mMainRadioPath != null)
        {
            mMainRadioPath.removeAll(mMainRadioPath);
        }
        String[] infos = getResources().getStringArray(R.array.radio_info);
        for (int i = 0; i < infos.length; i++)
        {
            if (i % 2 == 0)
            {
                mMainRadioName.add(infos[i]);
            }
            else
            {
                mMainRadioPath.add(infos[i]);
            }
        }
        
    }
    
    private void initDragLayout() {
        mImage = (ImageView)findViewById(R.id.MainImage);
        mImage.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                mDragLayout.open();
            }
        });
        mDragLayout = (DragLayout)findViewById(R.id.MainDragLayout);
        mDragLayout.setDragListener(new DragListener()
        {
            @Override
            public void onOpen()
            {
                MainMenuList.smoothScrollToPosition(0);
            }
            
            @Override
            public void onClose()
            {
                shake();
            }
            
            @Override
            public void onDrag(float percent)
            {
                ViewHelper.setAlpha(mImage, 1 - percent);
            }
        });
    }
    
    private void shake() {
        mImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }
    
    private void initMainMenu()
    {
        MainMenuList.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.menu_list_adapter, new String[] {
            "My recording"}));
        MainMenuList.setOnItemClickListener(this);
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == MainActivityList)
        {
            RadioName = mMainRadioName.get(position);
            RadioPath = mMainRadioPath.get(position);
            RadioPlay();
        }
        else if (parent == MainMenuList)
        {
            switch (position)
            {
                case 0:
                    Intent intent = new Intent(this, RecordActivity.class);
                    startActivity(intent);
                    break;
                
                default:
                    break;
            }
        }
    }
    
    private void initRadioService() {
        Intent intent = new Intent(MainActivity.this, RadioPlayService.class);
        intent.setAction(RadioOperationInfo.RADIO_OPERATION_ACTION);
        startService(intent);
    }
    
    private void RadioPlay() {
        if(RadioPath != null) {
            Intent intent = new Intent();
            intent.setAction(RadioOperationInfo.RADIO_OPERATION_PLAY);
            intent.putExtra(RadioOperationInfo.RADIO_INFO_NAME, RadioName);
            intent.putExtra(RadioOperationInfo.RADIO_INFO_PATH, RadioPath);
            sendBroadcast(intent);
            Log.i(TAG, "Play Radio");
            radioImage.startAnimation(anim);
            playBtn.setImageDrawable(new IconicsDrawable(MainActivity.this)
                    .icon(GoogleMaterial.Icon.gmd_pause_circle_filled)
                    .color(Color.WHITE)
                    .sizeDp(34));
            isPlay = true;
        }else {
            Toast.makeText(this, "Pilih radio", Toast.LENGTH_LONG).show();
        }
        
    }
    
    private void RadioStop() {
        playBtn.setImageDrawable(new IconicsDrawable(MainActivity.this)
                .icon(GoogleMaterial.Icon.gmd_play_circle_filled)
                .color(Color.WHITE)
                .sizeDp(34));
        isPlay = false;
        radioImage.clearAnimation();
        Intent intent = new Intent();
        intent.setAction(RadioOperationInfo.RADIO_OPERATION_STOP);
        intent.putExtra(RadioOperationInfo.RADIO_INFO_NAME, RadioName);
        sendBroadcast(intent);
        Log.i(TAG, "Stop Radio");
    }

    
    private void initReceiver() {
        if (mReceiver != null)
        {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        mReceiver = new RadioInfoChangeReceiver();
        IntentFilter inFilter = new IntentFilter();
        inFilter.addAction(RadioOperationInfo.RADIO_OPERATION_ACTION);
        inFilter.addAction(RadioOperationInfo.RADIO_OPERATION_CHANGE);
        registerReceiver(mReceiver, inFilter);
    }
    
    private class RadioInfoChangeReceiver extends BroadcastReceiver {
        
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (RadioOperationInfo.RADIO_OPERATION_ACTION.equals(intent.getAction()))
            {
                boolean tempIsPlay = intent.getBooleanExtra(RadioOperationInfo.RADIO_OPERATION_ISPLAY, false);
                String tempRadioName = intent.getStringExtra(RadioOperationInfo.RADIO_INFO_NAME);
                Message message = Message.obtain();
                message.what = 2;
                Bundle bundle = new Bundle();
                bundle.putBoolean(RadioOperationInfo.RADIO_OPERATION_ISPLAY, tempIsPlay);
                bundle.putString(RadioOperationInfo.RADIO_INFO_NAME, tempRadioName);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
            else if (RadioOperationInfo.RADIO_OPERATION_CHANGE.equals(intent.getAction()))
            {
                //radioNameView.setText("Loading Radio...");
            }
        }
        
    }
    
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (msg != null && msg.what == 2)
            {
                Bundle bundle = msg.getData();
                boolean tempIsPlay = bundle.getBoolean(RadioOperationInfo.RADIO_OPERATION_ISPLAY);
                String tempRadioName = bundle.getString(RadioOperationInfo.RADIO_INFO_NAME);
                if (IsPlay != tempIsPlay)
                {
                    IsPlay = tempIsPlay;
                    RadioName = tempRadioName;
                    if (IsPlay == true)
                    {
                        //radioNameView.setText(RadioName);
                    }
                }
            }
            
        };
    };

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //finish();
            mDragLayout.open();
        }

        return super.onOptionsItemSelected(item);
    }
}
