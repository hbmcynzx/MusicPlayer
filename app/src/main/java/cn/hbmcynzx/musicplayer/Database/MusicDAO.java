package cn.hbmcynzx.musicplayer.Database;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class MusicDAO{
	private Context context;
	private MusicSQLiteOpenHelper musicSQLiteOpenHelper;
	public MusicDAO(Context context) {
		this.context=context;
		musicSQLiteOpenHelper=new MusicSQLiteOpenHelper(context);
	}
	public void scanAllMusics(){
		musicSQLiteOpenHelper.cleanTable();
		ContentResolver resolver=context.getContentResolver();
		Cursor audio_cursor=resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		while(audio_cursor.moveToNext()){
			String title=audio_cursor.getString(audio_cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
			String artist=audio_cursor.getString(audio_cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
			String album=audio_cursor.getString(audio_cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
			String path=audio_cursor.getString(audio_cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			int duration=audio_cursor.getInt(audio_cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
			String album_art=null;
			int album_id=audio_cursor.getInt(audio_cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
			Cursor album_cursor=resolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Albums.ALBUM_ART}, MediaStore.Audio.Albums._ID+"=?", new String[]{String.valueOf(album_id)}, null);
			if(album_cursor!=null&&album_cursor.moveToNext()){
				album_art=album_cursor.getString(album_cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));
				album_cursor.close();
			}
			if(duration<1000*60*10&&duration>1000*30){
				Music music=new Music(title, artist, album,album_art, path, duration);
				musicSQLiteOpenHelper.insert(music);
			}
		}
		audio_cursor.close();
	}
	
}
