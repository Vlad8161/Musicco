package mmss.musicco.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmss.musicco.R;

/**
 * Created by vlad on 6/5/17.
 */

public class AddPlaylistActivity extends AppCompatActivity {
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

        setSupportActionBar(mToolbar);
    }
}
