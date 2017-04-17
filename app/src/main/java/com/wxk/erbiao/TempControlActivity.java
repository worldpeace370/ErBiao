package com.wxk.erbiao;

import android.os.Bundle;

import com.tomer.fadingtextview.FadingTextView;

public class TempControlActivity extends BaseActivity {

    private FadingTextView mTempBashRoom;
    private FadingTextView mTempChicken;
    private FadingTextView mTempLivingRoom;
    private FadingTextView mTempBedRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_control);
        bindViews();
        setListener();
        init();
    }


    @Override
    protected void bindViews() {
        mTempBashRoom = (FadingTextView) findViewById(R.id.temp_bash_room);
        mTempChicken = (FadingTextView) findViewById(R.id.temp_chicken);
        mTempLivingRoom = (FadingTextView) findViewById(R.id.temp_living_room);
        mTempBedRoom = (FadingTextView) findViewById(R.id.temp_bed_room);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {
        String[] texts = {"23","24","25"};
        mTempBashRoom.setTexts(texts);
        mTempBashRoom.setTimeout(1, FadingTextView.SECONDS);
        mTempChicken.setTexts(texts);
        mTempChicken.setTimeout(1, FadingTextView.SECONDS);
        mTempLivingRoom.setTexts(texts);
        mTempLivingRoom.setTimeout(1, FadingTextView.SECONDS);
        mTempBedRoom.setTexts(texts);
        mTempBedRoom.setTimeout(1, FadingTextView.SECONDS);
    }
}
