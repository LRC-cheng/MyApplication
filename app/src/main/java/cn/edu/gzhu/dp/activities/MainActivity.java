package cn.edu.gzhu.dp.activities;

import cn.edu.gzhu.dp.Datas.UserInfo;
import cn.edu.gzhu.dp.R;
import cn.edu.gzhu.dp.fragments.MainFragment;
import cn.edu.gzhu.dp.fragments.UserFragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView iv_main,iv_user;
    UserInfo User;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init(){
        iv_main=(ImageView)findViewById(R.id.iv_main);
        iv_user=(ImageView)findViewById(R.id.iv_user);

        iv_main.setOnClickListener(this);
        iv_user.setOnClickListener(this);

        changeFragment(new MainFragment());
        iv_main.setImageResource(R.drawable.home);
        iv_user.setImageResource(R.drawable.megray);
    }

    @Override
    public void onClick(View v) {
        iv_main.setImageResource(R.drawable.home_gray);
        iv_user.setImageResource(R.drawable.megray);
        switch(v.getId()){
            case R.id.iv_main:
                changeFragment(new MainFragment());
                iv_main.setImageResource(R.drawable.home);
                break;
            case R.id.iv_user:
                changeFragment(new UserFragment());
                iv_user.setImageResource(R.drawable.meorange);
                break;
            default:
                break;
        }
    }

    public void changeFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_container, fragment);
        ft.commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        iv_user.setImageResource(R.drawable.megray);
        changeFragment(new MainFragment());
        iv_main.setImageResource(R.drawable.home);
    }
}
