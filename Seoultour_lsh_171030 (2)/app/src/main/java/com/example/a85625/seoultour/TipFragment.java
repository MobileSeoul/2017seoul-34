package com.example.a85625.seoultour;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class TipFragment extends Fragment {
    //1. CardView Layout을 만든다.
//2. Activity에 SwipeRefreshLayout과 RecyclerView를 추가한다.
//3. LayoutManager를 만들고, RecyclerView의 Instance를 가져와 설정을 적용한다.
//4. RecyclerView에 입력된 아이템 클래스를 만든다.
//5. RecyclerAdapter를 만든다.


    RecyclerView recyclerView;
    List<Recycler_item> items = new ArrayList<Recycler_item>();
    SwipeRefreshLayout mSwipe;
    int pageNum = 0;
    String systemLang = "ko";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tip, container, false);

        mSwipe = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefresh);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext()); //context
        recyclerView = (RecyclerView)rootView.findViewById(R.id.RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new RecyclerAdapter(TipFragment.this, items, R.layout.fragment_tip, 0, this));

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount && mSwipe.isRefreshing() == false) {
                    //Toast.makeText(getApplicationContext(), "Last Position", Toast.LENGTH_SHORT).show();
                    LoadPage();
                }
            }
        });

        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                items.clear();
                fillRecyclerItem();  // 새로고침
            }
        });

        fillRecyclerItem();

        return rootView;
    }

    void LoadPage(){    // 카드뷰 리스트

        if (pageNum < 274){
            String lang = "kr";

            systemLang = TapActivity.tapActivity.getResources().getConfiguration().locale.getDefault().getLanguage();  // 시스템설정 언어 가져오기 20171030

            // 시스템 언어설정에 따라 구분
            if (systemLang.equals("ko")){
                lang = "kr";
            }else if (systemLang.equals("en")){
                lang = "en";
            }else if (systemLang.equals("ja")){
                lang = "jp";
            }

            int startNo = pageNum * 15;  // 15개씩 아이템 보여주기

            ArrayList<Recycler_item> list = DB.selectPreview(getContext(), lang, startNo);

            pageNum++;

            for(int i = 0; i < list.size(); i++)
            items.add(list.get(i));

            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    void fillRecyclerItem()   // 새로고침
    {
        pageNum=0;
        mSwipe.setRefreshing(true);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    }
                    catch (Exception e)
                    {}

                    Handler cHandler = new Handler(Looper.getMainLooper()) {
                        @Override public void handleMessage(Message msg) {
                            LoadPage();
                            mSwipe.setRefreshing(false);
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    };
                    cHandler.sendEmptyMessage(0);
                }
            }).start();

        }
        catch (Exception e) {}
    }
}

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    TipFragment context;
    List<Recycler_item> items;
    int item_layout;
    int mode=-1;
    Object parentActivity=null;
    public RecyclerAdapter(TipFragment context, List<Recycler_item> items, int item_layout, int mode, Object parent) {
        this.context=context;
        this.items=items;
        this.item_layout=item_layout;
        this.mode = mode;
        this.parentActivity=parent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Recycler_item item=items.get(position);
        holder.id = item.id;
        holder.title.setText(item.title);

        if(item.thumbPath.equals("null")){// 이미지 없을시
            Glide.with(context).load(R.drawable.no_image).into(holder.image);
        }else{
            Glide.with(context).load(item.thumbPath).into(holder.image);
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //아이템클릭 (상세정보)
                Intent intent = new Intent(view.getContext(), InfoActivity.class);
                intent.putExtra("id", holder.id);

                view.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        CardView card;
        int id;

        public ViewHolder(View itemView) {
            super(itemView);
            image=(ImageView)itemView.findViewById(R.id.image);
            title=(TextView)itemView.findViewById(R.id.txtTitle);
            card = (CardView)itemView.findViewById(R.id.cardview);

        }
    }
}

class Recycler_item {
    public int id;
    public String title;
    public String thumbPath;
    Recycler_item(){
    }
}