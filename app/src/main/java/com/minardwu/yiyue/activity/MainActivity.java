package com.minardwu.yiyue.activity;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.os.Build;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.adapter.DrawerItemAdapter;
import com.minardwu.yiyue.application.AppCache;
import com.minardwu.yiyue.event.ChageToolbarTextEvent;
import com.minardwu.yiyue.fragment.LocalMusicFragment;
import com.minardwu.yiyue.fragment.OnlineMusicFragment;
import com.minardwu.yiyue.model.DrawerItemBean;
import com.minardwu.yiyue.model.MusicBean;
import com.minardwu.yiyue.service.EventCallback;
import com.minardwu.yiyue.service.OnPlayerEventListener;
import com.minardwu.yiyue.service.PlayOnlineMusicService;
import com.minardwu.yiyue.service.PlayService;
import com.minardwu.yiyue.service.QuitTimer;
import com.minardwu.yiyue.utils.Notifier;
import com.minardwu.yiyue.utils.ParseUtils;
import com.minardwu.yiyue.utils.Preferences;
import com.minardwu.yiyue.utils.ToastUtils;
import com.minardwu.yiyue.widget.StopTimeDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private int currentFragment;
    private DrawerItemAdapter drawerItemAdapter;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private List<DrawerItemBean> drawerItemBeanList;
    private List<android.support.v4.app.Fragment> fragmentList;

    @BindView(R.id.tv_toolbar) TextView tv_toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.iv_search) ImageView iv_search;
    @BindView(R.id.iv_menu) ImageView iv_menu;
    @BindView(R.id.iv_localmusic) ImageView iv_localmusic;
    @BindView(R.id.iv_onlinemusic) ImageView iv_onlinemusic;
    @BindView(R.id.lv_drawer) ListView listView;
    @BindView(R.id.vp) ViewPager viewPager;


    @OnClick(R.id.iv_search) void startSearch(){

    }

    @OnClick(R.id.iv_menu) void openDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
        QuitTimer.getInstance().init(new EventCallback<Long>() {
            @Override
            public void onEvent(Long aLong) {
                Log.e(TAG, ParseUtils.formatTime("(mm:ss)",aLong));
            }
        });
        Notifier.init(getPlayService());
    }


    private void initData() {
        drawerItemBeanList = new ArrayList<DrawerItemBean>();
        drawerItemBeanList.add(new DrawerItemBean(R.drawable.icon_night,"夜间模式                     开"));
        drawerItemBeanList.add(new DrawerItemBean(R.drawable.icon_clock,"定时停止播放"));
        drawerItemBeanList.add(new DrawerItemBean(R.drawable.icon_balancer,"音效调节"));
        drawerItemBeanList.add(new DrawerItemBean(R.drawable.icon_mobiledata,"移动网络下载"));
        drawerItemBeanList.add(new DrawerItemBean(R.drawable.icon_filter,"文件时间过滤"));
        drawerItemBeanList.add(new DrawerItemBean(R.drawable.icon_filter,"文件大小过滤"));
        drawerItemBeanList.add(new DrawerItemBean(R.drawable.icon_info,"关于"));
        drawerItemBeanList.add(new DrawerItemBean(R.drawable.icon_exit,"退出应用"));

        fragmentList = new ArrayList<android.support.v4.app.Fragment>();
        fragmentList.add(new LocalMusicFragment());
        fragmentList.add(new OnlineMusicFragment());
    }

    private void initView(){
        tv_toolbar.setText(AppCache.getLocalMusicList().get(Preferences.getCurrentSongPosition()).getTitle());
        iv_localmusic.setSelected(true);
        //4.4以上、5.0以下的需要为drawlayout设置沉浸式
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            //将侧边栏顶部延伸至status bar
            drawerLayout.setFitsSystemWindows(true);
            //将主页面顶部延伸至status bar;虽默认为false,但经测试,DrawerLayout需显示设置
            drawerLayout.setClipToPadding(false);
        }
        //初始化侧边栏
        drawerItemAdapter = new DrawerItemAdapter(this,R.layout.list_drawer,drawerItemBeanList);
        listView.setAdapter(drawerItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 1:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        StopTimeDialog stopTimeDialog = new StopTimeDialog(MainActivity.this,R.style.StopTimeDialog);
                        stopTimeDialog.show();
                        break;
                }
            }
        });
        //初始化ViewPaper
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tv_toolbar.setText(Preferences.getCurrentSongTitle());
                        iv_localmusic.setSelected(true);
                        iv_onlinemusic.setSelected(false);
                        currentFragment = 0;
                        break;
                    case 1:
                        tv_toolbar.setText("易乐FM");
                        iv_localmusic.setSelected(false);
                        iv_onlinemusic.setSelected(true);
                        currentFragment = 1;
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChageToolbarTextEvent event) {
        tv_toolbar.setText(event.getMusicBean().getTitle());
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
