package com.wxk.erbiao;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {
    private ClearEditText mUserName;
    private ClearEditText mPassWord;
    private Button mBtnLogin;
    private long exitTime;
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
        initToolbar("", R.id.toolbar);
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

    /**
     * 如果两秒内按了两次返回键则退出程序,否则不会
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            if (System.currentTimeMillis() - exitTime > 2000){
                Toast.makeText(LoginActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            }else {
                finish();
            }
            exitTime = System.currentTimeMillis();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
