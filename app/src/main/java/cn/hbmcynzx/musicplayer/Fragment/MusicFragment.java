package cn.hbmcynzx.musicplayer.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import cn.hbmcynzx.musicplayer.Activity.BaseActivity;
import cn.hbmcynzx.musicplayer.Adapter.MusicListAdapter;
import cn.hbmcynzx.musicplayer.R;

/**
 * Created by hbmcynzx on 2016/7/3.
 */
public class MusicFragment extends Fragment implements AdapterView.OnItemClickListener{
    public static ListView music_listview;
    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_music,container,false);
        music_listview= (ListView) view.findViewById(R.id.music_list);
        music_listview.setAdapter(getAdapter());
        music_listview.setOnItemClickListener(this);
        music_listview.invalidateViews();
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent sendIntent=new Intent(BaseActivity.BUTTON_ACTION);
        sendIntent.putExtra("control", 0);
        sendIntent.putExtra("currentMusic", position);
        view.getContext().sendBroadcast(sendIntent);
    }

    private MusicListAdapter getAdapter() {
        MusicListAdapter adapter = new MusicListAdapter(view.getContext(), BaseActivity.musicList, R.layout.music_list);
        return adapter;
    }


}
