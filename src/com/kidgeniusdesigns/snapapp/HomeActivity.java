package com.kidgeniusdesigns.snapapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import com.habosa.javasnap.Friend;
import com.habosa.javasnap.Snap;
import com.habosa.javasnap.Snapchat;
import com.habosa.javasnap.Story;

public class HomeActivity extends Activity {
	
	
	String realPath, un, pw;
	
	ProgressDialog progressDialog;
	List<String> blockedFriendsNames;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		SnapData.ctx = this.getApplicationContext();
		
		blockedFriendsNames= new ArrayList<String>();
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#517fa4")));
		bar.setTitle("InstaSnap");
		bar.setIcon(
				   new ColorDrawable(getResources().getColor(android.R.color.transparent)));   
		getBlockedFriendsNames();
		
		
		un = getIntent().getStringExtra("username");
		pw = getIntent().getStringExtra("password");
		startProgressDialog("Logging in...");
SnapData.pw=pw;
		Login lg = new Login();
		lg.execute();
		
		
	}
public void goToFeed(View v){
	Intent i= new Intent(this, FeedActivity.class);
	i.putExtra("username", un);
	startActivity(i);
}
	
	
	// shords = new ArrayList<String>();
	// shords.add("amber_rubino");
	// shords.add("briannnax");
	// shords.add("brandymillerr");
	// shords.add("sophiaxoxorose");
	// shords.add("emileesawyer69");
	// shords.add("gillianhill");
	// shords.add("crystaleee");
	// shords.add("kclear24");
	// shords.add("lacylaplantee");
	// shords.add("itslindsanity");
	// shords.add("lnsylove14");
	// shords.add("sarrahmarie44");
	// shords.add("menacedennis");
	// shords.add("choiboiiiii");
	public void startProgressDialog(String screen) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Loading");
		progressDialog.setMessage(screen);
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();
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
					
					
					SnapData.myFriends=new ArrayList<Friend>();
					SnapData.myFriendsNames=new ArrayList<String>();
					// get friends list
					Friend[] possibleFriends = Snapchat.getFriends(loginObj);
					List<Friend> checkForFriendName=new ArrayList<Friend>();
					for(Friend fr:possibleFriends){								
						if(!blockedFriendsNames.contains(fr.getUsername())){
						//only add nonblocked ones
						checkForFriendName.add(fr);
						SnapData.myFriends.add(fr);
						
						}else{
							System.out.println("Too bad r blocked");
						}
					}
		
					//add snaps!
					SnapData.unreadSnaps = new ArrayList<Snap>();
					Snap[] snapsArray =Snapchat.getSnaps(loginObj);
					for(Snap s: snapsArray){
						System.out.println(s.getSender());
					}
					
					
					//SnapData.myFriends= new ArrayList<Friend>();
					// get storys
					Story[] notdownloadable = Snapchat.getStories(un,
							SnapData.authTokenSaved);
					
					SnapData.myStorys = new ArrayList<Story>();
					
					for(Story s:Story
							.filterDownloadable(notdownloadable)){
						//add to friends if not on there
						if(!SnapData.myFriendsNames.contains(s.getSender())){
							
							//only add nonblocked ones
							if(!blockedFriendsNames.contains(s.getSender())){
							SnapData.myFriendsNames.add(s.getSender());
							}else{
								System.out.println("Too bad r blocked");
							}
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
				startActivity(new Intent(getApplicationContext(),MainActivity.class));
			
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (progressDialog != null)
				progressDialog.dismiss();
			
			Intent i= new Intent(getApplicationContext(), FeedActivity.class);
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

	
	public void getBlockedFriendsNames(){
		String line;
		BufferedReader in = null;

		try {
			in = new BufferedReader(new FileReader(new File(
					getApplicationContext().getFilesDir(), "blocked.txt")));
			while ((line = in.readLine()) != null) {
				System.out.println(line);
				blockedFriendsNames.add(line);	
			}
			in.close();
			
			
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	
	public void goToFriendsList(View v){
startActivity(new Intent(this,FriendsList.class));
	}
	
	
}