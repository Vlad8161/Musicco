package mmss.musicco.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mmss.musicco.App;
import mmss.musicco.R;
import mmss.musicco.core.TracksRepo;

/**
 * Created by vlad on 6/5/17.
 */

public class AddPlaylistActivity extends AppCompatActivity {
    @Inject
    TracksRepo mRepo;

    @BindView(R.id.activity_add_playlist_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_add_playlist_btn_create)
    View mBtnCreate;

    @BindView(R.id.activity_add_playlist_et_name)
    EditText mEtName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist);
        ButterKnife.bind(this);
        App.getApp().inject(this);
        setSupportActionBar(mToolbar);
    }

    @OnClick(R.id.activity_add_playlist_btn_create)
    public void onBtnAddPlaylistClick(View v) {
        String playlistName = mEtName.getText().toString();
        if (playlistName.isEmpty()) {
            Toast.makeText(this, "Имя плейлиста не может быть пустым", Toast.LENGTH_SHORT).show();
        }

        mRepo.newPlaylist(playlistName);
        finish();
    }
}
