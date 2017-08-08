package com.e3lue.us.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.e3lue.us.R;
import com.e3lue.us.model.Expenses;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Leo on 2017/4/12.
 */

public class ExpensesSelectActivity extends SwipeBackActivity {

    Gson gson;
    List<Expenses> list;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.Expenses_Select)
    TagFlowLayout tagFlow;

    @BindView(R.id.Expenses_Select_ok)
    Button sure;

    @OnClick(R.id.Expenses_Select_ok)
    public void ok() {
        Set<Integer> choose = tagFlow.getSelectedList();
        if (choose.size() == 0) finish();
        ArrayList<Expenses> ok_list = new ArrayList<Expenses>();
        for (int value : choose) {
            ok_list.add(list.get(value));
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("expenses", ok_list);
        intent.putExtras(bundle);
        setResult(103, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expenses_select_activity);
        ButterKnife.bind(this);

        textHeadTitle.setText("选择未绑定报销单");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        gson = new Gson();
        getdata();
    }

    public void getdata() {
        OkGo.<String>post(HttpUrl.Url.EXPENSESLISTForBind)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progressDimss();
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            okdata(result.getData().toString());
                        } else {
                            showToast(result.getMsg());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        showToast("网络连接失败,请重试.");
                    }
                });
    }

    public void okdata(String data) {
        if (data == null || data.toString().contains("null")) {
            showToast("已无未绑定报销单");
            sure.setText("返回");
            return;
        }
        list = new ArrayList<Expenses>();
        list = gson.fromJson(
                data,
                new TypeToken<ArrayList<Expenses>>() {
                }.getType());
        final LayoutInflater mInflater = LayoutInflater.from(this);
        tagFlow.setAdapter(new TagAdapter<Expenses>(list) {
            @Override
            public View getView(FlowLayout parent, int position, Expenses entity) {
                TextView tv = (TextView) mInflater.inflate(R.layout.tag_tv,
                        tagFlow, false);
                tv.setText(Html.fromHtml("<font color='red'><big>" + entity.getAmount() + "元</big></font>&#160;<font color='gray'><small>" + entity.getNote() + "&#160;(" + entity.getCreateTime() + ")</small></font>"));
                return tv;
            }
        });
    }

}
