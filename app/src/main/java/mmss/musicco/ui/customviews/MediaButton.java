package mmss.musicco.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import mmss.musicco.R;

/**
 * Created by vlad on 3/5/17.
 */

public class MediaButton extends ImageView implements View.OnClickListener {
    private static float DEFAULT_SHADOW_RADIUS = 3.0f;
    private static int DEFAULT_SHADOW_COLOR = 0x3E000000;
    private static int DEFAULT_BACKGROUND_COLOR = 0xFFFAFAFA;
    private static int DEFAULT_BACKGROUND_CLICKED_COLOR = 0xFF7FDBFF;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mShadowRadius;
    private int mOffsetX;
    private int mOffsetY;
    private int mBackgroundColor;
    private int mBackgroundClickedColor;
    private int mShadowColor;
    private boolean mOffsetXSpecified = false;
    private boolean mOffsetYSpecified = false;

    public MediaButton(Context context) {
        super(context);
        float density = context.getResources().getDisplayMetrics().density;
        mBackgroundColor = DEFAULT_BACKGROUND_COLOR;
        mShadowColor = DEFAULT_SHADOW_COLOR;
        mBackgroundClickedColor = DEFAULT_BACKGROUND_CLICKED_COLOR;
        mShadowRadius = (int) (density * DEFAULT_SHADOW_RADIUS);
        mOffsetX = 0;
        mOffsetY = mShadowRadius / 2;
        init();
    }

    public MediaButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        float density = context.getResources().getDisplayMetrics().density;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MediaButton, 0, 0);
        try {
            mBackgroundColor = a.getColor(
                    R.styleable.MediaButton_backgroundColor,
                    DEFAULT_BACKGROUND_COLOR);
            mShadowColor = a.getColor(
                    R.styleable.MediaButton_shadowColor,
                    DEFAULT_SHADOW_COLOR);
            mBackgroundClickedColor = a.getColor(
                    R.styleable.MediaButton_backgroundClickedColor,
                    DEFAULT_BACKGROUND_CLICKED_COLOR);
            mShadowRadius = (int) a.getDimension(R.styleable.MediaButton_shadowRadius, DEFAULT_SHADOW_RADIUS * density);
            if (a.hasValue(R.styleable.MediaButton_shadowOffsetX)) {
                mOffsetXSpecified = true;
            }
            mOffsetX = (int) a.getDimension(R.styleable.MediaButton_shadowOffsetX, 0);
            if (a.hasValue(R.styleable.MediaButton_shadowOffsetY)) {
                mOffsetYSpecified = true;
            }
            mOffsetY = (int) a.getDimension(R.styleable.MediaButton_shadowOffsetY, mShadowRadius / 2);
        } finally {
            a.recycle();
        }
        init();
    }

    public MediaButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float density = context.getResources().getDisplayMetrics().density;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MediaButton, 0, 0);
        try {
            mBackgroundColor = a.getColor(
                    R.styleable.MediaButton_backgroundColor,
                    DEFAULT_BACKGROUND_COLOR);
            mShadowColor = a.getColor(
                    R.styleable.MediaButton_shadowColor,
                    DEFAULT_SHADOW_COLOR);
            mBackgroundClickedColor = a.getColor(
                    R.styleable.MediaButton_backgroundClickedColor,
                    DEFAULT_BACKGROUND_CLICKED_COLOR);
            mShadowRadius = (int) a.getDimension(R.styleable.MediaButton_shadowRadius, DEFAULT_SHADOW_RADIUS * density);
            if (a.hasValue(R.styleable.MediaButton_shadowOffsetX)) {
                mOffsetXSpecified = true;
            }
            mOffsetX = (int) a.getDimension(R.styleable.MediaButton_shadowOffsetX, 0);
            if (a.hasValue(R.styleable.MediaButton_shadowOffsetY)) {
                mOffsetYSpecified = true;
            }
            mOffsetY = (int) a.getDimension(R.styleable.MediaButton_shadowOffsetY, mShadowRadius / 2);
        } finally {
            a.recycle();
        }
        init();
    }

    private void init() {
        mPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        mPaint.setColor(mBackgroundColor);
        mPaint.setStyle(Paint.Style.FILL);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setBackground(new ShapeDrawable(new BackgroundShape()));
        setOnClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(-mOffsetX, -mOffsetY);
        super.onDraw(canvas);
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        mPaint.setColor(color);
        invalidate();
    }

    public int getBackgroundClickedColor() {
        return mBackgroundClickedColor;
    }

    public void setBackgroundClickColor(int color) {
        mBackgroundClickedColor = color;
        mPaint.setColor(color);
        invalidate();
    }

    public int getShadowColor() {
        return mShadowColor;
    }

    public void setShadowColor(int color) {
        mShadowColor = color;
        mPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        invalidate();
    }

    public int getShadowRadius() {
        return mShadowRadius;
    }

    public void setShadowRadius(int radius) {
        mShadowRadius = radius;
        if (!mOffsetYSpecified) {
            mOffsetY = mShadowRadius / 2;
        }
        mPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        invalidate();
    }

    public int getOffsetX() {
        return mOffsetX;
    }

    public void setOffsetX(int offsetX) {
        mOffsetX = offsetX;
        mOffsetXSpecified = true;
        mPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        invalidate();
    }

    public int getOffsetY() {
        return mOffsetY;
    }

    public void setOffsetY(int offsetY) {
        mOffsetY = offsetY;
        mOffsetYSpecified = true;
        mPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, mShadowColor);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mPaint.setColor(mBackgroundClickedColor);
            invalidate();
            return super.onTouchEvent(event);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mPaint.setColor(mBackgroundColor);
            invalidate();
            return super.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        // Это просто для того чтобы событие клика не шло на следующий элемент
        // и не сбивался фокус при клике
    }

    private class BackgroundShape extends Shape {

        @Override
        public void draw(Canvas canvas, Paint paint) {
            int cx = (int) (getWidth() / 2 - mOffsetX);
            int cy = (int) (getHeight() / 2 - mOffsetY);
            int r = (int) (Math.min(getWidth(), getHeight()) / 2 - mShadowRadius);
            canvas.drawCircle(cx, cy, r, mPaint);
        }
    }
}
