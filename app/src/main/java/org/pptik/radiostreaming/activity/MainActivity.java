package org.pptik.radiostreaming.activity;


import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;

import org.pptik.radiostreaming.R;
import org.pptik.radiostreaming.adapter.LoveListAdapter;
import org.pptik.radiostreaming.adapter.MainListAdapter;
import org.pptik.radiostreaming.database.DBManager;
import org.pptik.radiostreaming.service.RadioPlayService;
import org.pptik.radiostreaming.util.Radio;
import org.pptik.radiostreaming.util.RadioOperationInfo;
import org.pptik.radiostreaming.view.DragLayout;
import org.pptik.radiostreaming.view.ExitDialog;
import org.pptik.radiostreaming.view.DragLayout.DragListener;

import io.vov.vitamio.Vitamio;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity implements OnClickListener, OnItemClickListener
{
    private ListView MainMenuList;
    
    private ListView MainActivityList;
    
    private ListView MainLoveList;
    
    private ImageView mImage;
    
    private DragLayout mDragLayout;
    
    private RelativeLayout mLoveLayout;
    
    private Button radioControll;
    
    private Button mOpenLoveListDown;
    
    private Button mOpenLoveListUp;
    
    private TextView radioNameView;
    
    private MainListAdapter mMainListAdapter;
    
    private LoveListAdapter mLoveListAdapter;
    
    private RadioInfoChangeReceiver mReceiver = null;
    
    private ArrayList<String> mMainRadioName = new ArrayList<String>();
    
    private ArrayList<String> mMainRadioPath = new ArrayList<String>();
    
    private ArrayList<Radio> mLoveRadioList = new ArrayList<Radio>();
    
    private ArrayList<String> mLoveListName = new ArrayList<String>();
    
    private ArrayList<String> mLoveListPath = new ArrayList<String>();
    
    private String RadioName;
    
    private String RadioPath;
    
    private boolean IsPlay = false;
    
    private DBManager mDBManager = null;
    private String TAG = getClass().getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        
        super.onCreate(savedInstanceState);

        Vitamio.isInitialized(this);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_activity_layout);
        MainMenuList = (ListView)findViewById(R.id.MainMenuList);
        MainActivityList = (ListView)findViewById(R.id.MainActivityList);
        radioControll = (Button)findViewById(R.id.RadioControll);
        radioNameView = (TextView)findViewById(R.id.RadioName);
        initDragLayout();
        initMainMenu();
        initLoveListView();
        initMainListInfo();
        mMainListAdapter = new MainListAdapter(this, mMainRadioName, mMainRadioPath);
        MainActivityList.setAdapter(mMainListAdapter);
        radioControll.setOnClickListener(this);
        MainActivityList.setOnItemClickListener(this);
        initRadioService();
        
    }
    
    @Override
    protected void onResume()
    {
        
        super.onResume();
        initReceiver();
    }
    
    @Override
    protected void onDestroy()
    {
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
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
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
    
    private void initLoveListinfo()
    {
        if (mLoveListName != null)
        {
            mLoveListName.removeAll(mLoveListName);
        }
        if (mLoveListPath != null)
        {
            mLoveListPath.removeAll(mLoveListPath);
        }
        mDBManager = new DBManager(this);
        mLoveRadioList = mDBManager.queryAll();
        for (int i = 0; i < mLoveRadioList.size(); i++)
        {
            mLoveListName.add(mLoveRadioList.get(i).getName());
            mLoveListPath.add(mLoveRadioList.get(i).getPath());
        }
        mDBManager.closeDB();
    }
    
    private void initLoveListView()
    {
        mOpenLoveListDown = (Button)findViewById(R.id.MainOpenLoveListDown);
        mOpenLoveListUp = (Button)findViewById(R.id.MainOpenLoveListUp);
        mLoveLayout = (RelativeLayout)findViewById(R.id.LoveListlayout);
        MainLoveList = (ListView)findViewById(R.id.MainLoveList);
        mLoveListAdapter = new LoveListAdapter(this, mLoveListName);
        MainLoveList.setAdapter(mLoveListAdapter);
        MainLoveList.setOnItemClickListener(this);
        mOpenLoveListDown.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                mOpenLoveListDown.setVisibility(View.GONE);
                mOpenLoveListUp.setVisibility(View.VISIBLE);
                mLoveLayout.setVisibility(View.VISIBLE);
                MainActivityList.setVisibility(View.GONE);
                initLoveListinfo();
                mLoveListAdapter.notifyDataSetChanged();
            }
        });
        mOpenLoveListUp.setOnClickListener(new View.OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                mOpenLoveListDown.setVisibility(View.VISIBLE);
                mOpenLoveListUp.setVisibility(View.GONE);
                mLoveLayout.setVisibility(View.GONE);
                MainActivityList.setVisibility(View.VISIBLE);
                initMainListInfo();
                mMainListAdapter.notifyDataSetChanged();
                
            }
        });
    }
    
    private void initMainListInfo()
    {
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
    
    private void initDragLayout()
    {
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
    
    private void shake()
    {
        mImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }
    
    private void initMainMenu()
    {
        MainMenuList.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.menu_list_adapter, new String[] {
            "My recording"}));
        MainMenuList.setOnItemClickListener(this);
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (parent == MainActivityList)
        {
            RadioName = mMainRadioName.get(position);
            RadioPath = mMainRadioPath.get(position);
            RadioPlay();
        }
        else if (parent == MainLoveList)
        {
            RadioName = mLoveListName.get(position);
            RadioPath = mLoveListPath.get(position);
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
    
    private void initRadioService()
    {
        Intent intent = new Intent(MainActivity.this, RadioPlayService.class);
        intent.setAction(RadioOperationInfo.RADIO_OPERATION_ACTION);
        startService(intent);
    }
    
    private void RadioPlay()
    {
        Intent intent = new Intent();
        intent.setAction(RadioOperationInfo.RADIO_OPERATION_PLAY);
        intent.putExtra(RadioOperationInfo.RADIO_INFO_NAME, RadioName);
        intent.putExtra(RadioOperationInfo.RADIO_INFO_PATH, RadioPath);
        sendBroadcast(intent);
        Log.i(TAG, "Play Radio");
        
    }
    
    private void RadioStop()
    {
        Intent intent = new Intent();
        intent.setAction(RadioOperationInfo.RADIO_OPERATION_STOP);
        intent.putExtra(RadioOperationInfo.RADIO_INFO_NAME, RadioName);
        sendBroadcast(intent);
        Log.i(TAG, "Stop Radio");
    }
    
    @Override
    public void onClick(View v)
    {
        if (v == radioControll)
        {
            if (IsPlay == false)
            {
                if (RadioPath == null)
                {
                    Toast.makeText(this, "Please select a radio station first", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    RadioPlay();
                }
            }
            else
            {
                RadioStop();
            }
        }
    }
    
    private void initReceiver()
    {
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
    
    private class RadioInfoChangeReceiver extends BroadcastReceiver
    {
        
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
                radioNameView.setText("Loading Radio...");
            }
            else if (RadioOperationInfo.RADIO_LOVE_LIST_UPDATE.equals(intent.getAction()))
            {
                int position = intent.getIntExtra(RadioOperationInfo.RADIO_INFO_NUM, 0);
                mLoveListName.remove(position);
                mLoveListPath.remove(position);
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
                        radioNameView.setText(RadioName);
                    }
                }
                
                if (radioNameView.getText().toString().equals(RadioName))
                {
                    if (IsPlay == false)
                    {
                        radioControll.setBackgroundResource(R.drawable.ic_play);
                    }
                    else
                    {
                        radioControll.setBackgroundResource(R.drawable.ic_pause);
                    }
                }
            }
            
        };
    };
}
