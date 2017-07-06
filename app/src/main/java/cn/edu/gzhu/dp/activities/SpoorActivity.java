package cn.edu.gzhu.dp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.edu.gzhu.dp.R;
import cn.edu.gzhu.dp.Threads.GetDatas;

import static com.google.gson.internal.UnsafeAllocator.create;

/**
 * Created by LRC on 2016/12/20.
 */

public class SpoorActivity extends Activity implements View.OnClickListener {
    private ListView lv_spoor;
    private ImageView back;
    String line = null;
    SimpleAdapter adapter;
    Handler handler;
    GetDatas getDatas;
    String uID;
    String B_Name[] = null;
    int countItemPosition;
    SharedPreferences perferences;
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spoor);

        perferences = getSharedPreferences("DP", Context.MODE_PRIVATE);
        uID = perferences.getString("u_no", "null");
        back = (ImageView) findViewById(R.id.back);
        lv_spoor = (ListView) findViewById(R.id.lv_footprint);
        B_Name = new String[50];
        back.setOnClickListener(this);

        adapter = new SimpleAdapter(this, data,
                R.layout.item_spoor, new String[]{"business_img", "business_name",
                "introduce", "score", "address"}, new int[]{
                R.id.item_business, R.id.tv_item_business_title,
                R.id.tv_item_introduce, R.id.tv_item_score, R.id.tv_item_address});

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x123) {
                    line = msg.obj.toString();
                    if(line.equals("OK")){
                        data.remove(countItemPosition);
                        lv_spoor.setAdapter(adapter);
                    }else if(line.equals("false")){
                        Toast.makeText(SpoorActivity.this, "操作有误", Toast.LENGTH_SHORT).show();
                    }else {
                        initDatas();
                        lv_spoor.setAdapter(adapter);
                    }
                }
            }
        };

        lv_spoor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(SpoorActivity.this, DetailActivity.class);
                intent1.putExtra("Business_Name", B_Name[position]);
                startActivity(intent1);
            }
        });

        lv_spoor.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                countItemPosition = position;
                new AlertDialog.Builder(SpoorActivity.this)
                        .setTitle("tips")
                        .setMessage("是否删除该记录？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JsonObject object = new JsonObject();
                                object.addProperty("usersNumber", uID);
                                object.addProperty("businessName", B_Name[countItemPosition]);
                                Send("deleteSpoor" + object.toString());
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).create().show();

                return true;
            }
        });


        getDatas = new GetDatas(handler);
        new Thread(getDatas).start();
        Send("spoor " + uID);

    }

    void initDatas() {
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(line);

        for (int i = 0; i < array.size(); i++) {
            JsonObject subObject = array.get(i).getAsJsonObject();

            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("business_img", R.drawable.main);
            B_Name[i] = subObject.get("businessName").getAsString();
            map1.put("business_name", B_Name[i]);
            map1.put("introduce", subObject.get("businessBrief").getAsString());
            map1.put("score", subObject.get("businessAverageSum").getAsString());
            map1.put("address", subObject.get("businessAddress").getAsString());

            data.add(map1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                setResult(RESULT_OK);
                finish();
                End();
                break;
        }
    }

    void Send(final String context) {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(20);
                    Message msg = new Message();
                    msg.what = 0x345;
                    msg.obj = context;
                    getDatas.revHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    void End() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Message msg = new Message();
                    msg.what = 0x0001;
                    getDatas.revHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
