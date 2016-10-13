package mmss.musicco.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmss.musicco.App;
import mmss.musicco.R;
import mmss.musicco.models.TracksRepo;

/**
 * Created by User on 12.10.2016.
 */

public class ActorsFragment extends Fragment {

    @Inject
    public TracksRepo tracksRepo;

    @BindView(R.id.fragment_actors_list_view)
    public ListView listView;

    public ActorsFragment() {
        App.getApp().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actors, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("TAG", "ActorsFragment started");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("TAG", "ActorsFragment stopped");
    }

}
