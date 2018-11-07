package cn.hbmcynzx.musicplayer.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import cn.hbmcynzx.musicplayer.Database.Music;
import cn.hbmcynzx.musicplayer.Database.MusicDAO;
import cn.hbmcynzx.musicplayer.R;

public class WelcomeActivity extends AppCompatActivity{
	public static boolean inited=false;//是否了初始化数据
	private static int currentMusic;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		if(inited){
			handler.post(runnable);
		}
		else{
			handler.postDelayed(runnable, 1000);
		}
	}
	private void initData(){
		if(!inited){
			BaseActivity.musicList=MusicDAO.getAllMusics();
			if(BaseActivity.musicList.size()>0)
				currentMusic = 0;
			else
				currentMusic=-1;
			inited=true;
		}
		BaseActivity.config=getSharedPreferences("music_status", MODE_PRIVATE);
		BaseActivity.loop_status=BaseActivity.config.getInt("loop_status", 1);//音乐循环状态 1、顺序播放 2、单曲循环 3、随机播放
		BaseActivity.currentMusic=BaseActivity.config.getInt("currentMusic", currentMusic);//当前播放音乐
		BaseActivity.start_play=BaseActivity.config.getBoolean("start_play", false);
		BaseActivity.album_rotate=BaseActivity.config.getBoolean("album_rotate", true);
	}
	Handler handler=new Handler();
	Runnable runnable=new Runnable() {
		@Override
		public void run() {
			initData();
			Intent intent=new Intent();
			intent.setClass(getApplicationContext(), MainActivity.class);
			startActivity(intent);
			finish();
		}
	};
}
