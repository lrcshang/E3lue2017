package com.e3lue.us.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONArray;
import com.e3lue.us.R;
import com.e3lue.us.adapter.ContactPersonAdapter;
import com.e3lue.us.event.OnRecyclerViewItemTouchListener;
import com.e3lue.us.model.CheckIn;
import com.e3lue.us.model.ContactPerson;
import com.e3lue.us.model.HttpUrl;
import com.e3lue.us.model.JsonResult;
import com.e3lue.us.ui.UIHelper;
import com.e3lue.us.ui.view.PinnedHeaderDecoration;
import com.e3lue.us.ui.view.SearchEditText;
import com.e3lue.us.ui.waveside.LetterComparator;
import com.e3lue.us.utils.PinYinKit;
import com.e3lue.us.utils.PinyinComparator;
import com.gjiazhe.wavesidebar.WaveSideBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Leo on 2017/4/11.
 */

public class TongXunLuFragment extends Fragment {
    static final int GB_SP_DIFF = 160;
    // 存放国标一级汉字不同读音的起始区位码
    static final int[] secPosValueList = {1601, 1637, 1833, 2078, 2274, 2302,
            2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
            4086, 4390, 4558, 4684, 4925, 5249, 5600};
    // 存放国标一级汉字不同读音的起始区位码对应读音
    static final char[] firstLetter = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',
            'y', 'z'};
    private Activity context;
    private List<ContactPerson> mContactModels;
    private List<ContactPerson> mShowModels;
    private ContactPersonAdapter mAdapter;
    Gson gson;

    @BindView(R.id.contactperson_recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.contactperson_side_bar)
    WaveSideBar mWaveSideBarView;
    public PinyinComparator comparator = new PinyinComparator();
    @BindView(R.id.contactperson_search)
    SearchEditText mSearchEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contactperson_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initData();
    }

    private void initData() {
        mContactModels = new ArrayList<>();
        mShowModels = new ArrayList<>();
        gson = new Gson();

        bindView();
        OkGo.<String>post(HttpUrl.Url.ContactPersonList)
                .cacheKey("tongxunlu")
                .cacheTime(3000)
                .cacheMode(CacheMode.IF_NONE_CACHE_REQUEST)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            okData(result.getData().toString());

                        }
                    }

                    @Override
                    public void onCacheSuccess(Response<String> response) {
                        JsonResult result = gson.fromJson(response.body(), JsonResult.class);
                        if (result.getRet() == 0) {
                            okData(result.getData().toString());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {

                    }
                });
    }

    public void okData(String data) {
        Type listType = new TypeToken<LinkedList<ContactPerson>>() {
        }.getType();
        List<ContactPerson> list = gson.fromJson(data, listType);
        if (list == null) list = new ArrayList<ContactPerson>();
        try {
            list = filledData(list);
//            list.addAll(list);
//            list.addAll(list);
        } catch (BadHanyuPinyinOutputFormatCombination e1) {
            e1.printStackTrace();
        }
        Collections.sort(list, comparator);
        mContactModels.addAll(list);
        mShowModels.addAll(list);
        mAdapter.setContacts(mShowModels);
    }

    private List<ContactPerson> filledData(List<ContactPerson> list) throws BadHanyuPinyinOutputFormatCombination {
        for (int i = 0; i < list.size(); i++) {
            //汉字转换成拼音
            ContactPerson contact = list.get(i);
            String pinyin = PinYinKit.getPingYin(contact.getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                list.get(i).setIndex(sortString.toUpperCase());
            } else {
                list.get(i).setIndex("#");
            }

        }
        return list;
    }

    private void bindView() {

        mAdapter = new ContactPersonAdapter(getActivity(),mShowModels,0);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        final PinnedHeaderDecoration decoration = new PinnedHeaderDecoration();
        decoration.registerTypePinnedHeader(1, new PinnedHeaderDecoration.PinnedHeaderCreator() {
            @Override
            public boolean create(RecyclerView parent, int adapterPosition) {
                return true;
            }
        });
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.addOnItemTouchListener(new OnRecyclerViewItemTouchListener(mRecyclerView) {
//            @Override
//            protected void onItemClick(RecyclerView.ViewHolder vh) {
//                ContactPerson cp = mShowModels.get(vh.getLayoutPosition());
//                UIHelper.showContactPersonDetail(getActivity(), cp.getID());
//            }
//
//            @Override
//            protected void onItemLongClick(RecyclerView.ViewHolder vh) {
//                ContactPerson cp = mShowModels.get(vh.getLayoutPosition());
//                if (cp.getMobile() != null) {
//                    Intent myInt = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + cp.getMobile()));
//                    startActivity(myInt);
//                }
//            }
//        });

        // 侧边设置相关
        mWaveSideBarView.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(String letter) {
                for (int i = 0; i < mContactModels.size(); i++) {
                    if (mContactModels.get(i).getIndex().equals(letter)) {
                        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }
        });


        // 搜索按钮相关
        mSearchEditText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence str, int arg1, int arg2, int arg3) {
                try {
                    filerData(str.toString());
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    public static String getSpells(String characters) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < characters.length(); i++) {

            char ch = characters.charAt(i);
            if ((ch >> 7) == 0) {
                // 判断是否为汉字，如果左移7为为0就不是汉字，否则是汉字
            } else {
                char spell = getFirstLetter(ch);
                buffer.append(String.valueOf(spell));
            }
        }
        return buffer.toString();
    }

    // 获取一个汉字的首字母
    public static Character getFirstLetter(char ch) {

        byte[] uniCode = null;
        try {
            uniCode = String.valueOf(ch).getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        if (uniCode[0] < 128 && uniCode[0] > 0) { // 非汉字
            return null;
        } else {
            return convert(uniCode);
        }
    }

    /**
     * 获取一个汉字的拼音首字母。 GB码两个字节分别减去160，转换成10进制码组合就可以得到区位码
     * 例如汉字“你”的GB码是0xC4/0xE3，分别减去0xA0（160）就是0x24/0x43
     * 0x24转成10进制就是36，0x43是67，那么它的区位码就是3667，在对照表中读音为‘n’
     */
    static char convert(byte[] bytes) {
        char result = '-';
        int secPosValue = 0;
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] -= GB_SP_DIFF;
        }
        secPosValue = bytes[0] * 100 + bytes[1];
        for (i = 0; i < 23; i++) {
            if (secPosValue >= secPosValueList[i]
                    && secPosValue < secPosValueList[i + 1]) {
                result = firstLetter[i];
                break;
            }
        }
        return result;
    }

    private void filerData(String str) throws BadHanyuPinyinOutputFormatCombination {
        List<ContactPerson> fSortModels = new ArrayList<>();

        if (TextUtils.isEmpty(str))
            fSortModels = mContactModels;
        else {
            fSortModels.clear();
            for (ContactPerson sortModel : mContactModels) {
                String name = sortModel.getName();
                if (name.indexOf(str.toString()) != -1 ||
                        PinYinKit.getPingYin(name).startsWith(str.toString()) || PinYinKit.getPingYin(name).startsWith(str.toUpperCase().toString()) || getSpells(name).contains(str)) {
                    fSortModels.add(sortModel);
                }
            }

        }
        Collections.sort(fSortModels, comparator);
        mAdapter.setContacts(fSortModels);
    }


}
