package com.kidgeniusdesigns.snapapp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

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
	final int BUFFER_SIZE = 4096;
	ArrayList<String> vidSenders;
	ListView lv;

	int vidCounter;
	ProgressBar feedProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_videos);
		vidCounter = 0;
		((MyApplication) getApplication())
				.getTracker(MyApplication.TrackerName.APP_TRACKER);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#517fa4")));
		bar.setTitle("InstaSnap");
		bar.setIcon(new ColorDrawable(getResources().getColor(
				android.R.color.transparent)));

		feedProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		feedProgressBar.setVisibility(ProgressBar.VISIBLE);
		vidSenders = new ArrayList<String>();

		for (Story s : SnapData.videoStorys) {

			vidSenders.add(s.getSender() + "---" + s.getCaption());
		}

		checkEverySnapIndex = 0;

		lv = (ListView) findViewById(R.id.videosListView);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				try {

					File tempVidFile = new File(getFilesDir() + "/video.zip");
					FileOutputStream out = new FileOutputStream(tempVidFile);
					out.write(SnapData.videoByteList.get(position));
					out.close();

					File destinationFile = new File(getFilesDir() + "/videos");
					unzip(tempVidFile.getAbsolutePath(),
							destinationFile.getAbsolutePath());
					
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(),
							"This snap didn't load yet", Toast.LENGTH_LONG)
							.show();
				}
			}

		});
		adapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.list_item, vidSenders);
		lv.setAdapter(adapter);
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
				while (numLoading < 5) {

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
			adapter.notifyDataSetChanged();
			lv.invalidateViews();

			System.out.println("Done");
			if (checkEverySnapIndex < vidSenders.size() - 5) {
				LoadStories ls = new LoadStories();
				ls.execute();
			} else {
				feedProgressBar.setVisibility(ProgressBar.GONE);
			}

		}
	}

	public void addToGallery(File f) {
		File direct = new File(Environment.getExternalStorageDirectory(),
				"Saved Snaps");
		// Save the name and description of a video in a ContentValues map.
		ContentValues values = new ContentValues(2);
		values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
		values.put(MediaStore.Video.Media.DATA, direct.getPath() + vidCounter
				+ ".mp4");

		// Add a new record (identified by uri) without the video, but with the
		// values just set.
		Uri uri = getContentResolver().insert(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

		// Now get a handle to the file for that record, and save the data into
		// it.
		try {
			InputStream is = new FileInputStream(f);
			OutputStream os = getContentResolver().openOutputStream(uri);
			byte[] buffer = new byte[4096]; // tweaking this number may increase
											// performance
			int len;
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			os.flush();
			is.close();
			os.close();
		} catch (Exception e) {
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

	public void unzip(String zipFilePath, String destDirectory)
			throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(
				zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = destDirectory + File.separator + vidCounter
					+ ".mp4";
			vidCounter++;
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				if (entry.getSize() > 750000)
					extractFile(zipIn, filePath);
			} else {
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	private void extractFile(ZipInputStream zipIn, String filePath)
			throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();

		System.out.println(filePath);
		addToGallery(new File(filePath));
		Toast.makeText(getApplicationContext(), "Saved to Gallery. folder 0",
				Toast.LENGTH_LONG).show();
	}
	
	public void goToGallery(View v){
		Intent galleryIntent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivity(galleryIntent);
	}
}
