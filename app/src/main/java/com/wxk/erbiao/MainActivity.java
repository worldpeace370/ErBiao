package com.wxk.erbiao;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout mLedControl;
    private LinearLayout mTempControl;
    private LinearLayout mLightControl;
    private LinearLayout mSecurityControl;
    private LinearLayout mGasControl;
    private LinearLayout mVideoControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        setListener();
        init();
    }


    @Override
    protected void bindViews() {
        mLedControl = (LinearLayout) findViewById(R.id.led_control);
        mTempControl = (LinearLayout) findViewById(R.id.temp_control);
        mLightControl = (LinearLayout) findViewById(R.id.light_control);
        mSecurityControl = (LinearLayout) findViewById(R.id.security_control);
        mGasControl = (LinearLayout) findViewById(R.id.gas_control);
        mVideoControl = (LinearLayout) findViewById(R.id.video_control);
    }

    @Override
    protected void setListener() {
        mLedControl.setOnClickListener(this);
        mTempControl.setOnClickListener(this);
        mLightControl.setOnClickListener(this);
        mSecurityControl.setOnClickListener(this);
        mGasControl.setOnClickListener(this);
        mVideoControl.setOnClickListener(this);
    }

    @Override
    protected void init() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.led_control:
                startActivityByClassName(LedControlActivity.class);
                break;
            case R.id.temp_control:

                break;
            case R.id.light_control:

                break;
            case R.id.security_control:

                break;
            case R.id.gas_control:

                break;
            case R.id.video_control:

                break;
            default:
                break;
        }
    }
}
