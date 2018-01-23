package com.minardwu.yiyue.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.StopTimeItemAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.service.QuitTimer;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.ToastUtils;

/**
 * Created by MinardWu on 2018/1/8.
 */

public class StopTimeDialog extends Dialog {

    ListView listView;
    StopTimeItemAdapter stopTimeItemAdapter;
    ImageView iv_quitTillSongEnd;

    public StopTimeDialog(@NonNull Context context) {
        super(context);
    }

    public StopTimeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public StopTimeDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_stoptime,null);
        setContentView(view);
        iv_quitTillSongEnd = findViewById(R.id.iv_quitTillSongEnd);
        iv_quitTillSongEnd.setSelected(Preferences.getQuitTillSongEnd());
        listView = findViewById(R.id.lv_stop_time);
        stopTimeItemAdapter = new StopTimeItemAdapter(getContext(), R.array.stop_time);
        stopTimeItemAdapter.setShowImagePosition(Preferences.getStopTime());
        listView.setAdapter(stopTimeItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                stopTimeItemAdapter.setShowImagePosition(i);
                Preferences.saveStopTime(i);
                dismiss();
                if(i==0){
                    AppCache.getPlayService().resetOnCompletion();
                    AppCache.getPlayOnlineMusicService().resetOnCompletion();
                    QuitTimer.getInstance().stop();
                    ToastUtils.show("定时停止播放已停止");
                }else {
                    QuitTimer.getInstance().start(i*10*1000);
                    ToastUtils.show("将在"+i*10+"分钟后停止播放");
                }
            }
        });
        iv_quitTillSongEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iv_quitTillSongEnd.isSelected()){
                    Preferences.setQuitTillSongEnd(false);
                    iv_quitTillSongEnd.setSelected(false);
                }else {
                    Preferences.setQuitTillSongEnd(true);
                    iv_quitTillSongEnd.setSelected(true);
                }
            }
        });
    }
}
