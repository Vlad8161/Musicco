package mmss.musicco.ui.activities;

import java.util.List;

import mmss.musicco.dataobjects.Track;
import rx.Observable;

/**
 * Created by vlad on 3/4/17.
 */
public interface OnShowTracksListener {
    void onShowTracks(Observable<List<Track>> tracksObservable);
}
