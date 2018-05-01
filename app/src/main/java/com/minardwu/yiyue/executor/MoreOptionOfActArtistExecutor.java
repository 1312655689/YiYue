package com.minardwu.yiyue.executor;

import android.app.Activity;
import android.content.Intent;

import com.minardwu.yiyue.activity.AlbumActivity;
import com.minardwu.yiyue.activity.ArtistActivity;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.model.MusicBean;

/**
 * Created by MinardWu on 2018/3/15.
 */

public class MoreOptionOfActArtistExecutor {
    public static void execute(Activity activity,int position, MusicBean musicBean){
        switch (position){
            case 0:
                //position==0逻辑放在外面执行，不会跳到这里
                break;
            case 1:
                Intent albumIntent = new Intent(activity, AlbumActivity.class);
                albumIntent.putExtra("albumId",musicBean.getAlbumId());
                albumIntent.putExtra("albumName",musicBean.getAlbum());
                activity.startActivity(albumIntent);
                break;
        }
    }
}