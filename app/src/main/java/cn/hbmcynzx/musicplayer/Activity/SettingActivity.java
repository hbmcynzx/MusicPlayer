package cn.hbmcynzx.musicplayer.Activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import cn.hbmcynzx.musicplayer.Fragment.MusicFragment;
import cn.hbmcynzx.musicplayer.Database.MusicDAO;
import cn.hbmcynzx.musicplayer.Adapter.MusicListAdapter;
import cn.hbmcynzx.musicplayer.Service.MusicService;
import cn.hbmcynzx.musicplayer.R;

public class SettingActivity extends BaseActivity implements OnCheckedChangeListener{
	private ActivityReceiver activityReceiver;
	private ToggleButton setting_startplay,setting_album_rotate,setting_exit_in30min;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settingActivity=this;
		setContentView(R.layout.activity_setting);
		initReceiver();
		initView();
	}
	private void initReceiver(){
		activityReceiver=new ActivityReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction(UPDATE_ACTION);
		registerReceiver(activityReceiver, filter);
	}
	private void initView(){
		setting_startplay=(ToggleButton) findViewById(R.id.setting_startplay);
		setting_startplay.setChecked(start_play);
		setting_startplay.setOnCheckedChangeListener(this);
		setting_album_rotate=(ToggleButton) findViewById(R.id.setting_album_rotate);
		setting_album_rotate.setChecked(album_rotate);
		setting_album_rotate.setOnCheckedChangeListener(this);
		setting_exit_in30min=(ToggleButton) findViewById(R.id.setting_exit_in30min);
		setting_exit_in30min.setChecked(exit_in30min);
		setting_exit_in30min.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_exit:
			this.finish();
			MainActivity.exit();
			break;
			case R.id.setting_scan:
			    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
			        //查看是否有读写存储器权限
                    scanMusic();
                } else {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }

			break;
		}
		
	}

	private void scanMusic(){
        MusicDAO.scanAllMusics();
        musicList=MusicDAO.getAllMusics();
        MusicListAdapter adapter=new MusicListAdapter(this,musicList,R.layout.music_list);
        MusicFragment.music_listview.setAdapter(adapter);
        Intent service=new Intent(this,MusicService.class);
        startService(service);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    scanMusic();
                } else{
                    Toast.makeText(this,"未获取存储权限",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Editor edit=config.edit();
		switch (buttonView.getId()) {
		case R.id.setting_startplay:
			if(isChecked){
				start_play=true;
				edit.putBoolean("start_play", true);
			}
			else{
				start_play=false;
				edit.putBoolean("start_play", false);
			}
			break;
		case R.id.setting_album_rotate:
			if(isChecked){
				album_rotate=true;
				edit.putBoolean("album_rotate", true);
			}
			else{
				album_rotate=false;
				edit.putBoolean("album_rotate", false);
			}
			break;
		case R.id.setting_exit_in30min:
			if(isChecked){
				exit_in30min=true;
			}
			else{
				exit_in30min=false;
			}
			Intent sendIntent=new Intent(BUTTON_ACTION);
			sendIntent.putExtra("control", 6);
			sendBroadcast(sendIntent);
			break;
		}
		edit.commit();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(activityReceiver);
	}
	
	public class ActivityReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			int exitTime=intent.getIntExtra("exitTime", 0);
			if(setting_exit_in30min!=null&&exit_in30min)
				setting_exit_in30min.setText(String.format("%02d:%02d", exitTime/60, exitTime%60)+"后关闭");
		}
	}
}
