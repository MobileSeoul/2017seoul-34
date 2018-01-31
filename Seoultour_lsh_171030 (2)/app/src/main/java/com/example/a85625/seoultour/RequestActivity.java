package com.example.a85625.seoultour;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import org.json.JSONObject;

import java.util.Calendar;

public class RequestActivity extends AppCompatActivity {
    Button ok_btn, cancel_btn, message_btn, option_btn;
    EditText date_et, area_et, msg_et;

    String guide_id;
    String tourist_id;
    String selected_id;
    String name;
    int connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        init();

        String systemLang = getResources().getConfiguration().locale.getDefault().getLanguage();  // 시스템설정 언어 가져오기

        if(connected < 4) {
            ok_btn.setEnabled(false);
        }
        // 시스템 언어설정에 따라 구분
        if (systemLang.equals("ko")){
            if(connected == 0){
                ok_btn.setText("신청완료");
            }else if(connected == 1){
                ok_btn.setText("가이드 수락");
            }else if(connected == 2){
                ok_btn.setText("가이드 거절");
            }
        }else if (systemLang.equals("en")){
            if(connected == 0){
                ok_btn.setText("COMPLETE");
            }else if(connected == 1){
                ok_btn.setText("ACCEPT");
            }else if(connected == 2){
                ok_btn.setText("REFUSE");
            }
        }else if (systemLang.equals("ja")){
            if(connected == 0){
                ok_btn.setText("完了");
            }else if(connected == 1){
                ok_btn.setText("受け入れ");
            }else if(connected == 2){
                ok_btn.setText("拒絶");
            }
        }

        //과거 요청한/받은 정보 받아오기
        StringBuffer sb = new StringBuffer();
        sb.append("GUIDE_ID=").append(guide_id).append("&");
        sb.append("TOURIST_ID=").append(tourist_id);
        transHttp http = new transHttp(new HttpHelper() {
            @Override
            public void parseData(String content) {
                Log.d("LOG", "받아오는 관광객 상세정보 : " + content);
                try {
                    JSONObject json = new JSONObject(content);
                    String date = json.getString("CONNECTDATE");
                    String[] split = date.split("-");
                    date = String.format("%02d/%02d/%04d", split[1], split[2], split[0]);
                    String area = json.getString("AREA");
                    String msg = json.getString("MESSAGE");

                    date_et.setText(date);
                    area_et.setText(area);
                    msg_et.setText(msg);

                }catch (Exception e){}
            }
        }, sb, gVar.SERVER_ADDRESS + "request_detail.do");
        http.execute();


        option_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int cyear = c.get(Calendar.YEAR);
                int cmonth = c.get(Calendar.MONTH);
                int cday = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog.OnDateSetListener mDateSetListener =
                        new DatePickerDialog.OnDateSetListener() {
                            // onDateSet method
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String date = String.format("%02d/%02d/%04d", monthOfYear+1, dayOfMonth, year);
                                date_et.setText(date);
                            }
                        };
                DatePickerDialog alert = new DatePickerDialog(v.getContext(),  mDateSetListener, cyear, cmonth, cday);
                alert.show();
            }
        });

        message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), MessengerActivity.class);
                intent.putExtra("selected_id", selected_id);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });

        if(gVar.TYPE.equals("guide")){
            option_btn.setEnabled(false);

            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("guide=").append(guide_id).append("&");
                    sb.append("tourist=").append(tourist_id).append("&");
                    sb.append("accept=").append("yes").append("&");
                    sb.append("type=").append("0");

                    transHttp http = new transHttp(new HttpHelper() {
                        @Override
                        public void parseData(String content) {
                            Log.d("LOG", "가이드의 신청 버튼 결과 : " + content);
                            ok_btn.setEnabled(false);
                            cancel_btn.setEnabled(true);
                        }
                    }, sb, gVar.SERVER_ADDRESS + "request_accept.do");
                    http.execute();
                }
            });

            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    StringBuffer sb = new StringBuffer();
                    sb.append("guide=").append(guide_id).append("&");
                    sb.append("tourist=").append(tourist_id).append("&");
                    sb.append("accept=").append("no").append("&");
                    sb.append("type=").append("0");

                    transHttp http = new transHttp(new HttpHelper() {
                        @Override
                        public void parseData(String content) {
                            Log.d("LOG", "가이드의 신청 버튼 결과 : " + content);
                            ok_btn.setEnabled(true);
                            cancel_btn.setEnabled(false);
                        }
                    }, sb, gVar.SERVER_ADDRESS + "request_accept.do");
                    http.execute();
                }
            });

        }else {//관광객 사용자
            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String date = date_et.getText().toString();
                    String area = area_et.getText().toString();
                    String msg = msg_et.getText().toString();

                    StringBuffer sb = new StringBuffer();
                    sb.append("tourist=").append(gVar.ID).append("&");
                    sb.append("guide=").append(guide_id).append("&");
                    sb.append("date=").append(date).append("&");
                    sb.append("area=").append(area).append("&");
                    sb.append("msg=").append(msg);

                    Log.d("LOG", "신청한 데이터 : " + sb.toString());
                    transHttp http = new transHttp(new HttpHelper() {
                        @Override
                        public void parseData(String content) {
                            Log.d("LOG", "신청 성공여부" + content + "------" + guide_id);

                            if (content.equals("true")) {
                                ok_btn.setEnabled(false);
                            }
                        }
                    }, sb, gVar.SERVER_ADDRESS + "request.do");
                    http.execute();
                    finish();
                }
            });

            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    void init(){
        selected_id = getIntent().getStringExtra("selected_id");
        guide_id = selected_id;
        tourist_id = gVar.ID;
        connected = getIntent().getIntExtra("connected", 4);
        name = getIntent().getStringExtra("name");

        date_et = (EditText) findViewById(R.id.request_date_edittext);
        area_et = (EditText) findViewById(R.id.request_area_edittext);
        msg_et = (EditText)findViewById(R.id.request_message_edittext);

        ok_btn = (Button)findViewById(R.id.request_ok_button);
        cancel_btn = (Button)findViewById(R.id.request_cancel_button);
        message_btn = (Button)findViewById(R.id.request_message_button);
        option_btn = (Button)findViewById(R.id.request_option_button);

        if(gVar.TYPE.equals("guide")){ // 가이드 사용자
            guide_id = gVar.ID;
            tourist_id = selected_id;
        }
    }

}
