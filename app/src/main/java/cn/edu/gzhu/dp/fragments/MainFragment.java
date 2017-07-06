package cn.edu.gzhu.dp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.edu.gzhu.dp.R;
import cn.edu.gzhu.dp.Threads.GetDatas;
import cn.edu.gzhu.dp.activities.DetailActivity;
import cn.edu.gzhu.dp.activities.MoreListActivity;
import cn.edu.gzhu.dp.activities.SearchActivity;


/**
 * Created by LRC on 2016/10/31.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    private Button btn_more_list;
    private ImageView img_search, img_japan, img_tea, img_fastfood, img_coffee, img_buffet;
    private ImageView img_hanguo, img_bread, img_bbq, img_sichuang, img_hotpot, img_west, img_yuecai;
    private Button btn_adjacent, btn_food, btn_total;
    private PopupMenu popup = null;
    private RelativeLayout rl_list_one, rl_list_two, rl_list_three, rl_list_four, rl_list_five, rl_list_six, rl_list_seven, rl_list_eight, rl_list_nine, rl_list_ten;
    private ImageView img_messages_1, img_messages_2, img_messages_3, img_messages_4, img_messages_5, img_messages_6, img_messages_7, img_messages_8, img_messages_9, img_messages_10;
    private TextView tv_messages_1A, tv_messages_2A, tv_messages_3A, tv_messages_4A, tv_messages_5A, tv_messages_6A, tv_messages_7A, tv_messages_8A, tv_messages_9A, tv_messages_10A;
    private TextView tv_messages_1B, tv_messages_2B, tv_messages_3B, tv_messages_4B, tv_messages_5B, tv_messages_6B, tv_messages_7B, tv_messages_8B, tv_messages_9B, tv_messages_10B;
    private TextView tv_messages_type_1, tv_messages_type_2, tv_messages_type_3, tv_messages_type_4, tv_messages_type_5, tv_messages_type_6, tv_messages_type_7, tv_messages_type_8, tv_messages_type_9, tv_messages_type_10;

    private String type = "ranking", line;
    private String Business_Name[] = null;
    private RelativeLayout rl[] = null;

    private ImageView img_business[] = null;
    private TextView tv_name[] = null;
    private TextView tv_type[] = null;
    private TextView tv_district[] = null;

    Handler handler;
    GetDatas getDatas;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);

        Business_Name = new String[10];
        img_business = new ImageView[10];
        tv_name = new TextView[10];
        tv_type = new TextView[10];
        tv_district = new TextView[10];
        rl = new RelativeLayout[10];

        init(view);


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x123) {
                    line = msg.obj.toString();
                    for(int k = 0;k<10;k++){
                        rl[k].setVisibility(View.GONE);
                    }
                    upDateDatas();
                }
            }
        };
        getDatas = new GetDatas(handler);
        new Thread(getDatas).start();
        Send(type);


        return view;
    }

    public void init(View view) {
        Business_Name = new String[10];
        img_business = new ImageView[10];
        tv_name = new TextView[10];
        tv_type = new TextView[10];
        tv_district = new TextView[10];
        rl = new RelativeLayout[10];

        int i = 0;

        btn_more_list = (Button) view.findViewById(R.id.btn_more_list);
        img_search = (ImageView) view.findViewById(R.id.img_search);

        btn_adjacent = (Button) view.findViewById(R.id.btn_adjacent);
        btn_food = (Button) view.findViewById(R.id.btn_food);
        btn_total = (Button) view.findViewById(R.id.btn_total);

        img_japan = (ImageView) view.findViewById(R.id.img_japan);
        img_tea = (ImageView) view.findViewById(R.id.img_tea);
        img_fastfood = (ImageView) view.findViewById(R.id.img_fastfood);
        img_coffee = (ImageView) view.findViewById(R.id.img_coffee);
        img_buffet = (ImageView) view.findViewById(R.id.img_buffet);
        img_hanguo = (ImageView) view.findViewById(R.id.img_hanguo);
        img_bread = (ImageView) view.findViewById(R.id.img_bread);
        img_bbq = (ImageView) view.findViewById(R.id.img_bbq);
        img_sichuang = (ImageView) view.findViewById(R.id.img_sichuang);
        img_hotpot = (ImageView) view.findViewById(R.id.img_hotpot);
        img_west = (ImageView) view.findViewById(R.id.img_west);
        img_yuecai = (ImageView) view.findViewById(R.id.img_yuecai);

        rl[0] = rl_list_one = (RelativeLayout) view.findViewById(R.id.rl_list_one);
        rl[1] = rl_list_two = (RelativeLayout) view.findViewById(R.id.rl_list_two);
        rl[2] = rl_list_three = (RelativeLayout) view.findViewById(R.id.rl_list_three);
        rl[3] = rl_list_four = (RelativeLayout) view.findViewById(R.id.rl_list_four);
        rl[4] = rl_list_five = (RelativeLayout) view.findViewById(R.id.rl_list_five);
        rl[5] = rl_list_six = (RelativeLayout) view.findViewById(R.id.rl_list_six);
        rl[6] = rl_list_seven = (RelativeLayout) view.findViewById(R.id.rl_list_seven);
        rl[7] = rl_list_eight = (RelativeLayout) view.findViewById(R.id.rl_list_eight);
        rl[8] = rl_list_nine = (RelativeLayout) view.findViewById(R.id.rl_list_nine);
        rl[9] = rl_list_ten = (RelativeLayout) view.findViewById(R.id.rl_list_ten);

        for(int k = 0;k<10;k++){
            rl[k].setVisibility(View.GONE);
        }

        img_messages_1 = (ImageView) view.findViewById(R.id.img_messages_1);
        img_business[i] = img_messages_1;
        i++;
        img_messages_2 = (ImageView) view.findViewById(R.id.img_messages_2);
        img_business[i] = img_messages_2;
        i++;
        img_messages_3 = (ImageView) view.findViewById(R.id.img_messages_3);
        img_business[i] = img_messages_3;
        i++;
        img_messages_4 = (ImageView) view.findViewById(R.id.img_messages_4);
        img_business[i] = img_messages_4;
        i++;
        img_messages_5 = (ImageView) view.findViewById(R.id.img_messages_5);
        img_business[i] = img_messages_5;
        i++;
        img_messages_6 = (ImageView) view.findViewById(R.id.img_messages_6);
        img_business[i] = img_messages_6;
        i++;
        img_messages_7 = (ImageView) view.findViewById(R.id.img_messages_7);
        img_business[i] = img_messages_7;
        i++;
        img_messages_8 = (ImageView) view.findViewById(R.id.img_messages_8);
        img_business[i] = img_messages_8;
        i++;
        img_messages_9 = (ImageView) view.findViewById(R.id.img_messages_9);
        img_business[i] = img_messages_9;
        i++;
        img_messages_10 = (ImageView) view.findViewById(R.id.img_messages_10);
        img_business[i] = img_messages_10;
        i++;

        i = 0;
        tv_messages_1A = (TextView) view.findViewById(R.id.tv_messages_1A);
        tv_name[i] = tv_messages_1A;
        i++;
        tv_messages_2A = (TextView) view.findViewById(R.id.tv_messages_2A);
        tv_name[i] = tv_messages_2A;
        i++;
        tv_messages_3A = (TextView) view.findViewById(R.id.tv_messages_3A);
        tv_name[i] = tv_messages_3A;
        i++;
        tv_messages_4A = (TextView) view.findViewById(R.id.tv_messages_4A);
        tv_name[i] = tv_messages_4A;
        i++;
        tv_messages_5A = (TextView) view.findViewById(R.id.tv_messages_5A);
        tv_name[i] = tv_messages_5A;
        i++;
        tv_messages_6A = (TextView) view.findViewById(R.id.tv_messages_6A);
        tv_name[i] = tv_messages_6A;
        i++;
        tv_messages_7A = (TextView) view.findViewById(R.id.tv_messages_7A);
        tv_name[i] = tv_messages_7A;
        i++;
        tv_messages_8A = (TextView) view.findViewById(R.id.tv_messages_8A);
        tv_name[i] = tv_messages_8A;
        i++;
        tv_messages_9A = (TextView) view.findViewById(R.id.tv_messages_9A);
        tv_name[i] = tv_messages_9A;
        i++;
        tv_messages_10A = (TextView) view.findViewById(R.id.tv_messages_10A);
        tv_name[i] = tv_messages_10A;
        i++;

        i = 0;
        tv_messages_1B = (TextView) view.findViewById(R.id.tv_messages_1B);
        tv_district[i] = tv_messages_1B;
        i++;
        tv_messages_2B = (TextView) view.findViewById(R.id.tv_messages_2B);
        tv_district[i] = tv_messages_2B;
        i++;
        tv_messages_3B = (TextView) view.findViewById(R.id.tv_messages_3B);
        tv_district[i] = tv_messages_3B;
        i++;
        tv_messages_4B = (TextView) view.findViewById(R.id.tv_messages_4B);
        tv_district[i] = tv_messages_4B;
        i++;
        tv_messages_5B = (TextView) view.findViewById(R.id.tv_messages_5B);
        tv_district[i] = tv_messages_5B;
        i++;
        tv_messages_6B = (TextView) view.findViewById(R.id.tv_messages_6B);
        tv_district[i] = tv_messages_6B;
        i++;
        tv_messages_7B = (TextView) view.findViewById(R.id.tv_messages_7B);
        tv_district[i] = tv_messages_7B;
        i++;
        tv_messages_8B = (TextView) view.findViewById(R.id.tv_messages_8B);
        tv_district[i] = tv_messages_8B;
        i++;
        tv_messages_9B = (TextView) view.findViewById(R.id.tv_messages_9B);
        tv_district[i] = tv_messages_9B;
        i++;
        tv_messages_10B = (TextView) view.findViewById(R.id.tv_messages_10B);
        tv_district[i] = tv_messages_10B;
        i++;

        i = 0;
        tv_messages_type_1 = (TextView) view.findViewById(R.id.tv_messages_type_1);
        tv_type[i] = tv_messages_type_1;
        i++;
        tv_messages_type_2 = (TextView) view.findViewById(R.id.tv_messages_type_2);
        tv_type[i] = tv_messages_type_2;
        i++;
        tv_messages_type_3 = (TextView) view.findViewById(R.id.tv_messages_type_3);
        tv_type[i] = tv_messages_type_3;
        i++;
        tv_messages_type_4 = (TextView) view.findViewById(R.id.tv_messages_type_4);
        tv_type[i] = tv_messages_type_4;
        i++;
        tv_messages_type_5 = (TextView) view.findViewById(R.id.tv_messages_type_5);
        tv_type[i] = tv_messages_type_5;
        i++;
        tv_messages_type_6 = (TextView) view.findViewById(R.id.tv_messages_type_6);
        tv_type[i] = tv_messages_type_6;
        i++;
        tv_messages_type_7 = (TextView) view.findViewById(R.id.tv_messages_type_7);
        tv_type[i] = tv_messages_type_7;
        i++;
        tv_messages_type_8 = (TextView) view.findViewById(R.id.tv_messages_type_8);
        tv_type[i] = tv_messages_type_8;
        i++;
        tv_messages_type_9 = (TextView) view.findViewById(R.id.tv_messages_type_9);
        tv_type[i] = tv_messages_type_9;
        i++;
        tv_messages_type_10 = (TextView) view.findViewById(R.id.tv_messages_type_10);
        tv_type[i] = tv_messages_type_10;
        i++;


        btn_more_list.setOnClickListener(this);
        img_search.setOnClickListener(this);

        btn_adjacent.setOnClickListener(new ButtonListener());
        btn_food.setOnClickListener(new ButtonListener());
        btn_total.setOnClickListener(new ButtonListener());

        img_japan.setOnClickListener(this);
        img_tea.setOnClickListener(this);
        img_fastfood.setOnClickListener(this);
        img_coffee.setOnClickListener(this);
        img_buffet.setOnClickListener(this);
        img_hanguo.setOnClickListener(this);
        img_bread.setOnClickListener(this);
        img_bbq.setOnClickListener(this);
        img_sichuang.setOnClickListener(this);
        img_hotpot.setOnClickListener(this);
        img_west.setOnClickListener(this);
        img_yuecai.setOnClickListener(this);

        rl_list_one.setOnClickListener(this);
        rl_list_two.setOnClickListener(this);
        rl_list_three.setOnClickListener(this);
        rl_list_four.setOnClickListener(this);
        rl_list_five.setOnClickListener(this);
        rl_list_six.setOnClickListener(this);
        rl_list_seven.setOnClickListener(this);
        rl_list_eight.setOnClickListener(this);
        rl_list_nine.setOnClickListener(this);
        rl_list_ten.setOnClickListener(this);

    }

    private void upDateDatas() {
        //利用多线程获取服务器的数据
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(line);
        for (int i = 0; i < array.size() && i < 10; i++) {
            JsonObject subObject = array.get(i).getAsJsonObject();
            img_business[i].setImageResource(R.drawable.main);
            tv_name[i].setText(subObject.get("businessName").getAsString());
            Business_Name[i] = tv_name[i].getText().toString();
            tv_type[i].setText(subObject.get("businessStyle").getAsString());
            tv_district[i].setText(subObject.get("businessDistrict").getAsString());
            rl[i].setVisibility(View.VISIBLE);
        }
    }

    //监听列表
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_more_list:
                Intent intent = new Intent(getActivity(), MoreListActivity.class);
                startActivity(intent);
                End();
                break;
            case R.id.img_search:
                Intent intent_search = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent_search);
                End();
                break;
            case R.id.rl_list_one:
                Intent intent1 = new Intent(getActivity(), DetailActivity.class);
                intent1.putExtra("Business_Name", Business_Name[0]);
                startActivity(intent1);
                End();
                break;
            case R.id.rl_list_two:
                Intent intent2 = new Intent(getActivity(), DetailActivity.class);
                intent2.putExtra("Business_Name", Business_Name[1]);
                startActivity(intent2);
                End();
                break;
            case R.id.rl_list_three:
                Intent intent3 = new Intent(getActivity(), DetailActivity.class);
                intent3.putExtra("Business_Name", Business_Name[2]);
                startActivity(intent3);
                End();
                break;
            case R.id.rl_list_four:
                Intent intent4 = new Intent(getActivity(), DetailActivity.class);
                intent4.putExtra("Business_Name", Business_Name[3]);
                startActivity(intent4);
                End();
                break;
            case R.id.rl_list_five:
                Intent intent5 = new Intent(getActivity(), DetailActivity.class);
                intent5.putExtra("Business_Name", Business_Name[4]);
                startActivity(intent5);
                End();
                break;
            case R.id.rl_list_six:
                Intent intent6 = new Intent(getActivity(), DetailActivity.class);
                intent6.putExtra("Business_Name", Business_Name[5]);
                startActivity(intent6);
                End();
                break;
            case R.id.rl_list_seven:
                Intent intent7 = new Intent(getActivity(), DetailActivity.class);
                intent7.putExtra("Business_Name", Business_Name[6]);
                startActivity(intent7);
                End();
                break;
            case R.id.rl_list_eight:
                Intent intent8 = new Intent(getActivity(), DetailActivity.class);
                intent8.putExtra("Business_Name", Business_Name[7]);
                startActivity(intent8);
                End();
                break;
            case R.id.rl_list_nine:
                Intent intent9 = new Intent(getActivity(), DetailActivity.class);
                intent9.putExtra("Business_Name", Business_Name[8]);
                startActivity(intent9);
                End();
                break;
            case R.id.rl_list_ten:
                Intent intent10 = new Intent(getActivity(), DetailActivity.class);
                intent10.putExtra("Business_Name", Business_Name[9]);
                startActivity(intent10);
                End();
                break;
            case R.id.img_japan:
                Toast.makeText(getActivity(), "日本菜", Toast.LENGTH_SHORT).show();
                type = "style 日本菜";
                Send(type);
                break;
            case R.id.img_tea:
                Toast.makeText(getActivity(), "茶点", Toast.LENGTH_SHORT).show();
                type = "style 茶点";
                Send(type);
                break;
            case R.id.img_coffee:
                Toast.makeText(getActivity(), "咖啡", Toast.LENGTH_SHORT).show();
                type = "style 咖啡";
                Send(type);
                break;
            case R.id.img_fastfood:
                Toast.makeText(getActivity(), "快餐", Toast.LENGTH_SHORT).show();
                type = "style 快餐";
                Send(type);
                break;
            case R.id.img_buffet:
                Toast.makeText(getActivity(), "自助餐", Toast.LENGTH_SHORT).show();
                type = "style 自助餐";
                Send(type);
                break;
            case R.id.img_hanguo:
                Toast.makeText(getActivity(), "韩国菜", Toast.LENGTH_SHORT).show();
                type = "style 韩国菜";
                Send(type);
                break;
            case R.id.img_bbq:
                Toast.makeText(getActivity(), "BBQ", Toast.LENGTH_SHORT).show();
                type = "style BBQ";
                Send(type);
                break;
            case R.id.img_bread:
                Toast.makeText(getActivity(), "面包", Toast.LENGTH_SHORT).show();
                type = "style 面包";
                Send(type);
                break;
            case R.id.img_sichuang:
                Toast.makeText(getActivity(), "四川菜", Toast.LENGTH_SHORT).show();
                type = "style 四川菜";
                Send(type);
                break;
            case R.id.img_hotpot:
                Toast.makeText(getActivity(), "火锅", Toast.LENGTH_SHORT).show();
                type = "style 火锅";
                Send(type);
                break;
            case R.id.img_west:
                Toast.makeText(getActivity(), "西餐", Toast.LENGTH_SHORT).show();
                type = "style 西餐";
                Send(type);
                break;
            case R.id.img_yuecai:
                Toast.makeText(getActivity(), "粤菜", Toast.LENGTH_SHORT).show();
                type = "style 粤菜";
                Send(type);
                break;

            default:
                break;
        }
    }

    //分类菜单点击事件处理
    class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_adjacent:
                    popup = new PopupMenu(getActivity(), btn_adjacent);
                    getActivity().getMenuInflater().inflate(R.menu.menu_adjacent, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(getActivity(),
                                    "你点击了【" + item.getTitle() + "】菜单，即将更新信息条目",
                                    Toast.LENGTH_SHORT).show();
                            type = "district " + item.getTitle();
                            Send(type);
                            return true;
                        }
                    });
                    popup.show();
                    break;
                case R.id.btn_food:
                    popup = new PopupMenu(getActivity(), btn_food);
                    getActivity().getMenuInflater().inflate(R.menu.menu_food, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(getActivity(),
                                    "你点击了【" + item.getTitle() + "】菜单，即将更新信息条目",
                                    Toast.LENGTH_SHORT).show();
                            type = "style " + item.getTitle();
                            Send(type);
                            return true;
                        }
                    });
                    popup.show();
                    Send(type);
                    break;
                case R.id.btn_total:
                    popup = new PopupMenu(getActivity(), btn_total);
                    getActivity().getMenuInflater().inflate(R.menu.menu_total, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(getActivity(),
                                    "你点击了【" + item.getTitle() + "】菜单，即将更新信息条目",
                                    Toast.LENGTH_SHORT).show();
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
    public void onDestroy() {
        super.onDestroy();
        End();
    }
}
