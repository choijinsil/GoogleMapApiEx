package com.myway.korea.googlemap;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    TextView tv_longitude, tv_latitude;
    SeekBar seek_zoom;
    int zoom_level = 15;
    GoogleMap googleMap; // 객체를 만들어 줘야 한다.
    CheckBox chk_map_type;
    // 위도, 경도를 입력받아서 이동시키기 위한 객체들
    Button btn_move;
    EditText et_longitude, et_latitude;
    // 키보드를 숨기기 위한 객체
    InputMethodManager imm;

    // 위도와 경도를 입력받아 지도 이동 메소드
    public void moveMap(String lat, String lng) {
        // 기본값
        double latitude = 0;
        double longitude = 0;

        // 입력값이 없이 이동버튼을 클릭 시에는 서울 시청으로 이동
        if ("".equals(lat) || lng.equals("")) {
            latitude = 37.56662;
            longitude = 16.078159;
        } else {
            // 입력값이 존재하면
            latitude = Double.parseDouble(lat);
            longitude = Double.parseDouble(lng);
        }
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_longitude = findViewById(R.id.tv_longitude);
        tv_latitude = findViewById(R.id.tv_latitude);
        seek_zoom = findViewById(R.id.seek_zoom);

        chk_map_type = findViewById(R.id.chk_map_type);

        btn_move = findViewById(R.id.btn_move);
        et_longitude = findViewById(R.id.et_longitude);
        et_latitude = findViewById(R.id.et_latitude);

        // 안드로이드 시스템으로부터 객체 얻기 키보드
        imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        // 맵이 실행되면 onMapReady 실행
        mapFragment.getMapAsync(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        // 이동버튼 이벤트
        btn_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveMap(et_latitude.getText().toString()
                        , String.valueOf(et_longitude.getText()));

                // 키보드 숨기기
                // 키보드가 어떤것으로부터 발생된것만 숨겨라
                imm.hideSoftInputFromWindow(et_latitude.getWindowToken(),0);
                imm.hideSoftInputFromWindow(et_longitude.getWindowToken(),0);
            }
        });

        chk_map_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });

        // seekbar 기본값

        seek_zoom.setProgress(zoom_level);

        // 줌인, 줌아웃 변경
        seek_zoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // zoom_level은 1부터 시작한다.
                setZoomLevel(i + 1);
                Log.d("zoom>>>", i + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    // seekbar를 이용한 맵의 줌 레벨 변경
    private void setZoomLevel(int level) {
        // 구글맵의 줌 레벨을 변경해라
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(level));
    }

    public void setPosition() {
        tv_latitude.setText("위도: " + googleMap.getCameraPosition().target.latitude);
        tv_longitude.setText("위도: " + googleMap.getCameraPosition().target.longitude);
    }

    @Override
    public void onMapReady(final GoogleMap gMap) {
        this.googleMap = gMap;

        // 구글에서 등록한 api와 엮어주기
        // 시작위치를 서울 시청으로 변경
        LatLng cityHall = new LatLng(37.566622, 126.978159); // 서울시청 위도와 경도

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(cityHall));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom_level));

        // 시작시 마커 생성하기
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(cityHall);
        markerOptions.title("시청");
        markerOptions.snippet("서울 시청");

        // 생성된 마커 옵션을 지도에 표시
        googleMap.addMarker(markerOptions);

        // 서울광장마커
        // 회사 DB에 데이터를 가지고 있어야 된다.
        final LatLng plaza = new LatLng(37.565785, 126.978056);
        markerOptions.position(plaza);
        markerOptions.title("광장");
        markerOptions.snippet("서울 광장");
        googleMap.addMarker(markerOptions);

        //맵 로드 된 이후
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Toast.makeText(MainActivity.this, "Map로딩성공", Toast.LENGTH_SHORT).show();

                PolygonOptions options = drawMarkerWithRectangle(googleMap.getCameraPosition().target);
                googleMap.addPolygon(options);

                // 위도와 경도 표시하기.
                setPosition();
            }
        });
        //카메라 이동 시작
        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                Log.d("set>>", "start");
            }
        });
        // 카메라 이동 중
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                Log.d("set>>", "move");
            }
        });
        // 카메라 이동이 끝났을때
        // Idle - 게으른
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // 위도와 경도 표시
                setPosition();
            }
        });

        // 지도를 클릭하면 호출되는 이벤트
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // 기존 마커 정리
                googleMap.clear();

                // 클릭한 위치로 지도 이동하기
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // 신규 마커 추가
                MarkerOptions newMarker = new MarkerOptions();
                newMarker.position(latLng);
                googleMap.addMarker(newMarker);

                // 써클 마커 추가
                CircleOptions circleOptions = drawmarkerWithCircle(latLng);
                googleMap.addCircle(circleOptions);
            }
        });

        // 지도에서 마커를 클릭하면 해당 마커만 삭제하기
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                marker.setVisible(false);
                return false;
            }
        });

        // 지도 롱 클릭시 마커 생성
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // 신규 마커 추가
                MarkerOptions newMarker = new MarkerOptions();
                newMarker.position(latLng);
                //지도 객체에 마커 객체 추가
                googleMap.addMarker(newMarker);
            }
        });

        //지도 타입 변경 ( 위성, 길, 도시, 등고선 등..)
        //googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    // 원 그리기
    private CircleOptions drawmarkerWithCircle(LatLng position) {
        double rediusMeters = 100.0;
        int strokeColor = Color.RED;
        int shadeColor = 0x44ff0000;

        // 원 마커 설정
        CircleOptions circleOptions = new CircleOptions();
        // 서클옵션은 내가 클릭한 위도 경도
        circleOptions.center(position);
        circleOptions.radius(rediusMeters);
        circleOptions.fillColor(shadeColor);
        circleOptions.strokeColor(strokeColor);
        circleOptions.strokeWidth(15);

        return circleOptions;
    }

    // 사각형 그리기 - 선분을 가진것은 PolygonOptions
    private PolygonOptions drawMarkerWithRectangle(LatLng center) {
        //그리기 설정
        PolygonOptions options = new PolygonOptions();
        // 가운데 좌표를 기준으로 상하좌우의 크기를 가진 사각형 만들기
        options.addAll(createRectangle(center, 0.005, 0.005));
        // 색 채우기
        options.fillColor(0x44ff0000);
        //선 색
        options.strokeColor(Color.RED);
        // 선 굵기
        options.strokeWidth(5f);

        // 채우기 없애기
        options.addHole(createRectangle(center, 0.004, 0.004));

        // 객체 리턴하기
        return options;
    }

    // 상하좌우 크기 만들기 메소드
    private List<LatLng> createRectangle(LatLng center, Double halfWidth, Double halfHeight) {
        return Arrays.asList(
                new LatLng(center.latitude - halfHeight, center.longitude - halfWidth)
                , new LatLng(center.latitude - halfHeight, center.longitude + halfWidth)
                , new LatLng(center.latitude + halfHeight, center.longitude + halfWidth)
                , new LatLng(center.latitude + halfHeight, center.longitude - halfWidth)
                , new LatLng(center.latitude - halfHeight, center.longitude - halfWidth)
        );
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        String[] permissions = {
                // Manifest는 android를 import
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (String permission : permissions) {
            permissionCheck = this.checkSelfPermission(permission);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            this.requestPermissions(permissions, 1);
        }

    }
}
