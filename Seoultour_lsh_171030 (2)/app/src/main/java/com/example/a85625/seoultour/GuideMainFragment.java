package com.example.a85625.seoultour;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GuideMainFragment extends Fragment {
    TabLayout tabs;
    String systemLang = "";

    GuideSelectedListFragment guideSelectedListFragment;
    GuideAllListFragment guideAllListFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_guide_main, container, false);

        init(rootView);

        getChildFragmentManager().beginTransaction().replace(R.id.guide_container, guideAllListFragment).commit();


        systemLang = getResources().getConfiguration().locale.getDefault().getLanguage();  // 시스템설정 언어 가져오기

        // 시스템 언어설정에 따라 구분
        if (systemLang.equals("ko")){
            tabs.addTab(tabs.newTab().setText("전체"));
            tabs.addTab(tabs.newTab().setText("선택"));

        }else if (systemLang.equals("en")){
            tabs.addTab(tabs.newTab().setText("ALL"));
            tabs.addTab(tabs.newTab().setText("SELECTED"));

        }else if (systemLang.equals("ja")){
            tabs.addTab(tabs.newTab().setText("全体"));
            tabs.addTab(tabs.newTab().setText("選択"));
        }

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                Fragment selected = null;
                if (position == 0) {
                    selected = guideAllListFragment;
                } else {
                    selected = guideSelectedListFragment;
                }
                getChildFragmentManager().beginTransaction().replace(R.id.guide_container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return rootView;
    }

    void init(ViewGroup rootView){
        guideSelectedListFragment = new GuideSelectedListFragment();
        guideAllListFragment = new GuideAllListFragment();

        tabs = (TabLayout) rootView.findViewById(R.id.guide_tabs);

    }
}
