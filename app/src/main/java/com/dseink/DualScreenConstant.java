package com.dseink;

import android.content.Intent;

public class DualScreenConstant {
    public static boolean FORCE_TAB_REFRESH = false;

    /**
     *	20211012,add for dual-screen. which screen we want to launcher
     *	the activity.
     */
    public static final String EXTRA_LAUNCH_SCREEN =
            "android.intent.extra.LAUNCH_SCREEN";

    public static final int EXTRA_LAUNCH_SCREEN_PANEL_NONE	= 0;
    public static final int EXTRA_LAUNCH_SCREEN_PANEL_A 	= 1;
    public static final int EXTRA_LAUNCH_SCREEN_PANEL_B		= 2;
    public static final int EXTRA_LAUNCH_SCREEN_PANEL_BOTH 	= 3;

    public static boolean USE_HOME = false;
    public static boolean FORCE_BOTH_SCREEN = false;
    public static boolean USE_RIGHT_SCREEN = true;
    public static void launchFull(Intent intent, boolean useRightScreen, boolean useFull) {
        if (intent == null) {
            return;
        }
        if (FORCE_BOTH_SCREEN) {
            intent.putExtra(DualScreenConstant.EXTRA_LAUNCH_SCREEN,
                    DualScreenConstant.EXTRA_LAUNCH_SCREEN_PANEL_BOTH);
        } else {
            if (USE_RIGHT_SCREEN) {
                if (useFull) {
                    intent.putExtra(DualScreenConstant.EXTRA_LAUNCH_SCREEN,
                            DualScreenConstant.EXTRA_LAUNCH_SCREEN_PANEL_BOTH);
                } else if (useRightScreen) {
                    intent.putExtra(DualScreenConstant.EXTRA_LAUNCH_SCREEN,
                            DualScreenConstant.EXTRA_LAUNCH_SCREEN_PANEL_B);
                }
            }
        }
    }
}
