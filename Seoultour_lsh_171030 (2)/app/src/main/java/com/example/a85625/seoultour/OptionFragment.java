package com.example.a85625.seoultour;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.QuickContactBadge;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by 85625 on 2017-10-18.
 */

public class OptionFragment extends Fragment {
    Button edit_btn, logout_btn, leave_btn;
    LoginButton FB_btn;

    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_option, container, false);

        init(rootView);

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), JoinActivity.class);
                intent.putExtra("update", true);
                startActivity(intent);
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FB_btn.callOnClick();

            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            //페이스북 토큰이 갱신될때 호출되는 메소드(새로 로그인을 해도 호출됨)
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d("LOG", "페이스북 로그아웃 시도");
                if(currentAccessToken == null){
                    Log.d("LOG", "페이스북 로그아웃 성공");

                    gVar.putStringPreferences(TapActivity.tapActivity, gVar.PREF_FB_KEY, "");   //20171030
                    gVar.putStringPreferences(TapActivity.tapActivity, gVar.PREF_ID, "");       //20171030
                    gVar.FBTOKEN = "";
                    gVar.ID = "";

                    Intent intent = new Intent(TapActivity.tapActivity.getApplicationContext(), LandingActivity.class); //20171030
                    TapActivity.tapActivity.getApplicationContext().startActivity(intent);  //20171030
                    TapActivity.tapActivity.finish();
                }
            }
        };

        leave_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage();
            }
        });
        return rootView;
    }

    private void showMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("탈퇴");
        builder.setMessage("탈퇴 하시겠습니까?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener(){
           public void onClick(DialogInterface dialog, int whichButton){
               sendData();
           }
        });

        builder.setNegativeButton("no", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void sendData(){
        StringBuffer sb = new StringBuffer();
        sb.append("ID=").append(gVar.ID);
        transHttp http = new transHttp(new HttpHelper() {
            @Override
            public void parseData(String content) {
                try {
                    Log.d("LOG", "회원탈퇴 결과 " + content);

                    if(content.trim().equals("true")){
                        Intent intent = new Intent(getActivity(), LandingActivity.class);
                        startActivity(intent);
                        TapActivity.tapActivity.finish();


                    }
                }catch(Exception e){}
            }
        }, sb, gVar.SERVER_ADDRESS + "leave.do");
        http.execute();
    }

    void init(ViewGroup rootView){
        edit_btn = (Button)rootView.findViewById(R.id.option_edit_button);
        logout_btn = (Button)rootView.findViewById(R.id.option_logout_button);
        leave_btn = (Button)rootView.findViewById(R.id.option_leave_button);
        FB_btn = (LoginButton)rootView.findViewById(R.id.fb_button);

        callbackManager = CallbackManager.Factory.create();
    }
}
