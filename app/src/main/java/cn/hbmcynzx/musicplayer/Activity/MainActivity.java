package cn.hbmcynzx.musicplayer.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.hbmcynzx.musicplayer.Fragment.AlbumFragment;
import cn.hbmcynzx.musicplayer.Fragment.ArtistFragment;
import cn.hbmcynzx.musicplayer.Fragment.MusicFragment;
import cn.hbmcynzx.musicplayer.Database.Music;
import cn.hbmcynzx.musicplayer.Fragment.WebViewFragment;
import cn.hbmcynzx.musicplayer.Service.MusicService;
import cn.hbmcynzx.musicplayer.R;
import cn.hbmcynzx.musicplayer.Adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity implements OnPageChangeListener {
	private static Context context;
	private ActivityReceiver activityReceiver;
	private ImageView music_album;
	private TextView main_music_title, main_music_artist;
	private Button main_play_pause, main_next, main_back;
	private TextView tv_fragment_music,tv_fragment_artist,tv_fragment_album,tv_fragment_web;
	private ViewPager viewPager;
	//private List<Fragment> fragments;
	private LinearLayout ll_fragment_tab;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initReceiver();
		initService();
		initView();
		context = this;
		mainActivity = this;
	}

	private void initReceiver() {
		activityReceiver = new ActivityReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(UPDATE_ACTION);
		registerReceiver(activityReceiver, filter);
	}

	private void initService() {
		Intent service = new Intent();
		service.setClass(this, MusicService.class);
		startService(service);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		ll_fragment_tab= (LinearLayout) findViewById(R.id.ll_fragment_tab);
		initViewPager();
		tv_fragment_music= (TextView) findViewById(R.id.tv_fragment_music);
		tv_fragment_music.setOnClickListener(new Listener(0));
		tv_fragment_artist= (TextView) findViewById(R.id.tv_fragment_artist);
		tv_fragment_artist.setOnClickListener(new Listener(1));
		tv_fragment_album= (TextView) findViewById(R.id.tv_fragment_album);
		tv_fragment_album.setOnClickListener(new Listener(2));
		tv_fragment_web = (TextView) findViewById(R.id.tv_fragment_webview);
		tv_fragment_web.setOnClickListener(new Listener(3));
		main_play_pause = (Button) findViewById(R.id.play_pause);
		main_play_pause.setOnClickListener(this);
		main_next = (Button) findViewById(R.id.next);
		main_next.setOnClickListener(this);
		main_back = (Button) findViewById(R.id.back);
		main_back.setOnClickListener(this);
		main_music_title = (TextView) findViewById(R.id.music_title);
		main_music_artist = (TextView) findViewById(R.id.music_artist);
		music_album = (ImageView) findViewById(R.id.music_album);
		if (currentMusic >= 0) {
			Music music = musicList.get(currentMusic);
			main_music_title.setText(music.getTitle());
			main_music_artist.setText(music.getArtist());
			music_album.setImageBitmap(music.getAlbum_art_bitmap());
		}
	}
	private void initViewPager(){
		viewPager= (ViewPager) findViewById(R.id.viewpager);
		viewPager.setOffscreenPageLimit(3);
		List<Fragment> fragments=new ArrayList<>();
		/*MusicFragment musicFragment=new MusicFragment();
		ArtistFragment artistFragment=new ArtistFragment();
		AlbumFragment albumFragment=new AlbumFragment();
		WebViewFragment webViewFragment=new WebViewFragment();
		fragments.add(musicFragment);
		fragments.add(artistFragment);
		fragments.add(albumFragment);
		fragments.add(webViewFragment);*/
		fragments.addAll(Arrays.asList(
				new MusicFragment(),
				new ArtistFragment(),
				new AlbumFragment(),
				new WebViewFragment()));

		ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager(),fragments);
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(0);
		viewPager.addOnPageChangeListener(this);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		for(int i=0;i<ll_fragment_tab.getChildCount();i++){
			ll_fragment_tab.getChildAt(i).setBackgroundColor(Color.WHITE);
		}
		ll_fragment_tab.getChildAt(position).setBackgroundColor(Color.GREEN);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}


	private class Listener implements View.OnClickListener{
		private int index;
		private Listener(int index){
			this.index=index;
		}
		@Override
		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}
	}

	public void setting(View v) {
		switch (v.getId()) {
		case R.id.setting:
			Intent intent = new Intent();
			intent.setClass(this, SettingActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(activityReceiver);
	}

	public static void exit() {
		WelcomeActivity.inited=false;
		mainActivity.finish();
		if (playActivity != null)
			playActivity.finish();
		settingActivity.finish();
		exit_in30min = false;
		Intent service = new Intent();
		service.setClass(context, MusicService.class);
		context.stopService(service);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent i = new Intent(Intent.ACTION_MAIN);

			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			i.addCategory(Intent.CATEGORY_HOME);

			startActivity(i);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public class ActivityReceiver extends BroadcastReceiver {
		int curMusic= currentMusic;
		@Override
		public void onReceive(Context context, Intent intent) {
			int status = intent.getIntExtra("status", -1);// 获取播放状态
			int currentMusic = intent.getIntExtra("currentMusic", -1);// 获取当前播放曲目
			if (currentMusic >= 0) {
				if (isCurMusicChange(currentMusic)) {// 当前音乐如果改变
					setConstantView(currentMusic);
				} else {// 当前音乐没改变
					setVariableView(status);
				}
			}
		}
		public boolean isCurMusicChange(int currentMusic) {
			if (curMusic == currentMusic) {
				return false;
			} else {
				curMusic = currentMusic;
				return true;
			}
		}
		/**
		 * 设置不随当前音乐变的View
		 */
		public void setConstantView(int currentMusic) {
			Music music = musicList.get(currentMusic);
			main_music_title.setText(music.getTitle());
			main_music_artist.setText(music.getArtist());
			music_album.setImageBitmap(music.getAlbum_art_bitmap());
		}

		/**
		 * 设置随当前音乐变动的View
		 */
		public void setVariableView(int status) {
			switch (status) {
			case 1:// 没有播放状态
				main_play_pause.setBackgroundResource(R.mipmap.play_button);
				break;
			case 2:// 正在播放状态
				main_play_pause.setBackgroundResource(R.mipmap.pause_button);
				break;
			case 3:// 暂停播放状态
				main_play_pause.setBackgroundResource(R.mipmap.playing_button);
				break;
			}
		}
	}

}
