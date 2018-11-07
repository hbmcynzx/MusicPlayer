package cn.hbmcynzx.musicplayer.Database;

import android.os.Environment;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.List;

import cn.hbmcynzx.musicplayer.utils.MusicUtil;

public class MusicDAO{
	/**
	 * 扫描指定文件夹下音乐
	 */
	public static void scanAllMusics(){
        LitePal.deleteAll(Music.class,"");
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music";
		String imgPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/img";
        MusicUtil.deleteFile(imgPath);
        List<Music> musics = new ArrayList<>();
		MusicUtil.scanMusic(path,imgPath,musics);
		for(Music music : musics){
		    music.save();
        }
	}

    /**
     * 从数据库获取所有音乐
     */
	public static List<Music> getAllMusics(){
        List<Music> musicList = LitePal.findAll(Music.class);
        return musicList;
    }
	
}
