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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.gzhu.dp.R;
import cn.edu.gzhu.dp.Threads.GetDatas;

/**
 * Created by LRC on 2016/12/21.
 */

public class CommentActivity extends Activity implements View.OnClickListener {
    private ListView lv_comment;
    private ImageView back;
    private String line;
    SimpleAdapter adapter;
    String uID;
    String B_Name[] = null;
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    Handler handler;
    GetDatas getDatas;
    SharedPreferences perferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        perferences = getSharedPreferences("DP", Context.MODE_PRIVATE);
        uID = perferences.getString("u_no",null);

        back = (ImageView) findViewById(R.id.back);
        lv_comment=(ListView) findViewById(R.id.lv_comment);
        B_Name = new String[50];
        back.setOnClickListener(this);

        adapter = new SimpleAdapter(this, data,
                R.layout.item_comment, new String[]{"business_name", "photo",
                "comment","comment_time"}, new int[]{
                R.id.item_comment_business_name, R.id.item_user_upload_photo,
                R.id.item_user_comment,R.id.item_user_com_time});


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0x123){
                    line = msg.obj.toString();
                    initDatas();
                    lv_comment.setAdapter(adapter);
                }
            }
        };
        lv_comment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(CommentActivity.this,DetailActivity.class);
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
            map1.put("photo", R.drawable.main);
            B_Name[i] = subObject.get("businessName").getAsString();
            map1.put("business_name",B_Name[i]);
            map1.put("comment", subObject.get("commentText").getAsString());
            map1.put("comment_time", subObject.get("commentTime").getAsString());

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
                    msg.obj = "comment "+uID;
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
}
