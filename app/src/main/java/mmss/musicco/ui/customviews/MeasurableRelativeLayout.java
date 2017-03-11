package mmss.musicco.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlad on 3/11/17.
 */

public class MeasurableRelativeLayout extends RelativeLayout {
    private List<OnSizeChangedListener> mListeners = new ArrayList<>();

    public MeasurableRelativeLayout(Context context) {
        super(context);
    }

    public MeasurableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeasurableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addOnSizeChangedListener(OnSizeChangedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener can't be null");
        }
        mListeners.add(listener);
    }

    public void removeOnSizeChangedListener(OnSizeChangedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener can't be null");
        }
        mListeners.remove(listener);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        for (OnSizeChangedListener i : mListeners) {
            i.onSizeChanged(w, h, oldw, oldh);
        }
    }

    public interface OnSizeChangedListener {
        void onSizeChanged(int w, int h, int oldw, int oldh);
    }
}
