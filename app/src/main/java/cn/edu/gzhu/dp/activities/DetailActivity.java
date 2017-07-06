package cn.edu.gzhu.dp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.gzhu.dp.R;
import cn.edu.gzhu.dp.Threads.GetDatas;
import cn.edu.gzhu.dp.Threads.GetPhoto;

/**
 * Created by LRC on 2016/12/23.
 */

public class DetailActivity extends Activity implements View.OnClickListener {
    private ImageView back, business_picture, img_like;
    private Button btn_gotoWrite;
    private ListView lv_comment;
    private SimpleAdapter adapter;
    private Handler handler;
    private GetDatas getDatas;
    private String line = null;
    private String type = null, businessName;
    private SharedPreferences perferences;
    private Drawable.ConstantState light, gray, now;
    private JsonObject object;
    private TextView title_main, businessBrief, businessAverageSum, businessAddress, businessCall, commentsConsumptionScore, businessHour;
    private TextView businessAverageTaste, businessAverageEnvironment, businessAverageService;
    private String usersCollection = null;
    private DownloadImageTask task;
    private boolean downing = false;
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        businessName = intent.getStringExtra("Business_Name");

        adapter = new SimpleAdapter(this, data,
                R.layout.item_user_comment, new String[]{"user_img", "user_name",
                "user_comment", "comment_time", "commentsServiceScore",
                "commentsEnvironmentScore", "commentsTasteScore"}, new int[]{
                R.id.img_item_user_comment, R.id.tv_item_username,
                R.id.tv_item_usercomment, R.id.tv_item_usercomment_time,
                R.id.commentsServiceScore, R.id.commentsEnvironmentScore, R.id.commentsTasteScore});
        init();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x123) {
                    line = msg.obj.toString();
                    if (line.equals("OK")) {
                        Toast.makeText(DetailActivity.this, "OK", Toast.LENGTH_SHORT).show();
                    } else if (line.equals("false")) {
                        Toast.makeText(DetailActivity.this, "false", Toast.LENGTH_SHORT).show();
                    } else {
                        setDatas();
                        lv_comment.setAdapter(adapter);
                    }
                }
            }
        };
        getDatas = new GetDatas(handler);
        new Thread(getDatas).start();
    }

    public void init() {
        perferences = getSharedPreferences("DP", Context.MODE_PRIVATE);
        light = getResources().getDrawable(R.drawable.like_hightling).getConstantState();
        gray = getResources().getDrawable(R.drawable.like_gray).getConstantState();

        back = (ImageView) findViewById(R.id.back);
        lv_comment = (ListView) findViewById(R.id.lv_comment);
        btn_gotoWrite = (Button) findViewById(R.id.btn_gotoWrite);

        business_picture = (ImageView) findViewById(R.id.img_business_picture);
        img_like = (ImageView) findViewById(R.id.img_like);

        title_main = (TextView) findViewById(R.id.title_main);
        businessBrief = (TextView) findViewById(R.id.businessBrief);
        businessAverageSum = (TextView) findViewById(R.id.businessAverageSum);
        businessAddress = (TextView) findViewById(R.id.businessAddress);
        businessCall = (TextView) findViewById(R.id.businessCall);
        businessHour = (TextView) findViewById(R.id.businessHour);
        commentsConsumptionScore = (TextView) findViewById(R.id.commentsConsumptionScore);

        businessAverageTaste = (TextView) findViewById(R.id.businessAverageTaste);
        businessAverageEnvironment = (TextView) findViewById(R.id.businessAverageEnvironment);
        businessAverageService = (TextView) findViewById(R.id.businessAverageService);

        title_main.setText(businessName);

        back.setOnClickListener(this);
        img_like.setOnClickListener(this);
        btn_gotoWrite.setOnClickListener(this);
        String uno = perferences.getString("u_no", " ");
        object = new JsonObject();
        object.addProperty("businessName", businessName.toString());
        object.addProperty("usersNumber", uno);
        Send("business" + object.toString());
        if (uno != " ") {
            Send("insertSpoor" + object.toString());
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
            case R.id.img_like:
                now = img_like.getDrawable().getConstantState();
                if (now.equals(light)) {
                    Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
                    img_like.setImageResource(R.drawable.like_gray);
                    Send("deleteCollection" + object.toString());
                } else if (now.equals(gray)) {
                    Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
                    img_like.setImageResource(R.drawable.like_hightling);
                    Send("insertCollection" + object.toString());
                }
                break;
            case R.id.btn_gotoWrite:
                if (perferences.getBoolean("logined", false)) {
                    Intent intent = new Intent(this, WriteCommentActivity.class);
                    Bundle data = new Bundle();
                    data.putString("Business_Name", businessName.toString());
                    data.putString("address", businessAddress.getText().toString());
                    data.putString("introduce", businessBrief.getText().toString());
                    intent.putExtras(data);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "请登录后在来点评吧~~", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /*
    * 设置、跟新数据
    * 利用异步下载方式获取商家图片
    *
    * */
    void setDatas() {
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(line);

        businessBrief.setText(object.get("businessBrief").getAsString());
        businessAverageSum.setText(object.get("businessAverageSum").getAsString());
        businessAddress.setText(object.get("businessAddress").getAsString());
        businessCall.setText(object.get("businessCall").getAsString());
        businessHour.setText(object.get("businessHour").getAsString());
        commentsConsumptionScore.setText((object.get("businessAverageConsumption").getAsString()));

        businessAverageTaste.setText(object.get("businessAverageTaste").getAsString());
        businessAverageEnvironment.setText(object.get("businessAverageEnvironment").getAsString());
        businessAverageService.setText(object.get("businessAverageService").getAsString());

        usersCollection = object.get("usersCollection").getAsString();

        if (!downing) {
            task = new DownloadImageTask(business_picture);
            task.execute("http://www.gzhugeeyee.cn/webDemo/"+businessName+".jpg"); // 执行异步操作
            downing = true;
        }

        if (usersCollection.equals("false")) {
            img_like.setImageResource(R.drawable.like_gray);
        } else if (usersCollection.equals("true")) {
            img_like.setImageResource(R.drawable.like_hightling);
        }

        JsonArray array = object.get("comments").getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            JsonObject subObject = array.get(i).getAsJsonObject();
            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("user_img", R.drawable.main);
            map1.put("user_name", subObject.get("usersName").getAsString());
            map1.put("user_comment", subObject.get("commentsText").getAsString());
            map1.put("comment_time", subObject.get("commentsTime").getAsString());
            map1.put("commentsServiceScore", subObject.get("commentsServiceScore").getAsString());
            map1.put("commentsEnvironmentScore", subObject.get("commentsEnvironmentScore").getAsString());
            map1.put("commentsTasteScore", subObject.get("commentsTasteScore").getAsString());

            data.add(map1);
        }


    }

    class DownloadImageTask extends AsyncTask<String, Integer, Boolean> {
        ImageView image;
        private Bitmap mDownloadImage = null;

        public DownloadImageTask(ImageView image) {
            this.image = image;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            mDownloadImage = GetPhoto.getBitmap(params[0]);
            return true;
        }

        // 下载完成回调
        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            image.setImageBitmap(mDownloadImage);
            super.onPostExecute(result);
        }

        // 更新进度回调
        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
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
        if (downing) {
            task.cancel(true); // 取消操作
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (downing) {
            task.cancel(true); // 取消操作
        }
    }
}
