package com.example.a85625.seoultour;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessengerActivity extends AppCompatActivity {
    MessengerListAdapter messengerListAdapter;

    Button send_btn;
    EditText message_et;
    TextView name_tv;
    ListView listView;

    String selected_ID, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        init();

        //상대방 이름 지정
        name_tv.setText(name);

        //레이아웃이 키보드를 따라 줄어듬
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);

        //DB에 저장된 메시지 id로 가져오기
        ArrayList<MessengerItem> item = DB.selectMessage(getBaseContext(), selected_ID);

        //읽어온 메시지들 어댑터에 등록
        for(int i = 0; i < item.size(); i++)
            messengerListAdapter.addItem(item.get(i));

        //어댑터를 리스트에 등록
        listView.setAdapter(messengerListAdapter);

        //리스트를 마지막으로 내리기
        listView.setSelection(messengerListAdapter.getCount()-1);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message_et.getText().toString();
                message_et.setText("");

                //현재시간 포멧
                String date = getDate();

                //DB에 보내는 메시지 삽입
                String[] message = new String[]{gVar.ID, selected_ID, msg, "1", date};
                DB.insertMessage(getBaseContext(), message);

                //리스트에 삽입
                messengerListAdapter.addItem(new MessengerItem(gVar.ID, msg, date, false));

                //리스트뷰 새로고침
                refreshListFromPush();

                //서버로 데이터 전송
                sendData(msg, date);
            }
        });
    }

    String getDate(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String strNow = sdfNow.format(date);

        return strNow;
    }

    //수신받은 메시지
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("LOG", "메신저로 데이터 전달 받음");

            // 파싱
            String sender = intent.getStringExtra("sender");
            String message = intent.getStringExtra("message");
            String date = intent.getStringExtra("date");

            //리스트에 삽입
            messengerListAdapter.addItem(new MessengerItem(sender, message, date, true));

            //리스트뷰 새로고침
            refreshListFromPush();
        }
    };

    //서버로 데이터 전송
    void sendData(String msg, String strNow){
        StringBuffer sb = new StringBuffer();
        try {
            sb.append("SENDER_ID=").append(gVar.ID).append("&");
            sb.append("RECEIVER_ID=").append(selected_ID).append("&");
            sb.append("MESSAGE=").append(msg).append("&");
            sb.append("TYPE=").append("1").append("&");
            sb.append("DATE=").append(strNow);
            Log.d("LOG", "보내는 메시지 정보 : " + sb.toString());
        }
        catch (Exception e)
        {
            Log.d("LOG", "Encode Error : "  + e.getMessage());
        }

        transHttp http = new transHttp(new HttpHelper() {
            @Override
            public void parseData(String content) {
                Log.d("LOG", content);

            }
        }, sb, gVar.SERVER_ADDRESS + "message.do");
        http.execute();

        Log.d("LOG", "메시지 보내기 : " + sb.toString());
    }

    //초기화
    void init(){
        name = getIntent().getStringExtra("name");
        selected_ID = getIntent().getStringExtra("selected_id");

        send_btn = (Button) findViewById(R.id.messenger_send_button);
        message_et = (EditText) findViewById(R.id.messenger_send_edittext);
        name_tv = (TextView) findViewById(R.id.messenger_name_textview);
        listView = (ListView)findViewById(R.id.messenger_list);

        messengerListAdapter = new MessengerListAdapter(getBaseContext());
    }

    //메시지 화면에 들어왔을때 브로드캐스트 활성화
    @Override
    public void onResume() {
        super.onResume();
        getApplicationContext().registerReceiver(mMessageReceiver, new IntentFilter("pushRefresh"));
    }

    class MessengerListAdapter extends BaseAdapter {
        private final List<MessengerItem> messengerItems;
        private Context context;

        public MessengerListAdapter(Context context){
            this.context = context;
            messengerItems = new ArrayList<MessengerItem>();
        }

        @Override
        public int getCount() {
            if (messengerItems != null) {
                return messengerItems.size();
            } else {
                return 0;
            }
        }

        public void addItem(MessengerItem item) {
            messengerItems.add(item);
        }

        @Override
        public MessengerItem getItem(int position) {
            if (messengerItems != null) {
                return messengerItems.get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            MessengerItem messengerItem = getItem(position);
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = vi.inflate(R.layout.item_message, null);
                holder = createViewHolder(convertView);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            boolean myMsg = messengerItem.getIsme() ;//Just a dummy check to simulate whether it me or other sender
            setAlignment(holder, myMsg);
            holder.txtMessage.setText(messengerItem.getMessage());
            holder.txtInfo.setText(messengerItem.getDate());

            return convertView;
        }

        private void setAlignment(ViewHolder holder, boolean isMe) {
            if (!isMe) {
                holder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.contentWithBG.setLayoutParams(layoutParams);

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                holder.content.setLayoutParams(lp);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.txtMessage.setLayoutParams(layoutParams);

                layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.txtInfo.setLayoutParams(layoutParams);
            } else {
                holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                holder.contentWithBG.setLayoutParams(layoutParams);

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                holder.content.setLayoutParams(lp);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                holder.txtMessage.setLayoutParams(layoutParams);

                layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                holder.txtInfo.setLayoutParams(layoutParams);
            }
        }

        private ViewHolder createViewHolder(View v) {
            ViewHolder holder = new ViewHolder();
            holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
            holder.content = (LinearLayout) v.findViewById(R.id.content);
            holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
            holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
            return holder;
        }

        private class ViewHolder {
            public TextView txtMessage;
            public TextView txtInfo;
            public LinearLayout content;
            public LinearLayout contentWithBG;
        }
    }

    public void refreshListFromPush(){
        messengerListAdapter.notifyDataSetChanged();
        int count = messengerListAdapter.getCount();
        if(count > 3)
            listView.setSelection(count-1);
    }

    @Override
    public void onPause(){
        super.onPause();
        getApplicationContext().unregisterReceiver(mMessageReceiver);
    }
}

class MessengerItem {
    private String name;
    private String message;
    private String date;
    private boolean isMe; //내가 보낸경우 false 받은경우 true

    public MessengerItem(String name, String message, String date, boolean isMe){
        this.name = name;
        this.message = message;
        this.date = date;
        this.isMe = isMe;
    }

    public String getName(){
        return name;
    }
    public String getMessage(){ return message; }
    public String getDate(){ return date; }
    public boolean getIsme(){return isMe; }

    public void setName(String name){ this.name = name; }
    public void setMessgae(String message){
        this.message = message;
    }
    public void setDate(String date){ this.date = date; }
    public void setIsme(boolean isMe){ this.isMe = isMe; }
}