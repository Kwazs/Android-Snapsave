package com.kidgeniusdesigns.snapapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.kidgeniusdesigns.snapapp.helpers.MyApplication;

public class MainActivity extends Activity {
	private InterstitialAd interstitial;
	EditText username, password;
	static String usernameForLater;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Get a Tracker (should auto-report)
		((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);

		username=(EditText)findViewById(R.id.usernameBox);
		password=(EditText)findViewById(R.id.passwordBox);
		
		username.setHint("username");
		getNameAndPw();
		if(getIntent().getStringExtra("wrong")!=null)
			Toast.makeText(this, getIntent().getStringExtra("wrong"), Toast.LENGTH_LONG).show();
		
		
		// Create the interstitial.
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId("ca-app-pub-4742368221536941/6949454117");
		// Create ad request.
		AdRequest adRequest = new AdRequest.Builder().build();

		// Begin loading your interstitial.
		interstitial.loadAd(adRequest);
		

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
	public void logIn(View v){
		usernameForLater=username.getText().toString();
		saveToFile(usernameForLater,password.getText().toString());
		if(interstitial.isLoaded()){
		displayInterstitial();
		}else{
			System.out.println("Didnt finish loading yet");
		}
		Intent i= new Intent(this, HomeActivity.class);
		i.putExtra("username", usernameForLater);
		i.putExtra("password", password.getText().toString());
		startActivity(i);
	}
	
	public void saveToFile(String un, String pw) {
		try {
			FileWriter out = new FileWriter(new File(getApplicationContext()
					.getFilesDir(), "username.txt"));
			out.write(un);
			out.close();
			
			out = new FileWriter(new File(getApplicationContext()
					.getFilesDir(), "password.txt"));
			out.write(pw);
			out.close();
			
		} catch (IOException e) {
			System.out.print(e);
		}
	}
public void getNameAndPw(){
	String line;
	BufferedReader in = null;

	try {
		in = new BufferedReader(new FileReader(new File(
				getApplicationContext().getFilesDir(), "username.txt")));
		while ((line = in.readLine()) != null) {
			System.out.println(line);
			username.setText(line);
			
		}
		in.close();
		
		in = new BufferedReader(new FileReader(new File(
				getApplicationContext().getFilesDir(), "password.txt")));
		while ((line = in.readLine()) != null) {
			System.out.println(line);
			password.setText(line);
			
		}
		
		
	} catch (FileNotFoundException e) {
		System.out.println(e);
	} catch (IOException e) {
		System.out.println(e);
	}
}
//Invoke displayInterstitial() when you are ready to display an
	// interstitial.
	public void displayInterstitial() {
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}
}