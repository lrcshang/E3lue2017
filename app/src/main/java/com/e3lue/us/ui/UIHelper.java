package com.e3lue.us.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.e3lue.us.activity.ChangePassWordActivity;
import com.e3lue.us.activity.CheckInDetailActivity;
import com.e3lue.us.activity.CheckInListActivity;
import com.e3lue.us.activity.CheckInNewActivity;
import com.e3lue.us.activity.ContactPersonAddActivity;
import com.e3lue.us.activity.ContactPersonDetailActivity;
import com.e3lue.us.activity.DiaryDayActivity;
import com.e3lue.us.activity.DiaryDetailActivity;
import com.e3lue.us.activity.DiaryListActivity;
import com.e3lue.us.activity.DiaryManaActivity;
import com.e3lue.us.activity.ExpensesDetailActivity;
import com.e3lue.us.activity.ExpensesMainActivity;
import com.e3lue.us.activity.ExpensesNewActivity;
import com.e3lue.us.activity.FileShareActivity;
import com.e3lue.us.activity.GameClubListActivity;
import com.e3lue.us.activity.GameClubManaActivity;
import com.e3lue.us.activity.GameClubMapActivity;
import com.e3lue.us.activity.GameClubNewActivity;
import com.e3lue.us.activity.GameClubOrderDetailActivity;
import com.e3lue.us.activity.GmMailBoxNewActivity;
import com.e3lue.us.activity.LoginActivity;
import com.e3lue.us.activity.MainActivity;
import com.e3lue.us.activity.SettingActivity;
import com.e3lue.us.activity.SuggestionDetailActivity;
import com.e3lue.us.activity.SuggestionListActivity;
import com.e3lue.us.activity.SuggestionsNewActivity;
import com.e3lue.us.activity.SystemMessageActivity;
import com.e3lue.us.activity.WebViewerActivity;
import com.e3lue.us.model.CheckIn;
import com.e3lue.us.model.Diary;
import com.e3lue.us.model.Expenses;
import com.e3lue.us.model.GameClubOrder;
import com.e3lue.us.model.Suggestions;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 */
public class UIHelper {

    public final static String TAG = "UIHelper";

    public final static int RESULT_OK = 0x00;
    public final static int REQUEST_CODE = 0x01;

    public static void ToastMessage(Context cont, String msg) {
        if (cont == null || msg == null) {
            return;
        }
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, int msg) {
        if (cont == null || msg <= 0) {
            return;
        }
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, String msg, int time) {
        if (cont == null || msg == null) {
            return;
        }
        Toast.makeText(cont, msg, time).show();
    }

    public static void showHome(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void showLogin(Activity context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void showCheckInNew(Activity context) {
        Intent intent = new Intent(context, CheckInNewActivity.class);
        context.startActivity(intent);
    }

    public static void showCheckInList(Activity context) {
        Intent intent = new Intent(context, CheckInListActivity.class);
        context.startActivity(intent);
    }

    public static void showCheckInDetail(Activity context, CheckIn entity) {
        Intent intent = new Intent(context, CheckInDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("entity", entity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void showContactPersonAdd(Activity context) {
        Intent intent = new Intent(context, ContactPersonAddActivity.class);
        context.startActivity(intent);
    }

    public static void showContactPersonDetail(Context context, int CpID) {
        Intent intent = new Intent(context, ContactPersonDetailActivity.class);
        intent.putExtra("cpid", CpID);
        context.startActivity(intent);
    }

    public static void showExpensesNew(Activity context) {
        Intent intent = new Intent(context, ExpensesNewActivity.class);
        context.startActivity(intent);
    }

    public static void showExpensesMain(Activity context) {
        Intent intent = new Intent(context, ExpensesMainActivity.class);
        context.startActivity(intent);
    }

    public static void showExpensesDetail(Activity context, Expenses entity) {
        Intent intent = new Intent(context, ExpensesDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("entity", entity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void showGmMailBoxNew(Activity context) {
        Intent intent = new Intent(context, GmMailBoxNewActivity.class);
        context.startActivity(intent);
    }

    public static void showSuggestionsNew(Activity context) {
        Intent intent = new Intent(context, SuggestionsNewActivity.class);
        context.startActivity(intent);
    }

    public static void showSuggestionList(Activity context) {
        Intent intent = new Intent(context, SuggestionListActivity.class);
        context.startActivity(intent);
    }

    public static void showSuggestionDetail(Activity context, Suggestions entity) {
        Intent intent = new Intent(context, SuggestionDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("entity", entity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void showDiaryMana(Activity context) {
        Intent intent = new Intent(context, DiaryManaActivity.class);
        context.startActivity(intent);
    }

    public static void showDiaryList(Activity context) {
        Intent intent = new Intent(context, DiaryListActivity.class);
        context.startActivity(intent);
    }

    public static void showDiaryDay(Activity context, int val,String date,int user) {
        Intent intent = new Intent(context, DiaryDayActivity.class);
        intent.putExtra("isnew",val);
        intent.putExtra("date",date);
        intent.putExtra("user",user);
        context.startActivity(intent);
    }

    public static void showDiaryDetail(Activity context, Diary entity) {
        Intent intent = new Intent(context, DiaryDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("entity", entity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void showGcList(Activity context) {
        Intent intent = new Intent(context, GameClubListActivity.class);
        context.startActivity(intent);
    }

    public static void showGcMana(Activity context) {
        Intent intent = new Intent(context, GameClubManaActivity.class);
        context.startActivity(intent);
    }

    public static void showGcNew(Activity context) {
        Intent intent = new Intent(context, GameClubNewActivity.class);
        context.startActivity(intent);
    }

    public static void showGcOrderDetail(Activity context,GameClubOrder entity) {
        Intent intent = new Intent(context, GameClubOrderDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("entity", entity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void showSetting(Activity context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    public static void showChangePw(Activity context) {
        Intent intent = new Intent(context, ChangePassWordActivity.class);
        context.startActivity(intent);
    }

    public static void showWebViewer(Activity context,String title,String url) {
        Intent intent = new Intent(context, WebViewerActivity.class);
        intent.putExtra("Url",url);
        intent.putExtra("Title",title);
        context.startActivity(intent);
    }

    public static void showGameClubMap(Activity context) {
        Intent intent = new Intent(context, GameClubMapActivity.class);
        context.startActivity(intent);
    }

    public static void showSysMsg(Activity context) {
        Intent intent = new Intent(context, SystemMessageActivity.class);
        context.startActivity(intent);
    }

    public static void showFileDown(Activity context) {
        Intent intent = new Intent(context, FileShareActivity.class);
        context.startActivity(intent);
    }

}
