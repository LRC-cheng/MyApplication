package cn.edu.gzhu.dp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cn.edu.gzhu.dp.Datas.UserInfo;
import cn.edu.gzhu.dp.R;
import cn.edu.gzhu.dp.Threads.GetDatas;
import cn.edu.gzhu.dp.Threads.UploadUtil;


/**
 * Created by LRC on 2016/11/19.
 */
public class RegistActivity extends Activity implements View.OnClickListener {
    Button btn_OK;
    ImageView back, img_setphoto;
    Handler handler;
    GetDatas getDatas;
    UserInfo user;
    String line;
    String info, imPath;
    EditText et_user_account, et_user_name, et_user_sex, et_user_email, et_userphone, et_user_psw, et_user_password2;
    SharedPreferences perferences;
    SharedPreferences.Editor editor;
    private static final int IMAGE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        init();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x123) {
                    line = msg.obj.toString();
                    if (line.equals("false")) {
                        Toast.makeText(RegistActivity.this, "注册失败，已有该用户", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegistActivity.this, "注册成功，返回主页面", Toast.LENGTH_SHORT).show();
                        user.setU_no(line);

                        //把用户信息记录到本地
                        editor.putString("u_no", user.getU_no());
                        editor.putString("account", et_user_account.getText().toString());
                        editor.putString("name", et_user_name.getText().toString());
                        editor.putString("sex", et_user_sex.getText().toString());
                        editor.putString("email", et_user_email.getText().toString());
                        editor.putString("phone", et_userphone.getText().toString());
                        editor.putString("password", et_user_password2.getText().toString());
                        editor.putBoolean("logined", true);
                        editor.commit();

                        //上传过程：
//                        if (imPath != null) {
//                            final File file = new File(imPath);
//                            String fileName = Uri.encode(file.getName());
//
//                            String requestURL = "http://www.gzhugeeyee.cn/AppUploadFileHandler.ashx?type1386317253958=appuploadfile&folder=common&fileName=" + fileName;
//
//                            String request = UploadUtil.uploadFile(file, requestURL);
//
//
//                            if (request != null) {
//                                try {
//                                    JSONObject jsonObject = new JSONObject(request);
//                                    if (jsonObject.getString("Status").equals("0")) {
//                                        String reName = jsonObject.getString("FileName");
//                                        reName = Uri.encode(reName);
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }

                        Intent intent = new Intent(RegistActivity.this, MainActivity.class);
                        startActivity(intent);
                        End();
                        RegistActivity.this.finish();
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
        btn_OK = (Button) findViewById(R.id.btn_OK);
        back = (ImageView) findViewById(R.id.back);

        et_user_account = (EditText) findViewById(R.id.et_user_account);
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_userphone = (EditText) findViewById(R.id.et_userphone);
        et_user_sex = (EditText) findViewById(R.id.et_user_sex);
        et_user_email = (EditText) findViewById(R.id.et_user_email);
        et_user_psw = (EditText) findViewById(R.id.et_user_psd);
        et_user_password2 = (EditText) findViewById(R.id.et_user_password2);

        img_setphoto.setOnClickListener(this);
        btn_OK.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_OK:
                String account = et_user_account.getText().toString();
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

                    object.addProperty("user", user.getAccount());
                    object.addProperty("name", user.getName());
                    object.addProperty("password", user.getPassword());
                    object.addProperty("email", user.getEmail());
                    object.addProperty("phone", user.getPhone());
                    object.addProperty("sex", user.getSex());
                    object.addProperty("picture", "0");
                    editor.putString("imgPath", imPath);
                    info = object.toString();
                    Send("register" + info);
                }
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
        //获取图片路径
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
            String imagePath = cursor.getString(columnIndex);
            showImage(imagePath);
            cursor.close();
        }
    }

    //加载图片
    private void showImage(String imgPath) {
        Toast.makeText(this, "imgPath:" + imgPath, Toast.LENGTH_SHORT).show();
        imPath = imgPath;
        Bitmap bm = BitmapFactory.decodeFile(imgPath);
        img_setphoto.setImageBitmap(bm);
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
