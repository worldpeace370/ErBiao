package com.wxk.erbiao;

import android.os.Bundle;

import com.tomer.fadingtextview.FadingTextView;

public class GasControlActivity extends BaseActivity {

    private FadingTextView mGasBashRoom;
    private FadingTextView mGasChicken;
    private FadingTextView mGasLivingRoom;
    private FadingTextView mGasBedRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_control);
        bindViews();
        setListener();
        init();
    }

    @Override
    protected void bindViews() {
        mGasBashRoom = (FadingTextView) findViewById(R.id.gas_bash_room);
        mGasChicken = (FadingTextView) findViewById(R.id.gas_chicken);
        mGasLivingRoom = (FadingTextView) findViewById(R.id.gas_living_room);
        mGasBedRoom = (FadingTextView) findViewById(R.id.gas_bed_room);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void init() {
        String[] texts = {"23","24","25"};
        mGasBashRoom.setTexts(texts);
        mGasBashRoom.setTimeout(1, FadingTextView.SECONDS);
        mGasChicken.setTexts(texts);
        mGasChicken.setTimeout(1, FadingTextView.SECONDS);
        mGasLivingRoom.setTexts(texts);
        mGasLivingRoom.setTimeout(1, FadingTextView.SECONDS);
        mGasBedRoom.setTexts(texts);
        mGasBedRoom.setTimeout(1, FadingTextView.SECONDS);
    }
}
