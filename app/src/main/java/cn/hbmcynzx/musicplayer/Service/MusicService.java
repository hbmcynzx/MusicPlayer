package cn.hbmcynzx.musicplayer.Service;

import java.util.*;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

import cn.hbmcynzx.musicplayer.Activity.BaseActivity;
import cn.hbmcynzx.musicplayer.Activity.MainActivity;
import cn.hbmcynzx.musicplayer.Database.Music;
import cn.hbmcynzx.musicplayer.R;
import cn.hbmcynzx.musicplayer.lrc.LrcContent;
import cn.hbmcynzx.musicplayer.lrc.LrcProcess;


public class MusicService extends Service implements OnPreparedListener,OnSeekCompleteListener,OnCompletionListener{
	public static List<LrcContent> lrcList = new ArrayList<LrcContent>(); //存放歌词列表对象
	private MediaPlayer player;
	private Intent sendIntent;//Activity广播发送者
	private int status=1;//播放状态： 1、没有播放 2、正在播放 3、暂停播放
	private int currentMusic=-1;//当前曲目
	private ServiceReceiver serviceReceiver;
	private CallReceiver callReceiver;
	private List<Music> musicList;//音乐列表
	private int musicSize=0;//音乐总曲数
	private LrcProcess mLrcProcess; //歌词处理
	private int index = 0;          //歌词检索值  
	private Music music;
	private NotificationManager manager;
	private RemoteViews contentView;
	private int exitTime=0;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		contentView=new RemoteViews(getPackageName(), R.layout.my_notification);
		manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		sendIntent=new Intent(MainActivity.UPDATE_ACTION);
		initReceiver();
		initMusic();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		SharedPreferences config=getSharedPreferences("music_status", MODE_PRIVATE);
		currentMusic=config.getInt("currentMusic",-1);
		musicList=MainActivity.musicList;
		musicSize=musicList.size();
		if(musicSize>0){
			if(currentMusic==-1)
				BaseActivity.currentMusic=currentMusic=0;
			music=musicList.get(currentMusic);
			initLrc(music.getPath());
			sendIntent.putExtra("currentMusic",currentMusic);
			sendBroadcast(sendIntent);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 初始化广播接收者
	 * */
	private void initReceiver(){
		//ServiceReceiver
		serviceReceiver=new ServiceReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction(MainActivity.BUTTON_ACTION);
		registerReceiver(serviceReceiver, filter);
		//CallReceiver
		callReceiver=new CallReceiver();
		IntentFilter callFilter=new IntentFilter();
		callFilter.addAction("android.intent.action.PHONE_STATE");
		callFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(callReceiver, callFilter);
	}
	/**
	 * 初始化音乐
	 * */
	private void initMusic(){
		player=new MediaPlayer();
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		currentMusic=BaseActivity.currentMusic;
		musicList=MainActivity.musicList;
		musicSize=musicList.size();
		if(MainActivity.start_play&&musicSize>0){//是否启动时播放音乐
			prepareAndPlay();
			sendIntent.putExtra("currentMusic", currentMusic);
			sendIntent.putExtra("status", status);
			sendBroadcast(sendIntent);
		}
		if(musicSize>0&&currentMusic!=-1){
			Music music=musicList.get(currentMusic);
			initLrc(music.getPath());
		}
	}
	@Override
	public void onDestroy() {
		handler.removeCallbacks(updateThread);
		handler.removeCallbacks(runnable2);
		player.stop();
		player.release();
		unregisterReceiver(serviceReceiver);
		unregisterReceiver(callReceiver);
		manager.cancelAll();
	}
	
	Handler handler = new Handler();
    Runnable updateThread = new Runnable() {
        public void run() {
            // 获得歌曲现在播放位置并设置成播放进度条的值
            if (status!=-1) {
            	
                sendIntent.putExtra("currentPosition", player.getCurrentPosition());
                sendIntent.putExtra("lrcIndex", lrcIndex());
                sendBroadcast(sendIntent); 
                // 每次延迟100毫秒再启动线程
                handler.postDelayed(updateThread, 100);
            }
        }
    };
    public void initLrc(String filePath){  
        mLrcProcess = new LrcProcess();  
        //读取歌词文件  
        mLrcProcess.readLRC(filePath);  
        //传回处理后的歌词文件  
        lrcList = mLrcProcess.getLrcList();   
    }  
    
    public int lrcIndex() {  
    	int currentTime = 0,duration = 0;
        if(player.isPlaying()) {  
            currentTime = player.getCurrentPosition();
            duration = player.getDuration();  
        }  
        if(currentTime < duration) {  
            for (int i = 0; i < lrcList.size(); i++) {  
                if (i < lrcList.size() - 1) {  
                    if (currentTime < lrcList.get(i).getLrcTime() && i == 0) {  
                        index = i;  
                    }  
                    if (currentTime > lrcList.get(i).getLrcTime()  
                            && currentTime < lrcList.get(i + 1).getLrcTime()) {  
                        index = i;  
                    }  
                }  
                if (i == lrcList.size() - 1  
                        && currentTime > lrcList.get(i).getLrcTime()) {  
                    index = i;  
                }  
            }  
        }  
        return index;  
    }  

    public class ServiceReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			int control=intent.getIntExtra("control", -1);//控制命令：0、ItemClick 1、播放暂停按钮 2、上一曲 3、下一曲 4、进度条改变 5、停止
			currentMusic=intent.getIntExtra("currentMusic",currentMusic);
			switch (control) {
			case 0:
				prepareAndPlay();
				break;
			case 1://播放暂停按钮命令
				if(status==1){//没有播放状态
					if(musicSize>0)
						prepareAndPlay();
				}
				else if(status==2){//正在播放状态
					pause();
				}
				else if(status==3){//暂停播放状态
					play();
				}
				break;
			case 2://上一曲命令	
				back();
				break;
			case 3://下一曲命令
				next();
				break;
			case 4://进度条改变
				seekBar(intent.getIntExtra("currentPosition", 0));
				break;
			case 5://停止
				stop();
				return;
			case 6://30分钟关闭
				if(BaseActivity.exit_in30min){
					exitTime=1800;
					handler.post(runnable2);
				}
				else{
					handler.removeCallbacks(runnable2);
				}
				return;
			}
			sendIntent.putExtra("status", status);
			sendIntent.putExtra("currentMusic", currentMusic);
			sendBroadcast(sendIntent);
			notification();
		}
    	
    }
    public class CallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(player.isPlaying()){
				if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){//去电
					pause();
				}
				else{
					TelephonyManager tm=(TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
					PhoneStateListener listener=new PhoneStateListener(){
						@Override
						public void onCallStateChanged(int state, String incomingNumber) {
							super.onCallStateChanged(state, incomingNumber);
							switch (state) {
							case TelephonyManager.CALL_STATE_IDLE://挂断
								play();
								break;
							case TelephonyManager.CALL_STATE_OFFHOOK://接听
								break;
							case TelephonyManager.CALL_STATE_RINGING://来电
								pause();
								break;
							}
						}
					};
					tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
				}
				sendIntent.putExtra("status", status);
				sendBroadcast(sendIntent);
				notification();
			}
		}
    	
    }
    private void play(){//播放
    	player.start();
    	status=2;
    }
    private void pause(){//暂停
    	player.pause();
    	status=3;
    }
    private void next(){//下一曲
    	if(musicSize==0)
    		return;
    	currentMusic=currentMusic<musicSize-1?++currentMusic:0;
		prepareAndPlay();
    }
    private void back(){//上一曲
    	if(musicSize==0)
    		return;
    	currentMusic=currentMusic>0?--currentMusic:musicSize-1;
		prepareAndPlay();
    }
    private void stop(){//停止
    	status=1;
    	player.stop();
    	manager.cancelAll();
    	handler.removeCallbacks(updateThread);
    	sendIntent.putExtra("currentPosition", 0);
    	sendIntent.putExtra("status", status);
    	sendIntent.putExtra("lrcIndex", 0);
    	sendBroadcast(sendIntent);
    }
    private void random(){//随机播放
    	Random random=new Random();
		currentMusic=random.nextInt(musicSize);
		prepareAndPlay();
    }
    private void loop(){//单曲循环
    	prepareAndPlay();
    }
    private void order(){//顺序播放
    	currentMusic=currentMusic<musicSize-1?++currentMusic:0;
		prepareAndPlay();
    }
    private void seekBar(int msec){//进度条
    	player.seekTo(msec);
    	player.setOnSeekCompleteListener(this);
    	status=2;
    }
    private void prepareAndPlay(){
		SharedPreferences config=getSharedPreferences("music_status", MODE_PRIVATE);
		Editor edit=config.edit();
		edit.putInt("currentMusic", currentMusic);
		edit.commit();
		try{
			music=musicList.get(currentMusic);
			player.reset();
			player.setDataSource(music.getPath());
			player.prepareAsync();
			initLrc(music.getPath());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		status=2;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		switch (MainActivity.loop_status) {
		case 1://顺序播放
			order();
			break;
		case 2://单曲循环
			loop();
			break;
		case 3://随机播放
			random();
			break;
		}
		sendIntent.putExtra("currentMusic", currentMusic);
		sendBroadcast(sendIntent);
		notification();
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		player.start();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		player.start();
		handler.post(updateThread);
	}
	
	private void notification(){
		if(musicSize==0)
			return;
		Notification notification=new Notification.Builder(this).setSmallIcon(R.mipmap.album).build();
		notification.flags= Notification.FLAG_NO_CLEAR;
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		PendingIntent pendingIntent=PendingIntent.getActivity(this, 0, new Intent(this,MainActivity.class), 0);
		notification.contentIntent=pendingIntent;
		
		Intent intent=new Intent(MainActivity.BUTTON_ACTION);
		intent.putExtra("control", 1);
		PendingIntent play_pause_PendingIntent=PendingIntent.getBroadcast(this, 0, intent, 0);
		
		intent.putExtra("control", 3);
		PendingIntent next_PendingIntent=PendingIntent.getBroadcast(this, 1, intent, 0);
		
		intent.putExtra("control", 5);
		PendingIntent stop_PendingIntent=PendingIntent.getBroadcast(this, 2, intent, 0);
		
		contentView.setImageViewBitmap(R.id.music_album, music.getAlbum_art_bitmap());
		contentView.setTextViewText(R.id.music_title, music.getTitle());
		contentView.setTextViewText(R.id.music_artist, music.getArtist());
		if(status==2)//播放
			contentView.setImageViewResource(R.id.play_pause, R.mipmap.pause_button);
		else if(status==3)//暂停
			contentView.setImageViewResource(R.id.play_pause, R.mipmap.playing_button);
		contentView.setOnClickPendingIntent(R.id.play_pause, play_pause_PendingIntent);
		contentView.setOnClickPendingIntent(R.id.next, next_PendingIntent);
		contentView.setOnClickPendingIntent(R.id.stop, stop_PendingIntent);
		notification.contentView=contentView;
		manager.notify(0,notification);
		
	}
	
	Runnable runnable2=new Runnable() {
		@Override
		public void run() {
			exitTime--;
			if(exitTime==0)
				MainActivity.exit();
			sendIntent.putExtra("exitTime", exitTime);
			sendBroadcast(sendIntent);
			handler.postDelayed(runnable2, 1000);
		}
	};
    

}
