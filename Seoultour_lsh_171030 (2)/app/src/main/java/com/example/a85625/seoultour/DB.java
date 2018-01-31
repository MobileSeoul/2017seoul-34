package com.example.a85625.seoultour;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 85625 on 2017-10-20.
 */

public class DB {
    private static boolean db_exist = false;
    //assets에 있는 db파일을 스마트폰의 파일시스템에 올리는 작업이 필요함 1회
    private static void DBUpload(Context context){
        AssetManager am =null;
        InputStream[] arrIs = new InputStream[1];
        BufferedInputStream[] arrBis = new BufferedInputStream[1];

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        try
        {
            File f = new File("/data/data/com.example.a85625.seoultour/databases/seoultour.db");
            if(f.exists())
            {
                return;
                //f.delete();
                //f.createNewFile();
            }
            am = context.getAssets();

            for(int i = 0; i < arrIs.length; i++)

            {
                arrIs[i] = am.open("seoultour.db");
                arrBis[i] = new BufferedInputStream(arrIs[i]);

            }
            fos = new FileOutputStream(f);
            bos = new BufferedOutputStream(fos);

            int read = -1;
            byte[] buffer = new byte[1024];

            for(int i = 0; i < arrIs.length; i++)
            {
                while((read = arrBis[i].read(buffer, 0, 1024)) != -1)
                {
                    bos.write(buffer, 0, read);
                }
                bos.flush();
            }
        }
        catch(Exception e){}
    }

    public static String[] insertStringMessage(Context context, String message){
        String[] split = message.split("\\|");

        insertMessage(context, split);

        return split;
    }

    public static void insertMessage(Context context, String[] split){
        if(!db_exist)
            DBUpload(context);

        ArrayList<Object> obj = new ArrayList<Object>();

        for(int i = 0; i < split.length; i++)
        {
            Log.d("loop", i + "/" + split[i]);
            if(i == 3)
                obj.add(Integer.parseInt(split[i]));
            else
                obj.add(split[i]);
        }

        String sql = "insert into messages(sender, receiver, message, msgType, time) values(?,?,?,?,?);";

        SqliteExecuteDDL(context, sql, obj);
    }

    private static void SqliteExecuteDDL(Context context, String sql, ArrayList<Object> obj)
    {
        SQLiteDatabase db = context.openOrCreateDatabase("/data/data/com.example.a85625.seoultour/databases/seoultour.db", MODE_PRIVATE, null);
        SQLiteStatement sqlStmt = db.compileStatement(sql);

        int bindCnt = 0;

        for(int i = 0; i < obj.size(); i++)
        {
            if(obj.get(i).getClass().toString().trim().contains("String"))
            {
                sqlStmt.bindString(i+1, (String)obj.get(i));
                bindCnt++;
            }
            else if(obj.get(i).getClass().toString().trim().contains("Long"))
            {
                sqlStmt.bindLong(i+1,(Long)obj.get(i));
                bindCnt++;
            }
            else if(obj.get(i).getClass().toString().trim().contains("Integer"))
            {
                sqlStmt.bindLong(i+1,(Integer)obj.get(i));
                bindCnt++;
            }
            else if(obj.get(i).getClass().toString().trim().contains("Double"))
            {
                sqlStmt.bindDouble(i+1,(Double)obj.get(i));
                bindCnt++;
            }
            else if(obj.get(i).getClass().toString().trim().contains("Float"))
            {
                sqlStmt.bindDouble(i+1,(Float)obj.get(i));
                bindCnt++;
            }
        }

        if(bindCnt == obj.size()) {

            sqlStmt.execute();
        }

        Cursor c =  db.rawQuery("select * from messages", null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    //테이블에서 두개의 컬럼값을 가져와서
                    long contentid = c.getLong(c.getColumnIndex("msgID"));
                    Log.d("LOG", contentid+"");
                } while (c.moveToNext());
            }
        }
        db.close();
    }

    public static ArrayList<MessengerItem> selectMessage(Context context, String id){
        if(!db_exist)
            DBUpload(context);

        SQLiteDatabase db = context.openOrCreateDatabase("/data/data/com.example.a85625.seoultour/databases/seoultour.db", MODE_PRIVATE, null);
        Cursor c =  db.rawQuery("select * from messages where sender='" + id + "' or receiver='" + id + "' order by time", null);

        ArrayList<MessengerItem> item = new ArrayList<MessengerItem>();

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    //테이블에서 컬럼값을 가져와서
                    String sender = c.getString(c.getColumnIndex("sender"));
                    String receiver = c.getString(c.getColumnIndex("receiver"));
                    String message = c.getString(c.getColumnIndex("message"));
                    String date = c.getString(c.getColumnIndex("time"));
                    Log.d("LOG", "DB 데이터 읽기 : " +
                    "\nsender = " + sender +
                    "\nreceiver = " + receiver +
                    "\nmessage = " + message +
                    "\ndate = " + date);

                    if(sender.equals(gVar.ID))
                        item.add(new MessengerItem(sender, message, date, false));
                    else
                        item.add(new MessengerItem(sender, message, date, true));

                } while (c.moveToNext());
            }
        }
        db.close();
        return item;
    }

    public static ArrayList<Recycler_item> selectPreview(Context context, String language, int startNo){
        if(!db_exist)
            DBUpload(context);

        SQLiteDatabase db = context.openOrCreateDatabase("/data/data/com.example.a85625.seoultour/databases/seoultour.db", MODE_PRIVATE, null);
        Cursor c =  db.rawQuery("select * from preview where lang='" + language + "' limit "+ startNo + ",15", null);

        ArrayList<Recycler_item> list = new ArrayList<Recycler_item>();

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    Recycler_item tmpItem = new Recycler_item();
                    //테이블에서 두개의 컬럼값을 가져와서
                    tmpItem.id = c.getInt(c.getColumnIndex("contentid"));
                    if(c.getString(c.getColumnIndex("firstimage2")).isEmpty()){ //이미지 없을시
                        tmpItem.thumbPath = "null";
                    }else{
                        tmpItem.thumbPath = c.getString(c.getColumnIndex("firstimage2"));
                    }

                    tmpItem.title = c.getString(c.getColumnIndex("title"));
                    list.add(tmpItem);
                } while (c.moveToNext());
            }
        }

        db.close();

        return list;
    }

    public static ArrayList<Info_item> selectCommonInfo(Context context, int id){
        if(!db_exist)
            DBUpload(context);

        ArrayList<Info_item> list = new ArrayList<Info_item>();

        SQLiteDatabase db = context.openOrCreateDatabase("/data/data/com.example.a85625.seoultour/databases/seoultour.db", MODE_PRIVATE, null);
        Cursor c =  db.rawQuery("select * from commoninfo where contentid=" + id + "", null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    Info_item tmpItem = new Info_item();
                    //테이블에서 두개의 컬럼값을 가져와서
                    tmpItem.title = c.getString(c.getColumnIndex("title"));
                    if(c.getString(c.getColumnIndex("firstimage")).isEmpty()){
                        tmpItem.firstImage = "null";
                    }else{
                        tmpItem.firstImage = c.getString(c.getColumnIndex("firstimage"));
                    }
                    if(c.getString(c.getColumnIndex("zipcode")).isEmpty()){
                        tmpItem.zipcode = "null";
                    }else{
                        tmpItem.zipcode = c.getString(c.getColumnIndex("zipcode"));
                    }
                    if(c.getString(c.getColumnIndex("addr1")).isEmpty()){
                        tmpItem.addr1 = "null";
                    }else{
                        tmpItem.addr1 = c.getString(c.getColumnIndex("addr1"));
                    }
                    if(c.getString(c.getColumnIndex("tel")).isEmpty()){
                        tmpItem.tel = "null";
                    }else{
                        tmpItem.tel = c.getString(c.getColumnIndex("tel"));
                    }
                    if(c.getString(c.getColumnIndex("telname")).isEmpty()){
                        tmpItem.telname = "null";
                    }else{
                        tmpItem.telname = c.getString(c.getColumnIndex("telname"));
                    }
                    if(c.getString(c.getColumnIndex("homepage")).isEmpty()){
                        tmpItem.homepage = "null";
                    }else{
                        tmpItem.homepage = c.getString(c.getColumnIndex("homepage"));
                    }
                    if(c.getString(c.getColumnIndex("overview")).isEmpty()){
                        tmpItem.overview = "null";
                    }else{
                        tmpItem.overview = c.getString(c.getColumnIndex("overview"));
                    }
                    list.add(tmpItem);
                } while (c.moveToNext());
            }
        }

        db.close();

        return list;
    }

    public static ArrayList<MapInfo> selectPreview(Context context, String sql){
        if(!db_exist)
            DBUpload(context);

        ArrayList<MapInfo> list = new ArrayList<MapInfo>();

        SQLiteDatabase db = context.openOrCreateDatabase("/data/data/com.example.a85625.seoultour/databases/seoultour.db", MODE_PRIVATE, null);
        Cursor c =  db.rawQuery(sql, null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    //테이블에서 두개의 컬럼값을 가져와서
                    long contentid = c.getLong(c.getColumnIndex("contentid"));
                    Log.d("contentid", contentid+"");
                    String title = c.getString(c.getColumnIndex("title"));
                    Log.d("title", title+"");
                    float mapx = c.getFloat(c.getColumnIndex("mapx"));
                    Log.d("mapx", mapx+"");
                    float mapy = c.getFloat(c.getColumnIndex("mapy"));
                    Log.d("mapy", mapy+"");

                    list.add(new MapInfo(contentid,title, mapx, mapy));
                } while (c.moveToNext());
            }
        }

        db.close();
        return list;
    }
}

