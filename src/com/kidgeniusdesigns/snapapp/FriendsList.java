package com.kidgeniusdesigns.snapapp;

import java.util.Arrays;
import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.habosa.javasnap.Friend;
import com.kidgeniusdesigns.snapapp.helpers.MyApplication;

public class FriendsList extends ListActivity {
	List<String> friendsUserNames;
	String[] organizedFriendsUserNames;
	ArrayAdapter<String> adapter;
	 // Search EditText
    EditText inputSearch;
    
	// List<Friend> myFriends;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_list);
		//Get a Tracker (should auto-report)
		((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#517fa4")));
		bar.setTitle("InstaSnap");
		bar.setIcon(
				   new ColorDrawable(getResources().getColor(android.R.color.transparent)));   
		
		
		
		// myFriends=SnapData.myFriends;
		inputSearch = (EditText) findViewById(R.id.inputSearch);

		friendsUserNames = SnapData.myFriendsNames;
		if(friendsUserNames==null)
			finish();
		organizedFriendsUserNames = new String[friendsUserNames.size()];
		int i = 0;
		for (String fr : friendsUserNames) {
			organizedFriendsUserNames[i] = fr;
			i++;
		}
		Arrays.sort(organizedFriendsUserNames);

		adapter = new ArrayAdapter<String>(this,
				R.layout.list_item, organizedFriendsUserNames);

		// Assign adapter to List
		setListAdapter(adapter);

		inputSearch.addTextChangedListener(new TextWatcher() {
		     
		    @Override
		    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
		        // When user changed the Text
		        FriendsList.this.adapter.getFilter().filter(cs);   
		    }
		    @Override
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
		            int arg3) {	}
		    @Override
		    public void afterTextChanged(Editable arg0) {		    }
		});
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
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// get selected items
		String selectedValue = (String) getListAdapter().getItem(position);

		// set current friend and start next activity
		for (Friend fr : SnapData.myFriends) {
			if (fr.getUsername().contains(selectedValue)) {
				Intent i = new Intent(this, FriendsSnapActivity.class);
				SnapData.currentFriend = fr;
				i.putExtra("sender", fr.getUsername());
				startActivity(i);
			}

		}

	}
	public void goToHome(View v){
		finish();
	}
	public void goToHomeTryAgain(View v){
		Toast.makeText(getApplicationContext(),
				"try again", Toast.LENGTH_LONG).show();
		finish();
	}
public void goToTop(View v){
	getListView().smoothScrollToPosition(21);
}
//	public void blockUser(String un) {
//		try {
//			FileWriter out = new FileWriter(new File(getApplicationContext()
//					.getFilesDir(), "blocked.txt"), true);
//			out.write(un + "\n");
//			out.close();
//
//			Toast.makeText(getApplicationContext(),
//					un + " won't show up next session", Toast.LENGTH_LONG).show();
//		} catch (IOException e) {
//			System.out.print(e);
//		}
//	}
}