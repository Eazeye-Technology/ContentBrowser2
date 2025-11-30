package com.tvg;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.txkj.contentbrowser2.R;

/**
 * see http://www.cnblogs.com/slider/archive/2011/11/24/2262161.html
 * @author Administrator
 *
 */
public class AutoWrapViewGroup extends ViewGroup {
	private final static boolean D = false;
	private final static String TAG = "AutoWrapViewGroup";

	private final static int VIEW_MARGIN = 2;
	private int lengthX2;
	private int lengthY2;
	
	public AutoWrapViewGroup(Context context) {
		super(context);
	}

	public AutoWrapViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	private OnItemClickListener mOnItemClickListener;
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		for (int index = 0; index < getChildCount(); index++) {
			final View child = getChildAt(index);
			child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		}
		
		if (D) {
			Log.d(TAG, "onMeasure width = " + width);
		}
		int minHeight = getMH(width);
		if (D) {
			Log.d(TAG, "onMeasure minHeight = " + minHeight);
		}
		
		width = Math.max(width, getSuggestedMinimumWidth());
		height = Math.max(height, getSuggestedMinimumHeight());
		
		this.setMeasuredDimension(resolveSize(width, widthMeasureSpec), 
			resolveSize(minHeight, heightMeasureSpec));
	}
	
	private int getMH(int maxWidth) {
		int row = 0;
		int lengthX = 0;
		int lengthY = 0;
		final int count = getChildCount();
//		if (D) {
//			Log.d(TAG, "getMH getChildCount = " + count);
//		}
		int w = 0, h = 0;
        Resources res = this.getResources();
        for (int i = 0; i < count; i++) {
			final View child = this.getChildAt(i);
			if ((child instanceof ViewGroup && ((ViewGroup) child).getChildCount() == 0)) {
				lengthX = VIEW_MARGIN;
				row++;
				lengthY = row * (h + VIEW_MARGIN) + VIEW_MARGIN + h;
//				if (D) {
//					Log.d(TAG, "getMH lengthY = " + lengthY);
//				}
			} else {
				w = child.getMeasuredWidth();
				//h = res.getDimensionPixelSize(R.dimen.tinyTextSize);//child.getMeasuredHeight();
                h = child.getMeasuredHeight();
				lengthX += w + VIEW_MARGIN;
				lengthY = row * (h + VIEW_MARGIN) + VIEW_MARGIN + h;
				if (lengthX > maxWidth) {
					lengthX = w + VIEW_MARGIN;
					row++;
					lengthY = row * (h + VIEW_MARGIN) + VIEW_MARGIN + h;
				}
//				if (D) {
//					Log.d(TAG, "getMH lengthY = " + lengthY);
//				}
			}
		}
		return lengthY;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (D) {
			Log.d(TAG, "changed = " + changed + " left = " + l + " top = " + t
				+ " right = " + r + " botom = " + b);
		}
		final int count = getChildCount();
		int row = 0;// which row lay you view relative to parent
		int lengthX = l; // right position of child relative to parent
		int lengthY = t; // bottom position of child relative to parent
		int w = 0, h = 0;
        for (int i = 0; i < count; i++) {
			final View child = this.getChildAt(i);
			if ((child instanceof ViewGroup && ((ViewGroup) child).getChildCount() == 0)) {
				lengthX = VIEW_MARGIN + l;
				row++;
				lengthY = row * (h + VIEW_MARGIN) + VIEW_MARGIN + h + t;
				//FIXME:
				//child.layout(lengthX - w, lengthY - h, lengthX, lengthY);
			} else {
				w = child.getMeasuredWidth();
				h = child.getMeasuredHeight();
				lengthX += w + VIEW_MARGIN;
				lengthY = row * (h + VIEW_MARGIN) + VIEW_MARGIN + h + t;
				// if it can't drawing on a same line , skip to next line
				if (lengthX > r - VIEW_MARGIN) {
					//Log.d(TAG, "wrap");
					lengthX = w + VIEW_MARGIN + l;
					row++;
					lengthY = row * (h + VIEW_MARGIN) + VIEW_MARGIN + h + t;
				}
				child.layout(lengthX - w, lengthY - h, lengthX, lengthY);
			}
		}
		lengthX2 = lengthX - l;
		lengthY2 = lengthY - t;
		
		if (D) {
			Log.d(TAG, "lengthX2 = " + lengthX2 + " lengthY2 = " + lengthY2);
		}
	}
	
    public void output(final String title, final String id) {
//    	LinearLayout linearLayout = new LinearLayout(this.getContext());
//    	linearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//    	linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//    	linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
    	
    	Resources res = this.getResources();
    	
    	if (this.getChildCount() > 0) {
	        TextView textview2 = new TextView(this.getContext());
	        textview2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	        textview2.setText(" > ");
	        textview2.setTextSize(res.getDimensionPixelSize(R.dimen.tinyTextSize));
	        textview2.setTextColor(Color.GRAY);
	        this.addView(textview2);
    	}
    	
        final TextView textview1 = new TextView(this.getContext());
        textview1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        textview1.setText(title);
        textview1.setTextSize(res.getDimensionPixelSize(R.dimen.tinyTextSize));
        textview1.setTextColor(Color.BLACK);//Color.BLUE);
        textview1.setFocusable(true);
        textview1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClick(title, id);
				}
			}
		});
        
        this.addView(textview1);
    	
//	    this.addView(linearLayout);
    }
    
    
    public static interface OnItemClickListener {
    	void onItemClick(String title, String id);
	}
    
    public void clearViews() {
    	this.removeAllViews();
    }
}
