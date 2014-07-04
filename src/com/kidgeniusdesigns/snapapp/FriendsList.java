package com.kidgeniusdesigns.snapapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import com.kidgeniusdesigns.snapapp.helpers.SwipeDismissListViewTouchListener;

public class FriendsList extends ListActivity {
	List<String> friendsUserNames;
	String[] organizedFriendsUserNames;
	ArrayAdapter<String> adapter;

	// List<Friend> myFriends;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_list);
		// myFriends=SnapData.myFriends;

		friendsUserNames = SnapData.myFriendsNames;
		organizedFriendsUserNames = new String[friendsUserNames.size()];
		int i = 0;
		for (String fr : friendsUserNames) {
			organizedFriendsUserNames[i] = fr;
			i++;
		}
		Arrays.sort(organizedFriendsUserNames);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, organizedFriendsUserNames);

		// Assign adapter to List
		setListAdapter(adapter);

		// set swipe to block
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				getListView(),
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return true;
					}

					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							String fileData = adapter.getItem(position)
									.toString();
							blockUser(fileData);
						}
						adapter.notifyDataSetChanged();
					}
				});
		getListView().setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during
		// ListView scrolling,
		// we don't look for swipes.
		getListView().setOnScrollListener(touchListener.makeScrollListener());

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
				startActivity(i);
			}

		}
		Toast.makeText(getApplicationContext(), selectedValue,
				Toast.LENGTH_LONG).show();

	}

	public void blockUser(String un) {
		try {
			FileWriter out = new FileWriter(new File(getApplicationContext()
					.getFilesDir(), "blocked.txt"), true);
			out.write(un + "\n");
			out.close();

			Toast.makeText(getApplicationContext(),
					un + " won't show up next time", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			System.out.print(e);
		}
	}
}