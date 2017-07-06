package cn.edu.gzhu.dp.Threads;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by LRC on 2017/4/23.
 */

public class GetPhoto {

    public static Bitmap getBitmap(String url) {
        URL Url = null;
        Bitmap bitmap = null;

        try {
            Url = new URL(url);
            // 打开连接
            HttpURLConnection urlCon = (HttpURLConnection) Url.openConnection();
            urlCon.setDoInput(true);
            urlCon.connect();
            //获取输入流，利用输入流获取图片
            InputStream is = urlCon.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;

    }
}
