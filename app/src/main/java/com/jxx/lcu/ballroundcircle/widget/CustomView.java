package com.jxx.lcu.ballroundcircle.widget;


import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.jxx.lcu.ballroundcircle.MainActivity;
import com.jxx.lcu.ballroundcircle.R;

/**
 * Created by Administrator on 2018/3/8.
 */

public class CustomView extends View {

    private int mRingColor;
    private int mGlobColor;
    private float mRingWidth;
    private float mGlobuleRadius;
    private float mCycleTime;
    private Paint mPaint;
    private float mRingRadius;
    private double currentAngle = -1;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attrsArray = context.obtainStyledAttributes(
                attrs, R.styleable.AccelerateCircularView, defStyleAttr, 0);
        mRingColor = attrsArray.getColor(
                R.styleable.AccelerateCircularView_ringColor, Color.GRAY);
        mGlobColor = attrsArray.getColor(
                R.styleable.AccelerateCircularView_globColor, Color.BLUE);
        mRingWidth = attrsArray.getDimension(
                R.styleable.AccelerateCircularView_ringWidth, TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mGlobuleRadius = attrsArray.getDimension(
                R.styleable.AccelerateCircularView_globRadius, TypedValue
                        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
                                getResources().getDisplayMetrics()));
        mCycleTime = attrsArray.getFloat(R.styleable.AccelerateCircularView_cycleTime, 3000);
        attrsArray.recycle();
        mPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mWidth, mHeight;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widhtSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widhtSize;
        } else {
            mWidth = 169;
            if (widthMode == MeasureSpec.AT_MOST) {
                mWidth = Math.min(mWidth, widhtSize);
            }
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = 169;
            if (heightMode == MeasureSpec.AT_MOST) {
                mHeight = Math.min(mWidth, heightSize);
            }
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int central = Math.min(getWidth(), getHeight()) / 2;
        mRingRadius = central - mGlobuleRadius;
        if (mGlobuleRadius < mRingWidth / 2) {//小球嵌在环里
            mRingRadius = central - mRingWidth / 2;
        }

        mPaint.setStrokeWidth(mRingWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mRingColor);
        canvas.drawCircle(central, central, mRingRadius, mPaint);//绘制圆环
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mGlobColor);

        if (currentAngle == -1) {
            startCirMotion();
        }
        drawGlob(canvas, central);
    }

    private void drawGlob(Canvas canvas, int central) {
        float cx = central + (float) (mRingRadius * Math.cos(currentAngle));
        float cy = (float) (central + mRingRadius * Math.sin(currentAngle));
        canvas.drawCircle(cx, cy, mGlobuleRadius, mPaint);
    }

    //旋转小球
    private void startCirMotion() {
        ValueAnimator animator = ValueAnimator.ofFloat(90f, 450f);
        animator.setDuration((long) mCycleTime).setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float angle = (Float) animation.getAnimatedValue();
                currentAngle = angle * Math.PI / 180;
                invalidate();
            }
        });
        animator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                float output;
                if (input < 0.5) {
                    output = (float) Math.sin(input * Math.PI) / 2;
                } else {
                    output = 1 - (float) Math.sin(input * Math.PI) / 2;
                }
                return output;
            }
        });
        animator.start();
    }
}
