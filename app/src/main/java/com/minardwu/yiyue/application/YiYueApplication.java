package com.minardwu.yiyue.application;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.minardwu.yiyue.utils.CoverLoader;
import com.minardwu.yiyue.utils.FileUtils;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.ToastUtils;

import okhttp3.OkHttpClient;

/**
 * Created by MinardWu on 2017/12/30.
 */

public class YiYueApplication extends Application{

    private static Context context;
    public static boolean isJustIntoAppAndNotPlay;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Preferences.init(getApplicationContext());
        ToastUtils.init(getApplicationContext());
        CoverLoader.getInstance().init(getApplicationContext());
        Stetho.initializeWithDefaults(this);
        //获取MediaStore中的音乐信息，但MediaStore有可能没有更新到最新的状态
        AppCache.updateLocalMusicList();
        //开启一个新线程，更新MediaStore，并获取最新信息
        FileUtils.scanAll();
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        Fresco.initialize(this);
    }

    public static Context getAppContext(){
        return context;
    }
}
