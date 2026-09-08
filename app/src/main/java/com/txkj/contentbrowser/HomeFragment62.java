package com.txkj.contentbrowser;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.codeteenager.systemsettings.SystemSettingFragment;
import com.dseink.DualScreenConstant;
import com.dseink.EinkUtils;
import com.getdirectory.DirectoryFragment2;
import com.txkj.contentbrowser2.R;
import com.txkj.contentbrowser2.activity.CopyCutMenuDialog;
import com.txkj.contentbrowser2.activity.MainActivity6;
import com.txkj.smartanswer.AnswerFragment;

import java.io.File;
import java.util.ArrayList;

public class HomeFragment62 extends Fragment {
    final private boolean NEW_HOME = true;

    private View g_rootView = null;
    private View findViewById(int id) {
        if (g_rootView == null) {
            return null;
        }
        return g_rootView.findViewById(id);
    }
    private MainActivity6 getMyActivity() {
        return (MainActivity6) getActivity();
    }

    public void onDialogDismiss() {
        //FIXME:
        if (g_rootView != null) {
            EinkUtils.forceEinkFullUpdateWithView(g_rootView.findViewById(R.id.content_layout62));
        }
    }

    AutoCompleteTextView searchEditTextGlobal;

    private final static int icons[] = {
            R.id.menu_bar_item_1, //home
            R.id.menu_bar_item_2, //note
            R.id.menu_bar_item_3, //book
            R.id.menu_bar_item_4, //pdfs
            R.id.menu_bar_item_5, //storage
            R.id.menu_bar_item_6, //apps
            R.id.menu_bar_item_7, //settings
            R.id.menu_bar_item_1_R,
            R.id.menu_bar_item_2_R,
            R.id.menu_bar_item_3_R,
            R.id.menu_bar_item_4_R,
            R.id.menu_bar_item_5_R,
            R.id.menu_bar_item_6_R,
            R.id.menu_bar_item_7_R,
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home6_2, container, false);
        g_rootView = view;

        if (NEW_HOME) {
            findViewById(R.id.llTab).setVisibility(View.GONE);
        }

        for (int id : icons) {
            if (findViewById(id) != null) {
                findViewById(id).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClick2(id, true);
                    }
                });
            }
        }

        View viewIcon = findViewById(icons[0]);
        if (viewIcon != null) {
            viewIcon.findViewWithTag("binding_5").setVisibility(View.VISIBLE);
            viewIcon.findViewWithTag("binding_1").setActivated(true);
        }

        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSearch(false);
            }
        });
        findViewById(R.id.btnMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment currentFragment = null;
                FragmentManager fragmentManager = getMyActivity().getSupportFragmentManager();
                if (fragmentManager.getFragments().size() - 1 >= 0) {
                    currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
                }
                boolean res = false;
                if (currentFragment instanceof DirectoryFragment2 ||
                        currentFragment instanceof NoteFragment2 ||
                        currentFragment instanceof AppsFragment) {
                    //skip, because I hide the search button
                } else {
                    res = toggleSearch(true);
                }
                if (!res) {
                    if (currentFragment instanceof DirectoryFragment2) {
                        ((DirectoryFragment2) currentFragment).showPopupMenuDirectoryFragment2(view);
                    } else if (currentFragment instanceof NoteFragment2) {
                        ((NoteFragment2) currentFragment).showPopupMenuNoteFragment2(view);
                    } else if (currentFragment instanceof HomeFragment) {
//                        fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.replace(R.id.content_layout62, mHomeFragment2);
//                        fragmentTransaction.commit();
                    } else if (currentFragment instanceof AppsFragment) {
                        ((AppsFragment) currentFragment).showPopupMenuNoteFragment2(view);
                    } else {
                        //showPopupMenu(view);
                    }
                }
            }
        });
        searchEditTextGlobal = (AutoCompleteTextView) g_rootView.findViewById(R.id.filterLine_Library_global);
        searchEditTextGlobal.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sendMessage();
            }
        });
        //https://developer.android.google.cn/develop/ui/views/touch-and-input/keyboard-input/style?hl=zh-cn#java
        searchEditTextGlobal.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //close ime
                    if (searchEditTextGlobal != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchEditTextGlobal.getWindowToken(), 0);
                        searchEditTextGlobal.clearFocus();
                    }
                    sendMessage();
                    handled = true;
                }
                return handled;
            }
        });


        //https://github.com/dibakarece/AndroidFileExplorer
//        fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();

        mFirstFragment = new FirstFragment();
        mSecondFragment = new SecondFragment();

        mHomeFragment = new HomeFragment();
        mHomeFragment2 = new HomeFragment2();
        mHomeFragment4 = new HomeFragment4();
        mHomeFragmentRight = new HomeRightFragment();
        mBrowserFragment = new BrowserFragment();
        mSettingFragment = new SettingFragment();
        mNoteFragment = new NoteFragment2();
        mLibraryFragment2Book = new LibraryFragment2Book();
        mLibraryFragment2Pdf = new LibraryFragment2Pdf();
        mChatFragment = new ChatFragment();
        mAppsFragment = new AppsFragment();
        mHome3Fragment = new HomeFragment3();
        mAnswerFragment = new AnswerFragment();
        mSystemSettingFragment = new SystemSettingFragment();

        mDirectoryFragment = new DirectoryFragment2();
        mDirectoryFragment.setDelegate(new DirectoryFragment2.DocumentSelectActivityDelegate() {

            @Override
            public void startDocumentSelectActivity() {

            }

            @Override
            public void didSelectFiles(DirectoryFragment2 activity,
                                       ArrayList<String> files) {
//                mDirectoryFragment.showErrorBox(files.get(0).toString());
                if (false) {
                    mDirectoryFragment.showErrorBox(files.get(0).toString());
                } else {
                    if (files != null && files.size() > 0) {
                        //android.os.FileUriExposedException: file:///storage/emulated/0/Download/%E5%9B%9B%E5%A4%A7%E5%90%8D%E7%9D%80.epub
                        // exposed beyond app through Intent.getData()
                        try {
                            //https://github.com/microsoft/Visual-Audience-Polling/blob/db9536339145aa87a526c1a5fbf207e730ca7859/src/RosterList.java#L379
                            // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's
                            // file
                            // browser.
                            Intent intent = new Intent(Intent.ACTION_VIEW); //(Intent.ACTION_OPEN_DOCUMENT);
                            // Filter to only show results that can be "opened", such as a
                            // file (as opposed to a list of contacts or timezones)
                            //----//intent.addCategory(Intent.CATEGORY_OPENABLE);
                            // Filter to show only images, using the image MIME data type.
                            // If one wanted to search for ogg vorbis files, the type would be
                            // "audio/ogg".
                            // To search for all documents available via installed storage
                            // providers,
                            // it would be "*/*".
                            // Mime Types : https://developers.google.com/drive/web/mime-types
                            intent.setType("*/*");
                            intent.setData(Uri.fromFile(
                                    new File(files.get(0))));

                            //https://blog.csdn.net/kaiyuanheshang/article/details/49740489
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                            // Intent resultData = intent;
                            startActivity(intent);
                        } catch (Throwable eee) {
                            eee.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void updateToolBarName(String name) {
                if (getMyActivity().getSupportActionBar() != null) {
                    getMyActivity().getSupportActionBar().setTitle(name);
                }
            }
        });

        if (false) {
            fragmentTransaction.add(R.id.content_layout62, mDirectoryFragment, "" + mDirectoryFragment.toString());
            fragmentTransaction.commit();
        }
        this.currentTabId = icons[0];
        if (savedInstanceState != null) {
            this.currentTabId = savedInstanceState.getInt(KEY_CURRENT_TAB_ID, 0);//R.id.function_bar_item_home);
        }
        onClick2(this.currentTabId, false); //default choose home

        this.findViewById(R.id.top_left_menu_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!toggleSearch(true)) {
                    if (false) {
                        if (findViewById(R.id.left_menu) != null) {
                            if (findViewById(R.id.left_menu).getVisibility() == View.VISIBLE) {
                                findViewById(R.id.left_menu).setVisibility(View.GONE);
                            } else {
                                findViewById(R.id.left_menu).setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        if (findViewById(R.id.leftmenu2).getVisibility() == View.GONE) {
                            findViewById(R.id.leftmenu1).setVisibility(View.GONE);
                            findViewById(R.id.leftmenu2).setVisibility(View.VISIBLE);
                            ((ImageView) findViewById(R.id.top_left_menu_button_icon)).setImageResource(R.drawable.ic_baseline_menu_24);
                        } else {
                            findViewById(R.id.leftmenu1).setVisibility(View.VISIBLE);
                            findViewById(R.id.leftmenu2).setVisibility(View.GONE);
                            ((ImageView) findViewById(R.id.top_left_menu_button_icon)).setImageResource(R.drawable.ic_baseline_menu_open_24);
                        }
                    }
                }
                //FIXME:
                EinkUtils.forceEinkFullUpdateWithView(findViewById(R.id.content_layout62));
            }
        });
        View button = this.findViewById(R.id.btnTitleNewBook);
        View buttonR = this.findViewById(R.id.btnTitleNewBook_R);
        View.OnClickListener onClickListenerNewBook = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent();
//                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    if (false) {
                        intent.setClassName("com.txkj.notemobile2",
                                "com.txkj.notemobile2.BookListActivity");
                    } else if (false) {
                        intent.setClassName("com.txkj.notemobile",//"online.xournal.mobile",
                                "com.txkj.notemobile.MainActivity");//"online.xournal.mobile.MainActivity");
                    } else {
                        intent.setClassName("com.txkj.drawingapp",
                                "com.txkj.notemobile2.BookListActivity");
                        DualScreenConstant.launchFull(intent, true, false);
                    }

                    intent.putExtra("APP_OPEN", "NEW");

                    //https://blog.csdn.net/kaiyuanheshang/article/details/49740489
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        if (true) {
            if (button != null) {
                button.setOnClickListener(onClickListenerNewBook);
            }
            if (buttonR != null) {
                buttonR.setOnClickListener(onClickListenerNewBook);
            }
        }


        return view;
    }

    private boolean toggleSearch(boolean forceShowSearchButton) {
        boolean result = findViewById(R.id.ll_search_wx_global).getVisibility() == View.VISIBLE;
        if (NEW_HOME) {
            findViewById(R.id.ll_search_wx_global).setVisibility(View.VISIBLE);
            findViewById(R.id.tvTitleText).setVisibility(View.GONE);
            //findViewById(R.id.btnSearch).setVisibility(View.GONE);
            findViewById(R.id.ll_btnMore).setVisibility(View.GONE);
            if (searchEditTextGlobal != null) {
                searchEditTextGlobal.requestFocus();
            }
        } else {
            if (forceShowSearchButton || findViewById(R.id.ll_search_wx_global).getVisibility() == View.VISIBLE) {
                findViewById(R.id.ll_search_wx_global).setVisibility(View.GONE);
                findViewById(R.id.tvTitleText).setVisibility(View.VISIBLE);
                //findViewById(R.id.btnSearch).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_btnMore).setVisibility(View.VISIBLE);
                //AutoCompleteTextView searchEditTextGlobal = (AutoCompleteTextView) findViewById(R.id.filterLine_Library_global);
                if (searchEditTextGlobal != null) {
                    searchEditTextGlobal.setText("");
                }
            } else {
                findViewById(R.id.ll_search_wx_global).setVisibility(View.VISIBLE);
                findViewById(R.id.tvTitleText).setVisibility(View.GONE);
                //findViewById(R.id.btnSearch).setVisibility(View.GONE);
                findViewById(R.id.ll_btnMore).setVisibility(View.GONE);
                if (searchEditTextGlobal != null) {
                    searchEditTextGlobal.requestFocus();
                }
            }
        }
        onUpdateMenu();
        return result;
    }

    private void sendMessage() {
        String text = "";
        if (searchEditTextGlobal != null &&
                searchEditTextGlobal.getText() != null &&
                searchEditTextGlobal.getText().toString() != null) {
            text = searchEditTextGlobal.getText().toString();
        }
        if (text.length() >= 2 || text.length() == 0) {
            FragmentManager fragmentManager = getMyActivity().getSupportFragmentManager();
            if (fragmentManager.getFragments().size() > 0) {
                Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
                if (currentFragment instanceof LibraryFragment2Book) {
                    ((LibraryFragment2Book) currentFragment).setSearch(text);
                } else if (currentFragment instanceof NoteFragment2) {
                    ((NoteFragment2) currentFragment).setSearch(text);
                } else if (currentFragment instanceof AppsFragment) {
                    //search function
                    ((AppsFragment) currentFragment).setSearch(text);
                } else if (currentFragment instanceof DirectoryFragment2) {
                    ((DirectoryFragment2) currentFragment).setSearch(text);
                } else if (currentFragment instanceof HomeFragment4) {
                    ((HomeFragment4) currentFragment).setSearch(text);
                }
            }
        }
    }



    private final static String KEY_CURRENT_TAB_ID = "KEY_CURRENT_TAB_ID";
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_TAB_ID, currentTabId);
    }

    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private DirectoryFragment2 mDirectoryFragment;
    private FirstFragment mFirstFragment;
    private SecondFragment mSecondFragment;

    private HomeFragment mHomeFragment;
    private HomeFragment2 mHomeFragment2;
    private HomeFragment4 mHomeFragment4;
    private HomeRightFragment mHomeFragmentRight;
    private BrowserFragment mBrowserFragment;
    private SettingFragment mSettingFragment;
    private NoteFragment2 mNoteFragment;
    private LibraryFragment2Book mLibraryFragment2Book;
    private LibraryFragment2Pdf mLibraryFragment2Pdf;
    private ChatFragment mChatFragment;
    private AppsFragment mAppsFragment;
    private HomeFragment3 mHome3Fragment;
    private AnswerFragment mAnswerFragment;
    private SystemSettingFragment mSystemSettingFragment;

    private int currentTabId = R.id.function_bar_item_home;
    //@Override
    public void onClick2(int id, boolean isClick) {
//        if (isClick) {
//            throw new RuntimeException("not implementation");
//        }
        this.currentTabId = id;
        toggleSearch(true);
        View view = findViewById(id);
        for (int i = 0; i < icons.length; ++i) {
            View viewIcon = this.findViewById(icons[i]);
            if (viewIcon != null) {
                if (viewIcon.findViewWithTag("binding_0") != null) {
                    View card = viewIcon.findViewWithTag("binding_0");
                    if (card instanceof CardView) {
                        ((CardView) card).setCardBackgroundColor(0x00888888);
                        ((CardView) card).setCardElevation(0.0f);
                    }
                } else if (viewIcon instanceof CardView)  {
                    ((CardView) viewIcon).setCardBackgroundColor(0x00888888);
                    ((CardView) viewIcon).setCardElevation(0.0f);
                }
                if (viewIcon.findViewWithTag("binding_1") != null) {
                    ((AppCompatImageView)viewIcon.findViewWithTag("binding_1")).setBackgroundResource(getActiveIconId(icons[i], false));
                }
                if (viewIcon.findViewWithTag("binding_5") != null) {
                    ((TextView)viewIcon.findViewWithTag("binding_5")).setTextColor(0xFF000000);
                }
            }
        }
        if (view != null) {
            if (view.findViewWithTag("binding_0") != null) {
                View card = view.findViewWithTag("binding_0");
                if (card instanceof CardView) {
                    ((CardView) card).setCardBackgroundColor(0xFF888888);
                    ((CardView) card).setCardElevation(0.0f);//5.0f);
                }
            } else if (view instanceof CardView) {
                ((CardView) view).setCardBackgroundColor(0xFF888888);
                ((CardView) view).setCardElevation(0.0f);//5.0f);
            }

            if (view.findViewWithTag("binding_1") != null) {
                ((AppCompatImageView)view.findViewWithTag("binding_1")).setBackgroundResource(getActiveIconId(id, true));
            }
            if (view.findViewWithTag("binding_0") == null) {
                if (view.findViewWithTag("binding_5") != null) {
                    ((TextView) view.findViewWithTag("binding_5")).setTextColor(0xFFFFFFFF);
                }
            }
        }
        View viewR = findViewById(getEqualIconId(id));
        if (viewR != null) {
            if (viewR.findViewWithTag("binding_0") != null) {
                View card = viewR.findViewWithTag("binding_0");
                if (card instanceof CardView) {
                    ((CardView) card).setCardBackgroundColor(0xFF888888);
                    ((CardView) card).setCardElevation(0.0f);//5.0f);
                }
            } else if (viewR instanceof CardView) {
                ((CardView) viewR).setCardBackgroundColor(0xFF888888);
                ((CardView) viewR).setCardElevation(0.0f);//5.0f);
            }
            if (viewR.findViewWithTag("binding_1") != null) {
                ((AppCompatImageView)viewR.findViewWithTag("binding_1")).setBackgroundResource(getActiveIconId(getEqualIconId(id), true));
            }
            if (viewR.findViewWithTag("binding_0") == null) {
                if (viewR.findViewWithTag("binding_5") != null) {
                    ((TextView) viewR.findViewWithTag("binding_5")).setTextColor(0xFFFFFFFF);
                }
            }
        }

        fragmentManager = getMyActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        TextView tvTitleText = (TextView) findViewById(R.id.tvTitleText);
        if (id == R.id.menu_bar_item_1 || id == R.id.menu_bar_item_1_R) {
            tvTitleText.setText("Home");
            if (false) {
                fragmentTransaction.replace(R.id.content_layout62, mAnswerFragment);//mHome3Fragment); //mHomeFragment
            } else if (false) {
                fragmentTransaction.replace(R.id.content_layout62, mHomeFragment4);//mHomeFragment); //mHomeFragment, mHomeFragment2
            } else {
                fragmentTransaction.replace(R.id.content_layout62, mHomeFragmentRight);
            }
            fragmentTransaction.commit();
        } else if (id == R.id.menu_bar_item_2 || id == R.id.menu_bar_item_2_R) {
            tvTitleText.setText("Notes");
            fragmentTransaction.replace(R.id.content_layout62, mNoteFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.menu_bar_item_3 || id == R.id.menu_bar_item_3_R) {
            tvTitleText.setText("Books");
            fragmentTransaction.replace(R.id.content_layout62, mLibraryFragment2Book);
            fragmentTransaction.commit();
        } else if (id == R.id.menu_bar_item_4 || id == R.id.menu_bar_item_4_R) {
            tvTitleText.setText("PDFs");
            fragmentTransaction.replace(R.id.content_layout62, mLibraryFragment2Pdf);
            fragmentTransaction.commit();
        } else if (id == R.id.menu_bar_item_5 || id == R.id.menu_bar_item_5_R) {
            tvTitleText.setText("Storage");
            fragmentTransaction.replace(R.id.content_layout62, mDirectoryFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.menu_bar_item_6 || id == R.id.menu_bar_item_6_R) {
            tvTitleText.setText("Apps");
            fragmentTransaction.replace(R.id.content_layout62, mAppsFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.menu_bar_item_7 || id == R.id.menu_bar_item_7_R) {
            tvTitleText.setText("Settings");
            fragmentTransaction.replace(R.id.content_layout62, mSystemSettingFragment);//mSettingFragment);
            fragmentTransaction.commit();
        }
        onUpdateMenu();
        //FIXME:
        if (DualScreenConstant.FORCE_TAB_REFRESH) {
            EinkUtils.forceEinkFullUpdateWithView(findViewById(R.id.content_layout62));
        }

        if (id == R.id.menu_bar_item_1 || id == R.id.menu_bar_item_1_R) {
            findViewById(R.id.llTab).setVisibility(View.GONE);
            findViewById(R.id.ll_btnMore).setVisibility(View.GONE);
        } else {
            findViewById(R.id.llTab).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_btnMore).setVisibility(View.VISIBLE);
        }
    }

    //https://github.com/microsoft/Visual-Audience-Polling/blob/db9536339145aa87a526c1a5fbf207e730ca7859/src/RosterList.java#L379
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's
        // file
        // browser.

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be
        // "audio/ogg".
        // To search for all documents available via installed storage
        // providers,
        // it would be "*/*".

        // Mime Types : https://developers.google.com/drive/web/mime-types
        intent.setType("*/*");
        // Intent resultData = intent;
//        startActivityForResult(intent, READ_REQUEST_CODE);
    }


    @Override
    public void onDestroy() {
        mDirectoryFragment.onFragmentDestroy();
        super.onDestroy();
    }

    //@Override
    public boolean onBackPressed() {
        if (mDirectoryFragment.onBackPressed_()) {
            return true;
        }
        return false;
    }

    public void jumpNote() {
        onClick2(R.id.menu_bar_item_2, true);
    }

    public void jumpViewPdf() {
        onClick2(R.id.menu_bar_item_4, true);
    }

    public void jumpSettings() {
        onClick2(R.id.menu_bar_item_7, true);
    }

    public void jumpAiChat() {
        //fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_layout62, mAnswerFragment);
        fragmentTransaction.commit();
        onUpdateMenu();
    }





















    public int getActiveIconId(int id, boolean isActive) {
        //case R.id.menu_bar_item_1: return isActive ? R.drawable.ic_baseline_access_time_24_white: R.drawable.ic_baseline_access_time_24;
        if (id == R.id.menu_bar_item_1 || id == R.id.menu_bar_item_1_R) {
            //ic_my_nav_home_001_w
            return isActive ? R.drawable.ic_my_nav2_home_001: R.drawable.ic_my_nav_home_001;
        } else if (id == R.id.menu_bar_item_2 || id == R.id.menu_bar_item_2_R) {
            //ic_my_nav_note_002_w
            return isActive ? R.drawable.ic_my_nav2_note_002: R.drawable.ic_my_nav_note_002;
        } else if (id == R.id.menu_bar_item_3 || id == R.id.menu_bar_item_3_R) {
            //ic_my_nav_book_003_w
            return isActive ? R.drawable.ic_my_nav2_book_003: R.drawable.ic_my_nav_book_003;
        } else if (id == R.id.menu_bar_item_4 || id == R.id.menu_bar_item_4_R) {
            //ic_my_nav_pdf_004_w
            return isActive ? R.drawable.ic_my_nav2_pdf_004: R.drawable.ic_my_nav_pdf_004;
        } else if (id == R.id.menu_bar_item_5 || id == R.id.menu_bar_item_5_R) {
            //ic_my_nav_file_005_w
            return isActive ? R.drawable.ic_my_nav2_storage_005: R.drawable.ic_my_nav_file_005;
        } else if (id == R.id.menu_bar_item_6 || id == R.id.menu_bar_item_6_R) {
            //ic_my_nav_apps_006_w
            return isActive ? R.drawable.ic_my_nav2_apps_006: R.drawable.ic_my_nav_apps_006;
        } else if (id == R.id.menu_bar_item_7 || id == R.id.menu_bar_item_7_R) {
            //ic_my_nav_setting_007_w
            return isActive ? R.drawable.ic_my_nav2_settings_007 : R.drawable.ic_my_nav_setting_007;
        }
        return 0;
    }
    public int getEqualIconId(int id) {
        if (id == R.id.menu_bar_item_1) {
            return R.id.menu_bar_item_1_R;
        } else if (id == R.id.menu_bar_item_2) {
            return R.id.menu_bar_item_2_R;
        } else if (id == R.id.menu_bar_item_3) {
            return R.id.menu_bar_item_3_R;
        } else if (id == R.id.menu_bar_item_4) {
            return R.id.menu_bar_item_4_R;
        } else if (id == R.id.menu_bar_item_5) {
            return R.id.menu_bar_item_5_R;
        } else if (id == R.id.menu_bar_item_6) {
            return R.id.menu_bar_item_6_R;
        } else if (id == R.id.menu_bar_item_7) {
            return R.id.menu_bar_item_7_R;
        } else if (id == R.id.menu_bar_item_1_R) {
            return R.id.menu_bar_item_1;
        } else if (id == R.id.menu_bar_item_2_R) {
            return R.id.menu_bar_item_2;
        } else if (id == R.id.menu_bar_item_3_R) {
            return R.id.menu_bar_item_3;
        } else if (id == R.id.menu_bar_item_4_R) {
            return R.id.menu_bar_item_4;
        } else if (id == R.id.menu_bar_item_5_R) {
            return R.id.menu_bar_item_5;
        } else if (id == R.id.menu_bar_item_6_R) {
            return R.id.menu_bar_item_6;
        } else if (id == R.id.menu_bar_item_7_R) {
            return R.id.menu_bar_item_7;
        }
        return 0;
    }

    private void showPopupMenu(View view) {
        if (false) {
            PopupMenu popup = new PopupMenu(getMyActivity(), view);
//            try {
//                Field field = popup.getClass().getDeclaredField("mPopup");
//                field.setAccessible(true);
//                MenuPopupHelper helper = (MenuPopupHelper) field.get(popup);
//                helper.setForceShowIcon(true);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            int menuId = R.menu.popup_directory; //.popup_directory;
            popup.getMenuInflater().inflate(menuId, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(android.view.MenuItem menuItem) {
                    return false;
                }
            });
            popup.show();
        } else {
            //see LineWidthDialog
            CopyCutMenuDialog.show(getMyActivity(), view, new CopyCutMenuDialog.WidthChangedListener() {
                @Override
                public void onWidthChanged(float value) {

                }
            });
        }
    }

    private void onUpdateMenu() {
        if (false) {
            //not good
            FragmentManager fragmentManager = getMyActivity().getSupportFragmentManager();
            if (fragmentManager.getFragments().size() - 1 >= 0) {
                Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
                if (currentFragment instanceof DirectoryFragment2) {
                    //show
                    findViewById(R.id.btnMore).setVisibility(View.VISIBLE);
                } else if (currentFragment instanceof NoteFragment2) {
                    //show
                    findViewById(R.id.btnMore).setVisibility(View.VISIBLE);
                } else if (currentFragment instanceof HomeFragment) {
                    //hide
                    findViewById(R.id.btnMore).setVisibility(View.GONE);
                } else {
                    //hide
                    findViewById(R.id.btnMore).setVisibility(View.GONE);
                }
            }
        } else {
            if (NEW_HOME) {
                findViewById(R.id.ll_btnMore).setVisibility(View.GONE);
                findViewById(R.id.ll_btnSearch).setVisibility(View.GONE);
            } else {
                if (this.currentTabId == R.id.menu_bar_item_2 ||
                        this.currentTabId == R.id.menu_bar_item_2_R) {
                    //note, show
                    findViewById(R.id.ll_btnMore).setVisibility(View.VISIBLE);
                } else if (this.currentTabId == R.id.menu_bar_item_5 ||
                        this.currentTabId == R.id.menu_bar_item_5_R) {
                    //storage, show
                    findViewById(R.id.ll_btnMore).setVisibility(View.VISIBLE);
                } else {
                    //hide
                    findViewById(R.id.ll_btnMore).setVisibility(View.GONE);
                }
                if (this.currentTabId == R.id.menu_bar_item_7 ||
                        this.currentTabId == R.id.menu_bar_item_7_R) {
                    //settings hide
                    findViewById(R.id.ll_btnSearch).setVisibility(View.GONE);
                } else if (this.currentTabId == R.id.menu_bar_item_5 ||
                        this.currentTabId == R.id.menu_bar_item_5_R) {
                    //storage fragment hide
                    findViewById(R.id.ll_btnSearch).setVisibility(View.GONE);

                    try {
                        if (mDirectoryFragment != null) {
                            mDirectoryFragment.cancelSelect();
                        }
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                } else {
                    findViewById(R.id.ll_btnSearch).setVisibility(View.VISIBLE);
                }
            }
        }
    }

}