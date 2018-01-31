package com.example.a85625.seoultour;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.messaging.FirebaseMessaging;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONObject;

public class LandingActivity extends Activity {
    public static LandingActivity landing;
    public LandingHandler lHandler;

    Button facebook_btn;
    LoginButton FBLoginButton;
    ImageView image;

    private CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        init();

        FirebaseMessaging.getInstance().subscribeToTopic("notice");

        //랜딩화면 이미지 등록
        image.setImageResource(R.drawable.background);

        //일반 버튼 기능을 페이스북 버튼과 동일하게 만듬
        facebook_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FBLoginButton.callOnClick();
            }
        });

        //저장된 프리퍼런스 가져오기
        gVar.getStringPreferences(getBaseContext());

        //1. FCM Token 유무 판단. - 최초 실행인지 아닌지 확인
        if(!gVar.FCMTOKEN.equals(""))
        {
            //1-1. 이전에 받아놓은 FCM키가 있으면 FCM수신 성공 메시지를 핸들러에 전달.
            lHandler.sendEmptyMessage(gVar.LOAD_FCM_COMPLETE);
        }
        else
        {
            //FCM키가 없으면 최초 실행으로 간주하고 페이스북 로그인 버튼을 활성화
            facebook_btn.setVisibility(View.VISIBLE);
        }

        accessTokenTracker = new AccessTokenTracker() {
            //페이스북 토큰이 갱신될때 호출되는 메소드(새로 로그인을 해도 호출됨)
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                if(currentAccessToken != null)
                {
                    //페이스북에 새롭게 로그인을 할 경우 FB키와 유저ID를 저장
                    gVar.putStringPreferences(getBaseContext(), gVar.PREF_FB_KEY, currentAccessToken.getToken());
                    gVar.putStringPreferences(getBaseContext(), gVar.PREF_ID, currentAccessToken.getUserId());
                    gVar.FBTOKEN = currentAccessToken.getToken();
                    gVar.ID = currentAccessToken.getUserId();
                }
                else
                {
                    //페이스북 로그인에 문제가 있는 경우 FB키와 유저ID를 초기화
                    gVar.putStringPreferences(getBaseContext(), gVar.PREF_FB_KEY, "");
                    gVar.putStringPreferences(getBaseContext(), gVar.PREF_ID, "");
                    gVar.FBTOKEN = "";
                    gVar.ID = "";
                }

                if(!gVar.ID.trim().equals("") && isNewLogin == false)
                {
                    //3-3 페이스북 로그인이 문제없이 진행된 경우 핸들러에 FB로그인 성공 메시지 전송
                    lHandler.sendEmptyMessage(gVar.LOAD_FB_COMPLETE);
                }
            }
        };


        FBLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            //로그인 성공시 호출되는 메소드(AccessTokenChanged() 호출 후에 호출된다.)
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("LOG", "페이스북 정보 : " + object.toString());
                        //3-3 기존에 로그인돼있지 않은 상태에서 FB로그인을 했을 경우
                        //    FB에서 전달해준 id정보를 공용저장소(SharedPreference)에 저장한 후..
                        //    로그인 성공 콜백 메서드에서 핸들러에 FB로그인 성공 메시지 전송.
                        try {
//                            gVar.ID = object.getString("id");
//                            gVar.NAME = object.getString("name");
//                            gVar.GENDER = object.getString("gender");
//                            gVar.BIRTHDAY = object.getString("birthday");

                            //20171030
                            gVar.ID = jsonGetString(object, "id");
                            gVar.NAME = jsonGetString(object,"name");
                            gVar.GENDER = jsonGetString(object,"birth");
                            gVar.BIRTHDAY = jsonGetString(object,"birthday");
                            gVar.GENDER = jsonGetString(object, "gender");

                            gVar.putStringPreferences(getBaseContext(), gVar.PREF_ID, gVar.ID);
                            gVar.putStringPreferences(getBaseContext(), gVar.PREF_NAME, gVar.NAME);
                            gVar.putStringPreferences(getBaseContext(), gVar.PREF_GENDER, gVar.GENDER);
                            gVar.putStringPreferences(getBaseContext(), gVar.PREF_BIRTHDAY, gVar.BIRTHDAY);
                            gVar.putStringPreferences(getBaseContext(), gVar.PREF_AGE, gVar.AGE);

                            Log.d("LOG", "페이스북 로그인 데이터 : " + object);
                        }
                        catch (Exception e){}

                        lHandler.sendEmptyMessage(gVar.LOAD_FB_COMPLETE);
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }


    //20171030
    private String jsonGetString(JSONObject jObj, String key)
    {
        String rVal = "";
        try
        {
            rVal = jObj.getString(key);
        }
        catch (Exception e)
        {
            rVal = "";
        }

        return rVal;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    void init(){
        landing = this;
        lHandler = new LandingHandler(getMainLooper());
        callbackManager = CallbackManager.Factory.create();
        image =(ImageView)findViewById(R.id.landing_imageview);
        FBLoginButton = (LoginButton)findViewById(R.id.login_button);
        facebook_btn = (Button)findViewById(R.id.facebook_button);
    }

    private void checkAppdata()
    {
        StringBuffer sb = new StringBuffer();

        //2. Application Server에 데이터 버전 정보를 전송한다.
        transHttp http = new transHttp(new HttpHelper() {
            @Override
            public void parseData(String content) {

                int version = 0;
                try {
                    JSONObject json = new JSONObject(content);
                    version = json.getInt("version");
                    Log.d("LOG", "버전 확인 : " + version);
                }catch(Exception e){

                }
                //2-1. 서버에서 수신한 데이터(content)를 Json Parser를 이용해 Parsing한다.
                //     데이터 버전 정보 체크 후 서버에 저장된 데이터 버전이
                //     App에서 보유한 데이터 버전보다 최신버전일 경우
                //     최신 데이터를 다운 받는다.
                //     기존 버전과 동일할 경우 fillAppData를 호출하여 Collection에 AppData를 채운다.

                if(gVar.APP_DATA_VERSION < version)
                {
                    downloadAppData(version);//새로운 관광정보 버전이 있는경우 서버에서 받아온다.
                }
                else
                {
                    fillAppData(); //파일로 된 관광지 정보를 읽어서 파싱한다.
                }
            }
        }, sb, gVar.SERVER_ADDRESS + "version.do");
        http.execute();
    }

    void downloadAppData(int version)
    {
        StringBuffer sb = new StringBuffer();
        transHttp http = new transHttp(new HttpHelper() {
            @Override
            public void parseData(String content) {// 서버에서 준데이터는 content에 있음

                //2-2. 서버에 요청한 최신 데이터(content)를 File로 저장한다.
                //     매개변수로 받아온 version 정보를 공용저장소 sharedPreference에 저장한다.
                //     fillAppData를 호출하여 Collection에 AppData를 채운다.
                fillAppData();
            }
        }, sb, "http://www.naver.com");
        http.execute();
    }

    void fillAppData()
    {
        //2-3. 파일로 저장된 Appdata를 불러와 JSON Parser를 이용해 parsing한다.
        //     parsing결과를 이용해 Collection에 AppData를 저장한다.
        //     Collection에 데이터 채우기가 완료 되면 AppDataLoad 완료 메시지를 핸들러에 전송한다.
        LandingActivity.landing.lHandler.sendEmptyMessage(gVar.LOAD_APP_DATA_COMPLETE);
    }

    boolean isNewLogin = true;

    void FB_Login()
    {
        //3-1. 기존에 페이스북 로그인이 진행돼 있는 상태인지를 점검 한다.
        //3-2. 기존에 로그인된 상태라면 TOKEN을 갱신한다.
        //     기존에 로그인돼 있지 않은 상태라면 FB에 로그인 할 수 있는 버튼을 출력해 준다.

        if(!gVar.ID.equals("") && !gVar.FBTOKEN.trim().equals(""))
        {
            //토큰갱신
            AccessToken accessToken = AccessToken.getCurrentAccessToken();

            if(accessToken!=null)
            {
                isNewLogin = false;
                AccessToken.refreshCurrentAccessTokenAsync();
            }
        }
        else
        {
            //로그인 버튼 출력
            //btnLogin.setVisibility(View.VISIBLE);
        }
    }

    void appServerLogin()
    {
        StringBuffer sb = new StringBuffer();
        sb = sb.append("FCM_TOKEN").append("=").append(gVar.FCMTOKEN).append("&")
                .append("FB_TOKEN").append("=").append(gVar.FBTOKEN).append("&")
                .append("ID").append("=").append(gVar.ID);

        transHttp http = new transHttp(new HttpHelper() {
            @Override
            public void parseData(String content) {//서버에서 전달해준 로그인 결과 저장 변수
                content = content.trim();
                Log.d("LOG", "앱 로그인 결과 : " + content);
                if(content.length() == 0)
                    return;

                char type = content.charAt(content.length()-1);
                char bool = content.charAt(0);

                switch (type){
                    case '1':   gVar.TYPE = "tourist"; break;
                    case '2':   gVar.TYPE = "guide"; break;
                }
                //4. App 서버에 ID, FCM Token, FB Token을 전달한 후
                //  서버에서 전달해준 결과 데이터를 JSON Parser로 Parsing해
                //로그인 결과를 추출한다.

                if(bool == 'f')
                {
                    lHandler.sendEmptyMessage(gVar.LOGIN_NEED_JOIN);
                }
                else
                {
                    lHandler.sendEmptyMessage(gVar.LOGIN_COMPLETE);
                }
            }
        }, sb, gVar.SERVER_ADDRESS + "login.do");
        http.execute();
    }

    void showHome()
    {
        if(TapActivity.isLoad == true)
        {
            Log.d("Log", "0000000000showHome");
            return;
        }
        Intent intent = new Intent(LandingActivity.this, TapActivity.class);
        startActivity(intent);
    }

    void showJoin()
    {
        if(JoinActivity.isLoad)
            return;

        Intent intent = new Intent(getApplication(), JoinActivity.class);
        startActivity(intent);
    }

    class LandingHandler extends Handler
    {
        public LandingHandler(Looper looper)
        {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case gVar.LOAD_FCM_COMPLETE:
                    //1-2. FCM 수신 성공 시 App구동에 필요한 데이터의 버전 체크 및
                    // 수신 작업을 진행한다.
                    checkAppdata();
                    break;
                case gVar.LOAD_APP_DATA_COMPLETE:
                    //2-4. AppData 수신 및 Collection 채우기가 완료되면 FaceBook Login을 진행한다.
                    FB_Login();
                    break;
                case gVar.LOAD_FB_COMPLETE:
                    //3.4 FB로그인 성공 시.. App Server 로그인 진행
                    appServerLogin();
                    break;
                case gVar.LOGIN_COMPLETE:
                    showHome();
                    break;
                case gVar.LOGIN_NEED_JOIN:
                    showJoin();
                    break;
                case gVar.JOIN_COMPLETE:
                    break;
            }
        }
    }
}