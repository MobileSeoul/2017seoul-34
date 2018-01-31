package com.example.a85625.seoultour;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 85625 on 2017-09-27.
 */

public class gVar {
    public static String NAME="";
    public static String GENDER="";
    public static String BIRTHDAY="";
    public static String AGE="";
    public static String ID="";
    public static String FCMTOKEN="";
    public static String FBTOKEN="";
    public static String TYPE = "tourist"; //관광객, 가이드
    public static String SERVER_ADDRESS = "http://kok99274.cafe24.com/server/";

    public static final int LOAD_FCM_COMPLETE = 1000;
    public static final int LOAD_APP_DATA_COMPLETE = 1001;
    public static final int LOAD_FB_COMPLETE = 1002;
    public static final int LOGIN_COMPLETE = 1003;
    public static final int JOIN_COMPLETE = 1004;
    public static final int LOGIN_NEED_JOIN = 1005;

    public static boolean bGetFCMToken = false;

    public static String preferenceName = "APP";
    public static String PREF_FCM_KEY = "FCMTOKEN";
    public static String PREF_FB_KEY = "FBTOKEN";
    public static String PREF_ID = "ID";
    public static String PREF_TYPE = "TYPE";
    public static String PREF_NAME = "NAME";
    public static String PREF_GENDER = "GENDER";
    public static String PREF_BIRTHDAY = "BIRTHDAY";
    public static String PREF_AGE = "AGE";

    public static int APP_DATA_VERSION = 100;

    public static void putStringPreferences(Context context, String key, String data){
        SharedPreferences pref = context.getSharedPreferences(gVar.preferenceName, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, data);
        editor.commit();
    }

    public static void getStringPreferences(Context context){
        SharedPreferences pref = context.getSharedPreferences(gVar.preferenceName, MODE_PRIVATE);
        gVar.ID = pref.getString(gVar.PREF_ID,"");
        gVar.FCMTOKEN = pref.getString(gVar.PREF_FCM_KEY, "");
        gVar.FBTOKEN = pref.getString(gVar.PREF_FB_KEY,"");
        gVar.NAME = pref.getString(gVar.PREF_NAME,"");
        gVar.GENDER = pref.getString(gVar.PREF_GENDER, "");
        gVar.BIRTHDAY = pref.getString(gVar.PREF_BIRTHDAY, "");

        Log.d("LOG", "프리퍼런스 값 가져오기 : " +
                "\nID =  " + gVar.ID +
                "\nFCMTOKEN = " + gVar.FCMTOKEN +
                "\nFBTOKEN = " + gVar.FBTOKEN);
    }
}
