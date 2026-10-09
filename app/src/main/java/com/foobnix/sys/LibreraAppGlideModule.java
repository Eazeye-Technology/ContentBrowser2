package com.foobnix.sys;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Priority;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.foobnix.android.utils.LOG;

import com.foobnix.LibreraApp;
import com.foobnix.ui2.MainTabs2;

import java.io.InputStream;

import static com.bumptech.glide.load.engine.executor.GlideExecutor.newSourceBuilder;

//search GlideBuilder
@GlideModule(glideName = "LibreraAppGlideModuleName")
public class LibreraAppGlideModule extends AppGlideModule {
    static int loadingNumber = 0;

    static Bitmap cache;
    static String path;

    final static ModelLoader<String, Bitmap> modelLoader = new ModelLoader<String, Bitmap>() {
        @Nullable
        @Override
        public LoadData<Bitmap> buildLoadData(@NonNull final String s, int width, int height, @NonNull Options options) {

            final ObjectKey sourceKey = new ObjectKey(s);
            LOG.d("LibreraAppGlideModule sourceKey", s, sourceKey.hashCode());


            return new LoadData<>(sourceKey, new DataFetcher<Bitmap>() {
                volatile boolean isCanced = false;

                @Override
                public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Bitmap> callback) {
                    try {

                        if (isCanced) {  //this will stop reload
                            return;
                        }
                        if (MainTabs2.MOD_VERSION) {
                            //skip
                        } else {
                            if (s.equals(path)) {
                                callback.onDataReady(cache);
                                return;
                            }
                        }
                        loadingNumber++;
//                        if (loadingNumber > 20) {
//                            return;
//                        }
                        Log.e("LibreraAppGlideModule", "loadingNumber == " + loadingNumber);

                        LOG.d("LibreraAppGlideModule loadData", s);
                        if (MainTabs2.MOD_VERSION) {
                            //skip
                        }
                        final InputStream stream = ImageExtractor.getInstance(LibreraApp.context).getStream(s, null);
                        if (stream instanceof InputStreamBitmap) {
                            Bitmap bitmap = ((InputStreamBitmap) stream).getBitmap();
                            if (bitmap == null) {
                                if (MainTabs2.MOD_VERSION) {
                                    Thread.sleep(10);
                                } else {
                                    Thread.sleep(250);
                                }
                                bitmap = ImageExtractor.getInstance(LibreraApp.context).proccessOtherPage(s);
                                if (bitmap == null) {
                                    LOG.d("Bitmap-test-1-cancel", bitmap, s);
                                    callback.onDataReady(ImageExtractor.messageFileBitmap("#error null", ""));
                                    return;
                                }
                            }

                            if (isCanced) {  //this will stop reload
                                LOG.d("Bitmap-test-1-cancel", bitmap, s);
                                bitmap.recycle();
                                return;
                            }

                            LOG.d("Bitmap-test-1", bitmap, bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                            callback.onDataReady(bitmap);

                            path = s;
                            cache = bitmap;
                        } else {
                            callback.onDataReady(BitmapFactory.decodeStream(stream));
                        }
                        LOG.d("LibreraAppGlideModule onDataReady", stream);

                    } catch (Exception e) {
                        callback.onDataReady(ImageExtractor.messageFileBitmap("#error", ""));
                        LOG.e(e);
                    }

                }

                @Override
                public void cleanup() {
                    //isCanced = true; //this will stop reload
                    LOG.d("LibreraAppGlideModule cleanup", s);


                }

                @Override
                public void cancel() {
                    //isCanced = true; //this will stop reload
                    LOG.d("LibreraAppGlideModule cancel", s);

                }

                @NonNull
                @Override
                public Class<Bitmap> getDataClass() {
                    return Bitmap.class;
                }

                @NonNull
                @Override
                public DataSource getDataSource() {
                    return DataSource.LOCAL;
                }
            });
        }

        @Override
        public boolean handles(@NonNull String s) {
            LOG.d("LibreraAppGlideModule handles", s);
            //all file use this filter(???)
            return true;//s.startsWith("{");
            //return s != null && s.startsWith("{");
        }
    };

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
        builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565));
        if (MainTabs2.MOD_VERSION) {
            Log.e("applyOptions", "applyOptions");
            builder.setMemoryCache(new LruResourceCache(20 * 1024 * 1024L)); //FIXME:added
            builder.setBitmapPool(new LruBitmapPool(20 * 1024 * 1024L)); //FIXME:added
        }
        builder.setSourceExecutor(
                newSourceBuilder()
                        .setUncaughtThrowableStrategy(GlideExecutor.UncaughtThrowableStrategy.IGNORE)
                        .setThreadCount(1) //4
                        //.setThreadTimeoutMillis(2000)
                        .build());


    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        LOG.d("LibreraAppGlideModule registerComponents");
        registry.prepend(String.class, Bitmap.class, new ModelLoaderFactory<String, Bitmap>() {
            @NonNull
            @Override
            public ModelLoader<String, Bitmap> build(@NonNull MultiModelLoaderFactory multiFactory) {
                return modelLoader;
            }

            @Override
            public void teardown() {

            }
        });

    }


}