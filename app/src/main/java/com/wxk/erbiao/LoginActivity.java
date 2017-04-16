package com.wxk.erbiao;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {
    private ClearEditText mUserName;
    private ClearEditText mPassWord;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindViews();
        setListener();
        init();
    }

    @Override
    protected void bindViews() {
        mUserName = (ClearEditText) findViewById(R.id.editText_userName);
        mPassWord = (ClearEditText) findViewById(R.id.editText_pwd);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
    }

    @Override
    protected void setListener() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
    }

    @Override
    protected void init() {

    }

    /**
     * 账号: admin
     * 密码: 123
     */
    private void handleLogin() {
        if (mUserName.getText().toString().equals("admin") && mPassWord.getText().toString().equals("123")) {
            showCustomToast(R.mipmap.toast_done_icon, "登陆成功!", Toast.LENGTH_SHORT);
            startActivityByClassName(MainActivity.class);
            finish();
        }
    }
}
