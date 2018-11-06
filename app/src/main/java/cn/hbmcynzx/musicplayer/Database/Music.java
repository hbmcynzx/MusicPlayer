package cn.hbmcynzx.musicplayer.Database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Music extends LitePalSupport {
	@Column(unique = true)
	private int id;
	private String title;//音乐名称
	private String artist;//歌手
	private String album;//专辑
	private String album_art;//专辑图片路径
	private String path;//歌曲路径
	private int duration;//歌曲时长
	@Column(ignore = true)
	private Bitmap album_art_bitmap;//专辑图片
	public Music() {
		
	}

	public Music(String title, String artist, String album,String album_art, String path, int duration) {
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.album_art = album_art;
		this.path = path;
		this.duration = duration;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getArtist() {
		return artist;
	}


	public void setArtist(String artist) {
		this.artist = artist;
	}


	public String getAlbum() {
		return album;
	}


	public void setAlbum(String album) {
		this.album = album;
	}


	public String getAlbum_art() {
		return album_art;
	}


	public void setAlbum_art(String album_art) {
		this.album_art = album_art;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public int getDuration() {
		return duration;
	}


	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Bitmap getAlbum_art_bitmap() {
		if(album_art_bitmap == null){
			BitmapFactory.Options opts=new BitmapFactory.Options();
			opts.inTempStorage=new byte[12*1024];
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
			if(this.album_art != null && this.album_art.length() > 0){
				album_art_bitmap=BitmapFactory.decodeFile(album_art,opts);
			}
		}
		return album_art_bitmap;
	}

	public void setAlbum_art_bitmap(Bitmap album_art_bitmap) {
		this.album_art_bitmap = album_art_bitmap;
	}
}
