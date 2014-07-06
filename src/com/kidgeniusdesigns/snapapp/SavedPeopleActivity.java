package com.kidgeniusdesigns.snapapp;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

public class SavedPeopleActivity extends Activity {

	List<String> savedFriendsNames;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saved_people);
		savedFriendsNames=new ArrayList<String>();
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#517fa4")));
		bar.setTitle("InstaSnap");
		bar.setIcon(
				   new ColorDrawable(getResources().getColor(android.R.color.transparent)));   
		
		for(int i=0; i<100000; i++){
			if(i==99999)
				finish();
		}
		
	}
	public void back(View v){
		finish();
	}
//	public void getBlockedFriendsNames(){
//		String line;
//		BufferedReader in = null;
//
//		try {
//			in = new BufferedReader(new FileReader(new File(
//					getApplicationContext().getFilesDir(), "saved.txt")));
//			while ((line = in.readLine()) != null) {
//				System.out.println(line);
//				savedFriendsNames.add(line);	
//			}
//			in.close();
//			
//			
//		} catch (FileNotFoundException e) {
//			System.out.println(e);
//		} catch (IOException e) {
//			System.out.println(e);
//		}
//	}
}