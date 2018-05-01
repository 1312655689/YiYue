package com.minardwu.yiyue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.activity.ArtistActivity;
import com.minardwu.yiyue.activity.MainActivity;
import com.minardwu.yiyue.adapter.ImageAndTextAdapter;
import com.minardwu.yiyue.adapter.OnlineMusicRecycleViewAdapter;
import com.minardwu.yiyue.db.MyDatabaseHelper;
import com.minardwu.yiyue.event.UpdateOnlineMusicListPositionEvent;
import com.minardwu.yiyue.executor.IView;
import com.minardwu.yiyue.executor.MoreOptionOfCollectedSongExecutor;
import com.minardwu.yiyue.model.MusicBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class CollectedSongFragment extends CollectionBaseFragment implements IView{

    private OnlineMusicRecycleViewAdapter adapter;
    private List<MusicBean> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setVisibility(list.size()>0?View.VISIBLE:View.GONE);
        empty_view.setVisibility(list.size()>0?View.GONE:View.VISIBLE);
        return view;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        list = MyDatabaseHelper.init(getActivity()).queryCollectedSong();
        adapter = new OnlineMusicRecycleViewAdapter(list);
        adapter.setOnRecycleViewClickListener(new OnlineMusicRecycleViewAdapter.OnRecycleViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position == 0){
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    intent.putExtra(MainActivity.INDEX,MainActivity.ONLINE);
                    startActivity(intent);
                    playOnlineMusicService.playMusicList(list);
                }else if(playOnlineMusicService.getPlayingMusicId().equals(list.get(position-1).getId()+"")){
                    getActivity().finish();
                }else {
                    playOnlineMusicService.stop();
                    playOnlineMusicService.play((int) adapter.getMusicList().get(position-1).getId());
                    playOnlineMusicService.updatePlayingMusicPosition(position-1);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onMoreClick(View view, final int musicPosition) {
                final OptionDialogFragment fragment = new OptionDialogFragment();
                fragment.setHeader_titile("歌曲：");
                fragment.setHeader_text(list.get(musicPosition-1).getTitle());
                fragment.setListViewAdapter(new ImageAndTextAdapter(getActivity(),R.array.collected_song_more_img,R.array.collected_song_more_text));
                fragment.setOptionDialogFragmentClickListener(new OptionDialogFragment.OptionDialogFragmentClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        fragment.dismiss();
                        //点击播放的话直接使用上面的逻辑即可
                        if (position==0){
                            onItemClick(view,musicPosition);
                        }else{
                            MoreOptionOfCollectedSongExecutor.execute(getActivity(),position,list.get(musicPosition-1),CollectedSongFragment.this);
                        }
                    }
                });
                fragment.show(getActivity().getSupportFragmentManager(), "OptionDialogFragment");
            }
        });
        return adapter;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateOnlineMusicListPositionEvent(UpdateOnlineMusicListPositionEvent event){
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void updateViewForExecutor() {
        list.clear();
        list.addAll(MyDatabaseHelper.init(getActivity()).queryCollectedSong());
        recyclerView.setVisibility(list.size()>0?View.VISIBLE:View.GONE);
        empty_view.setVisibility(list.size()>0?View.GONE:View.VISIBLE);
        adapter.notifyDataSetChanged();
    }
}