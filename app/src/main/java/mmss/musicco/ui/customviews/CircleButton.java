package mmss.musicco.ui.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Private class created to work around issues with AnimationListeners being
 * called before the animation is actually complete and support shadows on older
 * platforms.
 */
class CircleButton extends ImageView {

    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;
    // PX
    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;
    private static final float SHADOW_RADIUS = 3.5f;
    private static final float PADDING_FACTOR = 0.7f;

    int mShadowRadius;

    private void init(int color) {
        final float density = getContext().getResources().getDisplayMetrics().density;
        final int shadowYOffset = (int) (density * Y_OFFSET);
        final int shadowXOffset = (int) (density * X_OFFSET);

        mShadowRadius = (int) (density * SHADOW_RADIUS);

        ShapeDrawable circle;
        OvalShape oval = new OvalShadow(mShadowRadius);
        circle = new ShapeDrawable(oval);
        ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, circle.getPaint());
        circle.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset,
                KEY_SHADOW_COLOR);
        final int padding = mShadowRadius;
        //setPadding(padding, padding, padding, padding);
        circle.getPaint().setColor(color);
        ViewCompat.setBackground(this, circle);
    }

    public CircleButton(Context context) {
        super(context);
        init(0xFFFAFAFA);
    }

    public CircleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(0xFFFAFAFA);
    }

    public CircleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(0xFFFAFAFA);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth() + mShadowRadius * 2, getMeasuredHeight()
                + mShadowRadius * 2);

        //int vertPadding = (int) (getMeasuredHeight() * PADDING_FACTOR);
        //int horzPadding = (int) (getMeasuredWidth() * PADDING_FACTOR);
        //setPadding(vertPadding, horzPadding);
    }

    public void setBackgroundColorRes(int colorRes) {
        setBackgroundColor(ContextCompat.getColor(getContext(), colorRes));
    }

    @Override
    public void setBackgroundColor(int color) {
        if (getBackground() instanceof ShapeDrawable) {
            ((ShapeDrawable) getBackground()).getPaint().setColor(color);
        }
    }

    private class OvalShadow extends OvalShape {
        private RadialGradient mRadialGradient;
        private Paint mShadowPaint;

        OvalShadow(int shadowRadius) {
            super();
            mShadowPaint = new Paint();
            mShadowRadius = shadowRadius;
            updateRadialGradient((int) rect().width());
        }

        @Override
        protected void onResize(float width, float height) {
            super.onResize(width, height);
            updateRadialGradient((int) width);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final int viewWidth = CircleButton.this.getWidth();
            final int viewHeight = CircleButton.this.getHeight();
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2, mShadowPaint);
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2 - mShadowRadius, paint);
        }

        private void updateRadialGradient(int diameter) {
            mRadialGradient = new RadialGradient(diameter / 2, diameter / 2,
                    mShadowRadius, new int[] { FILL_SHADOW_COLOR, Color.TRANSPARENT },
                    null, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mRadialGradient);
        }
    }
}
