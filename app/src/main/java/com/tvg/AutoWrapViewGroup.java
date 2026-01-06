package com.tvg;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.txkj.contentbrowser2.R;

public class AutoWrapViewGroup extends LinearLayout {
	private final static boolean D = false;
	private final static String TAG = "TreeViewGroup";

	private int mChildCount = 0;

	public AutoWrapViewGroup(Context context) {
		super(context);
		init();
	}

	public AutoWrapViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		this.setOrientation(LinearLayout.HORIZONTAL);
	}
	
	private OnItemClickListener mOnItemClickListener;
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}
	
    public void output(final String title, final String id) {
    	LinearLayout linearLayout = new LinearLayout(this.getContext());
    	linearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	linearLayout.setOrientation(LinearLayout.HORIZONTAL);
    	linearLayout.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//    	linearLayout.setBackgroundResource(R.drawable.border_ui);
    	
    	Resources res = this.getResources();
    	float textsize = 16;//res.getDimension(R.dimen.smallTextSize);
    	int textHeight = getFontHeight(textsize);

        if (mChildCount > 1) {
            final TextView textview_space = new TextView(this.getContext());
            textview_space.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            textview_space.setText(" > ");
            textview_space.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
            textview_space.setTextColor(Color.BLACK);
            textview_space.setFocusable(true);
            textview_space.setSingleLine(true);
            textview_space.setEllipsize(TextUtils.TruncateAt.END);
            linearLayout.addView(textview_space);
        }

        final TextView textview1 = new TextView(this.getContext());
        textview1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        textview1.setText(title);
        textview1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        textview1.setTextColor(Color.BLACK);
        textview1.setFocusable(true);
        textview1.setSingleLine(true);
        textview1.setEllipsize(TextUtils.TruncateAt.END);
        textview1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				Toast.makeText(getContext(), 
//					"hit", Toast.LENGTH_SHORT).show();
				if (mOnItemClickListener != null && id != null) {
					mOnItemClickListener.onItemClick(title, id);
				}
			}
		});
        linearLayout.addView(textview1);
        
        this.addView(linearLayout);
        mChildCount++;
    }
    
    public int getFontHeight(float fontSize)   {  
         Paint paint = new Paint();  
         paint.setTextSize(fontSize);  
         FontMetrics fm = paint.getFontMetrics();  
         return (int) Math.ceil(fm.descent - fm.top) + 2;  
    } 
    
    public static interface OnItemClickListener {
    	void onItemClick(String title, String id);
	}
    
    public void clearViews() {
    	this.removeAllViews();
    	mChildCount = 0;
    }
}
