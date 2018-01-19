package cn.hbmcynzx.musicplayer.Database;

import android.graphics.Bitmap;

public class Music{
	private String title;
	private String artist;
	private String album;
	private String album_art;
	private String path;
	private int duration;
	private Bitmap album_art_bitmap;
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
	public Music(String title, String artist, String album,String path, int duration,Bitmap album_art_bitmap) {
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.path = path;
		this.duration = duration;
		this.album_art_bitmap=album_art_bitmap;
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
		return album_art_bitmap;
	}

	public void setAlbum_art_bitmap(Bitmap album_art_bitmap) {
		this.album_art_bitmap = album_art_bitmap;
	}
}
