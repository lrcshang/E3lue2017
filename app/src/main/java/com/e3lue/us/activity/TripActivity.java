package com.e3lue.us.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e3lue.us.R;
import com.e3lue.us.model.TripData;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.e3lue.us.ui.widget.CustomDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripActivity extends SwipeBackActivity implements View.OnClickListener {
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
    private CustomDatePicker customDatePicker1;
    List<TripData> tripDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);
        selectDate.setOnClickListener(this);
        initDatePicker();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectDate:
                // 日期格式为yyyy-MM-dd
                customDatePicker1.show(currentDate.getText().toString());
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
            }
        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动
    }
}
