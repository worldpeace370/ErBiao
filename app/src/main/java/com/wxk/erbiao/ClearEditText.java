package com.wxk.erbiao;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * 当有输入的时候，右侧出现"清除icon";当点击的时候，清除当前EditText的输入信息
 * Created by wuxia on 2016/12/20.
 * Contacts by wuxiangkun2015@163.com
 */

public class ClearEditText extends EditText implements View.OnFocusChangeListener, TextWatcher {
    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;
    /**
     * 控件是否有焦点
     */
    private boolean mHasFocused;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle); // 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化相关属性
     */
    private void init() {
        mClearDrawable = getCompoundDrawables()[2]; // 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        if (mClearDrawable == null) {
            mClearDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.input_clean);
        }
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
                mClearDrawable.getIntrinsicHeight());
        setClearIconVisibility(false); // 默认设置隐藏图标
        setOnFocusChangeListener(this); // 设置焦点改变的监听
        addTextChangedListener(this); // 设置输入框里面内容发生改变的监听
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 -
     * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mHasFocused) {
            boolean isTouched = event.getX() > (getWidth() - getTotalPaddingRight())
                    && event.getX() < (getWidth() - getPaddingRight());
            if (isTouched) {
                setText("");
                final MotionEvent cancelEvent = MotionEvent.obtain(event);
                cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                post(new Runnable() {
                    @Override
                    public void run() {
                        dispatchTouchEvent(cancelEvent);
                        cancelEvent.recycle();
                    }
                });
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置"清除icon"是否可见
     *
     * @param isVisible true or false
     */
    private void setClearIconVisibility(boolean isVisible) {
        Drawable drawableRight = isVisible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1],
                drawableRight, getCompoundDrawables()[3]);
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        mHasFocused = hasFocus;
        if (hasFocus) {
            setClearIconVisibility(getText().length() > 0);
        } else {
            setClearIconVisibility(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (mHasFocused) {
            setClearIconVisibility(text.length() > 0);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
