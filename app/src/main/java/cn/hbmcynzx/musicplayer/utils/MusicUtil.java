package cn.hbmcynzx.musicplayer.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.AbstractDataType;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v23Frame;
import org.jaudiotagger.tag.id3.ID3v23Tag;

import cn.hbmcynzx.musicplayer.Database.Music;

public class MusicUtil {
	/**专辑*/
	private static final String ALBUM = "TALB";
	/**歌曲名*/
	private static final String SONGNAME = "TIT2";
	/**歌手名*/
	private static final String ARTIST = "TPE1";
	/**专辑图片*/
	private static final String ALBUM_IMAGE = "APIC";
	
	private MP3File mp3File;
	private Tag tag;
	
	public MusicUtil(){
		
	}
	
	public MusicUtil(File file){
		try {
			this.mp3File = new MP3File(file);
			this.tag = mp3File.getTag();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public MusicUtil(MP3File file){
		this.mp3File = file;
		this.tag = mp3File.getTag();
	}
	
	public MusicUtil(String filePath){
		try {
			mp3File = new MP3File(filePath);
			this.tag = mp3File.getTag();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public MP3File getMp3File() {
		return mp3File;
	}

	public void setMp3File(MP3File mp3File) {
		this.mp3File = mp3File;
		this.tag = mp3File.getTag();
	}

	/**
	 * 获取专辑
	 */
	public String getAlbum(){
		AbstractDataType value = getValue(ALBUM);
		return value == null ? "" : value.toString();
	}
	
	/**
	 * 获取歌手
	 */
	public String getArtist(){
		AbstractDataType value = getValue(ARTIST);
		return value == null ? "" : value.toString();
	}
	
	/**
	 * 获取歌曲名
	 */
	public String getSongName(){
		AbstractDataType value = getValue(SONGNAME);
		return value == null ? "" : value.toString();
	}
	
	/**
	 * 获取专辑图片
	 */
	public byte[] getAlbumImage(){
		AbstractDataType value = getValue(ALBUM_IMAGE);
		return value==null ? null : value.writeByteArray();
	}

	/**
	 * 获取歌曲文件路径
	 */
	public String getPath(){
		return this.mp3File.getFile().getAbsolutePath();
	}

    /**
     * 获取歌曲时长
     */
	public int getDuration(){
	    return this.mp3File.getMP3AudioHeader().getTrackLength() * 1000;
    }
	
	/**
	 * 写专辑图片到指定目录
	 */
	public String writeImage(String path){
		byte[] byteArray = getAlbumImage();
		String imgPath = writeImage(byteArray , path);
		return imgPath;
	}
	
	/**
	 * 写专辑图片到指定目录
	 */
	public static String writeImage(byte[] byteArray,String path){
		if(byteArray==null){
			return null;
		}
		try {
			File filePath = new File(path);
			if(!filePath.exists()){
				filePath.mkdirs();
			}
			String imgPath = filePath.getAbsolutePath() + File.separator + UUID.randomUUID().toString().replaceAll("-","") + ".jpg";
			FileOutputStream fos = new FileOutputStream(imgPath);
			fos.write(byteArray, 0, byteArray.length);
			fos.flush();
			fos.close();
			return imgPath;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 扫描指定目录下文件
	 */
	public static void scanMusic(String path,String imgPath,List<Music> list){
		File file = new File(path);
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for (File file_ : files) {
				scanMusic(file_.getAbsolutePath(),imgPath, list);
			}
		}else {
			String fileName = file.getName();
			String[] suffix = new String[]{"mp3","flac"};
			boolean isContain = false;
			for (String su : suffix) {
				isContain = fileName.contains(su);
				if(isContain){
				    break;
                }
			}
			if(isContain){
				MusicUtil util = new MusicUtil(file);
				Music music = util.getMusic(imgPath);
				list.add(music);
			}
		}
	}

	public static void deleteFile(String path){
	    File file = new File(path);
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File file_ : files) {
                deleteFile(file_.getAbsolutePath());
            }
        }else{
            if(file.exists()){
                file.delete();
            }
        }
    }
	
	private AbstractDataType getValue(String tagName){
		if(this.tag instanceof ID3v23Tag){
			AbstractID3v2Tag id3v2Tag = (AbstractID3v2Tag) this.tag;
			ID3v23Frame frame = (ID3v23Frame) id3v2Tag.getFrame(tagName);
			if(frame!=null){
				if(tagName.equals(ALBUM_IMAGE)){
					return frame.getBody().getObject("PictureData");
				}else {
					return frame.getBody().getObject("Text");
				}
			}
			return null;
		}else {
			return null;
		}
		
	}

	public Music getMusic(String imagePath){
		String songName = getSongName();
		String artist = getArtist();
		String album = getAlbum();
        String path = getPath();
        int duration = getDuration();
        byte[] albumImage = getAlbumImage();
        imagePath = writeImage(albumImage, imagePath);
        Music music = new Music(songName, artist, album,imagePath,path,duration);
		return music;
	}
	
	public static void main(String[] args) throws Exception{

	}
}
