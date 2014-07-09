package com.kidgeniusdesigns.snapapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.VideoView;

public class ViewVideosActivity extends Activity {
	Uri[] vidUris;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_videos);

		GridView vGrid=(GridView) findViewById(R.id.vGrid);
        vGrid.setAdapter(new VideoAdapter(this));

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Log.d("EditGalleryView", "uri:"+uri);
        String[] projection = {
                MediaStore.Video.Media.DESCRIPTION,
                MediaStore.Video.Media.DATA  
    };

        Cursor c = this.managedQuery(uri, projection, null, null,
                MediaStore.Video.Media.DATE_ADDED);
                         Log.d("EditGalleryView", "vids available:" +c.getCount());

                         ArrayList<Uri> experimentVids = new ArrayList<Uri>();


                             if (c.getCount() != 0) {
                                 c.moveToFirst();
                                 experimentVids.add(Uri.parse(c.getString(1)));
                                 while (c.moveToNext()) {
                                         experimentVids.add(Uri.parse(c.getString(1)));

                                  }
                      }
                             Log.d("ClassName", "experimentVids.length:" +experimentVids.size());
                                                  if (experimentVids.size() != 0) {
                                                    vidUris = new Uri[experimentVids.size()];
                                                      for (int i = 0; i < experimentVids.size(); i++) {
                                                          vidUris[i] = experimentVids.get(i);
                                                      }
                                                      Log.d("EditGalleryView", "vidUris:"+vidUris.length);
                                                  }
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
	            if (convertView == null) { // if it's not recycled, initialize some
	                                        // attributes
	            	
	            	//saves byte[] to file and sets videoview!!
//	            	FileOutputStream out = new FileOutputStream("sdcard path where you want to save video");
//	            	out.write(bytes);
//	            	out.close();
//	            	videoView.setVideoPath(Òpath to saved videoÒ).
	            	
	            	
	                videoView = new VideoView(mContext);
	                videoView.setFocusable(true);
	                videoView.setVideoURI(vidUris[position]);
	                videoView.setLayoutParams(new GridView.LayoutParams(160, 120));
	                // videoView.setScaleType(VideoView.ScaleType.CENTER_CROP);
	                videoView.setPadding(8, 8, 8, 8);
	                //videoView.start();
	            } else {
	                videoView = (VideoView) convertView;
	                videoView.start();
	            }

	            // imageView.setImageResource(mThumbIds[position]);
	            return videoView;
	        }


	    }   
	}
