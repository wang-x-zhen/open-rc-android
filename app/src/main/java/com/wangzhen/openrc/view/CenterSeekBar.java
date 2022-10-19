package com.wangzhen.openrc.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.ColorInt;

import com.wangzhen.openrc.R;

/**
 * 从中间可以向两边滑动 左负右正
 * Created by slack on 2016/12/16 10:11.
 */
public class CenterSeekBar extends View {

    private static final int DEFAULT_TOUCH_TARGETIZE = 40;
    private static final int PADDING = 200;
    private final int DEFAULT_TEXT_PADDING = 10;
    private final int DEFAULT_THUMB_COLOR = Color.GRAY;

    private final Paint paint;

    private float width = 800; // need <= getWidth()

    /**
     * progress start max
     */
    private int minProgress = 0;

    /**
     * progress end max
     */
    private int maxProgress = 100;

    /**
     * 进度条的颜色 底色 背景色
     */
    @ColorInt
    private int progressBackColor = Color.BLACK;

    /**
     * 进度条的底色 高度
     */
    private float progressBackHeight = 10;

    /**
     * 进度条 底色 圆角矩形边框 描边
     */
    @ColorInt
    private int progressFrameColor = Color.WHITE;

    /**
     * 进度条圆角矩形边框 高度
     */
    private float progressFrameHeight = 3;

    /**
     * 进度条的颜色
     */
    @ColorInt
    private int progressColor = Color.GREEN;

    /**
     * 进度条的 高度
     */
    private float progressHeight = 20;

    /**
     * 如果0在中间,负进度条的颜色
     */
    @ColorInt
    private int progressMinusColor = Color.RED;

    /**
     * current progress
     */
    private int progress = 50;

    /**
     * seekBar Thumb normal radius
     */
    private float mThumbNormalRadius = 14;

    /**
     * seekBar Thumb radius when touched
     */
    private float mThumbPressedRadius = 24;

    /**
     * seekBar Thumb color
     */
    @ColorInt
    private int mThumbColor = DEFAULT_THUMB_COLOR;

    private float mTextLocation = 1;
    /**
     * progress 字体大小
     */
    private float mTextSize = 40;

    /**
     * progress 字体 color
     */
    @ColorInt
    private int mTextColor = Color.WHITE;

    /**
     * progress 字体 背景 color
     */
    @ColorInt
    private int mTextBackColor = 0x7DD2D3D4;

    /**
     * progress 字体 背景 radius
     */
    private float mTextBackRadius = 10;

    /**
     * 判断是否是 0 在中间
     */
    private boolean mIsCenterState = false;

    private float mThumbRadius = mThumbNormalRadius;
    private float progressPosition;
    private boolean isTouchLegal = false;
    private ObjectAnimator mAnimator; //  seekBar Thumb Animator
    private RectF mTextRectF, mBackRectF, mProgressRectF;
    private int mThumbDrawColor = DEFAULT_THUMB_COLOR;

    private OnSeekBarChangeListener mOnSeekBarChangeListener;
    private OnSeekBarProgressListener mOnSeekBarProgressListener;
    private OnSeekBarFinishedListener mOnSeekBarFinishedListener;
    private OnSeekBarDrawTextListener mOnSeekBarDrawTextListener;

    public CenterSeekBar(Context context) {
        this(context, null);
    }

    public CenterSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CenterSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setAntiAlias(true);

        if (attrs != null) {
            TypedArray styledAttrs = getContext().obtainStyledAttributes(attrs,
                    R.styleable.CenterSeekBar, 0, 0);
            maxProgress = styledAttrs.getInteger(R.styleable.CenterSeekBar_maxProgress, 100);
            minProgress = styledAttrs.getInteger(R.styleable.CenterSeekBar_minProgress, 0);
            width = styledAttrs.getDimension(R.styleable.CenterSeekBar_width, 800);
            mIsCenterState = styledAttrs.getBoolean(R.styleable.CenterSeekBar_centerState, false);
            progressBackColor = styledAttrs.getColor(R.styleable.CenterSeekBar_backColor, Color.BLACK);
            progressBackHeight = styledAttrs.getDimension(R.styleable.CenterSeekBar_backHeight, 10);
            progressFrameColor = styledAttrs.getColor(R.styleable.CenterSeekBar_backFrameColor, Color.WHITE);
            progressFrameHeight = styledAttrs.getDimension(R.styleable.CenterSeekBar_backFrameSize, 3);
            progressColor = styledAttrs.getColor(R.styleable.CenterSeekBar_progressColor, Color.GREEN);
            progressHeight = styledAttrs.getDimension(R.styleable.CenterSeekBar_progressHeight, progressBackHeight);
            progressMinusColor = styledAttrs.getColor(R.styleable.CenterSeekBar_progressMinusColor, Color.RED);
            progress = styledAttrs.getInteger(R.styleable.CenterSeekBar_progress, 50);
            mThumbNormalRadius = styledAttrs.getDimension(R.styleable.CenterSeekBar_thumbNormalRadius, 14);
            mThumbPressedRadius = styledAttrs.getDimension(R.styleable.CenterSeekBar_thumbPressRadius, 24);
            mThumbColor = styledAttrs.getColor(R.styleable.CenterSeekBar_thumbColor, Color.BLUE);
            progressColor = styledAttrs.getColor(R.styleable.CenterSeekBar_progressColor, Color.BLUE);
            mTextColor = styledAttrs.getColor(R.styleable.CenterSeekBar_textColor, Color.WHITE);
            mTextSize = styledAttrs.getDimension(R.styleable.CenterSeekBar_textSize, 40);
            mTextBackColor = styledAttrs.getColor(R.styleable.CenterSeekBar_textBackColor, 0x7DD2D3D4);
            mTextBackRadius = styledAttrs.getDimension(R.styleable.CenterSeekBar_textBackRadius, 10);

            mThumbRadius = mThumbNormalRadius;
            mThumbDrawColor = mThumbColor;
            styledAttrs.recycle();
        }

        mAnimator = getTargetAnimator(false);
        mTextRectF = new RectF();
        mBackRectF = new RectF();
        mProgressRectF = new RectF();
    }

    /**
     * 设置是否是 0 在中间
     *
     * @param enable
     */
    public CenterSeekBar setCenterModeEnable(boolean enable) {
        // 将负值 变成正值
        if (mIsCenterState) {
            if (!enable) {
                if (progress < 0) {
                    progress = -progress;
                }
            }
        }
        mIsCenterState = enable;
        invalidate();
        return this;
    }

    public CenterSeekBar setProgress(int progress) {
        if (mIsCenterState) {
            if (progress <= maxProgress && progress >= minProgress - maxProgress) {
                this.progress = progress;
            } else {
                this.progress = minProgress;
            }
        } else {
            if (progress <= maxProgress && progress >= minProgress) {
                this.progress = progress;
            } else {
                this.progress = minProgress;
            }
        }
        invalidate();
        return this;
    }

    /**
     * 是否可用
     */
    public CenterSeekBar setProgressEnable(boolean enable) {
        if (enable) {
            this.setEnabled(true);
            mThumbDrawColor = mThumbColor;
        } else {
            this.setEnabled(false);
            this.progress = 0;
            mThumbDrawColor = DEFAULT_THUMB_COLOR;
        }
        invalidate();
        return this;
    }

    public int getProgress() {
        return progress;
    }

    public CenterSeekBar setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
        return this;
    }

    public CenterSeekBar setOnSeekBarProgressListener(OnSeekBarProgressListener l) {
        mOnSeekBarProgressListener = l;
        return this;
    }

    public CenterSeekBar setOnSeekBarFinishedListener(OnSeekBarFinishedListener l) {
        mOnSeekBarFinishedListener = l;
        return this;
    }
    public CenterSeekBar setOnSeekBarDrawTextListener(OnSeekBarDrawTextListener l) {
        mOnSeekBarDrawTextListener = l;
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.i("slack","onDraw... " + mThumbRadius);
        int centerX = getWidth() / 2; // x 是center
        int centerY = getHeight() / 2; // y 是 2/3 高度
        float startX = centerX - (width - PADDING) / 2;

        // draw background line
        paint.setColor(progressBackColor);
        paint.setStrokeWidth(progressBackHeight);
        paint.setStyle(Paint.Style.FILL); // 实心
        mBackRectF.left = startX;
        mBackRectF.top = centerY - progressBackHeight;
        mBackRectF.right = startX + (width - PADDING);
        mBackRectF.bottom = centerY;
        canvas.drawRoundRect(mBackRectF, mTextBackRadius, mTextBackRadius, paint);
        paint.setColor(progressFrameColor);
        paint.setStrokeWidth(progressFrameHeight);
        paint.setStyle(Paint.Style.STROKE); // 空心
        canvas.drawRoundRect(mBackRectF, mTextBackRadius, mTextBackRadius, paint);

//        canvas.drawLine(startX, centerY, centerX + width / 2, centerY, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(progressColor);

        // draw progress
        paint.setStrokeWidth(progressHeight);
        paint.setColor(progressColor);
        if (mIsCenterState) {
//            if (progress < 0) {
//                paint.setColor(progressMinusColor);
//            }
            startX = centerX;
            progressPosition = startX + (int) ((progress * ((width - PADDING) / 2f) / (maxProgress - minProgress)));
        } else {
            progressPosition = startX + ((progress * (width - PADDING) / (maxProgress - minProgress)));
        }
        mProgressRectF.top = centerY - progressBackHeight;
        mProgressRectF.bottom = centerY;
        if (progress > 0) {
            mProgressRectF.left = startX;
            mProgressRectF.right = progressPosition;
        } else {
            mProgressRectF.left = progressPosition;
            mProgressRectF.right = startX;
        }
        canvas.drawRoundRect(mProgressRectF, mTextBackRadius, mTextBackRadius, paint);

//        canvas.drawLine(startX, centerY, progressPosition, centerY, paint);

        // draw point
        paint.setColor(mThumbDrawColor);
        canvas.drawCircle(progressPosition, centerY - progressBackHeight / 2, mThumbRadius, paint);

        /** mThumbRadius will change
         * mThumbRadius  : mThumbNormalRadius  ----------------- mThumbPressedRadius
         *  alpha        :  0                  ------------------ 255
         *
         */
//        int alpha = (int) (255 * (mThumbRadius - mThumbNormalRadius) / (mThumbPressedRadius - mThumbNormalRadius));
        int alpha = 255;

        // draw text progress  up the Thumb  code is ok
        String text = progress + "%";
        if (mOnSeekBarDrawTextListener != null) {
            text = mOnSeekBarDrawTextListener.onGenText(progress);
        }
        if (mTextLocation == 1) {
            paint.setColor(mTextBackColor);
            paint.setAlpha(alpha);
            mTextRectF.bottom = centerY - mThumbPressedRadius - DEFAULT_TEXT_PADDING;
            mTextRectF.right = progressPosition + mTextSize + DEFAULT_TEXT_PADDING;
            mTextRectF.top = mTextRectF.bottom - mTextSize - DEFAULT_TEXT_PADDING * 3;
            mTextRectF.left = progressPosition - mTextSize - DEFAULT_TEXT_PADDING;
            canvas.drawRoundRect(mTextRectF, mTextBackRadius, mTextBackRadius, paint);
            paint.setTextSize(mTextSize);
            paint.setColor(mTextColor);
            paint.setAlpha(alpha);
            // 为了让text 在背景的中心
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text, progressPosition, mTextRectF.bottom - DEFAULT_TEXT_PADDING * 2, paint);
        } else if (mTextLocation == 2) {
            // draw text in Thumb
            paint.setTextSize(mTextSize);
            paint.setColor(mTextColor);
            paint.setAlpha(alpha);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text, progressPosition, centerY, paint);
        }
    }

    //    private int mLastProgress;
    private long mLastTime;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.i("slack","onTouchEvent " + event.toString());
        if (!isEnabled())
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                checkTouchingTarget(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouchLegal) {
                    progress = (int) clamp((int) event.getX() - getLeft() + PADDING / 2);
//                    if (mLastProgress == progress) {
//                        // 两次一样就不需要重画
//                        break;
//                    }

                    long currentTime = System.currentTimeMillis();
                    if (currentTime - mLastTime < 50) {
                        // 刷新 FPS 不超过 20 fps
                        break;
                    }
                    mLastTime = currentTime;

//                    Log.i("slack","progress " + progress);
                    invalidate(); // 在UI线程中使用 刷新View
                    if (mOnSeekBarChangeListener != null) {
                        mOnSeekBarChangeListener.onProgress(progress);
                    } else if (mOnSeekBarProgressListener != null) {
                        mOnSeekBarProgressListener.onProgress(progress);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                invalidate();
//                mLastProgress = -1;
                if (isTouchLegal) {
                    mAnimator.cancel();
                    mAnimator = getTargetAnimator(false);
                    mAnimator.start();
                    if (mOnSeekBarChangeListener != null) {
                        mOnSeekBarChangeListener.onFinished(progress);
                    } else if (mOnSeekBarFinishedListener != null) {
                        mOnSeekBarFinishedListener.onFinished(progress);
                    }
                }
                break;
        }
        return true;
    }

    /**
     * if touch , seekBar Thumb Animation
     */
    private void checkTouchingTarget(MotionEvent event) {
        if (isTouchingTarget(event)) {
            mAnimator.cancel();
            mAnimator = getTargetAnimator(true);
            mAnimator.start();
        }
    }

    /**
     * 判断是否 touch 在 seekBar thumb 上
     *
     * @param event
     * @return
     */
    private boolean isTouchingTarget(MotionEvent event) {
        float location = progressPosition;
        isTouchLegal = event.getX() > location - DEFAULT_TOUCH_TARGETIZE
                && event.getX() < location + DEFAULT_TOUCH_TARGETIZE;
        Log.i("slack", "isTouchLegal " + isTouchLegal + " " + event.getX() + " " +
                event.getY() + " " + progressPosition);
        return isTouchLegal;
    }

    /**
     * 自定义动画  ofFloat(Object target, String propertyName, float... values)
     * 第一个参数用于指定这个动画要操作的是哪个控件
     * 第二个参数用于指定这个动画要操作这个控件的哪个属性
     * 第三个参数是可变长参数，这个就跟ValueAnimator中的可变长参数的意义一样了，就是指这个属性值是从哪变到哪
     * 对于自定义的属性,d第二个参数需要提供setXXX 方法,like:  public void setMThumbRadius(int mThumbRadius)
     * set方法 属性的第一个字母记得要大写 !  ObjectAnimator.ofInt , ofInt 对应参数的类型,如果float 为ofFloat
     */
    private ObjectAnimator getTargetAnimator(boolean touching) {
        final ObjectAnimator anim = ObjectAnimator.ofFloat(this,
                "mThumbRadius",
                mThumbRadius,
                touching ? mThumbPressedRadius : mThumbNormalRadius);
        anim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                Log.i("slack","onAnimationUpdate...");
                postInvalidate(); // 在子线程中使用刷新View
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
//                Log.i("slack","onAnimationEnd...");
                anim.removeAllListeners();
            }
        });
        anim.setInterpolator(new AccelerateInterpolator());
        return anim;
    }

    public void setMThumbRadius(float mThumbRadius) {
//        Log.i("slack","setmThumbRadius...");
        this.mThumbRadius = mThumbRadius;
    }

    /**
     * return  progress
     * -maxProgress          minProgress              maxProgress
     * \------------------------0---------------------------\
     * min                   center     touch-->\          max
     * (min center touch max are positions in the screen)
     * touch progress = (touch - center) / (max - center) * maxProgress;
     */
    private float clamp(int value) {
        if (mIsCenterState) {
            int centerX = getWidth() / 2;
            float min = centerX - (width - PADDING) / 2;// the start point
            float max = centerX + (width - PADDING) / 2;// the end point
            if (value > centerX) {
                if (value >= max) {
                    return maxProgress;
                } else {
                    return (int) ((maxProgress - minProgress) * (value - centerX) / ((width - PADDING) / 2f));
                }
            } else if (value < centerX) {
                if (value <= min) {
                    return -maxProgress;
                } else {
                    return (int) ((maxProgress - minProgress) * (value - centerX) / ((width - PADDING) / 2f));
                }
            } else {
                return minProgress;
            }
        } else {
            int centerX = getWidth() / 2;
            float min = centerX - (width - PADDING) / 2;// the start point
            float max = centerX + (width - PADDING) / 2;// the end point
            if (value >= max) {
                return maxProgress;
            } else if (value <= min) {
                return minProgress;
            } else {
                return (maxProgress - minProgress) * (value - min) / (width - PADDING);
            }
        }
    }

    public interface OnSeekBarProgressListener {
        void onProgress(int progress);
    }

    public interface OnSeekBarFinishedListener {
        void onFinished(int progress);
    }

    public interface OnSeekBarDrawTextListener {
        String onGenText(int progress);
    }

    public interface OnSeekBarChangeListener extends OnSeekBarProgressListener, OnSeekBarFinishedListener {
    }

}

