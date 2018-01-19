package cn.hbmcynzx.musicplayer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hbmcynzx on 2016/7/3.
 */
public class MusicSQLiteOpenHelper extends SQLiteOpenHelper{
    private String[] strings=new String[]{"title","artist","album","path","duration","album_art"};
    public MusicSQLiteOpenHelper(Context context) {
        super(context, "musicdb.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE music(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT,artist TEXT,album TEXT,path TEXT,duration INTEGER,album_art TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean insert(Music music){
        SQLiteDatabase db=getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put(strings[0],music.getTitle());
        values.put(strings[1],music.getArtist());
        values.put(strings[2],music.getAlbum());
        values.put(strings[3],music.getPath());
        values.put(strings[4],music.getDuration());
        values.put(strings[5],music.getAlbum_art());
        long i=db.insert("music",null,values);
        if(i!=-1)
            return true;
        return false;
    }

    public List<Music> getAllMusics(){
        List<Music> musics=new ArrayList<Music>();
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.query(true, "music", strings, null, null, null, null, "id asc", null);
        BitmapFactory.Options opts=new BitmapFactory.Options();
        opts.inTempStorage=new byte[12*1024];
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap album_art_bitmap=null;
        while(cursor.moveToNext()){
            String title=cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String artist=cursor.getString(cursor.getColumnIndexOrThrow("artist"));
            String album=cursor.getString(cursor.getColumnIndexOrThrow("album"));
            String path=cursor.getString(cursor.getColumnIndexOrThrow("path"));
            int duration=cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
            String album_art=cursor.getString(cursor.getColumnIndexOrThrow("album_art"));
            if(album_art!=null&&album_art.length()>0){
                album_art_bitmap=BitmapFactory.decodeFile(album_art,opts);
            }
            Music music=new Music(title,artist,album,path,duration,album_art_bitmap);
            musics.add(music);
        }
        return musics;
    }
    public void cleanTable(){
        SQLiteDatabase db=getReadableDatabase();
        db.execSQL("delete from music");
        db.execSQL("update sqlite_sequence set seq=0 where name='music'");
    }
}
