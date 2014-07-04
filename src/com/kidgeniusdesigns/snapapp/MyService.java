package com.kidgeniusdesigns.snapapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class MyService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override 
	public int onStartCommand(Intent intent,int flags, int startId){
		NotificationCompat.Builder builder= new NotificationCompat.Builder(getApplicationContext());
		builder.setAutoCancel(true);
		builder.setContentTitle("Open Snap App");
		builder.setContentText("Ben Harvey gets paper");
		builder.setSmallIcon(R.drawable.ic_launcher);
		
		Notification notif =builder.build();
		NotificationManager nm =(NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
		nm.notify(8, notif);
		return START_STICKY;
		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_LONG).show();
	}
}
