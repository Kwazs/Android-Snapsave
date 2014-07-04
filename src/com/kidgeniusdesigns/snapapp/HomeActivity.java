package com.kidgeniusdesigns.snapapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.habosa.javasnap.Friend;
import com.habosa.javasnap.Snapchat;
import com.habosa.javasnap.Story;

public class HomeActivity extends Activity implements OnScrollListener {
	MyAdapter adapter;
	GridView gridView;
	Uri currImageURI;
	String realPath, un, pw;
	boolean sentOrNah;
	ProgressDialog progressDialog;
	int checkEverySnapIndex;
	Boolean finishedLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		checkEverySnapIndex = 0;
		SnapData.ctx = this.getApplicationContext();

		un = getIntent().getStringExtra("username");
		pw = getIntent().getStringExtra("password");
		startProgressDialog("Logging in...");

		finishedLoading = false;

		Login lg = new Login();
		lg.execute();

		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setOnScrollListener(this);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				SnapData.currentByte = SnapData.byteList.get(position);
				startActivity(new Intent(getApplicationContext(), BigView.class));
			}
		});
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

	public void getImageUri(View v) {
		// To open up a gallery browser
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				1);
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
				Bitmap bm2 = Bitmap.createScaledBitmap(bm, 280, 280, true);// convert
																			// decoded
																			// bitmap
																			// into
																			// well
																			// scalled
																			// Bitmap
																			// format.

				picture.setImageBitmap(bm2);
				System.out.println("Adding immage");

				return v;
			} catch (Exception e) {
				return null;
			}

		}
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
						checkForFriendName.add(fr);
						SnapData.myFriends.add(fr);
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
							SnapData.myFriendsNames.add(s.getSender());
						}
						
						
						//add to my storys
						if(s.isImage())
							SnapData.myStorys.add(s);

					}
					
					SnapData.byteList = new ArrayList<byte[]>();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "incorrect pw",
						Toast.LENGTH_LONG).show();
				finish();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (progressDialog != null)
				progressDialog.dismiss();
			adapter = new MyAdapter(getApplicationContext());
			gridView.setAdapter(adapter);
			finishedLoading = true;
			// adapter.notifyDataSetChanged();
			// gridView.invalidateViews();
		}
	}

	private class LoadStories extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... index) {
finishedLoading=false;
			// get authentication token

			int numLoading = 0;

			while (numLoading < 8) {
				Story s = SnapData.myStorys.get(checkEverySnapIndex);
				

				checkEverySnapIndex++;

				if ( !SnapData.byteList.contains(Snapchat.getStory(s, un,
								SnapData.authTokenSaved))) {
					

					byte[] storyBytes = Snapchat.getStory(s, un,
							SnapData.authTokenSaved);
					SnapData.byteList.add(storyBytes);

					numLoading++;
				}
			}
			return null;
		}

		@Override
		public void onProgressUpdate(Integer... args) {
			progressDialog.setProgress(args[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			
			adapter.notifyDataSetChanged();
			gridView.invalidateViews();
			finishedLoading=true;
		}
	}

	private void loadMore() {
		LoadStories loadMore = new LoadStories();
		loadMore.execute();
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

	private class UploadSnap extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				boolean video = false;
				File cur = new File(getFilesDir() + "/image");
				String mediaId = Snapchat.upload(cur, un,
						SnapData.authTokenSaved, video);
				int viewTime = 4; // seconds
				String caption = "My Story"; // This is only shown in the story
												// list, not on the actual story
												// photo/video.
				sentOrNah = Snapchat.sendStory(mediaId, viewTime, video,
						caption, un, SnapData.authTokenSaved);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "fnf",
						Toast.LENGTH_LONG).show();
			}
			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {
			// Toast.makeText(getApplicationContext(),
			// "ff"+sentOrNah+SnapData.authTokenSaved,
			// Toast.LENGTH_LONG).show();
			if (sentOrNah)
				Toast.makeText(getApplicationContext(),
						"Succesfully Uploaded to Story", Toast.LENGTH_SHORT)
						.show();
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}
	// ----------------------------------------------
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount >= (totalItemCount-2)) {
			// end has been reached. load more images

			if (finishedLoading) {
				loadMore();
				Toast.makeText(getApplicationContext(), "Loading more snaps", Toast.LENGTH_LONG).show();
				System.out.println("loading more on scroll");
			}

		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
	
	public void goToFriendsList(View v){
startActivity(new Intent(this,FriendsList.class));
	}
}