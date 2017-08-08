package com.e3lue.us.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.e3lue.us.R;
import com.e3lue.us.model.ContactPerson;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.swipebacklayout.SwipeBackActivity;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Leo on 2017/5/3.
 */

public class GameClubNewActivity extends SwipeBackActivity {

    public static final int REQUEST_CODE_Contact = 102;
    List<String> ListCurrency;
    List<String> ListDirect;

    private int CompanyID;
    private String CompanyName;

    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @BindView(R.id.gcorder_new_currency)
    TagFlowLayout mCurrency;

    @BindView(R.id.gcorder_new_direct)
    TagFlowLayout mDirect;

    @BindView(R.id.gcorder_new_company)
    TextView mCompany;

    @BindView(R.id.gcorder_new_clubname)
    EditText mClubName;

    @BindView(R.id.gcorder_new_responseperson)
    EditText mResponsePerson;

    @BindView(R.id.gcorder_new_tel)
    EditText mTel;

    @BindView(R.id.gcorder_new_addr)
    EditText mAddr;

    @BindView(R.id.gcorder_new_submit)
    Button submitBtn;

    @OnClick(R.id.gcorder_new_company)
    void click() {
        Intent intent = new Intent(this, ContactPersonSelectActivity.class);
        startActivityForResult(intent, REQUEST_CODE_Contact);
    }

    @OnClick(R.id.gcorder_new_submit)
    void submit() {
        if (valid()) {
            submitBtn.setEnabled(false);
            showProgress();
            Submit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gc_new_activity);
        ButterKnife.bind(this);

        textHeadTitle.setText("新增电竞馆");
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        init();
    }

    public void init() {
        final LayoutInflater mInflater = LayoutInflater.from(this);

        CompanyID = 0;
        CompanyName = "";

        ListCurrency = new ArrayList<String>();
        ListCurrency.add("人民币");
        ListCurrency.add("美元");
        ListCurrency.add("港币");

        mCurrency.setAdapter(new TagAdapter<String>(ListCurrency) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.tag_tv,
                        mCurrency, false);
                tv.setText(s);
                return tv;
            }
        });

        ListDirect = new ArrayList<String>();
        ListDirect.add("否");
        ListDirect.add("是");

        mDirect.setAdapter(new TagAdapter<String>(ListDirect) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.tag_tv,
                        mDirect, false);
                tv.setText(s);
                return tv;
            }
        });

    }

    public boolean valid() {
        if (mCurrency.getSelectedList().size() == 0) {
            showToast("请选择币种.");
            return false;
        }
        if (mDirect.getSelectedList().size() == 0) {
            showToast("请选择订单类型.");
            return false;
        }
        if (mClubName.getText().toString().length() == 0) {
            showToast("请输入馆名.");
            return false;
        }
        if (mResponsePerson.getText().toString().length() == 0) {
            showToast("请输入负责人姓名.");
            return false;
        }
        if (mTel.getText().toString().length() == 0) {
            showToast("请输入负责人电话.");
            return false;
        }
        if (mAddr.getText().toString().length() == 0) {
            showToast("请输入馆地址.");
            return false;
        }
        int direct = (int) mDirect.getSelectedList().toArray()[0] + 1;
        if (direct == 1 && CompanyID == 0) {
            showToast("请选择运营商");
            return false;
        }
        return true;
    }

    public void Submit() {
        //收起键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                    0);
        }

        String currencyName = ListCurrency.get((int) mCurrency.getSelectedList().toArray()[0]);
        int currency = getCurrency(currencyName);
        int direct = (int) mDirect.getSelectedList().toArray()[0] + 1;

        OkGo.<String>post(HttpUrl.Url.GbOrderSave)
                .params("clubname", mClubName.getText().toString().trim())
                .params("customer", CompanyID)
                .params("customername", CompanyName)
                .params("responseperson", mResponsePerson.getText().toString().trim())
                .params("responsepersonphone", mTel.getText().toString().trim())
                .params("addr", mAddr.getText().toString().trim())
                .params("currency", currency)
                .params("currencyname", currencyName)
                .params("direct", direct)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progressDimss();
                        Gson gson = new Gson();
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            showToast(result.getMsg());
                            finish();
                        } else {
                            showToast(result.getMsg());
                            submitBtn.setEnabled(true);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == REQUEST_CODE_Contact) {
            Bundle bundle = data.getExtras();
            ContactPerson contact = (ContactPerson) bundle.get("contact");
            if (contact.getCompanyID() > 0) {
                CompanyID = contact.getCompanyID();
                CompanyName = contact.getCompanyName();
                mCompany.setText(contact.getCompanyName());
            } else {
                showToast("该联系人未绑定到公司");
            }
        }
    }

    private int getCurrency(String bz) {
        switch (bz) {
            case "人民币":
                return 1000;
            case "美元":
                return 2000;
            case "港币":
                return 3000;
            default:
                return 1000;
        }
    }

}
