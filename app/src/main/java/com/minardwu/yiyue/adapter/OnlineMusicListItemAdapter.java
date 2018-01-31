package com.minardwu.yiyue.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.application.YiYueApplication;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.service.PlayService;
import com.minardwu.yiyue.utils.CoverLoader;
import com.minardwu.yiyue.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MinardWu on 2017/12/30.
 */

public class OnlineMusicListItemAdapter extends BaseAdapter {

    private long playingMusicId;
    private int playingMusicPosition = -1;
    private boolean justIn = true;
    private List<MusicBean> list = new ArrayList<MusicBean>();

    public OnlineMusicListItemAdapter(List<MusicBean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view==null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_localmusic,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //网络歌曲列表界面第一次创建UI，因为有很多不同的列表，所以不能用position而是用歌曲id
        //而当点击歌曲时更新UI就不能再使用歌曲id了，因为有时网络加载播放较慢，不能及时获取到你点击的歌曲的id，所以这时用position速度较快
        if ((list.get(position).getId()==playingMusicId&&justIn)||position==playingMusicPosition) {
            viewHolder.v_Playing.setVisibility(View.VISIBLE);
            viewHolder.tv_count.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.colorGreenDeep));
            viewHolder.tv_Title.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.colorGreenDeep));
            viewHolder.tv_Artist.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.colorGreenDeep));
        } else {
            viewHolder.v_Playing.setVisibility(View.INVISIBLE);
            viewHolder.tv_count.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.grey));
            viewHolder.tv_Title.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.black_l));
            viewHolder.tv_Artist.setTextColor(YiYueApplication.getAppContext().getResources().getColor(R.color.grey));
        }
        MusicBean music = list.get(position);
        viewHolder.tv_Title.setText(music.getTitle());
        viewHolder.tv_count.setText(position+1+"");
        String artist = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        viewHolder.tv_Artist.setText(artist);
        viewHolder.iv_More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mListener != null) {
//                    mListener.onMoreClick(position);
//                }
            }
        });
        viewHolder.v_Divider.setVisibility(position != AppCache.getLocalMusicList().size() - 1 ? View.VISIBLE : View.GONE);
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.v_playing)
         View v_Playing;
        @BindView(R.id.iv_cover)
         ImageView iv_Cover;
        @BindView(R.id.tv_count)
         TextView tv_count;
        @BindView(R.id.tv_title)
         TextView tv_Title;
        @BindView(R.id.tv_artist)
         TextView tv_Artist;
        @BindView(R.id.iv_more)
         ImageView iv_More;
        @BindView(R.id.v_divider)
         View v_Divider;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void updatePlayingMusicId(PlayOnlineMusicService playOnlineMusicService) {
        if (playOnlineMusicService.getPlayingMusic() != null) {
            playingMusicId = playOnlineMusicService.getPlayingMusic().getId();
        } else {
            playingMusicId = -1;
        }
    }

    public void updatePlayingMusicPosition(int position) {
        justIn = false;
        playingMusicPosition = position;
    }

    public int getPlayingMusicPosition() {
        return playingMusicPosition;
    }

    public List<MusicBean> getTargetList() {
        return list;
    }

}