package com.e3lue.us.ui.view;



import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.e3lue.us.R;

public class SearchEditText extends EditText implements  OnFocusChangeListener, TextWatcher
{
	// the delete button
    private Drawable mClearDrawable; 
 
    public SearchEditText(Context context) 
    { 
    	this(context, null); 
    } 
 
    public SearchEditText(Context context, AttributeSet attrs) 
    { 
    	this(context, attrs, android.R.attr.editTextStyle); 
    } 
    
    public SearchEditText(Context context, AttributeSet attrs, int defStyle) 
    {
        super(context, attrs, defStyle);
        init();
    }
    
    
    private void init() 
    { 
    	
    	mClearDrawable = getCompoundDrawables()[2]; 
        if (mClearDrawable == null) { 
        	mClearDrawable = getResources() 
                    .getDrawable(R.drawable.btn_tips_close);
        } 
        mClearDrawable.setBounds(-20, 0, mClearDrawable.getIntrinsicWidth()-20, mClearDrawable.getIntrinsicHeight()); 
        setClearIconVisible(false); 
        setOnFocusChangeListener(this); 
        addTextChangedListener(this); 
    } 
 
 
    /**
     * ��Ϊ���ǲ���ֱ�Ӹ�EditText���õ���¼������������ü�ס���ǰ��µ�λ����ģ�����¼�
     * �����ǰ��µ�λ�� ��  EditText�Ŀ�� - ͼ�굽�ؼ��ұߵļ�� - ͼ��Ŀ��  ��
     * EditText�Ŀ�� - ͼ�굽�ؼ��ұߵļ��֮�����Ǿ�������ͼ�꣬��ֱ����û�п���
     */
    @Override 
    public boolean onTouchEvent(MotionEvent event) { 
        if (getCompoundDrawables()[2] != null) { 
            if (event.getAction() == MotionEvent.ACTION_UP) { 
            	boolean touchable = event.getX() > (getWidth() 
                        - getPaddingRight() - mClearDrawable.getIntrinsicWidth()) 
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) { 
                    this.setText(""); 
                } 
            } 
        } 
 
        return super.onTouchEvent(event); 
    } 
 
    /**
     * ��ClearEditText���㷢���仯��ʱ���ж������ַ��������������ͼ�����ʾ������
     */
    public void onFocusChange(View v, boolean hasFocus) { 
        if (hasFocus) { 
            setClearIconVisible(getText().length() > 0); 
        } else { 
            setClearIconVisible(false); 
        } 
    } 
 
 
    /**
     * �������ͼ�����ʾ�����أ�����setCompoundDrawablesΪEditText������ȥ
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) { 
        Drawable right = visible ? mClearDrawable : null; 
        setCompoundDrawables(getCompoundDrawables()[0], 
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]); 
    } 
     
    
    /**
     * ��������������ݷ����仯��ʱ��ص��ķ���
     */
    @Override 
    public void onTextChanged(CharSequence s, int start, int count, 
            int after) { 
        setClearIconVisible(s.length() > 0); 
    } 
 
    public void beforeTextChanged(CharSequence s, int start, int count, 
            int after) { 
         
    } 
 
    public void afterTextChanged(Editable s) { 
         
    } 
    
   
    /**
     * ���ûζ�����
     */
    public void setShakeAnimation(){
    	this.setAnimation(shakeAnimation(5));
    }
    
    
    /**
     * �ζ�����
     * @param counts 1���ӻζ�������
     * @return
     */
    public static Animation shakeAnimation(int counts){
    	Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
    	translateAnimation.setInterpolator(new CycleInterpolator(counts));
    	translateAnimation.setDuration(1000);
    	return translateAnimation;
    }
}
