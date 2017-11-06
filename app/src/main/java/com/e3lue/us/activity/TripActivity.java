package com.e3lue.us.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e3lue.us.R;
import com.e3lue.us.model.TripData;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.ui.view.ListViewForScrollView;
import com.e3lue.us.ui.view.MyScrollView;
import com.e3lue.us.ui.widget.CustomDatePicker;

import org.wlf.filedownloader.base.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TripActivity extends SwipeBackActivity implements View.OnClickListener, MyScrollView.OnScrollListener {
    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.check_text)
    TextView checkText;
    @BindView(R.id.btnMore)
    ImageView btnMore;
    @BindView(R.id.layout_header)
    RelativeLayout layoutHeader;
    @BindView(R.id.currentDate)
    TextView currentDate;
    @BindView(R.id.selectDate)
    LinearLayout selectDate;
    @BindView(R.id.Locale)
    EditText locale1;
    @BindView(R.id.CompanyName)
    EditText CompanyName;
    @BindView(R.id.ContactPerson)
    EditText ContactPerson;
    @BindView(R.id.Departs)
    EditText Departs;
    @BindView(R.id.Destination)
    EditText Destination;
    @BindView(R.id.selectLocale)
    LinearLayout selectLocale;
    @BindView(R.id.Transport)
    EditText Transport;
    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.fee1)
    EditText fee1;
    @BindView(R.id.fee2)
    EditText fee2;
    @BindView(R.id.fee3)
    EditText fee3;
    @BindView(R.id.note)
    EditText note;
    @BindView(R.id.search01)
    LinearLayout search01;
    @BindView(R.id.search02)
    LinearLayout search02;
    @BindView(R.id.rlayout)
    RelativeLayout rlayout;
    @BindView(R.id.listView)
    ListViewForScrollView listView;
    @BindView(R.id.myScrollView)
    MyScrollView myScrollView;
    @BindView(R.id.search_edit)
    HorizontalScrollView hView;
 List<String> arr_data;
    TextView check_text;
    private CustomDatePicker customDatePicker1;
    List<TripData> tripDatas;//出差需要提交参数数组
    TripData tripData;
    private int searchLayoutTop;
    ArrayAdapter arr_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        check_text = (TextView) findViewById(R.id.check_text);
        ButterKnife.bind(this);
        arr_data = new ArrayList<>();

        //新建一个数组适配器ArrayAdapter绑定数据，参数(当前的Activity，布局文件，数据源)

        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr_data);

        //视图(ListView)加载适配器
        listView.setAdapter(arr_adapter);
        myScrollView.smoothScrollTo(0, 0);
        myScrollView.setOnScrollListener(this);
        tripDatas = new ArrayList<>();
        tripData = new TripData();
        check_text.setText("保存");
        check_text.setOnClickListener(this);
        selectDate.setOnClickListener(this);
        initDatePicker();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            searchLayoutTop = listView.getBottom();//获取searchLayout的顶部位置
        }
    }

    @Override
    public void onScroll(int scrollY) {
        rlayout.scrollTo(0, -scrollY / 4);
        if (scrollY >= searchLayoutTop) {

            if (hView.getParent() != search01) {
                search02.removeView(hView);
                search01.addView(hView);
            }
        } else {
            if (hView.getParent() != search02) {
                search01.removeView(hView);
                search02.addView(hView);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectDate:
                // 日期格式为yyyy-MM-dd
                customDatePicker1.show(currentDate.getText().toString());
                tripData.setPlanDate(currentDate.getText().toString());
                Log.i("xinxi", currentDate.getText().toString());
                break;
            case R.id.check_text:
                arr_data.add("sss");
                arr_adapter.notifyDataSetChanged();
                searchLayoutTop = listView.getBottom()+getResources().getDimensionPixelOffset(R.dimen.trip_layout_height);
                if (hView.getParent() != search02) {
                    search01.removeView(hView);
                    search02.addView(hView);
                }
                break;
        }
    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        currentDate.setText(now.split(" ")[0]);

        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                currentDate.setText(time.split(" ")[0]);
                tripData.setPlanDate(time.split(" ")[0].toString());
                Log.i("xinxi", time.split(" ")[0].toString());
            }
        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动
    }

    @OnClick(R.id.save)
    public void onViewClicked() {
        arr_data.add("sss");
        arr_adapter.notifyDataSetChanged();
        searchLayoutTop = listView.getBottom()+getResources().getDimensionPixelOffset(R.dimen.trip_layout_height);
        if (hView.getParent() != search02) {
            search01.removeView(hView);
            search02.addView(hView);
        }
    }
}
