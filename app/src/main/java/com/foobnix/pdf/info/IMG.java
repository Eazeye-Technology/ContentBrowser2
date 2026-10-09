package com.foobnix.pdf.info;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.foobnix.android.utils.Dips;
import com.foobnix.android.utils.LOG;
import com.foobnix.model.AppState;
import com.foobnix.pdf.info.wrapper.MagicHelper;
import com.txkj.contentbrowser2.R;
import com.foobnix.pdf.search.activity.HorizontalViewActivity;
import com.foobnix.sys.ImageExtractor;
import com.foobnix.ui2.MainTabs2;

import com.foobnix.LibreraApp;

import org.ebookdroid.ui.viewer.VerticalViewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class IMG {

    public static final float WIDTH_DK = 1.4f;
    public static final int DP5 = -Dips.dpToPx(40);
    public static final Config BMP_CFG = Config.RGB_565;
    public static final int TWO_LINE_COVER_SIZE = 74;
    private static final ColorDrawable COLOR_DRAWABLE = new ColorDrawable(Color.LTGRAY);
    public static boolean RESET_VIEW_BEFORE_LOADING = true;
    public static Drawable bookBGWithMark;
    public static Drawable bookBGNoMark;
    public static Context context;
    private static String pattern = Pattern.quote("||");

    public static void init(Context context) {

        IMG.context = context;

        bookBGWithMark = context.getResources().getDrawable(R.drawable.bookeffect2);
        bookBGNoMark = context.getResources().getDrawable(R.drawable.bookeffect1);

    }

    public static int getImageSize() {
        if (MainTabs2.MOD_VERSION) {
            //return IMG_SIZE;
            //see ImageExtractor.decodeImage()
            return Dips.dpToPx(Math.max(AppState.get().coverSmallSize, AppState.get().coverBigSize));
        } else {
            return Dips.dpToPx(Math.max(AppState.get().coverSmallSize, AppState.get().coverBigSize));
        }
    }

    public static void updateLayoutHeightSizeSmall(ViewGroup imageView) {
        if (imageView == null || imageView.getLayoutParams() == null) {
            return;
        }
        int widht = Dips.dpToPx(AppState.get().coverSmallSize);
        LayoutParams lp = imageView.getLayoutParams();
        lp.height = (int) (widht * WIDTH_DK);
    }

    public static void updateLayoutHeightSizeBig(ViewGroup imageView) {
        if (imageView == null || imageView.getLayoutParams() == null) {
            return;
        }
        int widht = Dips.dpToPx(AppState.get().coverBigSize);
        LayoutParams lp = imageView.getLayoutParams();
        lp.height = (int) (widht * WIDTH_DK);
    }

    public static LayoutParams updateImageSizeSmall(View imageView) {
        if (imageView == null || imageView.getLayoutParams() == null) {
            return null;
        }
        LayoutParams lp = imageView.getLayoutParams();
        lp.width = Dips.dpToPx(AppState.get().coverSmallSize);
        lp.height = (int) (lp.width * WIDTH_DK);
        return lp;
    }

    public static LayoutParams updateImageSizeSmallDir(View imageView) {
        if (imageView == null || imageView.getLayoutParams() == null) {
            return null;
        }
        LayoutParams lp = imageView.getLayoutParams();
        lp.width = (int) (Dips.dpToPx(AppState.get().coverSmallSize) / 1.5);
        lp.height = (int) (lp.width * WIDTH_DK);
        return lp;
    }

    public static void updateImageSizeBig(View imageView) {
        if (imageView == null || imageView.getLayoutParams() == null) {
            return;
        }

        LayoutParams lp = imageView.getLayoutParams();
        lp.width = Dips.dpToPx(AppState.get().coverBigSize);
        lp.height = (int) (lp.width * WIDTH_DK);
    }

    public static void updateImageSizeBig(View imageView, int sizeDP) {
        if (imageView == null || imageView.getLayoutParams() == null) {
            return;
        }

        LayoutParams lp = imageView.getLayoutParams();
        lp.width = Dips.dpToPx(sizeDP);
        lp.height = (int) (lp.width * WIDTH_DK);
    }

    public static int alphaColor(int persent, String color) {
        try {
            int parseColor = Color.parseColor(color);
            int alpha = 255 * persent / 100;
            return Color.argb(alpha, Color.red(parseColor), Color.green(parseColor), Color.blue(parseColor));
        } catch (Exception e) {
            return Color.WHITE;
        }
    }

    public static void clearMemoryCache() {
        if (true) {
            if (LibreraApp.context != null) {
                Glide.get(LibreraApp.context).clearMemory();
            }
        }
    }

    public static RequestManager with(Context a) {
        if (a instanceof HorizontalViewActivity) {
            return Glide.with((HorizontalViewActivity) a);
        } else if (a instanceof VerticalViewActivity) {
            return Glide.with((VerticalViewActivity) a);
        } else if (a instanceof MainTabs2) {
            return Glide.with((MainTabs2) a);
        } else {
            return Glide.with(LibreraApp.context);
        }
    }

    public static void pauseRequests(Context a) {
        LOG.d("Glide-pause", a);
        //with(a).pauseRequests();
    }

    public static void resumeRequests(Context a) {
        LOG.d("Glide-resume", a);
        //with(a).resumeRequests();
    }

    public static void clear(ImageView image) {
        try {
            LOG.d("Glide-clear", image.getContext());
            Activity activity = ((Activity) image.getContext());
            if (Build.VERSION.SDK_INT < 17 || !activity.isDestroyed()) {
                with(image.getContext()).clear(image);
            }
        } catch (Exception e) {
            LOG.e(e);
        }
    }

    public static void clear(Context c, Target t) {
        LOG.d("Glide-clear", c);
        try {
            with(c).clear(t);
        } catch (Exception e) {
            LOG.e(e);
        }

    }

    public static void clearDiscCache() {
        if (true) {
            new Thread("@T clearDiscCache") {
                @Override
                public void run() {
                    try {
                        if (LibreraApp.context != null) {
                            Glide.get(LibreraApp.context).clearDiskCache();
                        }
                    } catch (Exception e) {
                        LOG.e(e);
                    }
                }
            }.start();
        }
    }

    public static void clearCache(String path) {
        try {


            String url = IMG.toUrl(path, ImageExtractor.COVER_PAGE, IMG.getImageSize());
            //Glide.get(LibreraApp.context).clearMemory();
            //Glide.get(LibreraApp.context).getRegistry()

        } catch (Exception e) {
            LOG.e(e);
        }
    }

    public static String getRealPathFromURI(final Context c, final Uri contentURI) {
        final Cursor cursor = c.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            final int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public static void getCoverPage(ImageView img, String path, int width) {
        try {
            final String url = IMG.toUrl(path, ImageExtractor.COVER_PAGE, width);
            Glide.with(LibreraApp.context).asBitmap().load(url).into(img);
        } catch (Exception e) {
            LOG.e(e);
        }
    }

    public static interface ResourceReady {
        void onResourceReady(Bitmap bitmap);
    }

    //search LruBitmapPool
    private static List<Bitmap> globalRemovedBitmap = new ArrayList<>();
    private static Map<String, Bitmap> globalBitmap = new HashMap<String, Bitmap>();
    private static Map<String, Integer> globalStatus = new HashMap<String, Integer>();
//    private static Map<String, Integer> globalNoCache = new HashMap<String, Integer>();
//    private Map<Integer, GridViewHolder> globalHolder = new HashMap<>();
    private final static int IMG_SIZE = 100;
    public static boolean enableGetCover = true;
//    public static void setNoCache(String path) {
//        globalNoCache.put(path, 2);
//    }
    public static void getCoverPageWithEffect(ImageView img, String path, int width, ResourceReady run) {
        if (true) { //if (enableGetCover) {
            if (img != null) {
                //Integer noCache = globalNoCache.get(path);
                if (globalStatus.get(path) != null && globalStatus.get(path).equals(2) /*&&
                        (noCache == null || noCache.intValue() <= 0)*/ ) { //fileMeta.bmpBGStatus == 2) {
                    Log.e("cover", "recycled bitmap gen 1, " + globalBitmap.get(path));
                    img.setImageBitmap(globalBitmap.get(path));//fileMeta.bmpBG);
                } else if (globalStatus.get(path) == null || globalStatus.get(path).equals(0) /*||
                        (noCache != null && noCache.intValue() > 0)*/) { //fileMeta.bmpBGStatus == 0) {
//                    if (noCache != null) {
//                        globalNoCache.put(path, noCache.intValue() - 1);
//                    }
                    Log.e("cover", "setImageBitmap 2");
                    globalStatus.put(path, 1); //fileMeta.bmpBGStatus = 1;
                    File file = new File(path);
                    if (!file.exists() || !file.canRead()) {
                        return;
                    }
                    //String url = IMG.toUrl(path, ImageExtractor.COVER_PAGE, IMG_SIZE);//width);
                    String url = IMG.toUrl(path, ImageExtractor.COVER_PAGE, width);//width);
                    LOG.d("Bitmap-test-load", path);
                    IMG.with(img.getContext())
                            .asBitmap()
                            .load(url)
                            //.override(100, 100)//Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .diskCacheStrategy(DiskCacheStrategy.NONE) //RESOURCE)
                            .skipMemoryCache(true) //false) //true)
                            .listener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                    LOG.d("Bitmap-test-2", "failed");

                                    globalBitmap.put(path, null);//fileMeta.bmpCover = bitmap;
                                    globalStatus.put(path, 2);//fileMeta.bmpCoverStatus = 2;
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap bitmap, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                    target.onResourceReady(bitmap, null);
                                    LOG.d("Bitmap-test-2", bitmap, bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

                                    if (run != null) {
                                        run.onResourceReady(bitmap);//null);
                                    }

                                    globalBitmap.put(path, bitmap);//fileMeta.bmpCover = bitmap;
                                    globalStatus.put(path, 2);//fileMeta.bmpCoverStatus = 2;
                                    return true;
                                }
                            })
                            .into(img);
                }
            }
        }
    }
    public static void clearCoverPage(String path) {
        if (globalBitmap.containsKey(path)) {
            Bitmap bmp = globalBitmap.get(path);
            if (bmp != null && !bmp.isRecycled()) {
                if (false) {
                    bmp.recycle(); //don't do this, crash
                } else {
                    globalRemovedBitmap.add(bmp);
                }
            }
            globalBitmap.remove(path);
        }
        if (globalStatus.containsKey(path)) {
            globalStatus.remove(path);
        }
    }
    public static void clearAllCoverPage() {
//        clearMemoryCache();
//        clearDiscCache();
        if (globalBitmap != null) {
            //not need, bitmap is reused in Glide
//            for (Bitmap bitmap : globalBitmap.values()) {
//                if (bitmap != null) {
//                    try {
//                        if (!bitmap.isRecycled()) {
//                            Log.e("IMG", "recycled bitmap == " + bitmap);
//                            bitmap.recycle();
//                        }
//                    } catch (Throwable eee) {
//                        eee.printStackTrace();
//                    }
//                }
//            }
            globalBitmap.clear();
        }
        if (globalRemovedBitmap != null) {
            //not need, bitmap is reused in Glide
            /*
            for (Bitmap bitmap : globalRemovedBitmap) {
                if (bitmap != null) {
                    try {
                        if (!bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                }
            }*/
            globalRemovedBitmap.clear();
        }
        if (globalStatus != null) {
            globalStatus.clear();
        }
    }
    public static void setScroll(GridView recyclerView, BaseAdapter searchAdapter) {
        recyclerView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    try {
                        Glide.with(LibreraApp.context).resumeRequests(); //simple and faster
                    } catch (Exception e) {
                        LOG.e(e);
                    }
                    if (searchAdapter != null) {
                        searchAdapter.notifyDataSetChanged();
                    }
                } else {
                    try {
                        Glide.with(LibreraApp.context).pauseRequests(); //simple and faster
                    } catch (Exception e) {
                        LOG.e(e);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }


    public static void getCoverPageWithEffectPos(ImageView img, String path, int width, int pos) {
        final String url = IMG.toUrlPos(path, ImageExtractor.COVER_PAGE, width, pos);
        try {
            Glide.with(LibreraApp.context).asBitmap().load(url).into(img);
        } catch (Exception e) {
            LOG.e(e);
        }
    }

    public static String toUrl(final String path, final int page, final int width) {
        PageUrl pdfUrl = new PageUrl(path, page, width, 0, false, false, 0);
        pdfUrl.setUnic(0);
        //pdfUrl.hash = ("" + AppState.get().isBookCoverEffect).hashCode();

        pdfUrl.hash = ("" + AppState.get().isBookCoverEffect + TintUtil.getColorInDayNighth() + AppState.get().sortByBrowse).hashCode() + MagicHelper.hash();

        return pdfUrl.toString();
    }

    public static String toUrlPos(final String path, final int page, final int width, int pos) {
        PageUrl pdfUrl = new PageUrl(path, page, width, 0, false, false, 0);
        return pdfUrl.toString();
    }

    public static PageUrl toPageUrl(final String path, final int page, final int width) {
        return new PageUrl(path, page, width, 0, false, false, 0);
    }

    public static String toUrlWithContext(final String path, final int page, final int width) {
        PageUrl pdfUrl = new PageUrl(path, page, width, 0, false, false, 0);
        return pdfUrl.toString();
    }

    public static String toUrl(final String path, final int page, final int width, int heigth) {
        return new PageUrl(path, page, width, 0, false, false, 0, heigth).toString();
    }

    public static String toUrl(final Uri path, final int page, final int width) {
        return toUrl(path.getPath(), page, width);
    }

    public static String toUrl(final Uri path, final int page, final int width, final int number) {
        return new PageUrl(path.getPath(), page, width, number, false, false, 0).toString();
    }

    public static String[] fromUrl(final String url) {
        return url.split(pattern);
    }
}
