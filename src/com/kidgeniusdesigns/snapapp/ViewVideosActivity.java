package com.kidgeniusdesigns.snapapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.habosa.javasnap.Snapchat;
import com.habosa.javasnap.Story;
import com.kidgeniusdesigns.snapapp.helpers.MyApplication;

public class ViewVideosActivity extends Activity {
	Uri[] vidUris;
	int checkEverySnapIndex;
	GridView vGrid;
	VideoAdapter vAdapter;
	ArrayAdapter<String> adapter;

	ArrayList<String> vidSenders;
	ListView lv;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_videos);

		((MyApplication) getApplication())
				.getTracker(MyApplication.TrackerName.APP_TRACKER);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#517fa4")));
		bar.setTitle("InstaSnap Story Videos");
		bar.setIcon(new ColorDrawable(getResources().getColor(
				android.R.color.transparent)));

		vidSenders = new ArrayList<String>();

		for (Story s : SnapData.videoStorys) {

			vidSenders.add(s.getSender() + "---" + s.getCaption());
		}

		checkEverySnapIndex = 0;

		lv = (ListView) findViewById(R.id.videosListView);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String o = lv.getItemAtPosition(position).toString();

				File tempVidFile = new File(getFilesDir() + "/video");
				try {
					FileOutputStream out = new FileOutputStream(tempVidFile);
					// change to position!!!! only doing 1st one now
					out.write(SnapData.videoByteList.get(0));

					out.close();

					// addToGallery(tempVidFile,SnapData.videoByteList.get(position));

					// Intent intent = new Intent(Intent.ACTION_VIEW,
					// Uri.parse(tempVidFile.getAbsolutePath()));
					// intent.setDataAndType(Uri.parse(tempVidFile.getPath()),
					// "video/*");
					// startActivity(intent);

					Toast toast = Toast.makeText(ViewVideosActivity.this,
							"Downloading videos will be available July 14th",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					toast.show();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});

		LoadStories ls = new LoadStories();
		ls.execute();

	}

	public class VideoAdapter extends BaseAdapter {
		private Context mContext;

		public VideoAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			// return mThumbIds.length;
			if (vidUris != null) {
				return vidUris.length;
			}
			return 0;
		}

		public Object getItem(int position) {
			// return null;
			return position;
		}

		public long getItemId(int position) {
			// return 0;
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			VideoView videoView;
			System.out.println("Showing");

			if (convertView == null) { // if it's not recycled, initialize some
										// attributes

				// saves byte[] to file and sets videoview!!
				File tempVidFile = new File(getFilesDir().getAbsolutePath()
						+ SnapData.videoStorys.get(position).getSender()
						+ "/video.mp4");
				try {
					FileOutputStream out = new FileOutputStream(tempVidFile);
					out.write(SnapData.videoByteList.get(position));
					System.out.println(SnapData.videoStorys.get(position));
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				videoView = new VideoView(mContext);
				videoView.setFocusable(true);
				videoView.setVideoPath(tempVidFile.getPath());

				videoView.setLayoutParams(new GridView.LayoutParams(160, 120));
				// videoView.setScaleType(VideoView.ScaleType.CENTER_CROP);
				videoView.setPadding(8, 8, 8, 8);
				videoView.pause();
			} else {
				videoView = (VideoView) convertView;
				videoView.start();
				videoView.pause();
			}

			// imageView.setImageResource(mThumbIds[position]);
			return videoView;
		}

	}

	private class LoadStories extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... index) {
			// finishedLoading = false;

			int numLoading = 0;
			try {
				while (numLoading < 1) {

					Story s = SnapData.videoStorys.get(checkEverySnapIndex);

					checkEverySnapIndex++;
					if (s != null) {
						if (!SnapData.videoByteList.contains(Snapchat.getStory(
								s, getIntent().getStringExtra("username"),
								SnapData.authTokenSaved))) {

							byte[] storyBytes = Snapchat.getStory(s,
									getIntent().getStringExtra("username"),
									SnapData.authTokenSaved);
							SnapData.videoByteList.add(storyBytes);
							System.out.println("Adding");
							numLoading++;
						}
					}

				}
			} catch (Exception e) {
				// finishedLoading = true;
				System.out.println("YAY FINISHED LOADING EM ALL");
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			adapter = new ArrayAdapter<String>(getApplicationContext(),
					R.layout.list_item, vidSenders);
			lv.setAdapter(adapter);
			System.out.println("Done");

		}
	}

	public void addToGallery(File f, byte[] buffer) {
		// Save the name and description of a video in a ContentValues map.
		ContentValues values = new ContentValues(2);
		values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
		values.put(MediaStore.Video.Media.DATA, f.getAbsolutePath());

		// Add a new record (identified by uri) without the video, but with the
		// values just set.
		Uri uri = getContentResolver().insert(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

		// Now get a handle to the file for that record, and save the data into
		// it.
		try {
			InputStream is = new FileInputStream(f);
			OutputStream os = getContentResolver().openOutputStream(uri);
			int len;
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			os.flush();
			is.close();
			os.close();
		} catch (Exception e) {
			// Log.e(TAG, "exception while writing video: ", e);
		}

		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Get an Analytics tracker to report app starts & uncaught exceptions
		// etc.
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		// Stop the analytics tracking
		GoogleAnalytics.getInstance(this).reportActivityStop(this);

	}

	public void goToFriendsList(View v) {
		Intent intent = new Intent(this, FriendsList.class);

		startActivity(intent);
	}

	public void back(View v) {
		finish();
	}
	
}
