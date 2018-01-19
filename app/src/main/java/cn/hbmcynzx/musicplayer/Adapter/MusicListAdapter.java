package cn.hbmcynzx.musicplayer.Adapter;

import java.util.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.hbmcynzx.musicplayer.Database.Music;
import cn.hbmcynzx.musicplayer.R;

@SuppressLint("ViewHolder")
public class MusicListAdapter extends BaseAdapter{
    private Context context;
    private List<Music> list;
    private int res;
    
    
    
	public MusicListAdapter(Context context, List<Music> list, int res) {
		this.context = context;
		this.list = list;
		this.res = res;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=null;
		Holder holder=null;
		if(convertView!=null){
			view=convertView;
			holder=(Holder) view.getTag();
		}
		else{
			view=View.inflate(context, res, null);
			holder=new Holder();
			holder.music_title=(TextView) view.findViewById(R.id.music_title);
			holder.music_artist=(TextView) view.findViewById(R.id.music_artist);
			view.setTag(holder);
		}
		holder.music_title.setText(list.get(position).getTitle());
		holder.music_artist.setText((String)list.get(position).getArtist());
		return view;
	}
	
	private static class Holder{
		public TextView music_title=null;
		public TextView music_artist=null;
	}

	

}
