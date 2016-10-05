package org.pptik.radiostreaming.service;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.pptik.radiostreaming.util.RadioOperationInfo;

import java.io.IOException;

public class RadioPlayService extends Service
{
    private MediaPlayer mMediaPlayer = null;
    private String RadioName;
    private String RadioPath;
    private RadioOperationReceiver mReceiver = null;
    private boolean ISFIRST = false;
    private String TAG = getClass().getSimpleName();
    
    @Override
    public IBinder onBind(Intent intent)
    {
        
        return null;
    }
    
    @Override
    public void onCreate()
    {
        
        super.onCreate();
        Vitamio.isInitialized(this);
        if (mMediaPlayer != null)
        {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mReceiver != null)
        {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        mMediaPlayer = new MediaPlayer(RadioPlayService.this);
        mReceiver = new RadioOperationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RadioOperationInfo.RADIO_OPERATION_PLAY);
        filter.addAction(RadioOperationInfo.RADIO_OPERATION_STOP);
        registerReceiver(mReceiver, filter);
    }
    
    @Override
    public void onDestroy()
    {
        
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        if (mReceiver != null)
        {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        if (mMediaPlayer != null)
        {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void RadioStop()
    {
        if (mMediaPlayer != null)
        {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer.reset();
            mMediaPlayer = null;
        }
    }
    
    private void NextRadioPlay()
    {
        if (mMediaPlayer != null)
        {
           //Todo:: handle next station
            mMediaPlayer.pause();

        }
    }
    
    private void FirstRadioPlay()
    {
        try
        {
            mMediaPlayer.reset();
            streamAudio(RadioPath);
            mHandler.sendEmptyMessage(1);
            Intent intent = new Intent();
            intent.setAction(RadioOperationInfo.RADIO_OPERATION_CHANGE);
            sendBroadcast(intent);
        }
        catch (Exception e)
        {
            System.out.println("Error" + e.getMessage());
        }
    }
    
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            if (msg.what == 1)
            {
                if (mMediaPlayer != null)
                {
                    Intent intent = new Intent();
                    intent.setAction(RadioOperationInfo.RADIO_OPERATION_ACTION);
                    intent.putExtra(RadioOperationInfo.RADIO_INFO_NAME, RadioName);
                    intent.putExtra(RadioOperationInfo.RADIO_OPERATION_ISPLAY, mMediaPlayer.isPlaying());
                    sendBroadcast(intent);
                    if (RadioName != null)
                    {
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                    }
                }
            }
        };
    };



    private class RadioOperationReceiver extends BroadcastReceiver
    {
        
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (RadioOperationInfo.RADIO_OPERATION_PLAY.equals(action))
            {
                String TempRadioName = intent.getStringExtra(RadioOperationInfo.RADIO_INFO_NAME);
                String TempRadioPath = intent.getStringExtra(RadioOperationInfo.RADIO_INFO_PATH);
                if (TempRadioName.equals(RadioName) == false)
                {
                    ISFIRST = false;
                    RadioName = TempRadioName;
                    RadioPath = TempRadioPath;
                    if (RadioPath != null && ISFIRST == false)
                    {
                        FirstRadioPlay();
                        ISFIRST = true;
                        Log.i(TAG, "First Play");
                    }
                }
                else
                {
                    NextRadioPlay();
                    Log.i(TAG, "Next Play");
                }
            }
            else if (RadioOperationInfo.RADIO_OPERATION_STOP.equals(action))
            {
                String TempRadioName = intent.getStringExtra(RadioOperationInfo.RADIO_INFO_NAME);
                if (RadioPath != null && TempRadioName.equals(RadioName))
                {
                    RadioStop();
                }
            }
        }
        
    }


    private void streamAudio(String streamURL) {
        mMediaPlayer = new MediaPlayer(this);
        try {
            mMediaPlayer.setDataSource(streamURL);
            mMediaPlayer.prepareAsync();

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "Start Play");
                    mp.start();
                }
            });

            mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_FILE_OPEN_OK:
                            Log.d(TAG, "Media OK");
                            long buffersize = mMediaPlayer.audioTrackInit();
                            mMediaPlayer.audioInitedOk(buffersize);
                            break;
                    }
                    return true;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
