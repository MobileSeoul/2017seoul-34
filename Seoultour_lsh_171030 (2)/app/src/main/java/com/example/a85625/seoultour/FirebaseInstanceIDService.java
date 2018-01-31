package com.example.a85625.seoultour;

import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by 85625 on 2017-09-26.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService{
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh(){
        final String token = FirebaseInstanceId.getInstance().getToken();

        //서버에 키 등록
        Log.d("token", token);
        gVar.bGetFCMToken=true;
        gVar.FCMTOKEN = token;

        //1. 앱에 할당된 FCM Token이 없을 경우 App은 자동으로 Google에 FCM Token을 요청한다.
        //   FCM Token이 정상적으로 수신되면 이 메서드가 자동으로 호출됨.
        //  수신된 FCM Token을 공용저장소(SharedPreference)에 저장하고,
        //  Main Thread의 Handler FCM Token 수신 완료 메시지를 전달해준다.
        gVar.putStringPreferences(getBaseContext(), gVar.PREF_FCM_KEY, token);

//        LandingActivity.landing.lHandler.sendEmptyMessage(gVar.LOAD_FCM_COMPLETE);
        /*
        Handler cHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                // 이제 오류 안 뜸.
                httpSendEvent tmp = new httpSendEvent();

                StringBuffer sb = new StringBuffer();
                String json="";

                sb.append("id").append("=").append("3").append("&");
                sb.append("Token").append("=").append(token);//id=3&Token=(token)

                //http://hoserver.noip.me/coffee/mobile/event.php?email=leesh2143@naver.com&id=0

                tmp.pageUrl="http://192.168.0.23/fcm/regist.php";
                tmp.buffer=sb;
                tmp.execute();
            }
        };
        cHandler.sendEmptyMessage(0);
        */
    }

//    class httpSendEvent extends AsyncTask<Void, Void, String> {
//        public StringBuffer buffer;
//        public String pageUrl;
//        public int commandCode=-1;
//        public Object obj;
//        @Override
//        protected void onPreExecute() {
//
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            String strResult="";
//
//            String strBuf = buffer.toString();
//
//            try {
//                strBuf = URLEncoder.encode(strBuf, "UTF8");
//                URL url = new URL(pageUrl);       // URL 설정
//                HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속
//
//                http.setDefaultUseCaches(false);
//                http.setDoInput(true);                         // 서버에서 읽기 모드 지정
//                http.setDoOutput(true);                // 서버로 쓰기 모드 지정
//                http.setRequestMethod("POST");         // 전송 방식은 POST
//                http.setConnectTimeout(10000);
//                http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
//
//                OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8");
//                PrintWriter writer = new PrintWriter(outStream);
//                writer.write(buffer.toString());
//                writer.flush();
//
//                InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");
//                BufferedReader reader = new BufferedReader(tmp);
//                StringBuilder builder = new StringBuilder();
//                String str;
//                while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
//                    builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
//                }
//                strResult = builder.toString();                       // 전송결과를 전역 변수에 저장
//            }
//            catch(Exception e)
//            {
//                Log.d("Err",e.getMessage());
//            }
//            finally {
//
//            }
//            return strResult;
//        }
//
//        @Override
//        protected void onPostExecute(String content) {
//
//        }
//    }
}
