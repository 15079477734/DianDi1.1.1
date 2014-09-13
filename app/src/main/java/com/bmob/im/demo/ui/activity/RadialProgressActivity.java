package com.bmob.im.demo.ui.activity;

import android.os.Bundle;
import android.view.WindowManager;

import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.Plan;
import com.bmob.im.demo.db.DBUtils;
import com.bmob.im.demo.view.HeaderLayout;
import com.touchmenotapps.widget.radialmenu.progress.widget.RadialProgressWidget;
import com.touchmenotapps.widget.radialmenu.progress.widget.RadialProgressWidget.OnRadialViewValueChanged;

public class RadialProgressActivity extends BaseActivity {

    private RadialProgressWidget mView;
    private int mPlanId;
    private Plan mPlan;
    private DBUtils mDBUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        initView();
    }

    @Override
    void findView() {
        setContentView(R.layout.activity_radial_progress);
        mView = (RadialProgressWidget) findViewById(R.id.radial_view);
    }

    @Override
    void initView() {
        initTopBarForBoth("进度", R.drawable.base_action_bar_true_bg_selector, new HeaderLayout.onRightImageButtonClickListener() {
            @Override
            public void onClick() {
                mView.getCurrentValue();
                mPlan.setProgress(mView.getCurrentValue());
                mDBUtils.createPlan(mPlan);
                finish();
            }
        });
        mDBUtils = new DBUtils(this);
        mPlanId = getIntent().getIntExtra(Plan.PLAN_ID, 0);
        mPlan = mDBUtils.getPlanById(mPlanId);
        mView.setSecondaryText("进度");
        //Use this to switch between static progress view and an interactive one
        //mView.setTouchEnabled(false);
        bindEvent();
    }



    @Override
    void bindEvent() {
        mView.setOnRadialViewValueChanged(new OnRadialViewValueChanged() {
            @Override
            public void onValueChanged(int value) {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.screenBrightness = value / 100.0f;
                //getWindow().setAttributes(lp);
                if(value==99)
                {
                    mView.setCurrentValue(100);
                }
            }
        });

        if ((int) (getWindow().getAttributes().screenBrightness * 100) < 0)
            mView.setCurrentValue(mPlan.getProgress());
        else
            mView.setCurrentValue((int) (getWindow().getAttributes().screenBrightness * 100));

    }
}
