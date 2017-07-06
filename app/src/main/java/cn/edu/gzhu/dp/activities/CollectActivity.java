package cn.edu.gzhu.dp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.gzhu.dp.R;
import cn.edu.gzhu.dp.Threads.GetDatas;

/**
 * Created by LRC on 2016/12/21.
 */

public class CollectActivity extends Activity implements View.OnClickListener {
    private ListView lv_collect;
    private ImageView back;
    String line;
    Handler handler;
    SimpleAdapter adapter;
    GetDatas getDatas;
    String uID;
    SharedPreferences perferences;
    String B_Name[] = null;
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        perferences = getSharedPreferences("DP", Context.MODE_PRIVATE);
        uID = perferences.getString("u_no",null);
        back = (ImageView) findViewById(R.id.back);
        lv_collect=(ListView) findViewById(R.id.lv_collect);
        B_Name = new String[50];

        back.setOnClickListener(this);

        adapter = new SimpleAdapter(this, data,
                R.layout.item_collect, new String[]{"business_img", "business_name",
                "introduce","score","address"}, new int[]{
                R.id.item_collect_business, R.id.tv_item_collect_business_title,
                R.id.tv_item_collect_introduce,R.id.tv_item_collect_score,R.id.tv_item_collect_address});

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0x123){
                    line = msg.obj.toString();
                    if(line.equals("OK")){

                    }else if(line.equals("false")){
                        Toast.makeText(CollectActivity.this, "操作有误", Toast.LENGTH_SHORT).show();
                    }else {
                        initDatas();
                        lv_collect.setAdapter(adapter);
                    }
                }
            }
        };
        lv_collect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(CollectActivity.this,DetailActivity.class);
                intent1.putExtra("Business_Name",B_Name[position]);
                startActivity(intent1);
                End();
            }
        });
        getDatas = new GetDatas(handler);
        new Thread(getDatas).start();
        Send();

    }

    void initDatas(){
        JsonParser parser = new JsonParser();
        //JsonObject object = (JsonObject)parser.parse(test);
        JsonArray array = (JsonArray)parser.parse(line);

        for(int i = 0; i<array.size(); i++) {
            JsonObject subObject = array.get(i).getAsJsonObject();

            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("business_img", R.drawable.main);
            B_Name[i] = subObject.get("businessName").getAsString();
            map1.put("business_name",B_Name[i]);
            map1.put("introduce", subObject.get("businessBrief").getAsString());
            map1.put("score", subObject.get("businessAverageSum").getAsString());
            map1.put("address", subObject.get("businessAddress").getAsString());

            data.add(map1);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                setResult(RESULT_OK);
                finish();
                End();
                break;
        }
    }

    void Send() {
        new Thread() {
            @Override
            public void run() {
                try
                {
                    sleep(100);
                    Message msg = new Message();
                    msg.what = 0x345;
                    msg.obj = "collection "+uID;
                    getDatas.revHandler.sendMessage(msg);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    void End() {
        new Thread() {
            @Override
            public void run() {
                try
                {
                    sleep(100);
                    Message msg = new Message();
                    msg.what = 0x0001;
                    getDatas.revHandler.sendMessage(msg);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        End();
    }
}
