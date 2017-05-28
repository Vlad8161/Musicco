package mmss.musicco.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import mmss.musicco.R;

/**
 * Created by vlad on 5/27/17.
 */

public class NotOverlappingBottomSheetLayout extends ViewGroup {
    private final int DEFAULT_PEEK_HEIGHT = 0;

    private int mBottomSheetPeekHeight;

    public NotOverlappingBottomSheetLayout(Context context) {
        super(context);
        mBottomSheetPeekHeight = DEFAULT_PEEK_HEIGHT;
    }

    public NotOverlappingBottomSheetLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NotOverlappingBottomSheetLayout, 0, 0);
        try {
            mBottomSheetPeekHeight = (int) a.getDimension(
                    R.styleable.NotOverlappingBottomSheetLayout_bottomSheetPeekHeight,
                    DEFAULT_PEEK_HEIGHT
            );
        } finally {
            a.recycle();
        }
    }

    public NotOverlappingBottomSheetLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NotOverlappingBottomSheetLayout, 0, 0);
        try {
            mBottomSheetPeekHeight = (int) a.getDimension(
                    R.styleable.NotOverlappingBottomSheetLayout_bottomSheetPeekHeight,
                    DEFAULT_PEEK_HEIGHT
            );
        } finally {
            a.recycle();
        }
    }

    public int getBottomSheetPeekHeight() {
        return mBottomSheetPeekHeight;
    }

    public void setBottomSheetPeekHeight(int bottomSheetPeekHeight) {
        mBottomSheetPeekHeight = bottomSheetPeekHeight;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (childCount > 0) {
            int freeHeight = Math.max(height - mBottomSheetPeekHeight, 0);
            for (int i = 0; i < childCount - 1; i++) {
                View child = getChildAt(i);
                int mode = child.getLayoutParams().height == LayoutParams.MATCH_PARENT ? MeasureSpec.EXACTLY : MeasureSpec.AT_MOST;
                child.measure(
                        MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(freeHeight, mode)
                );
                int childHeight = child.getMeasuredHeight();
                freeHeight -= childHeight;
            }
            View bottomSheet = getChildAt(childCount - 1);
            bottomSheet.measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
            );
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();

        if (childCount > 0) {
            int currY = 0;
            for (int i = 0; i < childCount - 1; i++) {
                View child = getChildAt(i);
                int measuredWidth = child.getMeasuredWidth();
                int measuredHeight = child.getMeasuredHeight();
                child.layout(0, currY, measuredWidth, currY + measuredHeight);
            }

            View bottomSheet = getChildAt(childCount - 1);
            int myMeasuredWidth = getMeasuredWidth();
            int myMeasuredHeight = getMeasuredHeight();
            int bottomSheetMeasuredHeight = bottomSheet.getMeasuredHeight();
            bottomSheet.layout(
                    0, myMeasuredHeight - mBottomSheetPeekHeight, myMeasuredWidth,
                    myMeasuredHeight - mBottomSheetPeekHeight + bottomSheetMeasuredHeight
            );
        }
    }
}
