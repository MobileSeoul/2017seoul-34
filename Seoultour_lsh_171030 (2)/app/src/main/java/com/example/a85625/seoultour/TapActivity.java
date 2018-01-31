package com.example.a85625.seoultour;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class TapActivity extends AppCompatActivity {
    public static TapActivity tapActivity;
    public static boolean isLoad = false;

    TabLayout tabs;
    TipFragment tip_fragment;
    MapFragment map_fragment;
    GuideMainFragment guide_fragment;
    OptionFragment option_fragment;
    RequestListFragment request_fragment;
    String systemLang = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isLoad=true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap);
        init();

        if(JoinActivity.join != null && !JoinActivity.isLoad)
            JoinActivity.join.finish();
        LandingActivity.landing.finish();



        getSupportFragmentManager().beginTransaction().replace(R.id.container_tap, tip_fragment).commit();

        systemLang = getResources().getConfiguration().locale.getDefault().getLanguage();  // 시스템설정 언어 가져오기

        // 시스템 언어설정에 따라 구분
        if (systemLang.equals("ko")){
            tabs.addTab(tabs.newTab().setText("관광목록"));
            tabs.addTab(tabs.newTab().setText("지도"));
            tabs.addTab(tabs.newTab().setText(gVar.TYPE.equals("tourist") ? "가이드" : "신청자"));
            tabs.addTab(tabs.newTab().setText("설정"));

        }else if (systemLang.equals("en")){
            tabs.addTab(tabs.newTab().setText("PLACE LIST"));
            tabs.addTab(tabs.newTab().setText("MAP"));
            tabs.addTab(tabs.newTab().setText(gVar.TYPE.equals("tourist") ? "GUIDE" : "TOURIST"));
            tabs.addTab(tabs.newTab().setText("SETTING"));

        }else if (systemLang.equals("ja")){
            tabs.addTab(tabs.newTab().setText("観光リスト"));
            tabs.addTab(tabs.newTab().setText("地図"));
            tabs.addTab(tabs.newTab().setText(gVar.TYPE.equals("tourist") ? "ガイド" : "申請者"));
            tabs.addTab(tabs.newTab().setText("設定"));
        }

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();

                Fragment selected = null;
                if (position == 0) {
                    selected = tip_fragment;
                } else if (position == 1) {
                    selected = map_fragment;
                } else if (position == 2) {
                    if(gVar.TYPE.equals("tourist"))
                        selected = guide_fragment;
                    else
                        selected = request_fragment;
                }else if (position == 3) {
                    selected = option_fragment;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.container_tap, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isLoad=false;
    }


    void init(){

        tapActivity = this;

        tip_fragment = new TipFragment();
        map_fragment = new MapFragment();
        guide_fragment = new GuideMainFragment();
        option_fragment = new OptionFragment();
        request_fragment = new RequestListFragment();

        tabs = (TabLayout)findViewById(R.id.tabs);
    }
}
