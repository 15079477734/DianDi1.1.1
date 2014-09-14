package com.bmob.im.demo.ui.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.PlanAdapter;
import com.bmob.im.demo.bean.Plan;
import com.bmob.im.demo.db.DBUtils;
import com.bmob.im.demo.ui.activity.RadialProgressActivity;
import com.bmob.im.demo.ui.activity.WritePlanActivity;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.bmob.im.demo.view.dialog.ListDialog;

import java.util.ArrayList;


/**
 * ************************************************************
 * *********    User : SuLinger(462679107@qq.com) .
 * *********    Date : 2014-09-12  .
 * *********    Time:  2014-09-12  .
 * *********    Project name :PBOX1.3 .
 * *********    Copyright @ 2014, SuLinger, All Rights Reserved
 * *************************************************************
 */


public class PlanFragment extends BaseFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    DBUtils mDBUtils;
    private ListView mPlanListView;
    private View mView;
    private PlanAdapter mPlanAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        mView = layoutInflater.inflate(R.layout.activity_my_list, null);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findView();
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    public void onRefresh() {
        mPlanAdapter.setList(mDBUtils.getSortPlans());
    }

    @Override
    void findView() {
        mPlanListView = (ListView) mView.findViewById(android.R.id.list);
    }


    @Override
    void initView() {
        mDBUtils = new DBUtils(getActivity());
        mPlanAdapter = new PlanAdapter(getActivity(), mDBUtils.getSortPlans());
        mPlanListView.setAdapter(mPlanAdapter);
        bindEvent();
    }

    @Override
    void bindEvent() {
        mPlanListView.setOnItemClickListener(this);
        mPlanListView.setOnItemLongClickListener(this);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        final Plan plan = mPlanAdapter.getE(position);
        DialogTips dialog = new DialogTips(getActivity(), plan.getTitle(), "删除计划", "确定", true, true);
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDBUtils.deletePlan(plan);
                onRefresh();
            }
        });
        dialog.show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        final Plan plan = mPlanAdapter.getE(position);

        final ArrayList<String> list = new ArrayList<String>();
        list.add("设置进度");
        list.add("修改计划");
        final ListDialog listDialog = new ListDialog(getActivity(), "操作", list);
        listDialog.show();
        listDialog.setOnListItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Intent intent = new Intent(getActivity(), RadialProgressActivity.class);
                    intent.putExtra(Plan.PLAN_ID, plan.get_id());
                    startActivity(intent);
                    listDialog.dismiss();
                }
                if (i == 1) {
                    Intent intent = new Intent(getActivity(), WritePlanActivity.class);
                    intent.putExtra(Plan.PLAN_ID, plan.get_id());
                    startActivity(intent);
                    listDialog.dismiss();
                }
            }
        });
    }

}
