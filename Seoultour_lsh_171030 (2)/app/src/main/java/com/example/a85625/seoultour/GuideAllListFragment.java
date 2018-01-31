package com.example.a85625.seoultour;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 85625 on 2017-10-17.
 */

public class GuideAllListFragment extends Fragment {
    ListView listView;
    GuideListAdapter guideListAdapter;
    MemberItem memberItem[];
    MemberDetailActivity pop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_guide_all_list, container, false);

        guideListAdapter = new GuideListAdapter();
        listView = (ListView) rootView.findViewById(R.id.guide_all_list);

        sendData(); //memberItem[]에 가이드 전체 정보를 가져옴

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MemberItem item = (MemberItem)guideListAdapter.getItem(i);
                pop = new MemberDetailActivity(getContext(),item.getID(),item.getAccept());
                pop.show();
            }
        });

        return rootView;
    }

    class GuideListAdapter extends BaseAdapter {
        ArrayList<MemberItem> items = new ArrayList<MemberItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(MemberItem item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            MemberItemView view = new MemberItemView(getActivity().getApplicationContext());

            MemberItem item = items.get(position);
            view.setName(item.getName());
            view.setGender(item.getGender());
            view.setAge(item.getAge());
            view.setLanguage(item.getLanguage());

            return view;
        }
    }

    void sendData(){
        StringBuffer sb = new StringBuffer();
        transHttp http = new transHttp(new HttpHelper() {
            @Override
            public void parseData(String content) {
                Log.d("LOG", "가이드 전체 데이터 : " + content);
                try {
                    JSONArray jarray = new JSONArray(content);

                    memberItem = new MemberItem[jarray.length()];

                    for(int i = 0; i < jarray.length(); i++){
                        JSONObject json = jarray.getJSONObject(i);
                        memberItem[i] = new MemberItem(
                                json.getString("MEM_ID"),
                                json.getString("MEM_LANGUAGE"),
                                json.getString("MEM_GENDER"),
                                json.getString("MEM_BIRTHDAY").trim(),
                                json.getString("MEM_LINK"),
                                json.getString("MEM_NAME"));

                        guideListAdapter.addItem(memberItem[i]);
                    }
                    listView.setAdapter(guideListAdapter);
                }catch(Exception e) {Log.d("Err", e.getMessage()); }
            }
        }, sb, gVar.SERVER_ADDRESS + "all_guides.do");
        http.execute();
    }
}
