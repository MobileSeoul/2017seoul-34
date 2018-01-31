package com.example.a85625.seoultour;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * Created by 85625 on 2017-09-26.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
    private static final String TAG = "FirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        sendPushNotification(remoteMessage.getData().get("message"));
    }

    private void sendPushNotification(String message){
        Log.d("LOG", "받은 메시지 : " + message);
        //System.out.println("received message : " + message);
        try {
            message = URLDecoder.decode(message, "UTF-8");
        }
        catch (Exception e)
        {
            Log.d("LOG", "Decode Error" + e.getMessage());
        }
        //DB데이터 삽입
        String[] split = DB.insertStringMessage(getBaseContext(), message);

        //브로드캐스트 메시지를 즉시 메신저로 전달
        Intent intent_broadcast = new Intent("pushRefresh");
        intent_broadcast.putExtra("sender", split[0]);
        intent_broadcast.putExtra("message", split[2]);
        intent_broadcast.putExtra("date", split[4]);
        getApplicationContext().sendBroadcast(intent_broadcast);

        Intent intent = new Intent(this, TapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.noti).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Push Title")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri).setLights(000000255, 500, 2000)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakelock.acquire(5000);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
