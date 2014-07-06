package com.kidgeniusdesigns.snapapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.habosa.javasnap.Snapchat;
import com.habosa.javasnap.Story;

public class FeedActivity extends Activity  implements OnScrollListener{
	MyAdapter adapter;
	GridView gridView;
	Boolean finishedLoading;
	int checkEverySnapIndex, numOfSnapsOnScreen;
	String un;
	ProgressDialog progressDialog;
	boolean sentOrNah;
	Uri currImageURI;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#517fa4")));
		bar.setTitle("InstaSnap");
		bar.setIcon(
				   new ColorDrawable(getResources().getColor(android.R.color.transparent)));   
		
		
		checkEverySnapIndex = 0;
		numOfSnapsOnScreen=0;
		un = getIntent().getStringExtra("username");
		finishedLoading = false;
		gridView = (GridView) findViewById(R.id.gridview);

		loadMore();
		startProgressDialog("Fetching stories...");
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				SnapData.currentByte = SnapData.byteList.get(position);
				Intent intnr = new Intent(getApplicationContext(), BigView.class);
				intnr.putExtra("sender",SnapData.myStorys.get(position).getSender());
				startActivity(intnr);
			}
		});
	
		
		adapter = new MyAdapter(getApplicationContext());
		gridView.setAdapter(adapter);
		gridView.setOnScrollListener(this);
	}
	
	public void getImageUri(View v) {
		// To open up a gallery browser
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				1);
	}
	public void goToBottom(View v){
		gridView.smoothScrollToPosition(0);
	}
	private class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public MyAdapter(Context context) {
			inflater = LayoutInflater.from(context);

		}

		@Override
		public int getCount() {			
			return SnapData.byteList.size();
		}

		@Override
		public Object getItem(int i) {
			return SnapData.byteList.get(i);
		}

		@Override
		public long getItemId(int i) {
			return (long) (SnapData.byteList.get(i).hashCode());
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			try {
				View v = view;
				ImageView picture;
				TextView name;
				
				if (v == null) {
					v = inflater.inflate(R.layout.gridview_item, viewGroup,
							false);
					v.setTag(R.id.picture, v.findViewById(R.id.picture));
					v.setTag(R.id.text, v.findViewById(R.id.text));
				}
				picture = (ImageView) v.getTag(R.id.picture);
				name = (TextView)v.getTag(R.id.text);
				name.setText(SnapData.myStorys.get(i).getSender());
				
				byte[] storyBytes = SnapData.byteList.get(i);
				BitmapFactory.Options options = new BitmapFactory.Options();// Create
																			// object
																			// of
																			// bitmapfactory's
																			// option
																			// method
																			// for
																			// further
																			// option
																			// use
				options.inPurgeable = true; // inPurgeable is used to free up
											// memory while required
				Bitmap bm = BitmapFactory.decodeByteArray(storyBytes, 0,
						storyBytes.length, options);// Decode image, "thumbnail"
													// is
				// the object of image file
				Bitmap bm2 = Bitmap.createScaledBitmap(bm, 240, 240, true);// convert
																			// decoded
																			// bitmap
																			// into
																			// well
																			// scalled
																			// Bitmap
																			// format.

				picture.setImageBitmap(bm2);
				System.out.println("Adding immage");
//when view is set increment num of images on screen
				numOfSnapsOnScreen++;
				
				return v;
			} catch (Exception e) {
			e.printStackTrace();
			return view;
			}

		}
	}
	
	public void goToFriendsList(View v){
		Intent intent=new Intent(this,FriendsList.class);
		

	    startActivity(intent);
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
				UploadSnap upl = new UploadSnap();
				upl.execute("", "");
			}
		}
	}
	
	private class UploadSnap extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				boolean video = false;
				File cur = new File(getFilesDir() + "/image");
				String mediaId = Snapchat.upload(cur, un,
						SnapData.authTokenSaved, video);
				int viewTime = 10; // seconds
				String caption = "My Story"; // This is only shown in the story
												// list, not on the actual story
												// photo/video.
				sentOrNah = Snapchat.sendStory(mediaId, viewTime, video,
						caption, un, SnapData.authTokenSaved);
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
	

	private class LoadStories extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... index) {
			finishedLoading=false;

			int numLoading = 0;
			try{
			while (numLoading < 8) {
				
				Story s = SnapData.myStorys.get(checkEverySnapIndex);
				

				checkEverySnapIndex++;
				if(s!=null){
				if ( !SnapData.byteList.contains(Snapchat.getStory(s, un,
								SnapData.authTokenSaved))) {
					

					byte[] storyBytes = Snapchat.getStory(s, un,
							SnapData.authTokenSaved);
					SnapData.byteList.add(storyBytes);

					numLoading++;
				}
				}
			
			}
			}catch(Exception e){
				finishedLoading=true;
				System.out.println("YAY FINISHED LOADING EM ALL");
				System.out.println(""+checkEverySnapIndex);
				System.out.println(""+SnapData.myStorys.size());
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (progressDialog != null)
				progressDialog.dismiss();
			
			adapter.notifyDataSetChanged();
			gridView.invalidateViews();
			finishedLoading=true;
		}
	}

	private void loadMore() {
		try{
		LoadStories loadMore = new LoadStories();
		loadMore.execute();
		}catch(Exception e){
			
		}
	}
	public void startProgressDialog(String screen) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Loading");
		progressDialog.setMessage(screen);
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();
	}
	public void goToDumbActivity(View v){
		Toast toast = Toast.makeText(getApplicationContext(), "Scrolling to the bottom", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
		gridView.smoothScrollToPosition(numOfSnapsOnScreen-1);
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount >= (totalItemCount-4)) {
			// end has been reached. load more images
			if (finishedLoading) {
				loadMore();
				System.out.println();
				Toast toast = Toast.makeText(getApplicationContext(), "loading more", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}

		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
	
}