package com.kidgeniusdesigns.snapapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.VideoView;

import com.habosa.javasnap.Snapchat;
import com.habosa.javasnap.Story;

public class ViewVideosActivity extends Activity {
	Uri[] vidUris;
	int checkEverySnapIndex;
	GridView vGrid;
	VideoAdapter vAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_videos);
		checkEverySnapIndex=0;
		vGrid=(GridView) findViewById(R.id.vGrid);
		vAdapter=new VideoAdapter(getApplicationContext());
        vGrid.setAdapter(vAdapter);
		LoadStories ls = new LoadStories();
		ls.execute();
                                              }

	
	 public class VideoAdapter extends BaseAdapter {
	        private Context mContext;

	        public VideoAdapter(Context c) {
	            mContext = c;
	        }

	        public int getCount() {
	            //return mThumbIds.length;
	           if(vidUris!=null){
	            return vidUris.length;}
	        return 0;
	        }


	        public Object getItem(int position) {
	            //return null;
	            return position;
	        }

	        public long getItemId(int position) {
	            //return 0;
	            return position;
	        }

	        public View getView(int position, View convertView, ViewGroup parent) {
	            VideoView videoView;
	            System.out.println("Showing");
	            
	            if (convertView == null) { // if it's not recycled, initialize some
	                                        // attributes
	            	
	            	//saves byte[] to file and sets videoview!!
	            	File tempVidFile=new File(getFilesDir().getAbsolutePath() + "/video.mp4");
	            	try {
	            	FileOutputStream out = new FileOutputStream(tempVidFile);
	            	out.write(SnapData.videoByteList.get(position));
	            	
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
				//finishedLoading = false;

				int numLoading = 0;
				try {
					while (numLoading < 8) {

						Story s = SnapData.videoStorys.get(checkEverySnapIndex);

						checkEverySnapIndex++;
						if (s != null) {
							if (!SnapData.videoByteList.contains(Snapchat.getStory(s,
									getIntent().getStringExtra("username"), SnapData.authTokenSaved))) {

								byte[] storyBytes = Snapchat.getStory(s, getIntent().getStringExtra("username"),
										SnapData.authTokenSaved);
								SnapData.videoByteList.add(storyBytes);
System.out.println("Adding");
								numLoading++;
							}
						}

					}
				} catch (Exception e) {
					//finishedLoading = true;
					System.out.println("YAY FINISHED LOADING EM ALL");
					return null;
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
//doneSystem.out.println("Adding");
				System.out.println("Done");
				
		        
					vAdapter.notifyDataSetChanged();
					vGrid.invalidateViews();
				
			}
		}

	 private class SavePhotoTask extends AsyncTask<byte[], String, String> {
		    @Override
		    protected String doInBackground(byte[]... jpeg) {
		      File photo=new File(Environment.getExternalStorageDirectory(), "video.mp4");

		      if (photo.exists()) {
		            photo.delete();
		      }

		      try {
		        FileOutputStream fos=new FileOutputStream(photo.getPath());

		        fos.write(jpeg[0]);
		        fos.close();
		      }
		      catch (java.io.IOException e) {
		        Log.e("PictureDemo", "Exception in photoCallback", e);
		      }

		      return(null);
		    }
		}
	 
	}
