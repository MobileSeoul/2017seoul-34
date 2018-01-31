package com.example.a85625.seoultour;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

public class MemberDetailActivity extends Dialog {
    TextView txtTitle, txtContnet;
    Button btnClose,btnOk;

    String selected_id;
    int connected;

    public MemberDetailActivity(Context context, String id, int connect) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        selected_id=id;
        connected=connect;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.layout_popup);

        txtTitle = (TextView)findViewById(R.id.txtTitle);
        txtContnet = (TextView)findViewById(R.id.txtContent);
        btnOk = (Button)findViewById(R.id.member_detail_ok_button);
        btnClose = (Button)findViewById(R.id.member_detail_cancel_button);

        StringBuffer sb = new StringBuffer();
        sb.append("MEMBER_ID=").append(selected_id);

        transHttp http = new transHttp(new HttpHelper() {
            @Override
            public void parseData(String content) {
                try {
                    Log.d("LOG", "선택자 상세정보 : " + content);
                    JSONObject json = new JSONObject(content);
                    String name = json.getString("NAME");
                    String gender = json.getString("GENDER");
                    String birthday = json.getString("BIRTHDAY");
                    String nation = json.getString("NATION");
                    String language = json.getString("LANGUAGE");
                    String inturoduce = json.getString("INTRODUCE");

                    String strContent = "";
                    strContent = name+"\n"+gender+"\n"+birthday.substring(0,10)+"\n"+nation+"\n"+language+"\n"+inturoduce;
                    txtContnet.setText(strContent);

                }catch(Exception e){

                }
            }
        }, sb, gVar.SERVER_ADDRESS + "member_detail.do");
        http.execute();

        txtTitle.setFocusable(true);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RequestActivity.class);
                intent.putExtra("selected_id", selected_id);
                intent.putExtra("connected", connected);
                getContext().startActivity(intent);
                dismiss();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}