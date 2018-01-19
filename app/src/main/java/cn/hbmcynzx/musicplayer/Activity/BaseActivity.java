package cn.hbmcynzx.musicplayer.Activity;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import cn.hbmcynzx.musicplayer.Database.Music;
import cn.hbmcynzx.musicplayer.R;

public class BaseActivity extends AppCompatActivity implements OnClickListener,OnSeekBarChangeListener{
	public static Activity baseActivity,mainActivity,playActivity,settingActivity;
	public static final String BUTTON_ACTION="BUTTON_ACTION";//主界面按钮广播
	public static final String UPDATE_ACTION="UPDATE_ACTION";//Service中的广播
	public static List<Music> musicList;
	public static int currentMusic=-1;//当前播放音乐
	public static int loop_status=1;//音乐循环状态 1、顺序播放 2、单曲循环 3、随机播放
	public static boolean start_play=false;
	public static boolean album_rotate=true;
	public static boolean exit_in30min=false;
	public static SharedPreferences config;
	public Button loop;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		baseActivity=this;
	}

	@Override
	public void onClick(View v) {
		Intent sendIntent=new Intent(BUTTON_ACTION);
		switch (v.getId()) {
		case R.id.play_pause:
			sendIntent.putExtra("control", 1);
			break;
		case R.id.back://上一首
			sendIntent.putExtra("control", 2);
			break;
		case R.id.next://下一首
			sendIntent.putExtra("control", 3);
			break;
		case R.id.ll_buttom://底部跳转
			Intent intent=new Intent();
			intent.setClass(this, PlayActivity.class);
			startActivity(intent);
			return;
		case R.id.loop://设置循环状态
			Editor edit=config.edit();
		if(loop_status==1){//顺序播放
			loop_status=2;
			edit.putInt("loop_status", loop_status);
			loop.setBackgroundResource(R.mipmap.play_list_mode_repeat_one);
		}
		else if(loop_status==2){//单曲循环
			loop_status=3;
			edit.putInt("loop_status", loop_status);
			loop.setBackgroundResource(R.mipmap.play_list_mode_shuffle);
		}
		else if(loop_status==3){//随机播放
			loop_status=1;
			edit.putInt("loop_status", loop_status);
			loop.setBackgroundResource(R.mipmap.play_list_mode_sequent);
		}
		edit.commit();
		return;
		}
		sendBroadcast(sendIntent);
	}
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		Intent sendIntent=new Intent(BUTTON_ACTION);
		sendIntent.putExtra("control", 4);
		sendIntent.putExtra("currentPosition", seekBar.getProgress());
		sendBroadcast(sendIntent);
	}
	@SuppressLint("DefaultLocale")
	public String toTime(int time) {
		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		minute %= 60;
		/** 返回结果用string的format方法把时间转换成字符类型 **/
		return String.format("%02d:%02d", minute, second);
	}
}
