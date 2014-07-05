package com.kidgeniusdesigns.snapapp;

import java.util.List;

import android.content.Context;

import com.habosa.javasnap.Friend;
import com.habosa.javasnap.Story;

public class SnapData {
	public static List<Friend> myFriends;
	public static List<String> myFriendsNames;
	public static List<Story> myStorys, friendsStorys;
	public static String authTokenSaved;
	public static List<byte[]> byteList,friendsByteList;
	public static byte[] currentByte;
	public static Context ctx;
	public static Friend currentFriend;
	public static String pw;

	public SnapData(Context context) {
	}
	
}
