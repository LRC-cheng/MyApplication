package cn.edu.gzhu.dp.Threads;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;


public class UploadUtil {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码

    public static String uploadFile(File file, String RequestURL) {
        String result = null;
        // 定义数据分隔的边界标识  //边界标识   随机生成
        String BOUNDARY = UUID.randomUUID().toString();
        // boundary前缀
        String boundaryPrefix = "--";
        // 换行符
        String newLine = "\r\n";
        // 内容类型
        String CONTENT_TYPE = "multipart/form-data";


        OutputStream outputStream = null;
        DataInputStream dataInputStream = null;

        HttpURLConnection conn = null;

        try {
            URL url = new URL(RequestURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //Post请求方式

            // 设置请求头参数
            conn.setRequestProperty("Charset", CHARSET);  // 设置编码方式
            conn.addRequestProperty("connection", "keep-alive");
            conn.addRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);


            outputStream = new DataOutputStream(conn.getOutputStream());


            // 上传文件
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(boundaryPrefix); // "--" boundary的前缀
            stringBuilder.append(BOUNDARY);      // boundary
            stringBuilder.append(newLine);      // "\r\n" 换行

            // 文件参数，photo参数名可以随意修改
            stringBuilder.append("Content-Disposition: form-data;name=\"photo\";filename=\"" + file.getName()
                    + "\"");
            stringBuilder.append(newLine);
            stringBuilder.append("Content-Type:application/octet-stream");
            // 参数头设置完之后需要两个换行，然后才是参数内容
            stringBuilder.append(newLine);
            stringBuilder.append(newLine);

            // 将参数头的数据写入到输出流中
            outputStream.write(stringBuilder.toString().getBytes());


            // 数据输入流，用于读取文件数据
            dataInputStream = new DataInputStream(new FileInputStream(file));
            byte[] bufferOut = new byte[1024];
            int bytes = 0;
            // 每次读取1KB数据，并且将文件数据写入到输出流中
            while ((bytes = dataInputStream.read(bufferOut)) != -1) {
                outputStream.write(bufferOut, 0, bytes);
            }
//            dataInputStream.close();   // 在最后才close吧

            // 最后添加换行
            outputStream.write(newLine.getBytes());
            // 定义最后数据分割线，即--加上BOUNDAY再加上--。
            byte[] end_data = (newLine + boundaryPrefix + BOUNDARY + boundaryPrefix + newLine).getBytes();
            // 写上结尾标识
            outputStream.write(end_data);
            outputStream.flush();


            /**
             * 获取响应码  200=成功
             * 当响应成功，获取响应的流
             */
            int res = conn.getResponseCode();
            Log.i(TAG, "response code:" + res);

            // 这里要进行判断 响应吗200 表示成功 然后读取服务器发来的响应

            if(res == 200) {
                Log.i(TAG, "request success");
                InputStream input = conn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(input, "utf-8");
                StringBuffer sb1 = new StringBuffer();
                int ss;
                while ((ss = inputStreamReader.read()) != -1) {
                    sb1.append((char) ss);
                }
                result = sb1.toString();
                Log.i(TAG, "result : " + result);
            }
            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

    /**
     * 检测网络是否已经连接 以及联网的权限
     */
    public static Boolean checkNetworkConnection(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            boolean wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            boolean mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if (wifiConnected) {
//                Toast.makeText(context,
//                        "wifi已连接", Toast.LENGTH_SHORT).show();

            } else if (mobileConnected) {
//                Toast.makeText(context,
//                        "移动网络GPRS已连接", Toast.LENGTH_SHORT).show();

            }
        } else {
//            Toast.makeText(context,
//                    "网络没有连接，请连接后重试", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
