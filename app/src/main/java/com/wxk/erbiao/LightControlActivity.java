package com.wxk.erbiao;

import android.os.Bundle;

import com.tomer.fadingtextview.FadingTextView;

public class LightControlActivity extends BaseActivity {

    private FadingTextView mLightBashRoom;
    private FadingTextView mLightChicken;
    private FadingTextView mLightLivingRoom;
    private FadingTextView mLightBedRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_control);
        bindViews();
        setListener();
        init();
    }

    @Override
    protected void bindViews() {
        mLightBashRoom = ((FadingTextView) findViewById(R.id.light_bash_room));
        mLightChicken = ((FadingTextView) findViewById(R.id.light_chicken));
        mLightLivingRoom = ((FadingTextView) findViewById(R.id.light_living_room));
        mLightBedRoom = ((FadingTextView) findViewById(R.id.light_bed_room));
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {
        String[] texts = {"23","24","25"};
        mLightBashRoom.setTexts(texts);
        mLightBashRoom.setTimeout(1, FadingTextView.SECONDS);
        mLightChicken.setTexts(texts);
        mLightChicken.setTimeout(1, FadingTextView.SECONDS);
        mLightLivingRoom.setTexts(texts);
        mLightLivingRoom.setTimeout(1, FadingTextView.SECONDS);
        mLightBedRoom.setTexts(texts);
        mLightBedRoom.setTimeout(1, FadingTextView.SECONDS);
    }
}
