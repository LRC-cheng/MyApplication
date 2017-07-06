package cn.edu.gzhu.dp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import cn.edu.gzhu.dp.Datas.UserInfo;
import cn.edu.gzhu.dp.R;
import cn.edu.gzhu.dp.activities.CollectActivity;
import cn.edu.gzhu.dp.activities.CommentActivity;
import cn.edu.gzhu.dp.activities.SpoorActivity;
import cn.edu.gzhu.dp.activities.LoginActivity;
import cn.edu.gzhu.dp.activities.UserActivity;

/**
 * Created by LRC on 2016/10/31.
 */
public class UserFragment extends Fragment implements View.OnClickListener {
    ImageView img_user_photo;
    Boolean checklogined = false;
    RelativeLayout rl_user_setting, rl_connect_us, rl_footprint, rl_collection, rl_comment;
    TextView user_name;
    SharedPreferences perferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, null);

        init(view);

        if (perferences.getBoolean("logined", false)) {
            checklogined = true;
            user_name.setText(perferences.getString("name", null));
            String imgPath = perferences.getString("imgPath", null);
//            Toast.makeText(getActivity(), imgPath, Toast.LENGTH_SHORT).show();
            if (imgPath != null) {
                Bitmap bm = BitmapFactory.decodeFile(imgPath);
                img_user_photo.setImageBitmap(bm);
            }

        } else {
            checklogined = false;
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_user_photo:
                if (checklogined) {
                    Intent intent = new Intent(getActivity(), UserActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    Intent intent2 = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent2, 1);

                }
                break;
            case R.id.rl_user_setting:
                Intent intent = new Intent(getActivity(), UserActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.rl_connect_us:
                Toast.makeText(getActivity(), "客服电话：020-04843", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_footprint:
                Intent intent3 = new Intent(getActivity(), SpoorActivity.class);
                startActivityForResult(intent3, 1);
                break;
            case R.id.rl_collection:
                Intent intent4 = new Intent(getActivity(), CollectActivity.class);
                startActivityForResult(intent4, 1);
                break;
            case R.id.rl_comment:
                Intent intent5 = new Intent(getActivity(), CommentActivity.class);
                startActivityForResult(intent5, 1);
                break;
        }
    }

    public void init(View view) {
        perferences = getActivity().getSharedPreferences("DP", Context.MODE_PRIVATE);

        img_user_photo = (ImageView) view.findViewById(R.id.img_user_photo);
        rl_user_setting = (RelativeLayout) view.findViewById(R.id.rl_user_setting);
        rl_connect_us = (RelativeLayout) view.findViewById(R.id.rl_connect_us);
        rl_footprint = (RelativeLayout) view.findViewById(R.id.rl_footprint);
        rl_collection = (RelativeLayout) view.findViewById(R.id.rl_collection);
        rl_comment = (RelativeLayout) view.findViewById(R.id.rl_comment);
        user_name = (TextView) view.findViewById(R.id.tv_user_name);

        img_user_photo.setOnClickListener(this);
        rl_user_setting.setOnClickListener(this);
        rl_connect_us.setOnClickListener(this);
        rl_footprint.setOnClickListener(this);
        rl_collection.setOnClickListener(this);
        rl_comment.setOnClickListener(this);

    }
}
