package com.kidgeniusdesigns.snapapp;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.habosa.javasnap.Friend;
import com.kidgeniusdesigns.snapapp.helpers.Utility;

public class BigView extends Activity {
	ImageView iv;
	File imageFileFolder, imageFileName;
	MediaScannerConnection msConn;
	FileOutputStream fileOutputStream;
	File file1;
	Bitmap bm;
	Button nameButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_big_view);		
		
		iv = (ImageView) findViewById(R.id.imageView1);
		nameButton=(Button) findViewById(R.id.nameButton);
		nameButton.setText(getIntent().getStringExtra("sender"));
		
		bm = Utility.getPhoto(SnapData.currentByte);
		iv.setImageBitmap(bm);
			

	}
	public void goToFriendsList(View v){
		Intent intent=new Intent(this,FriendsList.class);
		

	    startActivity(intent);
			}
	public void savePhoto(Bitmap bmp) {
		imageFileFolder = new File(Environment.getExternalStorageDirectory(),
				"Saved Snaps");
		imageFileFolder.mkdir();
		FileOutputStream out = null;
		Calendar c = Calendar.getInstance();
		String date = fromInt(c.get(Calendar.MONTH))
				+ fromInt(c.get(Calendar.DAY_OF_MONTH))
				+ fromInt(c.get(Calendar.YEAR))
				+ fromInt(c.get(Calendar.HOUR_OF_DAY))
				+ fromInt(c.get(Calendar.MINUTE))
				+ fromInt(c.get(Calendar.SECOND));
		imageFileName = new File(imageFileFolder, date.toString() + ".jpg");
		try {
			out = new FileOutputStream(imageFileName);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			scanPhoto(imageFileName.toString());
			out = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String fromInt(int val) {
		return String.valueOf(val);
	}

	public void scanPhoto(final String imageFileName) {
		msConn = new MediaScannerConnection(BigView.this,
				new MediaScannerConnectionClient() {
					public void onMediaScannerConnected() {
						msConn.scanFile(imageFileName, null);
						Log.i("msClient obj  in Photo Utility",
								"connection established");
					}

					public void onScanCompleted(String path, Uri uri) {
						msConn.disconnect();
						Log.i("msClient obj in Photo Utility", "scan completed");
					}
				});
		msConn.connect();
		Toast.makeText(getApplicationContext(), "Saved to Gallery", Toast.LENGTH_LONG)
				.show();
	}
	public void save(View v){
		savePhoto(bm);
	}
	public void goToSnaps(View v){
		// set current friend and start next activity
				for (Friend fr : SnapData.myFriends) {
					if (fr.getUsername().contains(getIntent().getStringExtra("sender"))) {
						Intent i = new Intent(this, FriendsSnapActivity.class);
						i.putExtra("sender",getIntent().getStringExtra("sender"));
						SnapData.currentFriend = fr;
						startActivity(i);
					}

				}
	}
	public void back(View v){
		finish();
	}
}