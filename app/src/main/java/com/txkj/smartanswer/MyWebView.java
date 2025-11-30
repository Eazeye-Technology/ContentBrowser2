package com.txkj.smartanswer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class MyWebView extends WebView {
	private boolean _mDisable = false;
	public void setTouchDisable(boolean isDisable) {
		this._mDisable = isDisable;
	}
	
	public MyWebView(Context context) {
		super(context);
	}
	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public MyWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public MyWebView(Context context, AttributeSet attrs, int defStyle,
			boolean privateBrowsing) {
		super(context, attrs, defStyle, privateBrowsing);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (_mDisable) {
			return true;
		}
		return super.onTouchEvent(event);
	}
}
