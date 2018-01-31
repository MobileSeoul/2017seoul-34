package com.example.a85625.seoultour;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class transHttp extends AsyncTask<Void, Void, String> {
    private StringBuffer params;
    private String pageUrl;

    private HttpHelper helper;

    public transHttp(HttpHelper helper)
    {
        this.helper = helper;
    }

    public transHttp(HttpHelper helper, StringBuffer param, String url)
    {
        this(helper);
        setParam(param);
        setPageUrl(url);
    }

    public void setParam(StringBuffer params)
    {
        this.params = params;
    }

    public StringBuffer getParam()
    {
        return this.params;
    }

    public void setPageUrl(String pageUrl)
    {
        this.pageUrl = pageUrl;
    }

    public  String getPageurl()
    {
        return this.pageUrl;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Void... v) {
        String strResult="";

        String strBuf = params.toString();

        try {
            strBuf = URLEncoder.encode(strBuf, "UTF-8");
            URL url = new URL(pageUrl);       // URL 설정
            HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속

            http.setDefaultUseCaches(false);
            http.setDoInput(true);                         // 서버에서 읽기 모드 지정
            http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST");         // 전송 방식은 POST
            http.setConnectTimeout(10000);
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(params.toString());
            writer.flush();

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
            }
            strResult = builder.toString();                       // 전송결과를 전역 변수에 저장
        }
        catch(Exception e)
        {
            Log.d("Err",e.getMessage());
        }
        finally {

        }
        return strResult;
    }

    @Override
    protected void onPostExecute(String content) {
        if(this.helper != null) {
            helper.parseData(content);
        }
    }
}

interface HttpHelper
{
    void parseData(String content);
}

