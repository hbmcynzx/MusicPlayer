package cn.hbmcynzx.musicplayer.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import cn.hbmcynzx.musicplayer.Database.Music;
import cn.hbmcynzx.musicplayer.Service.MusicService;
import cn.hbmcynzx.musicplayer.R;
import cn.hbmcynzx.musicplayer.View.RoundImageView;
import cn.hbmcynzx.musicplayer.lrc.LrcView;

public class PlayActivity extends BaseActivity {
	private ActivityReceiver activityReceiver;
	private RoundImageView play_round_music_alnum;
	private LrcView play_lrcView;
	private TextView play_currentTime, play_totalTime, play_music_title, play_music_artist;
	private Button play_pause, next, back;
	private SeekBar play_seekBar;
	private static int curMusic= currentMusic;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		playActivity = this;
		setContentView(R.layout.activity_play);
		initReceiver();
		initView();
	}

	private void initReceiver() {
		activityReceiver = new ActivityReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(UPDATE_ACTION);
		registerReceiver(activityReceiver, filter);
	}

	private void initView() {
		play_round_music_alnum = (RoundImageView) findViewById(R.id.music_album);
		if (album_rotate) {
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha_z);
			LinearInterpolator lir = new LinearInterpolator();
			anim.setInterpolator(lir);
			play_round_music_alnum.setAnimation(anim);
		}
		play_pause = (Button) findViewById(R.id.play_pause);
		play_pause.setOnClickListener(this);
		next = (Button) findViewById(R.id.next);
		next.setOnClickListener(this);
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		play_seekBar = (SeekBar) findViewById(R.id.seekbar);
		play_seekBar.setEnabled(false);
		play_seekBar.setOnSeekBarChangeListener(this);
		play_currentTime = (TextView) findViewById(R.id.currentTime);
		play_totalTime = (TextView) findViewById(R.id.totalTime);
		play_music_title = (TextView) findViewById(R.id.music_title);
		play_music_artist = (TextView) findViewById(R.id.music_artist);
		play_lrcView = (LrcView) findViewById(R.id.lrcShowView);
		loop = (Button) findViewById(R.id.loop);
		switch (loop_status) {
		case 1:
			loop.setBackgroundResource(R.mipmap.play_list_mode_sequent);
			break;
		case 2:
			loop.setBackgroundResource(R.mipmap.play_list_mode_repeat_one);
			break;
		case 3:
			loop.setBackgroundResource(R.mipmap.play_list_mode_shuffle);
			break;
		}
		loop.setOnClickListener(this);
		if (currentMusic >= 0) {
			Music music = musicList.get(curMusic);
			play_round_music_alnum.setImageBitmap(music.getAlbum_art_bitmap());
			play_seekBar.setMax(music.getDuration());
			play_totalTime.setText(toTime(music.getDuration()));
			play_music_title.setText(music.getTitle());
			play_music_artist.setText(music.getArtist());
			play_lrcView.setmLrcList(MusicService.lrcList);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(activityReceiver);
	}

	public class ActivityReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int status = intent.getIntExtra("status", -1);// 获取播放状态
			int currentMusic = intent.getIntExtra("currentMusic", -1);// 获取当前播放曲目
			int lrcIndex = intent.getIntExtra("lrcIndex", 0);
			int currentPosition = intent.getIntExtra("currentPosition", 0);
			if (currentMusic >= 0) {
				if (isCurMusicChange(currentMusic)) {// 当前音乐如果改变
					setConstantView(currentMusic);
				} else {// 当前音乐没改变
					setVariableView(currentPosition, status, lrcIndex);
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
			play_music_title.setText(music.getTitle());
			play_music_artist.setText(music.getArtist());
			play_totalTime.setText(toTime(music.getDuration()));
			play_seekBar.setMax(music.getDuration());
			play_round_music_alnum.setImageBitmap(music.getAlbum_art_bitmap());
			play_lrcView.setmLrcList(MusicService.lrcList);
		}

		/**
		 * 设置随当前音乐变动的View
		 */
		public void setVariableView(int currentPosition, int status, int lrcIndex) {
			play_currentTime.setText(toTime(currentPosition));
			play_seekBar.setProgress(currentPosition);
			play_lrcView.setIndex(lrcIndex);
			play_lrcView.invalidate();
			switch (status) {
			case 1:// 没有播放状态
				play_seekBar.setEnabled(false);
				play_pause.setBackgroundResource(R.mipmap.play_button);
				break;
			case 2:// 正在播放状态
				play_seekBar.setEnabled(true);
				play_pause.setBackgroundResource(R.mipmap.pause_button);
				break;
			case 3:// 暂停播放状态
				play_seekBar.setEnabled(true);
				play_pause.setBackgroundResource(R.mipmap.playing_button);
				break;
			}
		}
	}
}
