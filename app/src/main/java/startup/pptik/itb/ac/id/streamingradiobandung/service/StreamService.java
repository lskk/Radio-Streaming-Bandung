package startup.pptik.itb.ac.id.streamingradiobandung.service;

/**
 * Created by hynra on 5/11/2016.
 */
import java.io.IOException;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import startup.pptik.itb.ac.id.streamingradiobandung.R;
import startup.pptik.itb.ac.id.streamingradiobandung.RadioActivity;

public class StreamService extends Service {
    private static final String TAG = "StreamService";
    MediaPlayer mp;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    NotificationManager notificationManager;
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";
    int notifId = 5315;
    String currStation, currUrl, currPathImage;
    boolean isAvailable = true;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        // Init the SharedPreferences and Editor
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "onStart");
        currStation = intent.getStringExtra("radio name");
        currUrl = intent.getStringExtra("url");
        Log.i(TAG, "radio url : "+currUrl);
        currPathImage = intent.getStringExtra("path image");

        // Set up the buffering notification
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Context context = getApplicationContext();
        String notifTitle = context.getResources().getString(R.string.app_name);
        String notifMessage = context.getResources().getString(R.string.buffering);
        Intent nIntent = new Intent(context, RadioActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, nIntent, 0);
        // Defining notification
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setSmallIcon(R.drawable.radio_icon);
        nBuilder.setContentTitle(notifTitle);
        nBuilder.setAutoCancel(false);
        nBuilder.setContentText(notifMessage);
        nBuilder.setContentIntent(pIntent);
        nBuilder.setOngoing(true);
        notificationManager.notify(notifId, nBuilder.build());

        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        sendBroadcast(" | Buffering ...");
        try {
            mp.setDataSource(currUrl);
            mp.prepare();


        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, "SecurityException");
            e.printStackTrace();
            sendBroadcast("Radio sedang Offline");
            isAvailable = false;
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "SecurityException");
            e.printStackTrace();
            sendBroadcast("Radio sedang Offline");
            isAvailable = false;
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "IllegalStateException");
            e.printStackTrace();
            sendBroadcast("Radio sedang Offline");
            isAvailable = false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "IOException");
            sendBroadcast(" | Radio sedang Offline");
            isAvailable = false;
            e.printStackTrace();
        }
        mp.start();

        // Set the isPlaying preference to true
        editor.putBoolean("isPlaying", true);
        editor.commit();
        Log.d(TAG, String.valueOf(prefs.getBoolean("isPlaying", false)));
        notifTitle = context.getResources().getString(R.string.app_name);
        notifMessage = "Now Playing : "+currStation;
        nIntent = new Intent(context, RadioActivity.class);
        pIntent = PendingIntent.getActivity(context, 0, nIntent, 0);

        // Defining notification
        nBuilder = new NotificationCompat.Builder(this);

        nBuilder.setSmallIcon(R.drawable.radio_icon);
        nBuilder.setContentTitle(notifTitle);
        nBuilder.setContentText(notifMessage);
        nBuilder.setContentIntent(pIntent);
        nBuilder.setOngoing(true);
        notificationManager.notify(notifId, nBuilder.build());
        if (isAvailable) sendBroadcast(" | Now Playing");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        mp.stop();
        mp.release();
        mp = null;
        if(isAvailable) sendBroadcast("");
        else {
            sendBroadcast(" | Radio sedang Offline");
        }
        editor.putBoolean("isPlaying", false);
        editor.commit();
        notificationManager.cancel(notifId);
    }

    private void sendBroadcast(String status) {
        Intent new_intent = new Intent();
        new_intent.setAction(ACTION_STRING_ACTIVITY);
        new_intent.putExtra("status", status);
        sendBroadcast(new_intent);
    }

}