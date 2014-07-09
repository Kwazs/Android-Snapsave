package com.kidgeniusdesigns.snapapp;

import java.io.File;
import java.io.FileNotFoundException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.habosa.javasnap.Snapchat;
import com.kidgeniusdesigns.snapapp.helpers.MyApplication;
import com.kidgeniusdesigns.snapapp.helpers.Utility;

public class UploadVideoActivity extends Activity {
	Uri videoLocation;
	VideoView videoView;
	MediaController mediaController;
	boolean sentOrNah;
	String filePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_video);

		//Get a Tracker (should auto-report)
				((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
		
				
				ActionBar bar = getActionBar();
				bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#517fa4")));
				bar.setTitle("InstaSnap");
				bar.setIcon(new ColorDrawable(getResources().getColor(
						android.R.color.transparent)));
				
		// select vid from gallery
		Intent pickMedia = new Intent(Intent.ACTION_GET_CONTENT);
		pickMedia.setType("video/*");
		startActivityForResult(pickMedia, 12345);

		
		videoView = (VideoView) findViewById(R.id.videoView1);
		mediaController = new MediaController(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 12345) {
			if (resultCode == Activity.RESULT_OK) {
				videoLocation = data.getData();
				
				filePath=Utility.getPath(getApplicationContext(), videoLocation);

				
				mediaController.setAnchorView(videoView);
				videoView.setMediaController(mediaController);
				videoView.setVideoURI(videoLocation);
				videoView.start();
				
				
				// Do something with the data...
			}

		}
	}
	
	@Override
    protected void onStart() {
        super.onStart();
      //Get an Analytics tracker to report app starts & uncaught exceptions etc.
    	GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }
    /* (non-Javadoc)
    * @see android.app.Activity#onStop()
    */
    @Override
    protected void onStop() {
        super.onStop();
      //Stop the analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);

    }
public void uploadVid(View v){
	UploadSnap us = new UploadSnap();
	us.execute();
}


private class UploadSnap extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
		try {
			boolean video = true;
			File cur = new File(filePath);
			String mediaId = Snapchat.upload(cur, getIntent().getStringExtra("username"),
					SnapData.authTokenSaved, video);
			int viewTime = 10; // seconds
			String caption = ""; // This is only shown in the story
											// list, not on the actual story
											// photo/video.
			sentOrNah = Snapchat.sendStory(mediaId, viewTime, video,
					caption, getIntent().getStringExtra("username"), SnapData.authTokenSaved);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "Executed";
	}

	@Override
	protected void onPostExecute(String result) {

		if (sentOrNah){
			Toast tst= 
			Toast.makeText(getApplicationContext(),
					"Succesfully Uploaded to Story", Toast.LENGTH_SHORT);
			tst.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
					
					tst.show();
					finish();
		}else{
			Toast tst=Toast.makeText(getApplicationContext(),
					"File is too long or wrong format", Toast.LENGTH_SHORT);
			tst.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
			
			tst.show();
		}
		
		
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected void onProgressUpdate(Void... values) {
	}
}
public void cancelVid(View v){
	finish();
}

}