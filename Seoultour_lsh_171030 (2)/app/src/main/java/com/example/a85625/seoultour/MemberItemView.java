package com.example.a85625.seoultour;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 85625 on 2017-09-25.
 */

public class MemberItemView extends LinearLayout {
    TextView name_tv;
    TextView gender_tv;
    TextView age_tv;
    TextView language_tv;

    public MemberItemView(Context context){
        super(context);
        init(context);
    }

    public MemberItemView(Context context, AttributeSet attrs){
        super(context, attrs);

        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_member, this, true);

        name_tv = (TextView)findViewById(R.id.member_name);
        gender_tv = (TextView)findViewById(R.id.member_gender);
        age_tv = (TextView)findViewById(R.id.member_age);
        language_tv = (TextView)findViewById(R.id.member_language);
    }

    public void setName(String name){
        this.name_tv.setText(name);
    }

    public void setGender(String gender){
        this.gender_tv.setText(gender);
    }

    public void setAge(String age){
        this.age_tv.setText(age);
    }

    public void setLanguage(String language){
        this.language_tv.setText(language);
    }
}

class MemberItem {
    private String name;
    private int age;
    private String link;
    private String gender;
    private String language;
    private String birthday;
    private String id;
    private int accept = 4;

    public MemberItem(){}

    public MemberItem(String id, String language, String gender, String birthday, String link, String name) {
        this.id = id;
        this.language = language;
        this.gender = gender;
        this.birthday = birthday;
        this.link = link;
        this.name = name;
        this.age = calculateAge(birthday);
    }

    public MemberItem(String id, String language, String gender, String birthday, String name, int accept) {
        this.id = id;
        this.language = language;
        this.gender = gender;
        this.birthday = birthday;
        this.name = name;
        this.accept = accept;
        this.age = calculateAge(birthday);
    }

    public static int calculateAge(String birthday){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String getTime = sdf.format(date);

        String[] birth_s = birthday.substring(0, 10).split("-");
        String[] today_s = getTime.split("-");

        int[] today_i = new int[3];
        int[] birth_i = new int[3];

        for(int i = 0; i < 3; i++) {
            birth_i[i] = Integer.parseInt(birth_s[i]);
            today_i[i] = Integer.parseInt(today_s[i]);
        }

        int age = today_i[0] - birth_i[0];
        if(today_i[1] > birth_i[1])
            age--;
        else if(today_i[1] == birth_i[1] && today_i[2] > birth_i[2])
            age--;

        return age;
    }

    public String getName(){
        return name;
    }

    public String getGender(){ return gender; }

    public String getID(){
        return id;
    }

    public String getAge(){
        return String.format("%d", age);
    }

    public String getLanguage(){
        return language;
    }

    public int getAccept(){ return accept; }
}