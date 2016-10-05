package org.pptik.radiostreaming.activity;

import io.vov.vitamio.LibsChecker;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.pptik.radiostreaming.adapter.RecordListAdapter;
import org.pptik.radiostreaming.R;
import org.pptik.radiostreaming.view.ExitDialog;

public class RecordActivity extends Activity implements OnClickListener, OnItemClickListener
{
    private Button RecordActivityBack;
    
    private Button record_start;
    
    private Button record_stop;
    
    private ArrayList<String> RecordFile = new ArrayList<String>();
    
    private ListView RecordActivityList;
    
    private RecordListAdapter mRecordAdapter;
    
    private File mRecordPath;
    
    private File mRecordFile;
    
    private String TempFileName = "MyRecording_";
    
    private MediaRecorder mMediaRecorder;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        
        super.onCreate(savedInstanceState);
        if (!LibsChecker.checkVitamioLibs(this))
        {
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.record_activity_layout);
        RecordActivityBack = (Button)findViewById(R.id.RecordActivityBack);
        record_start = (Button)findViewById(R.id.record_start);
        record_stop = (Button)findViewById(R.id.record_stop);
        RecordActivityList = (ListView)findViewById(R.id.RecordActivityList);
        initRecordList();
        RecordActivityBack.setOnClickListener(this);
        record_start.setOnClickListener(this);
        record_stop.setOnClickListener(this);
        RecordActivityList.setOnItemClickListener(this);
        record_start.setEnabled(true);
        record_stop.setEnabled(false);
    }
    
    @Override
    protected void onDestroy()
    {
        
        super.onDestroy();
        finish();
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
    
    @SuppressLint("SdCardPath")
    private void initRecordList()
    {
        
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            String sDir = "/sdcard/MyRecord";
            File destDir = new File(sDir);
            if (!destDir.exists())
            {
                destDir.mkdirs();
            }
            mRecordPath = new File(sDir);
            File home = mRecordPath;
            if (home.listFiles(new MusicFilter()).length > 0)
            {
                for (File file : home.listFiles(new MusicFilter()))
                {
                    RecordFile.add(file.getName());
                }
                mRecordAdapter = new RecordListAdapter(this, RecordFile);
                RecordActivityList.setAdapter(mRecordAdapter);
            }
        }
        else
        {
            Toast.makeText(this, "There is no SD card", Toast.LENGTH_LONG).show();
        }
    }

    class MusicFilter implements FilenameFilter
    {
        public boolean accept(File dir, String name)
        {
            return (name.endsWith(".aac"));
        }
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        File playfile = new File(mRecordPath.getAbsolutePath() + File.separator + RecordFile.get(position));
        playRecord(playfile);
    }

    private void playRecord(File file)
    {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "audio");
        startActivity(intent);
    }
    
    @Override
    public void onClick(View v)
    {
        if (v == record_start)
        {
            try
            {
                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                try
                {
                    mRecordFile = File.createTempFile(TempFileName, ".aac", mRecordPath);
                    
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                mMediaRecorder.setOutputFile(mRecordFile.getAbsolutePath());
                mMediaRecorder.prepare();
                mMediaRecorder.start();
                record_start.setEnabled(false);
                record_stop.setEnabled(true);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if (v == record_stop)
        {
            if (mRecordFile != null)
            {
                mMediaRecorder.stop();
                RecordFile.add(mRecordFile.getName());
                mRecordAdapter = new RecordListAdapter(this, RecordFile);
                RecordActivityList.setAdapter(mRecordAdapter);
                mMediaRecorder.release();
                mMediaRecorder = null;
                record_start.setEnabled(true);
                record_stop.setEnabled(false);
            }
        }
        else if (v == RecordActivityBack)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
