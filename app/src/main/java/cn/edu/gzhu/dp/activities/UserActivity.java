package cn.edu.gzhu.dp.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import cn.edu.gzhu.dp.Datas.UserInfo;
import cn.edu.gzhu.dp.R;
import cn.edu.gzhu.dp.Threads.GetDatas;

/**
 * Created by LRC on 2016/11/6.
 */
public class UserActivity extends Activity implements View.OnClickListener {
    ImageView back, img_setphoto;
    Button btn_OK, btn_cancle;
    EditText et_user_name, et_user_sex, et_user_email, et_userphone, et_user_psw, et_user_password2;
    Handler handler;
    GetDatas getDatas;
    String info, line, account;
    UserInfo user;
    SharedPreferences perferences;
    SharedPreferences.Editor editor;
    Boolean logined;

    private static final int IMAGE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        init();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x123) {
                    line = msg.obj.toString();
                    if (line == "false") {
                        Toast.makeText(UserActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserActivity.this, "修改成功，返回主页面", Toast.LENGTH_SHORT).show();

                        //把用户信息记录到本地
                        editor.putString("name", et_user_name.getText().toString());
                        editor.putString("sex", et_user_sex.getText().toString());
                        editor.putString("email", et_user_email.getText().toString());
                        editor.putString("phone", et_userphone.getText().toString());
                        editor.putString("password", et_user_password2.getText().toString());
                        editor.putBoolean("logined", true);
                        editor.commit();


                        Intent intent = new Intent(UserActivity.this, MainActivity.class);
                        startActivity(intent);
                        End();
                        UserActivity.this.finish();
                    }
                }
            }
        };

        getDatas = new GetDatas(handler);
        new Thread(getDatas).start();
    }

    public void init() {
        perferences = getSharedPreferences("DP", Context.MODE_PRIVATE);
        editor = perferences.edit();

        img_setphoto = (ImageView) findViewById(R.id.img_setphoto);
        btn_cancle = (Button) findViewById(R.id.btn_cancel);
        btn_OK = (Button) findViewById(R.id.btn_save);
        back = (ImageView) findViewById(R.id.back);

        et_user_name = (EditText) findViewById(R.id.et_user_myname);
        et_userphone = (EditText) findViewById(R.id.et_myphone);
        et_user_sex = (EditText) findViewById(R.id.et_user_mysex);
        et_user_email = (EditText) findViewById(R.id.et_user_myemail);
        et_user_psw = (EditText) findViewById(R.id.et_user_mypsd);
        et_user_password2 = (EditText) findViewById(R.id.et_user_mypassword2);

        img_setphoto.setOnClickListener(this);
        btn_OK.setOnClickListener(this);
        back.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        logined = perferences.getBoolean("logined", false);
        if (logined) {
            account = perferences.getString("account", null);
            et_user_name.setText(perferences.getString("name", null));
            et_userphone.setText(perferences.getString("phone", null));
            et_user_sex.setText(perferences.getString("sex", null));
            et_user_email.setText(perferences.getString("email", null));
            et_user_psw.setText(perferences.getString("password", null));
            et_user_password2.setText(perferences.getString("password", null));

            String imgPath = perferences.getString("imgPath", null);

            if (imgPath != null) {
                Bitmap bm = BitmapFactory.decodeFile(imgPath);
                img_setphoto.setImageBitmap(bm);
            }
        }
    }


    void Send(final String type) {
        new Thread() {
            @Override
            public void run() {
                try {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                String phone = et_userphone.getText().toString();
                String name = et_user_name.getText().toString();
                String sex = et_user_sex.getText().toString();
                String email = et_user_email.getText().toString();
                String psw = et_user_psw.getText().toString();
                String psw2 = et_user_password2.getText().toString();

                if (phone.isEmpty() && name.isEmpty() && sex.isEmpty()
                        && email.isEmpty() && psw.isEmpty() && psw2.isEmpty()) {
                    Toast.makeText(this, "请完善基本信息", Toast.LENGTH_SHORT).show();
                    break;
                } else if (!psw.equals(psw2)) {
                    Toast.makeText(this, "2次密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
                } else {
                    user = new UserInfo(account, name, sex, phone, email, psw);

                    //生成Jason字符串记录数据
                    JsonObject object = new JsonObject();
                    String uno = (perferences.getString("u_no", null));
                    object.addProperty("userNumber", uno);
                    object.addProperty("name", user.getName());
                    object.addProperty("user", user.getAccount());
                    object.addProperty("password", user.getPassword());
                    object.addProperty("email", user.getEmail());
                    object.addProperty("phone", user.getPhone());
                    object.addProperty("sex", user.getSex());
                    object.addProperty("picture", "0");
                    info = object.toString();
                    Send("modify" + info);
                }

                break;
            case R.id.btn_cancel:
                editor.clear();
                editor.putBoolean("logined", false);
                editor.commit();
                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent);
                End();
                UserActivity.this.finish();
                break;

            case R.id.img_setphoto:
                Intent intent2 = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent2, IMAGE);
                break;
            case R.id.back:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        storagepermissions(this);
        //获取图片路径
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            if (c != null) {
                c.moveToFirst();
            }
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            showImage(imagePath);
            c.close();
        }

    }
    //加载图片
    private void showImage(String imgPath) {
        //Toast.makeText(this, "imgPath:"+imgPath, Toast.LENGTH_SHORT).show();

        if (imgPath != null) {
            editor.putString("imgPath", imgPath);
            editor.commit();
            Bitmap bm = BitmapFactory.decodeFile(imgPath);
            img_setphoto.setImageBitmap(bm);
        } else {
            Toast.makeText(this, "false to get photo", Toast.LENGTH_SHORT).show();
        }
    }

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static void storagepermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
}
