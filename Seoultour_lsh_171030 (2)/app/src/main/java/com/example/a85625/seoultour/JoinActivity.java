package com.example.a85625.seoultour;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.reflect.Member;
import java.util.Calendar;

public class JoinActivity extends Activity {
    public static JoinActivity join;
    public static boolean isLoad = false;
    Button join_btn, option_btn;
    EditText name_et, gender_et, birthday_et, nation_et, language_et, introduce_et;
    RadioButton rb;

    String name, gender, birthday, nation, language, select, introduce;
    RadioGroup rg;

    boolean update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        init();

        //레이아웃이 키보드에 맞춰 올라감
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendJoinData();
                finish();
            }
        });

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
                                birthday_et.setText(date);
                            }
                        };
                DatePickerDialog alert = new DatePickerDialog(v.getContext(),  mDateSetListener, cyear, cmonth, cday);
                alert.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isLoad=false;
    }

    void sendJoinData()
    {
        name = name_et.getText().toString();
        gender = gender_et.getText().toString();
        birthday = birthday_et.getText().toString();
        nation = nation_et.getText().toString();
        language = language_et.getText().toString();
        rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
        select = rb.getText().toString();
        introduce = introduce_et.getText().toString();

        String server_address;

        if(update){
            server_address = gVar.SERVER_ADDRESS + "join_update.do";
        }else {
            server_address = gVar.SERVER_ADDRESS + "join.do";
        }

        StringBuffer sb = new StringBuffer();
        sb.append("ID=").append(gVar.ID).append("&");
        sb.append("NAME=").append(name).append("&");
        sb.append("GENDER=").append(gender).append("&");
        sb.append("BIRTHDAY=").append(birthday).append("&");
        sb.append("NATION=").append(nation).append("&");
        sb.append("LANGUAGE=").append(language).append("&");
        sb.append("INTRODUCE=").append(introduce).append("&");
        sb.append("SELECT=").append(select).append("&");
        sb.append("FBTOKEN=").append(gVar.FBTOKEN).append("&");
        sb.append("FCMTOKEN=").append(gVar.FCMTOKEN);

        Log.d("LOG", "조인 보내는 정보 : " + sb.toString());
        transHttp http = new transHttp(new HttpHelper() {
            @Override
            public void parseData(String content) {//서버에서 전달해준 로그인 결과 저장 변수
                content = content.trim();
                //4. App 서버에 Email, FCM Token, FB Token을 전달한 후
                //  서버에서 전달해준 결과 데이터를 JSON Parser로 Parsing해
                //로그인 결과를 추출한다.

                if(content.equals("true"))
                {
                    SharedPreferences pref = getSharedPreferences(gVar.preferenceName, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(gVar.PREF_TYPE, rb.getText().toString());
                    editor.commit();

                    gVar.TYPE = rb.getText().toString();

                    Intent intent = new Intent(getApplication(), TapActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                    JoinActivity.join.finish();
                    Toast.makeText(getApplication(),"회원가입/수정 성공",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplication(),"회원가입/수정 실패",Toast.LENGTH_SHORT).show();
                }
            }
        }, sb, server_address);
        http.execute();
    }

    void init(){
        join = this;

        update = getIntent().getBooleanExtra("update", false);

        name_et = (EditText) findViewById(R.id.name_edittext);
        gender_et = (EditText) findViewById(R.id.gender_edittext);
        birthday_et = (EditText) findViewById(R.id.birthday_edittext);
        nation_et = (EditText) findViewById(R.id.nation_edittext);
        language_et = (EditText) findViewById(R.id.language_edittext);
        rg = (RadioGroup) findViewById(R.id.select_radiogroup);
        introduce_et = (EditText) findViewById(R.id.introduce_edittext);

        name_et.setText(gVar.NAME);
        gender_et.setText(gVar.GENDER);
        birthday_et.setText(gVar.BIRTHDAY);

        join_btn = (Button) findViewById(R.id.join_button);
        option_btn = (Button) findViewById(R.id.birthday_option_button);

        if(update){
            StringBuffer sb = new StringBuffer();
            sb.append("MEMBER_ID=").append(gVar.ID);

            transHttp http = new transHttp(new HttpHelper() {
                @Override
                public void parseData(String content) {//서버에서 전달해준 로그인 결과 저장 변수
                    try {
                        Log.d("LOG", "개인 상세정보 : " + content);
                        JSONObject json = new JSONObject(content);
                        nation = json.getString("NATION");
                        language = json.getString("LANGUAGE");
                        introduce = json.getString("INTRODUCE");
                        select = json.getString("SELECT");

                        nation_et.setText(nation);
                        language_et.setText(language);
                        introduce_et.setText(introduce);
                        if(select.equals("1")){
                            rb = (RadioButton)findViewById(R.id.tourist_radiobutton);
                            rb.setChecked(true);
                        }else if(select.equals("2")){
                            rb = (RadioButton)findViewById(R.id.guide_radiobutton);
                            rb.setChecked(true);
                        }
                    }catch (Exception e){
                        Log.d("Err", e.getMessage());
                    }
                }
            }, sb, gVar.SERVER_ADDRESS + "member_detail.do");
            http.execute();
        }
    }
}