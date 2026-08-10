package com.txkj.contentbrowser2.activity;

public class DualScreenConstant {
    /**
     *	20211012,add for dual-screen.whitch screen we want to launcher
     *	the activity.
     */
    public static final String EXTRA_LAUNCH_SCREEN =
            "android.intent.extra.LAUNCH_SCREEN";

    public static final int EXTRA_LAUNCH_SCREEN_PANEL_NONE	= 0;
    public static final int EXTRA_LAUNCH_SCREEN_PANEL_A 	= 1;
    public static final int EXTRA_LAUNCH_SCREEN_PANEL_B		= 2;
    public static final int EXTRA_LAUNCH_SCREEN_PANEL_BOTH 	= 3;
}
