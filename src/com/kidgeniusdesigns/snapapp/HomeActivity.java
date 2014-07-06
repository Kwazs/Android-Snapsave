package com.kidgeniusdesigns.snapapp;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;

import com.habosa.javasnap.Friend;
import com.habosa.javasnap.Snapchat;
import com.habosa.javasnap.Story;

public class HomeActivity extends Activity {
	
	
	String realPath, un, pw;
	
	ProgressBar loadingCircle;	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		SnapData.ctx = this.getApplicationContext();

		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#517fa4")));
		bar.setTitle("InstaSnap");
		bar.setIcon(
				   new ColorDrawable(getResources().getColor(android.R.color.transparent)));   		
		
		un = getIntent().getStringExtra("username");
		pw = getIntent().getStringExtra("password");
		loadingCircle=(ProgressBar)findViewById(R.id.progressBar1);
		loadingCircle.setVisibility(ProgressBar.VISIBLE);
		
		
		SnapData.pw=pw;
		Login lg = new Login();
		lg.execute();
		
		
	}
public void goToFeed(View v){
	Intent i= new Intent(this, FeedActivity.class);
	i.putExtra("username", un);
	startActivity(i);
}
	

	
	private class Login extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... index) {
			JSONObject loginObj = Snapchat.login(un, pw);
			try {
				
				
				// get authentication token
				if (loginObj != null) {
					SnapData.authTokenSaved = loginObj
							.getString(Snapchat.AUTH_TOKEN_KEY);
					
					SnapData.myFriendsNames=new ArrayList<String>();
					
					Friend[] possibleFriends = Snapchat.getFriends(loginObj);					
						SnapData.myFriends=Arrays.asList(possibleFriends);

	
					
					// get storys
					Story[] notdownloadable = Snapchat.getStories(un,
							SnapData.authTokenSaved);
					
					SnapData.myStorys = new ArrayList<Story>();
					
					for(Story s:Story
							.filterDownloadable(notdownloadable)){
						String sender=s.getSender();
						//add to friends if not on there
						if(!SnapData.myFriendsNames.contains(sender)){
							
							SnapData.myFriendsNames.add(sender);
						}
						
						
						//add to my storys
						if(s.isImage())
							SnapData.myStorys.add(s);
					}
					
					SnapData.byteList = new ArrayList<byte[]>();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				//wrong password
				Intent i=new Intent(getApplicationContext(),MainActivity.class);
				i.putExtra("wrong", "Wrong password");
				startActivity(i);
			
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (loadingCircle != null)
				loadingCircle.setVisibility(ProgressBar.GONE);
			
			Intent i= new Intent(getApplicationContext(), FeedActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra("username", un);
			startActivity(i);
		}
	}

	

	// And to convert the image URI to the direct file system path of the image
	// file
	public String getRealPathFromURI(Uri contentUri) {

		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(contentUri, proj, // Which columns to
														// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}
	
	
	public void goToFriendsList(View v){
startActivity(new Intent(this,FriendsList.class));
	}
	
	
}