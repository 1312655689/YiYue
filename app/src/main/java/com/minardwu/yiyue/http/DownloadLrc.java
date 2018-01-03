package com.minardwu.yiyue.http;

import android.net.Uri;
import android.util.Log;

import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.utils.FileUtils;
import com.minardwu.yiyue.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MinardWu on 2018/1/3.
 */

public abstract class DownloadLrc implements DownloadLrcListener{

//    private static DownloadLrc myOkHttp;
//    private static DownloadLrcListener downloadLrcListener;

//    private DownloadLrc(){}
//
//    public static DownloadLrc getInstance(){
//        synchronized (DownloadLrc.class){
//            if (myOkHttp==null){
//                synchronized (DownloadLrc.class){
//                    if(myOkHttp==null){
//                        myOkHttp = new DownloadLrc();
//                    }
//                }
//            }
//        }
//        return myOkHttp;
//    }

//    public DownloadLrcListener getDownloadLrcListener() {
//        return downloadLrcListener;
//    }
//
//    public void setDownloadLrcListener(DownloadLrcListener downloadLrcListener) {
//        this.downloadLrcListener = downloadLrcListener;
//    }

    private String artist;
    private String title;

    public DownloadLrc(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    @Override
    public void execute() {
        downloadLrcPrepare();
        getLrcUrl();
    }


    private void getLrcUrl() {
        final String getLrcUrl = "http://gecimi.com/api/lyric/"+title+"/"+artist;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(getLrcUrl).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                downloadLrcFail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject root = new JSONObject(response.body().string());
                    JSONArray jsonArray = root.getJSONArray("result");
                    String lrcUrl = jsonArray.getJSONObject(0).getString("lrc");
                    downloadLrc(lrcUrl,FileUtils.getLrcDir());
                } catch (JSONException e) {
                    e.printStackTrace();
                    downloadLrcFail(e.toString());
                }
            }
        });
    }


    private void downloadLrc(String lrcUrl, final String lrcDir){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(lrcUrl).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                downloadLrcFail("downloadFail:"+e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(lrcDir, artist+" - "+title+".lrc");
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    downloadLrcSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                    downloadLrcFail("createFileFail:"+e.toString());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }

            }
        });
    }

}