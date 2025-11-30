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

public class TreeViewGroup extends LinearLayout {
	private final static boolean D = false;
	private final static String TAG = "TreeViewGroup";
	
	private int mChildCount = 0;
	
	public TreeViewGroup(Context context) {
		super(context);
		init();
	}

	public TreeViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		this.setOrientation(LinearLayout.VERTICAL);
	}
	
	private OnItemClickListener mOnItemClickListener;
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}
	
    public void output(final String title, final String id) {
    	LinearLayout linearLayout = new LinearLayout(this.getContext());
    	linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    	linearLayout.setOrientation(LinearLayout.HORIZONTAL);
    	linearLayout.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//    	linearLayout.setBackgroundResource(R.drawable.border_ui);
    	
    	Resources res = this.getResources();
    	float textsize = res.getDimension(R.dimen.smallTextSize);
    	int textHeight = getFontHeight(textsize);
    	int imageHeight = (int)(1 * textHeight);
    	
    	//private int img_tree_space_n = R.drawable.tree_space_n;
    	//private int img_tree_space_y = R.drawable.tree_space_y;
    	LinearLayout space = new LinearLayout(this.getContext());
    	space.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	space.setOrientation(LinearLayout.HORIZONTAL);
    	space.setGravity(Gravity.CENTER_HORIZONTAL);
    	for (int i = 0; i < mChildCount; i++) {
        	final ImageView line = new ImageView(this.getContext());
        	line.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        	line.setScaleType(ImageView.ScaleType.FIT_CENTER);
        	line.setLayoutParams(new LayoutParams(imageHeight, imageHeight));
        	if (i == mChildCount - 1) {
        		line.setImageResource(R.drawable.tree_space_2); //丁字
        	} else {
        		line.setImageResource(R.drawable.tree_space_n); //空白
            }
    		space.addView(line);
    	}
    	linearLayout.addView(space);
    	
    	final ImageView imageView1 = new ImageView(this.getContext());
    	imageView1.setLayoutParams(new LayoutParams(imageHeight, imageHeight));
    	imageView1.setImageResource(R.drawable.l_folder);
    	//imageView1.setImageResource(R.drawable.border_ui);
    	imageView1.setScaleType(ImageView.ScaleType.FIT_CENTER);
    	linearLayout.addView(imageView1);
    	imageView1.setFocusable(true);
    	imageView1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				Toast.makeText(getContext(), 
//					"hit", Toast.LENGTH_SHORT).show();
				if (mOnItemClickListener != null && id != null) {
					mOnItemClickListener.onItemClick(title, id);
				}
			}
		});
    	
        final TextView textview1 = new TextView(this.getContext());
        textview1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        textview1.setText(title);
        textview1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        textview1.setTextColor(Color.BLUE);
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
