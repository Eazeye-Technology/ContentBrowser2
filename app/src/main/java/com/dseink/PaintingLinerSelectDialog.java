package com.dseink;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.txkj.contentbrowser2.R;

public class PaintingLinerSelectDialog {
    private Dialog dialog;
    private ImageView ivLine1;
    private ImageView ivLine2;
    private ImageView ivLine3;
    private ImageView ivLine4;
    private TextView tvPencil;
    private TextView tvPen;
    private int currentType;
    private int currentWidth;
    private PaintingLinerSelectDialog.OnSelectListener onSelectListener;
    private final Context context;

    public final PaintingLinerSelectDialog builder() {
        this.dialog = new Dialog(this.context);
        Dialog var10000 = this.dialog;
        //Intrinsics.checkNotNull(var10000);
        var10000.setContentView(R.layout.dialog_painting_tool);
        var10000 = this.dialog;
        //Intrinsics.checkNotNull(var10000);
        Window var5 = var10000.getWindow();
        if (var5 != null) {
            var5.setBackgroundDrawableResource(android.R.color.transparent);
        }

        var10000 = this.dialog;
        if (false) {
            Window window = var10000 != null ? var10000.getWindow() : null;
            WindowManager.LayoutParams layoutParams = window != null ? window.getAttributes() : null;
            if (layoutParams != null) {
                layoutParams.gravity = Gravity.CENTER_VERTICAL;
            } else {
                layoutParams.gravity = Gravity.END;
            }

            if (layoutParams != null) {
                layoutParams.x = (1404 - DP2PX.dip2px(this.context, 425.0F)) / 2;
            }
        } else {
            if (this.context instanceof Activity) {
                EinkUtils.centerToRightScreen((Activity) this.context, dialog);
            }
        }

        var10000 = this.dialog;
        //Intrinsics.checkNotNull(var10000);
        var10000.show();
        var10000 = this.dialog;
        //Intrinsics.checkNotNull(var10000);
        Switch var3 = (Switch)var10000.findViewById(R.id.sw_image);
        var3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public final void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PaintingLinerSelectDialog.OnSelectListener var10000 =
                        PaintingLinerSelectDialog.this.onSelectListener;
                if (var10000 != null) {
                    var10000.setOpenRule(b);
                }
            }
        });
        Dialog var10001 = this.dialog;
        //Intrinsics.checkNotNull(var10001);
        this.tvPencil = (TextView)var10001.findViewById(R.id.tv_pencil);
        TextView var6 = this.tvPencil;
        //Intrinsics.checkNotNull(var6);
        var6.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View it) {
                PaintingLinerSelectDialog var10000 = PaintingLinerSelectDialog.this;
                TextView var10001 = PaintingLinerSelectDialog.this.tvPencil;
                //Intrinsics.checkNotNull(var10001);
                var10000.setSelectPen(var10001, 14);
            }
        });
        var10001 = this.dialog;
        //Intrinsics.checkNotNull(var10001);
        this.tvPen = (TextView)var10001.findViewById(R.id.tv_pen);
        var6 = this.tvPen;
        //Intrinsics.checkNotNull(var6);
        var6.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View it) {
                PaintingLinerSelectDialog var10000 = PaintingLinerSelectDialog.this;
                TextView var10001 = PaintingLinerSelectDialog.this.tvPen;
                //Intrinsics.checkNotNull(var10001);
                var10000.setSelectPen(var10001, 0);
            }
        });
        var10001 = this.dialog;
        //Intrinsics.checkNotNull(var10001);
        this.ivLine1 = (ImageView)var10001.findViewById(R.id.line_1);
        ImageView var7 = this.ivLine1;
        //Intrinsics.checkNotNull(var7);
        var7.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View it) {
                setSelectImageView(ivLine1, 1);
            }
        });
        var10001 = this.dialog;
        //Intrinsics.checkNotNull(var10001);
        this.ivLine2 = (ImageView)var10001.findViewById(R.id.line_2);
        var7 = this.ivLine2;
        //Intrinsics.checkNotNull(var7);
        var7.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View it) {
                setSelectImageView(ivLine2, 3);
            }
        });
        var10001 = this.dialog;
        //Intrinsics.checkNotNull(var10001);
        this.ivLine3 = (ImageView)var10001.findViewById(R.id.line_3);
        var7 = this.ivLine3;
        //Intrinsics.checkNotNull(var7);
        var7.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View it) {
                setSelectImageView(ivLine3, 5);
            }
        });
        var10001 = this.dialog;
        //Intrinsics.checkNotNull(var10001);
        this.ivLine4 = (ImageView)var10001.findViewById(R.id.line_4);
        var7 = this.ivLine4;
        //Intrinsics.checkNotNull(var7);
        var7.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View it) {
                setSelectImageView(ivLine4, 7);
            }
        });

//        int drawType = SPUtil.getInt(this.context, "PaintingDrawTYpe");
//        TextView var8;
//        if (drawType == android.view.PWDrawObjectHandler.DRAW_OBJ_RANDOM_PENCIL) {
//            var8 = this.tvPencil;
//            //Intrinsics.checkNotNull(var8);
//            this.setSelectPen(var8, 14);
//        } else {
//            var8 = this.tvPen;
//            //Intrinsics.checkNotNull(var8);
//            this.setSelectPen(var8, this.currentType);
//        }

        //ImageView var9 = this.ivLine2;
        //Intrinsics.checkNotNull(var9);
        if (this.currentWidth == 1) {
            this.setSelectImageView(this.ivLine1, 1);
        } else if (this.currentWidth == 3) {
            this.setSelectImageView(this.ivLine2, 3);
        } else if (this.currentWidth == 5) {
            this.setSelectImageView(this.ivLine3, 5);
        } else if (this.currentWidth == 7) {
            this.setSelectImageView(this.ivLine4, 7);
        }

        TextView tv_cancel = (TextView)this.dialog.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        TextView tv_update = (TextView) this.dialog.findViewById(R.id.tv_update);
        tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPUtil.setInt(context, SPUtil.KEY_PEN_WIDTH, currentWidth);
                if (onSelectListener != null) {
                    onSelectListener.setPenWidth(currentWidth);
                }
                dismiss();
            }
        });

        return this;
    }

    private final void setSelectPen(TextView view, int des) {
        TextView var10000 = this.tvPen;
        if (var10000 != null) {
            var10000.setBackgroundResource(R.color.color_transparent);
        }

        var10000 = this.tvPencil;
        if (var10000 != null) {
            var10000.setBackgroundResource(R.color.color_transparent);
        }

        view.setBackgroundResource(R.drawable.bg_black_stroke_0dp_corner);
        this.currentType = des;
        PaintingLinerSelectDialog.OnSelectListener var3 = this.onSelectListener;
        if (var3 != null) {
            var3.setDrawType(this.currentType);
        }
    }

    private final void setSelectImageView(ImageView view, int des) {
        ImageView var10000 = this.ivLine1;
        if (var10000 != null) {
            var10000.setBackgroundResource(R.color.color_transparent);
        }

        var10000 = this.ivLine2;
        if (var10000 != null) {
            var10000.setBackgroundResource(R.color.color_transparent);
        }

        var10000 = this.ivLine3;
        if (var10000 != null) {
            var10000.setBackgroundResource(R.color.color_transparent);
        }

        var10000 = this.ivLine4;
        if (var10000 != null) {
            var10000.setBackgroundResource(R.color.color_transparent);
        }

        view.setBackgroundResource(R.drawable.bg_black_stroke_0dp_corner);
        this.currentWidth = des;
        PaintingLinerSelectDialog.OnSelectListener var3 = this.onSelectListener;
        if (var3 != null) {
            var3.setWidth(this.currentWidth);
        }

    }

    public final void dismiss() {
        if (this.dialog != null) {
            Dialog var10000 = this.dialog;
            if (var10000 != null) {
                var10000.dismiss();
            }
        }

    }

    public final void show() {
        if (this.dialog != null) {
            Dialog var10000 = this.dialog;
            if (var10000 != null) {
                var10000.show();
            }
        }
    }

    public final void setOnSelectListener(PaintingLinerSelectDialog.OnSelectListener onSelectListener) {
        //Intrinsics.checkNotNullParameter(onSelectListener, "onSelectListener");
        this.onSelectListener = onSelectListener;
    }

    public PaintingLinerSelectDialog(Context context) {
        //Intrinsics.checkNotNullParameter(context, "context");
        super();
        this.context = context;
        this.currentWidth = 3;
        this.currentWidth = SPUtil.getInt(context, SPUtil.KEY_PEN_WIDTH, 1);
    }

    public static final void access$setOnSelectListener$p(PaintingLinerSelectDialog $this, PaintingLinerSelectDialog.OnSelectListener var1) {
        $this.onSelectListener = var1;
    }

    public static final void access$setTvPencil$p(PaintingLinerSelectDialog $this, TextView var1) {
        $this.tvPencil = var1;
    }

    public static final void access$setTvPen$p(PaintingLinerSelectDialog $this, TextView var1) {
        $this.tvPen = var1;
    }

    public static final void access$setIvLine1$p(PaintingLinerSelectDialog $this, ImageView var1) {
        $this.ivLine1 = var1;
    }

    public static final void access$setIvLine2$p(PaintingLinerSelectDialog $this, ImageView var1) {
        $this.ivLine2 = var1;
    }

    public static final void access$setIvLine3$p(PaintingLinerSelectDialog $this, ImageView var1) {
        $this.ivLine3 = var1;
    }

    public static final void access$setIvLine4$p(PaintingLinerSelectDialog $this, ImageView var1) {
        $this.ivLine4 = var1;
    }

    public interface OnSelectListener {
        void setWidth(int var1);
        void setDrawType(int var1);
        void setOpenRule(boolean var1);
        void setPenWidth(int penWidth);
    }
}
