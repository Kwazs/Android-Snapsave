package com.kidgeniusdesigns.snapapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.habosa.javasnap.Snapchat;
import com.habosa.javasnap.Story;
import com.kidgeniusdesigns.snapapp.helpers.MyApplication;

public class FeedActivity extends Activity implements OnScrollListener {
	MyAdapter adapter;
	GridView gridView;
	Boolean finishedLoading;
	int checkEverySnapIndex, numOfSnapsOnScreen;
	String un;
	ProgressDialog progressDialog;
	boolean sentOrNah, first;

	ProgressBar feedProgressBar;
	
	private InterstitialAd interstitial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);
		// Get a Tracker (should auto-report)
		((MyApplication) getApplication())
				.getTracker(MyApplication.TrackerName.APP_TRACKER);
		first = true;
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#517fa4")));
		bar.setTitle("InstaSnap");
		bar.setIcon(new ColorDrawable(getResources().getColor(
				android.R.color.transparent)));

		
		feedProgressBar = (ProgressBar)findViewById(R.id.feedProgressBar);
		feedProgressBar.setVisibility(ProgressBar.INVISIBLE);
		
		
		
		checkEverySnapIndex = 0;
		numOfSnapsOnScreen = 0;
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
				Intent intnr = new Intent(getApplicationContext(),
						BigView.class);
				intnr.putExtra("sender", SnapData.myStorys.get(position)
						.getSender());
				startActivity(intnr);
			}
		});

		gridView.setOnScrollListener(this);

		// set up ads

		// Create the interstitial.
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId("ca-app-pub-4742368221536941/6949454117");
		// Create ad request.
		AdRequest adRequest = new AdRequest.Builder().build();

		// Begin loading your interstitial.
		interstitial.loadAd(adRequest);
	}

	// Invoke displayInterstitial() when you are ready to display an
	// interstitial.
	public void displayInterstitial() {
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
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

	public void getImageUri(View v) {
		Intent i = new Intent(this, UploadSnapActivity.class);
		i.putExtra("username", getIntent().getStringExtra("username"));
		startActivity(i);
	}

	public void goToBottom(View v) {
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
				name = (TextView) v.getTag(R.id.text);
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

				DisplayMetrics dimension = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dimension);
				int width = dimension.widthPixels;

				// the object of image file
				Bitmap bm2 = Bitmap.createScaledBitmap(bm, width / 2,
						2 * width / 3, true);// convert
				// decoded
				// bitmap
				// into
				// well
				// scalled
				// Bitmap
				// format.

				picture.setImageBitmap(bm2);
				System.out.println("Adding immage");
				// when view is set increment num of images on screen
				numOfSnapsOnScreen++;
				
				if((numOfSnapsOnScreen==48)){
					displayInterstitial();
				}
				
				return v;
			} catch (Exception e) {
				e.printStackTrace();
				return view;
			}

		}
	}

	public void goToFriendsList(View v) {
		Intent intent = new Intent(this, FriendsList.class);

		startActivity(intent);
	}

	private class LoadStories extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... index) {
			finishedLoading = false;

			int numLoading = 0;
			try {
				while (numLoading < 8) {

					Story s = SnapData.myStorys.get(checkEverySnapIndex);

					checkEverySnapIndex++;
					if (s != null) {
						if (!SnapData.byteList.contains(Snapchat.getStory(s,
								un, SnapData.authTokenSaved))) {

							byte[] storyBytes = Snapchat.getStory(s, un,
									SnapData.authTokenSaved);
							SnapData.byteList.add(storyBytes);

							numLoading++;
						}
					}

				}
			} catch (Exception e) {
				finishedLoading = true;
				System.out.println("YAY FINISHED LOADING EM ALL");
				System.out.println("" + checkEverySnapIndex);
				System.out.println("" + SnapData.myStorys.size());
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (progressDialog != null)
				progressDialog.dismiss();

			feedProgressBar.setVisibility(ProgressBar.GONE);
			
			if (first) {
				if (SnapData.byteList.size() < 1 || SnapData.byteList == null) {
					// wrong password
					Intent i = new Intent(getApplicationContext(),
							MainActivity.class);
					i.putExtra("wrong", "Wrong password");
					startActivity(i);
				}

				finishedLoading = true;
				adapter = new MyAdapter(getApplicationContext());
				gridView.setAdapter(adapter);
				first = false;
			} else {
				adapter.notifyDataSetChanged();
				gridView.invalidateViews();
				finishedLoading = true;
			}
		}
	}

	private void loadMore() {
		feedProgressBar.setVisibility(ProgressBar.VISIBLE);
		try {
			LoadStories loadMore = new LoadStories();
			loadMore.execute();
		} catch (Exception e) {

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

	public void goToDumbActivity(View v) {
		Toast toast = Toast.makeText(getApplicationContext(),
				"Scrolling to the bottom", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
		gridView.smoothScrollToPosition(numOfSnapsOnScreen - 1);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount >= (totalItemCount - 4)) {
			// end has been reached. load more images
			if (finishedLoading) {
				loadMore();
				System.out.println();
			}	
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

}