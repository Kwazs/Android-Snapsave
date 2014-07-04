package com.kidgeniusdesigns.snapapp;

import java.util.Arrays;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.habosa.javasnap.Friend;

public class FriendsList extends ListActivity {
	List<String> friendsUserNames;
	String[] organizedFriendsUserNames;
	//List<Friend> myFriends;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_list);
		//myFriends=SnapData.myFriends;
		
		friendsUserNames= SnapData.myFriendsNames;
		organizedFriendsUserNames= new String[friendsUserNames.size()];
		int i=0;
		for(String fr: friendsUserNames){
			organizedFriendsUserNames[i]=fr;
			i++;
		}
		Arrays.sort(organizedFriendsUserNames);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, organizedFriendsUserNames);
		
        // Assign adapter to List
        setListAdapter(adapter); 
	}
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    //get selected items
    String selectedValue = (String) getListAdapter().getItem(position);
    
    //set current friend and start next activity
    for(Friend fr: SnapData.myFriends){
    	if(fr.getUsername().contains(selectedValue)){
    		Intent i= new Intent(this, FriendsSnapActivity.class);
    		SnapData.currentFriend=fr;
    		startActivity(i);
    	}

    }
    
    
    
    Toast.makeText(getApplicationContext(), selectedValue, Toast.LENGTH_LONG).show();
    		
    }
}