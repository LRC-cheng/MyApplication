package cn.edu.gzhu.dp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.edu.gzhu.dp.R;
import cn.edu.gzhu.dp.Threads.GetDatas;

/**
 * Created by LRC on 2017/4/5.
 */

public class WriteCommentActivity extends Activity implements View.OnClickListener {
    ImageView back,img_write;
    EditText commentText,et_ConsumptionScore;
    RatingBar ratingBar, ratingBar2, ratingBar3;
    float TasteScore, EnvironmentScore, ServiceScore;
    Button btn_commit;
    Handler handler;
    GetDatas getDatas;
    SharedPreferences perferences;
    String businessName = null,data;
    RelativeLayout rl_writecomment,rl_commentsConsumptionScore;
    String userNumber = null,addr,intro;
    TextView B_Name,introduce,address;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writecomment);

        Intent intent = getIntent();
        businessName = intent.getStringExtra("Business_Name");
        addr = intent.getStringExtra("address");
        intro = intent.getStringExtra("introduce");

        init();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0x123){
                    Toast.makeText(WriteCommentActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        };
        getDatas = new GetDatas(handler);
        new Thread(getDatas).start();



    }

    public void init() {
        perferences = getSharedPreferences("DP", Context.MODE_PRIVATE);
        userNumber = perferences.getString("u_no",null);
        back = (ImageView) findViewById(R.id.back);
        img_write = (ImageView) findViewById(R.id.img_write);


        address = (TextView) findViewById(R.id.tv_item_collect_address);
        introduce = (TextView) findViewById(R.id.tv_item_collect_introduce);
        B_Name = (TextView) findViewById(R.id.tv_item_collect_business_title);
        commentText = (EditText) findViewById(R.id.et_comment);
        et_ConsumptionScore = (EditText) findViewById(R.id.et_ConsumptionScore);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
        ratingBar3 = (RatingBar) findViewById(R.id.ratingBar3);
        btn_commit = (Button) findViewById(R.id.btn_ucommit);
        rl_writecomment = (RelativeLayout)findViewById(R.id.rl_writecomment);
        rl_commentsConsumptionScore = (RelativeLayout) findViewById(R.id.rl_commentsConsumptionScore);

        back.setOnClickListener(this);
        btn_commit.setOnClickListener(this);
        img_write.setOnClickListener(this);

        B_Name.setText(businessName);
        address.setText(addr);
        introduce.setText(intro);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                TasteScore = rating*10/5;
            }
        });
        ratingBar2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                EnvironmentScore = rating*10/5;
            }
        });
        ratingBar3.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ServiceScore = rating*10/5;
            }
        });
        rl_writecomment.setVisibility(View.GONE);
        rl_commentsConsumptionScore.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_ucommit:
                String comment = commentText.getText().toString();
                String ConsumptionScore =et_ConsumptionScore.getText().toString();

                JsonObject object = new JsonObject();
                object.addProperty("commentsTasteScore",TasteScore);
                object.addProperty("commentsEnvironmentScore", EnvironmentScore);
                object.addProperty("commentsServiceScore", ServiceScore);
                object.addProperty("userNumber", userNumber);
                object.addProperty("commentText", comment);
                object.addProperty("commentsConsumptionScore", ConsumptionScore);
                object.addProperty("businessName", businessName.toString());
                object.addProperty("commentsPercapita", "30");
                object.addProperty("commentsPicture", "1");
                Toast.makeText(this, object.toString(), Toast.LENGTH_SHORT).show();
                String data = "operateComment"+"Insert"+object.toString();
                Send(data);
                rl_writecomment.setVisibility(View.GONE);
                rl_commentsConsumptionScore.setVisibility(View.GONE);
                img_write.setVisibility(View.VISIBLE);
                break;
            case R.id.img_write:
                rl_writecomment.setVisibility(View.VISIBLE);
                rl_commentsConsumptionScore.setVisibility(View.VISIBLE);
                img_write.setVisibility(View.GONE);
                break;
        }
    }


    void Send(final String type) {
        new Thread() {
            @Override
            public void run() {
                try
                {
                    Message msg = new Message();
                    msg.what = 0x345;
                    msg.obj = type;
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
