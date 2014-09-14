package com.bmob.im.demo.ui.fragment;

/**
 * ************************************************************
 * *********    User : SuLinger(462679107@qq.com) .
 * *********    Date : 2014-08-29  .
 * *********    Time:  2014-08-29  .
 * *********    Project name :BmobChatDemo .
 * *********    Copyright @ 2014, SuLinger, All Rights Reserved
 * *************************************************************
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bmob.im.demo.CustomApplication;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.AIContentAdapter;
import com.bmob.im.demo.bean.DianDi;
import com.bmob.im.demo.config.Constant;
import com.bmob.im.demo.db.DatabaseUtil;
import com.bmob.im.demo.ui.activity.CommentActivity;
import com.bmob.im.demo.ui.activity.FavoriteActivity;
import com.bmob.im.demo.ui.activity.NewDiandiActivity;
import com.bmob.im.demo.ui.activity.PlanActivity;
import com.bmob.im.demo.ui.activity.WritePlanActivity;
import com.bmob.im.demo.util.LogUtils;
import com.bmob.im.demo.view.ArcMenu;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;
import com.bmob.im.demo.view.xlist.XListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;


public class DianDiFragment extends BaseFragment implements XListView.IXListViewListener, AdapterView.OnItemClickListener {
    private ArrayList<DianDi> mListItems;
    private AIContentAdapter mAdapter;
    private XListView mListView;
    private TextView networkTips;
    private ArcMenu mArcMenu;
    private int pageNum;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diandi, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findView();
        initView();
        loadData();
    }

    public void onResume() {
        super.onResume();
        onRefresh();
    }


    void findView() {
        mListView = (XListView) findViewById(R.id.fragment_diandi_list);
        networkTips = (TextView) findViewById(R.id.fragment_diandi_networktips);
        mArcMenu = (ArcMenu) findViewById(R.id.fragment_diandi_arcmenu);

    }

    public void initView() {
        mListItems = new ArrayList<DianDi>();
        pageNum = 0;
        initMenu();
        initTopBarForRight("点滴", R.drawable.ic_action_edit_selector, new onRightImageButtonClickListener() {
            @Override
            public void onClick() {
                startAnimActivity(NewDiandiActivity.class);
            }
        });
        initXListView();
        bindEvent();
    }

    private void initMenu() {

    }


    private void initXListView() {
        mListView.setOnItemClickListener(this);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.pullRefreshing();
        mAdapter = new AIContentAdapter(getActivity(), mListItems);
        mListView.setAdapter(mAdapter);
    }

    void bindEvent() {
        mArcMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                switch (view.getId()) {
                    case R.id.fragment_diandi_writeplan_img:
                        startAnimActivity(WritePlanActivity.class);
                        break;
                    case R.id.fragment_diandi_seeplan_img:
                        startAnimActivity(PlanActivity.class);
                        break;
                    case R.id.fragment_diandi_love_img:
                        startAnimActivity(FavoriteActivity.class);
                        break;
                }
            }
        });


    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), CommentActivity.class);
        intent.putExtra("data", mListItems.get(position - 1));
        startActivity(intent);
    }

    public void loadData() {
        BmobQuery<DianDi> query = new BmobQuery<DianDi>();
        query.order("-createdAt");
        query.setLimit(Constant.NUMBERS_PER_PAGE);
        BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
        query.addWhereLessThan("createdAt", date);
        LogUtils.i(TAG, "SIZE:" + Constant.NUMBERS_PER_PAGE * pageNum);
        query.setSkip(Constant.NUMBERS_PER_PAGE * (pageNum++));
        LogUtils.i(TAG, "SIZE:" + Constant.NUMBERS_PER_PAGE * pageNum);
        query.include("author");
        query.findObjects(getActivity(), new FindListener<DianDi>() {

            @Override
            public void onSuccess(List<DianDi> list) {
                LogUtils.i(TAG, "find success." + list.size());
                if (list.size() != 0 && list.get(list.size() - 1) != null) {
                    mListItems.clear();
                    if (list.size() < Constant.NUMBERS_PER_PAGE) {
                        LogUtils.i(TAG, "已加载完所有数据~");
                    }
                    if (CustomApplication.getInstance().getCurrentUser() != null) {
                        list = DatabaseUtil.getInstance(getActivity()).setFav(list);
                    }
                    mListItems.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    networkTips.setVisibility(View.INVISIBLE);

                } else {
                    ShowToast("暂无更多数据~");
                    pageNum--;
                }
                refreshPull();
            }

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                LogUtils.i(TAG, "find failed." + arg1);
                pageNum--;
                refreshPull();
            }

        });
    }

    @Override
    public void onRefresh() {
        pageNum = 0;
        String lastItemTime = getCurrentTime();
        loadData();
    }

    private void refreshLoad() {
        if (mListView.getPullLoading()) {
            mListView.stopLoadMore();
        }
    }

    private void refreshPull() {
        if (mListView.getPullRefreshing()) {
            mListView.stopRefresh();
            networkTips.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoadMore() {
        loadData();
    }

    private String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String times = formatter.format(new Date(System.currentTimeMillis()));
        return times;
    }
}

