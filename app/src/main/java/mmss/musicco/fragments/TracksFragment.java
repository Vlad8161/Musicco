package mmss.musicco.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mmss.musicco.R;

/**
 * Created by User on 12.10.2016.
 */

public class TracksFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracks, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("TAG", "TracksFragment started");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("TAG", "TracksFragment stopped");
    }

}
