package com.jackchen.day_23;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Email: 2185134304@qq.com
 * Created by JackChen 2018/3/8 15:20
 * Version 1.0
 * Params:
 * Description:   画圆
*/
public class CircleView extends View {


    // 画笔
    private Paint mPaint ;
    // 圆形颜色
    private int mColor ;

    public CircleView(Context context) {
        this(context,null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 初始化画笔
        mPaint = new Paint() ;
        mPaint.setAntiAlias(true);  // 设置抗锯齿
        mPaint.setDither(true);    //  设置防抖动
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画背景圆形
        int cx = getWidth()/2 ;
        int cy = getHeight()/2 ;
        canvas.drawCircle(cx , cy , cx , mPaint);
    }


    /**
     * 交换颜色
     * @param color
     */
    public void exchangeColor(int color){
        this.mColor = color ;
        mPaint.setColor(color);
        invalidate();
    }


    /**
     * 获取当前的颜色
     * @return
     */
    public int getColor(){
        return mColor ;
    }
}
