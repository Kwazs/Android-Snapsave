package com.kidgeniusdesigns.snapapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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
	int checkEverySnapIndex;
	String un;
	ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);
		checkEverySnapIndex = 0;
		un = getIntent().getStringExtra("username");
		finishedLoading = false;
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setOnScrollListener(this);
		loadMore();
		startProgressDialog("Fetching stories...");
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				SnapData.currentByte = SnapData.byteList.get(position);
				startActivity(new Intent(getApplicationContext(), BigView.class));
			}
		});
	
		
		adapter = new MyAdapter(getApplicationContext());
		gridView.setAdapter(adapter);
	}
	public void goBack(View v){
		finish();
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
		protected void onPostExecute(String result) {
			if (progressDialog != null)
				progressDialog.dismiss();
			
			adapter.notifyDataSetChanged();
			gridView.invalidateViews();
			finishedLoading=true;
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
	private void loadMore() {
		LoadStories loadMore = new LoadStories();
		loadMore.execute();
	}
	public void startProgressDialog(String screen) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Loading");
		progressDialog.setMessage(screen);
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();
	}
}