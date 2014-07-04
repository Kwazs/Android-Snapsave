package com.kidgeniusdesigns.snapapp;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.habosa.javasnap.Friend;

public class FriendsList extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_list);
		Friend[] myFriends=SnapData.myFriends;
		
		String[] friendsUserNames= new String[myFriends.length];
		int i=0;
		for(Friend fr: myFriends){
			friendsUserNames[i]=fr.getUsername();
			i++;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, friendsUserNames);

		Toast.makeText(getApplicationContext(), "Soon youll be able to filter main snaps", Toast.LENGTH_LONG).show();
		Toast.makeText(getApplicationContext(), "Soon youll be able to filter main snaps", Toast.LENGTH_LONG).show();
		Toast.makeText(getApplicationContext(), "Soon youll be able to filter main snaps", Toast.LENGTH_LONG).show();

		
        // Assign adapter to List
        setListAdapter(adapter); 
	}
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    //get selected items
    String selectedValue = (String) getListAdapter().getItem(position);
    Toast.makeText(getApplicationContext(), selectedValue, Toast.LENGTH_LONG).show();
    		
    }
}