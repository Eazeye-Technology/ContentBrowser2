package com.tvg;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.media.Image;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.txkj.contentbrowser2.R;

import java.util.ArrayList;
import java.util.List;

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
    	linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	linearLayout.setOrientation(LinearLayout.HORIZONTAL);
    	linearLayout.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
//    	linearLayout.setBackgroundResource(R.drawable.border_ui);
    	
    	Resources res = this.getResources();
    	//float textSize = 16;//res.getDimension(R.dimen.smallTextSize);
    	float textSize = res.getDimensionPixelSize(R.dimen.autowrapviewgroup_text_size);
        int textHeight = getFontHeight(textSize);

        //----------------

        if (mChildCount > 0) { //1) {
            if (false) {
                final TextView textview_space = new TextView(this.getContext());
                LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                p1.setMargins(100, 0, 100, 0);
                textview_space.setLayoutParams(p1);
                textview_space.setText(">");
                textview_space.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                textview_space.setTextColor(Color.BLACK);
                textview_space.setFocusable(true);
                textview_space.setSingleLine(true);
                textview_space.setEllipsize(TextUtils.TruncateAt.END);
                linearLayout.addView(textview_space);
            } else {
                final ImageView textview_space = new ImageView(this.getContext());
                LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(24, 24);
                p1.setMargins(10, 0, 10, 0);
                p1.gravity = Gravity.CENTER | Gravity.CENTER_VERTICAL;
                textview_space.setLayoutParams(p1);
                textview_space.setImageResource(R.drawable.glyphicons_224_chevron_right);
                textview_space.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                textview_space.setFocusable(true);
                textview_space.setPadding(3, 3, 3, 3);
                linearLayout.addView(textview_space);
            }
        }

        //-----------------

        final TextView textview1 = new TextView(this.getContext());

        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        p2.setMargins(0, 0, 0, 0);
        p2.gravity = Gravity.CENTER | Gravity.CENTER_VERTICAL;

        textview1.setLayoutParams(p2);
        textview1.setText(title);
        textview1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textview1.setTextColor(Color.BLACK); //464647, 1C1C1C //Color.RED);//
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
        allTextView.add(textview1);



        //----------------
        this.addView(linearLayout);
        mChildCount++;

        setAllTextViewColor();
    }

    private void setAllTextViewColor() {
        int folderColor = ContextCompat.getColor(this.getContext(), R.color.md_theme_onSurface_highContrast);
        int tailFolderColor = ContextCompat.getColor(this.getContext(), R.color.md_theme_secondary_highContrast);
        if (allTextView != null) {
            for (int i = 0; i < allTextView.size(); ++i) {
                TextView textView = allTextView.get(i);
                if (textView != null) {
                    if (i == allTextView.size() - 1) {
                        textView.setTextColor(tailFolderColor);
                    } else {
                        textView.setTextColor(folderColor);
                        textView.setTypeface(null, Typeface.BOLD);
                    }
                }
            }
        }
    }

    List<TextView> allTextView = new ArrayList<>();
    
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
        this.allTextView.clear();
    }
}
