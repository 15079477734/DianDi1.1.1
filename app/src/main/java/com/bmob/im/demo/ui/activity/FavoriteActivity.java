package com.bmob.im.demo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.AIContentAdapter;
import com.bmob.im.demo.adapter.base.BaseContentAdapter;
import com.bmob.im.demo.bean.DianDi;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.Constant;
import com.bmob.im.demo.ui.fragment.BaseFragment;
import com.bmob.im.demo.util.LogUtils;
import com.bmob.im.demo.view.ArcMenu;
import com.bmob.im.demo.view.HeaderLayout;
import com.bmob.im.demo.view.xlist.XListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

public class FavoriteActivity extends BaseActivity implements XListView.IXListViewListener, AdapterView.OnItemClickListener {
    private TextView networkTips;
    private XListView mListView;
    private ArrayList<DianDi> mListItems;
    private BaseContentAdapter<DianDi> mAdapter;
    private int pageNum;
    private String lastItemTime;
    private ProgressBar progressbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        initView();
        loadData();

    }

    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    void findView() {
        setContentView(R.layout.activity_favorite);
        mListView = (XListView) findViewById(R.id.fragment_favorite_list);
        networkTips = (TextView) findViewById(R.id.activity_favorite_networkTips);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    void initView() {
        pageNum = 0;
        initTopBarForBoth("点滴", R.drawable.ic_action_edit_selector, new HeaderLayout.onRightImageButtonClickListener() {
            @Override
            public void onClick() {
                startAnimActivity(NewDiandiActivity.class);
            }
        });
        mListItems = new ArrayList<DianDi>();
        initXListView();
        bindEvent();
    }

    private void initXListView() {
        mListView.setOnItemClickListener(this);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.pullRefreshing();
        mAdapter = new AIContentAdapter(this, mListItems);
        mListView.setAdapter(mAdapter);
    }


    @Override
    void bindEvent() {

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent();
        intent.setClass(this, CommentActivity.class);
        intent.putExtra("data", mListItems.get(position - 1));
        startActivity(intent);
    }

    public void loadData() {
        progressbar.setVisibility(View.VISIBLE);
        User user = BmobUser.getCurrentUser(mContext, User.class);
        BmobQuery<DianDi> query = new BmobQuery<DianDi>();
        query.addWhereRelatedTo("favorite", new BmobPointer(user));
        query.order("-createdAt");
        query.setLimit(Constant.NUMBERS_PER_PAGE);
        BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
        query.addWhereLessThan("createdAt", date);
        query.setSkip(Constant.NUMBERS_PER_PAGE * (pageNum++));
        query.include("author");
        query.findObjects(FavoriteActivity.this, new FindListener<DianDi>() {

            @Override
            public void onSuccess(List<DianDi> list) {
                // TODO Auto-generated method stub
                if (list.size() != 0 && list.get(list.size() - 1) != null) {
                    {
                        mListItems.clear();
                    }
                    if (list.size() < Constant.NUMBERS_PER_PAGE) {
                        ShowToast("已加载完所有数据~");
                    }
                    mListItems.addAll(list);
                    mAdapter.notifyDataSetChanged();

                } else {
                    ShowToast("暂无更多数据~");
                    if (list.size() == 0 && mListItems.size() == 0) {

                        networkTips.setText("暂无收藏。快去首页收藏几个把~");
                        pageNum--;

                        Log.i(TAG, "SIZE:" + list.size() + "ssssize" + mListItems.size());
                        return;
                    }
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
        lastItemTime = getCurrentTime();
        loadData();


    }

    @Override
    public void onLoadMore() {
        loadData();
    }

    private void refreshPull() {
        if (mListView.getPullRefreshing()) {
            mListView.stopRefresh();
            networkTips.setVisibility(View.INVISIBLE);
        }
        progressbar.setVisibility(View.GONE);
        networkTips.setVisibility(View.GONE);
    }

    private String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String times = formatter.format(new Date(System.currentTimeMillis()));
        return times;
    }
}
