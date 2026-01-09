/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.material.catalog.feature;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.txkj.contentbrowser2.R;

/** Base Fragment class that provides a demo screen structure for a single demo. */
public abstract class DemoFragment extends Fragment {
    public static final String ARG_DEMO_TITLE = "demo_title";
//    private Toolbar toolbar;
    private ViewGroup demoContainer;

    public int getDemoTitleResId() {
        return 0;
    }

    @Nullable
    @Override
    @SuppressLint("ClickableViewAccessibility") // Keep this hidden from a11y services for now.
    public View onCreateView(
            LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View view =
                layoutInflater.inflate(R.layout.cat_demo_fragment, viewGroup, false /* attachToRoot */);

//        Bundle arguments = getArguments();
//        if (arguments != null) {
//            String transitionName = arguments.getString(FeatureDemoUtils.ARG_TRANSITION_NAME);
//            ViewCompat.setTransitionName(view, transitionName);
//        }

//        toolbar = view.findViewById(R.id.toolbar);

        demoContainer = view.findViewById(R.id.cat_demo_fragment_container);
//        initDemoActionBar();
        View demoView = onCreateDemoView(layoutInflater, viewGroup, bundle);
        demoContainer.addView(demoView);

        return view;
    }

    //@Override
    public boolean shouldShowDefaultDemoActionBar() {
        return true;
    }

//    private void initDemoActionBar() {
//        if (shouldShowDefaultDemoActionBar()) {
//            AppCompatActivity activity = (AppCompatActivity) getActivity();
//            activity.setSupportActionBar(toolbar);
//            if (getSupportActionBar() != null) setDemoActionBarTitle(activity.getSupportActionBar());
//        } else {
//            toolbar.setVisibility(View.GONE);
//        }
//    }

    private void setDemoActionBarTitle(ActionBar actionBar) {
        if (getDemoTitleResId() != 0) {
            actionBar.setTitle(getDemoTitleResId());
        } else {
            actionBar.setTitle(getDefaultDemoTitle());
        }
    }

    protected String getDefaultDemoTitle() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getString(ARG_DEMO_TITLE, "");
        } else {
            return "";
        }
    }









    public abstract View onCreateDemoView(
            LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle);
}
