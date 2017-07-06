package cn.edu.gzhu.dp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cn.edu.gzhu.dp.Datas.UserInfo;
import cn.edu.gzhu.dp.R;
import cn.edu.gzhu.dp.Threads.GetDatas;

/**
 * Created by LRC on 2016/11/6.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    Button btn_register, btn_login;
    EditText et_user_account, et_password;
    String account,userPassword;
    String line;
    Handler handler;
    GetDatas getDatas;
    UserInfo User;
    SharedPreferences perferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0x123){
                    line = msg.obj.toString();
                    //Toast.makeText(LoginActivity.this,"Get Data is----"+line, Toast.LENGTH_SHORT).show();
                    if (line.equals("false")) {
                        Toast.makeText(LoginActivity.this, "登录失败，请输入正确的账号密码", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        JsonParser parser = new JsonParser();
                        JsonObject object = (JsonObject)parser.parse(line);

                        User.setU_no(object.get("usersNumber").getAsString());
                        User.setAccount(et_user_account.getText().toString());
                        User.setName(object.get("usersName").getAsString());
                        User.setPassword(et_password.getText().toString());
                        User.setPhone(object.get("usersPhone").getAsString());
                        User.setSex(object.get("usersSex").getAsString());
                        User.setEmail(object.get("usersEmail").getAsString());

                        editor.putString("name",User.getName());
                        editor.putString("sex",User.getSex());
                        editor.putString("phone",User.getPhone());
                        editor.putString("u_no",User.getU_no());
                        editor.putString("email",User.getEmail());
                        editor.putString("password",User.getPassword());
                        editor.putString("account",User.getAccount());

                        editor.putBoolean("logined",true);
                        editor.commit();

                        End();      //结束通信，关闭服务器
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };

        getDatas= new GetDatas(handler);
        new Thread(getDatas).start();
        Send("login");
    }

    public void init(){
        User = new UserInfo();
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_login = (Button) findViewById(R.id.btn_login);
        et_user_account = (EditText) findViewById(R.id.et_user_account);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        perferences = getSharedPreferences("DP",Context.MODE_PRIVATE);
        editor = perferences.edit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                JsonObject object = new JsonObject();

                account = et_user_account.getText().toString();
                userPassword = et_password.getText().toString();
                if(account!=null&&userPassword!=null) {
                    User.setPassword(userPassword);
                    object.addProperty("user", account);
                    object.addProperty("password", userPassword);
                    Send(object.toString());
                }else{
                    Toast.makeText(this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.btn_register:
                Intent intent = new Intent(this, RegistActivity.class);
                startActivity(intent);

                break;
        }
    }

    void Send(final String type) {
        new Thread() {
            @Override
            public void run() {
                try
                {
                    sleep(100);
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

