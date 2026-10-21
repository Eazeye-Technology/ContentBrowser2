package com.txkj.contentbrowser2.activity;

import android.app.Activity;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.txkj.contentbrowser.HomeFragment4;
import com.txkj.contentbrowser.HomeRightFragment;
import com.txkj.contentbrowser.NoteFragment2;

public class MainActivity6Util {
    public static void refreshAllFragments(Activity act, Fragment fragment, View view) {
        if (act == null || fragment == null) {
            return;
        }
        if (view != null) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (act instanceof MainActivity6) {
                        MainActivity6 act6 = (MainActivity6) act;
                        if (fragment instanceof NoteFragment2) {
                            act6.mHomeFragment61.mHomeFragment4.refresh();
                            act6.mHomeFragment62.mHomeFragmentRight.refresh();
                            //act6.mHomeFragment62.mNoteFragment.refresh();
                        } else if (fragment instanceof HomeFragment4) {
                            //act6.mHomeFragment61.mHomeFragment4.refresh();
                            act6.mHomeFragment62.mHomeFragmentRight.refresh();
                            act6.mHomeFragment62.mNoteFragment.refresh();
                        } else if (fragment instanceof HomeRightFragment) {
                            act6.mHomeFragment61.mHomeFragment4.refresh();
                            //act6.mHomeFragment62.mHomeFragmentRight.refresh();
                            act6.mHomeFragment62.mNoteFragment.refresh();
                        }
                    }
                }
            }, 3000);
        }

    }
}
