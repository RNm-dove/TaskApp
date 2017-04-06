package com.example.ryosuke.nakagawa.taskapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.method.MultiTapKeyListener;
import android.util.Log;

import io.realm.Realm;

/**
 * Created by ryosuke on 2017/01/25.
 */
public class TaskAlarmReceiver  extends BroadcastReceiver{
    @Override
    public void onReceive(Context context,Intent intent){
        Log.d("TaskApp","onReceive");

        //通知の設定を行う
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.large_icon));
        builder.setSmallIcon(R.drawable.small_icon);
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setAutoCancel(true);

        //extra_task　から　task のidを取得して、いｄから  taskのインスタンスを取得する
        int taskId = intent.getIntExtra(MainActivity.EXTRA_TASK,-1);
        Realm realm = Realm.getDefaultInstance();
        Task task = realm.where(Task.class).equalTo("id",taskId).findFirst();
        realm.close();

        //タスクの情報を設定する
        builder.setTicker(task.getTitle()); //5以降は表示されない
        builder.setContentTitle(task.getTitle());
        builder.setContentText(task.getContents());

        //通知をタップしたらアプリを起動
        Intent startAppIntent = new Intent(context,MainActivity.class);
        startAppIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,startAppIntent,0);
        builder.setContentIntent(pendingIntent);


        //通知を表示する
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(task.getId(),builder.build());
    }
}
