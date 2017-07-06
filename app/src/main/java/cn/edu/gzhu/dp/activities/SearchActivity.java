package cn.edu.gzhu.dp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;

import cn.edu.gzhu.dp.R;

/**
 * Created by LRC on 2016/11/12.
 */
public class SearchActivity extends Activity implements GeocodeSearch.OnGeocodeSearchListener {
    MapView mMapView = null;
    AMap aMap;
    ImageView back, img_search;
    EditText et_search;
    Button map_search;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);

        init();

    }

    public void init() {
        et_search = (EditText) findViewById(R.id.et_search);
        img_search = (ImageView) findViewById(R.id.img_search);
        map_search = (Button) findViewById(R.id.map_search);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new MyClickListener());
        img_search.setOnClickListener(new MyClickListener());
        map_search.setOnClickListener(new MyClickListener());


        if (aMap == null) {
            aMap = mMapView.getMap();
            LatLng pos =new LatLng(23.040636503,113.3680723943);
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(pos,17);
            aMap.moveCamera(cu);
        }

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        if (i == 1000) {
            if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null
                    && geocodeResult.getGeocodeAddressList().size() > 0) {
                aMap.clear();
                GeocodeAddress geo = geocodeResult.getGeocodeAddressList().get(0);
                LatLonPoint pos = geo.getLatLonPoint();
                LatLng targetPos = new LatLng(pos.getLatitude(), pos.getLongitude());
                CameraUpdate cu = CameraUpdateFactory.changeLatLng(targetPos);
                aMap.moveCamera(cu);
                GroundOverlayOptions options = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory
                                .fromResource(R.drawable.gift_point))
                        .position(targetPos, 64);
                aMap.addGroundOverlay(options);
                CircleOptions cOptions = new CircleOptions()
                        .center(targetPos)
                        .fillColor(0x80ffff00)
                        .radius(80)
                        .strokeWidth(1)
                        .strokeColor(0xff000000);
                aMap.addCircle(cOptions);

            } else {
                Toast.makeText(SearchActivity.this, "请输入正确的地址" + i + geocodeResult, Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.img_search:
                    String text = et_search.getText().toString();
                    Intent intent2 = new Intent(SearchActivity.this, MoreListActivity.class);
                    intent2.putExtra("Text", text);
                    startActivity(intent2);
                    finish();
                    break;
                case R.id.map_search:
                    String addr = et_search.getText().toString();
                    if (addr.equals("")) {
                        Toast.makeText(SearchActivity.this, "请输入有效地址", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SearchActivity.this, addr, Toast.LENGTH_SHORT).show();
                        GeocodeSearch search = new GeocodeSearch(SearchActivity.this);
                        search.setOnGeocodeSearchListener(SearchActivity.this);
                        GeocodeQuery query = new GeocodeQuery(addr, "广州");
                        search.getFromLocationNameAsyn(query);
                    }
                    break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }
}
