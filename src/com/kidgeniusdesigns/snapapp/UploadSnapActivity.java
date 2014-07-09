package com.kidgeniusdesigns.snapapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.habosa.javasnap.Snapchat;
import com.kidgeniusdesigns.snapapp.helpers.MyApplication;
import com.kidgeniusdesigns.snapapp.helpers.Utility;

public class UploadSnapActivity extends Activity {
	boolean sentOrNah;
	Uri currImageURI;
	ImageView picture;
	EditText captionEditText;
	Bitmap curBit;
	Button uploadButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_snap);
		//Get a Tracker (should auto-report)
		((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
		picture=(ImageView)findViewById(R.id.uploadImageView);
		captionEditText=(EditText)findViewById(R.id.captionEditText);
		captionEditText.setClickable(false);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#517fa4")));
		bar.setTitle("InstaSnap");
		bar.setIcon(new ColorDrawable(getResources().getColor(
				android.R.color.transparent)));
		
		
		
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
 
			// set title
			alertDialogBuilder.setTitle("Upload");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Photo or Video")
				.setCancelable(false)
				.setPositiveButton("Video",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close
						// current activity
						Intent i=new Intent(getApplicationContext(), UploadVideoActivity.class);
						i.putExtra("username", getIntent().getStringExtra("username"));
				startActivity(i);
				finish();
					}
				  })
				.setNegativeButton("Photo",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						
						// To open up a gallery browser
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(Intent.createChooser(intent, "Select Picture"),
								1);
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				try{
				alertDialog.show();
				}catch(Exception e){
					
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
	public void upload(View v){
		String caption;
		if(captionEditText.getText().toString().length()>1){
			caption=captionEditText.getText().toString();
		}else{
			caption=" ";
		}
		
		Bitmap withCaption=drawTextToBitmap(getApplicationContext(), 
				  curBit, 
				  caption);
		picture.setImageBitmap(withCaption);
		
		//create a file to write bitmap data
		File f = new File(getFilesDir() + "/image");
		try {
			f.createNewFile();
		

		//Convert bitmap to byte array
		Bitmap bitmap = withCaption;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
		byte[] bitmapdata = bos.toByteArray();

		//write the bytes in file
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(bitmapdata);
		
		bos.flush();
		fos.close();
		UploadSnap us = new UploadSnap();
		us.execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				// currImageURI is the global variable I'm using to hold the
				// content:// URI of the image
				currImageURI = data.getData();
				InputStream iStream;
				try {
					iStream = getContentResolver()
							.openInputStream(currImageURI);
					byte[] inputData = getBytes(iStream);
					curBit = Utility.getPhoto(inputData);
					picture.setImageBitmap(curBit);
					captionEditText.setClickable(true);
					String filename = "image";
					FileOutputStream outputStream = openFileOutput(filename,
							Context.MODE_PRIVATE);
					outputStream.write(inputData);
					outputStream.close();
					
					
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
				
				
//				UploadSnap upl = new UploadSnap();
//				upl.execute("", "");
			}
		}
	}
	public byte[] getBytes(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];

		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			byteBuffer.write(buffer, 0, len);
		}
		return byteBuffer.toByteArray();
	}
	public Bitmap drawTextToBitmap(Context gContext, 
			  Bitmap bm, 
			  String gText) {
			  Resources resources = gContext.getResources();
			  float scale = resources.getDisplayMetrics().density;
			 
			  android.graphics.Bitmap.Config bitmapConfig =
			      bm.getConfig();
			  // set default bitmap config if none
			  if(bitmapConfig == null) {
			    bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
			  }
			  // resource bitmaps are imutable, 
			  // so we need to convert it to mutable one
			  Bitmap newBm = bm.copy(bitmapConfig, true);
			 
			  Canvas canvas = new Canvas(newBm);
			  // new antialised Paint
			  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			  // text color - #3D3D3D
			  paint.setColor(Color.WHITE);
			  // text size in pixels
			  paint.setTextSize((int) (44 * scale));
			  // text shadow
			  paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
			 
			  // draw text to the Canvas center
			  Rect bounds = new Rect();
			  paint.getTextBounds(gText, 0, gText.length(), bounds);
			  int x = (newBm.getWidth() - bounds.width())/2;
			  int y = (newBm.getHeight() + bounds.height())/2;
			 
			  canvas.drawText(gText, x, y, paint);
			 
			  return newBm;
			}
	
	public void preview(View v){
		picture.setImageBitmap(drawTextToBitmap(getApplicationContext(), 
				  curBit, 
				  captionEditText.getText().toString()));
	}
	
	private class UploadSnap extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				boolean video = false;
				File cur = new File(getFilesDir() + "/image");
				String mediaId = Snapchat.upload(cur, getIntent().getStringExtra("username"),
						SnapData.authTokenSaved, video);
				int viewTime = 10; // seconds
				String caption = "My Story"; // This is only shown in the story
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
			// Toast.makeText(getApplicationContext(),
			// "ff"+sentOrNah+SnapData.authTokenSaved,
			// Toast.LENGTH_LONG).show();
			if (sentOrNah){
				Toast tst= 
				Toast.makeText(getApplicationContext(),
						"Succesfully Uploaded to Story", Toast.LENGTH_SHORT);
				tst.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
						
						tst.show();
						finish();
			}else{
				Toast tst=Toast.makeText(getApplicationContext(),
						"Error occured please try again later", Toast.LENGTH_SHORT);
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
	
	public void goToVideo(View v){
		Intent i=new Intent(this, UploadVideoActivity.class);
				i.putExtra("username", getIntent().getStringExtra("username"));
		startActivity(i);
	}
}