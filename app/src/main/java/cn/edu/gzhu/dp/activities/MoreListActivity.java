package cn.edu.gzhu.dp.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by LRC on 2016/11/12.
 */
public class MoreListActivity extends Activity implements View.OnClickListener {
    private ListView lv_food;
    private ImageView back;
    String line = null;
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private Button btn_adjacent, btn_food, btn_total;
    private PopupMenu popup = null;
    SimpleAdapter adapter;
    Handler handler;
    GetDatas getDatas;
    String B_Name[] = null, type;
    Boolean writeed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_list);

        init();

        adapter = new SimpleAdapter(this, data,
                R.layout.item_food, new String[]{"food_img", "describe_one",
                "describe_two", "describe_three"}, new int[]{
                R.id.iv_fooditem_img, R.id.tv_describe_one,
                R.id.tv_describe_two, R.id.tv_describe_three});

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x123) {
                    line = msg.obj.toString();
                    initDatas();
                    lv_food.setAdapter(adapter);
                }
            }
        };
        lv_food.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(MoreListActivity.this, DetailActivity.class);
                intent1.putExtra("Business_Name", B_Name[position]);
                startActivity(intent1);
                End();
            }
        });

        getDatas = new GetDatas(handler);
        new Thread(getDatas).start();
        Send("ranking");

    }

    public void init() {
        back = (ImageView) findViewById(R.id.back);
        lv_food = (ListView) findViewById(R.id.lv_food);

        btn_adjacent = (Button) findViewById(R.id.btn_adjacent);
        btn_food = (Button) findViewById(R.id.btn_food);
        btn_total = (Button) findViewById(R.id.btn_total);

        btn_adjacent.setOnClickListener(new ButtonListener());
        btn_food.setOnClickListener(new ButtonListener());
        btn_total.setOnClickListener(new ButtonListener());
        B_Name = new String[50];
        back.setOnClickListener(this);
    }

    public void initDatas() {
        if(writeed) {
            data.clear();
        }
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(line);

        for (int i = 0; i < array.size(); i++) {
            JsonObject subObject = array.get(i).getAsJsonObject();

            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("food_img", R.drawable.main);
            B_Name[i] = subObject.get("businessName").getAsString();
            map1.put("describe_one", B_Name[i]);
            map1.put("describe_two", subObject.get("businessStyle").getAsString());
            map1.put("describe_three", subObject.get("businessDistrict").getAsString());
            data.add(map1);
        }
        writeed = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                Intent intent = new Intent(MoreListActivity.this, MainActivity.class);
                startActivity(intent);
                End();
        }
    }

    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_adjacent:
                    popup = new PopupMenu(MoreListActivity.this, btn_adjacent);
                    MoreListActivity.this.getMenuInflater().inflate(R.menu.menu_adjacent, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            type = "district " + item.getTitle();
                            Send(type);
                            return true;
                        }
                    });
                    popup.show();
                    break;
                case R.id.btn_food:
                    popup = new PopupMenu(MoreListActivity.this, btn_food);
                    MoreListActivity.this.getMenuInflater().inflate(R.menu.menu_food, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            type = "style " + item.getTitle();
                            Send(type);
                            return true;
                        }
                    });
                    popup.show();
                    break;
                case R.id.btn_total:
                    popup = new PopupMenu(MoreListActivity.this, btn_total);
                    MoreListActivity.this.getMenuInflater().inflate(R.menu.menu_total, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            type = "ranking " + item.getTitle();
                            Send(type);
                            return true;
                        }
                    });
                    popup.show();
                    break;
            }
        }
    }

    void Send(final String type) {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(100);
                    Message msg = new Message();
                    msg.what = 0x345;
                    msg.obj = type;
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
                    sleep(100);
                    Message msg = new Message();
                    msg.what = 0x0001;
                    getDatas.revHandler.sendMessage(msg);
                } catch (Exception e) {
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