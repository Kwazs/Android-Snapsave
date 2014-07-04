package com.kidgeniusdesigns.snapapp;

import java.util.List;

import android.content.Context;

import com.habosa.javasnap.Friend;
import com.habosa.javasnap.Story;

public class SnapData {
	public static Friend[] myFriends;
	public static List<Story> myStorys;
	public static String authTokenSaved;
	public static List<byte[]> byteList;
	public static byte[] currentByte;
	public static Context ctx;

	public SnapData(Context context) {
	}
	
}
