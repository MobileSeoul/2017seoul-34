package com.example.a85625.seoultour;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MapFragment extends Fragment implements OnMapReadyCallback{
    private GoogleMap mMap;
    int address_idx = -1, categori_idx = -1;
    String address_text = "", categori_text = "";

    ArrayList<MapInfo> list;

    Spinner address_spnr, categori_spnr;
    ArrayAdapter addressAdapter, categoriAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);

        init(rootView);

        //주소어댑터에 리소스의 내용 넣기
        addressAdapter = ArrayAdapter.createFromResource(getContext(), R.array.date_sigungu, android.R.layout.simple_spinner_item);
        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        //주소스피너에 어댑터를 붙이고 클릭 이벤트 지정
        address_spnr.setAdapter(addressAdapter);
        address_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 선택한 아이템의 포지션과 이름을 저장
                address_idx = position;
                address_text = (String) address_spnr.getSelectedItem();

                //DB에서 선택된 옵션으로 데이터를 읽어옴
                getData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        //카테고리어댑터에 리소스의 내용 넣기
        categoriAdapter = ArrayAdapter.createFromResource(getContext(), R.array.date_code, android.R.layout.simple_spinner_item);
        categoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        //카테고리스피너에 어댑터를 연결하고 클릭이벤트 지정
        categori_spnr.setAdapter(categoriAdapter);
        categori_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //선택한 아이템의 포지션과 텍스트를 저장
                categori_idx = position;
                categori_text = (String) categori_spnr.getSelectedItem();

                //DB에서 선택한 옵션으로 데이터를 읽어옴
                getData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // 지도 프래그먼트 붙이기?
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return rootView;
    }

    //초기화
    void init(ViewGroup rootView){
        list = new ArrayList<MapInfo>();

        address_spnr = (Spinner)rootView.findViewById(R.id.spinner_sigungu);
        categori_spnr = (Spinner)rootView.findViewById(R.id.spinner_sigungu_code);
    }

    void getData()
    {
        //언어 설정
        String lang = "kr";
        String systemLang = getResources().getConfiguration().locale.getDefault().getLanguage();

        if (systemLang.equals("ko")) {
            lang = "kr";
        }else if (systemLang.equals("en")){
            lang = "en";
        }else if (systemLang.equals("ja")){
            lang = "jp";
        }

        //language

        String sql = "select * from preview where lang='" + lang + "'" ;

        if(!address_text.trim().equals("전체")){
            sql += " and sigungucode = " + (address_idx +1);
        }
        if(categori_text.trim().equals("관광지")){
            sql += " and contenttypeid = 12";
        }else if(categori_text.trim().equals("문화시설")){
            sql += " and contenttypeid = 14";
        }else if(categori_text.trim().equals("축제 공연 행사")){
            sql += " and contenttypeid = 15";
        }else if(categori_text.trim().equals("여행코스")){
            sql += " and contenttypeid = 25";
        }else if(categori_text.trim().equals("레포츠")){
            sql += " and contenttypeid = 28";
        }else if(categori_text.trim().equals("숙박")){
            sql += " and contenttypeid = 32";
        }else if(categori_text.trim().equals("쇼핑")){
            sql += " and contenttypeid = 38";
        }else if(categori_text.trim().equals("Tourist Attractions")){
            sql += " and contenttypeid = 76";
        }else if(categori_text.trim().equals("Cultural Facilities")){
            sql += " and contenttypeid = 78";
        }else if(categori_text.trim().equals("Festivals/Events/Performances")){
            sql += " and contenttypeid = 85";
        }else if(categori_text.trim().equals("Leisure/Sports")){
            sql += " and contenttypeid = 75";
        }else if(categori_text.trim().equals("Accommodation")){
            sql += " and contenttypeid = 80";
        }else if(categori_text.trim().equals("Shopping")){
            sql += " and contenttypeid = 79";
        }else if(categori_text.trim().equals("Transportation")){
            sql += " and contenttypeid = 77";
        }else if(categori_text.trim().equals("観光地")){
            sql += " and contenttypeid = 76";
        }else if(categori_text.trim().equals("文化施設")){
            sql += " and contenttypeid = 78";
        }else if(categori_text.trim().equals("祭り／公演/イベント")){
            sql += " and contenttypeid = 85";
        }else if(categori_text.trim().equals("レジャースポーツ")){
            sql += " and contenttypeid = 75";
        }else if(categori_text.trim().equals("宿泊")){
            sql += " and contenttypeid = 80";
        }else if(categori_text.trim().equals("ショッピング")){
            sql += " and contenttypeid = 79";
        }else if(categori_text.trim().equals("交通")){
            sql += " and contenttypeid = 77";
        }

        Log.d("sql",sql);

        list = DB.selectPreview(getContext(), sql);

        drawMarker();
    }

    void drawMarker()
    {
        if(mMap == null)
            return;
        mMap.clear();

        for(int i= 0; i < list.size(); i++)
        {
            MarkerOptions marker = new MarkerOptions();
            marker.position(new LatLng(list.get(i).getLon(), list.get(i).getLat())).title(list.get(i).getTitle());


            //.snippet("Seoul Station");
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher4));
            Log.d("test",Long.toString(list.get(i).getContentid()));
            Integer tmpId = new Integer((int)list.get(i).getContentid());
            mMap.addMarker(marker).setTag(tmpId); // 마커추가,화면에출력
        }

        // 마커클릭 이벤트 처리
        // GoogleMap 에 마커클릭 이벤트 설정 가능.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                // TODO Auto-generated method stub
                marker.hideInfoWindow();
                new AlertDialog.Builder(getActivity())
                        .setTitle(marker.getTitle())
                        .setMessage("\n\n\n자세한 내용을 확인하시려면\n\n\n아래의 (확인)버튼을 눌러주세요")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               /* Log.d("test",marker.getTitle());*/

                                Intent intent = new Intent(getContext(), InfoActivity.class);
                                intent.putExtra("id", (Integer)marker.getTag() );
                                startActivity(intent);
                                // 이 버튼 클릭시 삭제 진행
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 아무일도 안 일어남

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // ↑매개변수로 GoogleMap 객체가 넘어옵니다.

        mMap = googleMap;

        // camera 좌표를 서울역 근처로 옮김
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(37.555744, 126.970431)   // 위도, 경도
        ));

        // 구글지도(지구) 에서의 zoom 레벨은 1~23 까지 가능
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);
        googleMap.animateCamera(zoom);   // moveCamera 는 바로 변경하지만,
        // animateCamera() 는 근거리에선 부드럽게 변경합니다

        // marker 표시
        // market 의 위치, 타이틀, 짧은설명 추가 가능.
    }
}

class MapInfo {
    private String title="";
    private double lat=0.0;
    private double lon=0.0;
    private long contentid = 0;

    public MapInfo(long contentid, String title, double lat, double lon)
    {
        this.contentid=contentid;
        this.title = title;
        this.lat = lat;
        this.lon = lon;
    }

    public long getContentid() {
        return contentid;
    }

    public String getTitle(){
        return title;
    }

    public double getLat(){
        return lat;
    }

    public double getLon(){
        return lon;
    }
}