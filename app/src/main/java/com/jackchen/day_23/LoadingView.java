package com.jackchen.day_23;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * Email: 2185134304@qq.com
 * Created by JackChen 2018/3/8 15:29
 * Version 1.0
 * Params:
 * Description:  花束直播加载效果
*/
public class LoadingView extends RelativeLayout {



    // 位移动画的偏移距离    30dp 需要转为 px
    private int mTranslationDistance = 30 ;
    // 左边的圆，中间的圆，右边的圆
    private CircleView mLeftView , mMiddleView , mRightView;
    // 动画时长
    private final long ANIMATION_TIME = 350;


    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 两个圆形进行位移动画 中间的运动间距
        mTranslationDistance = dp2px(mTranslationDistance) ;
        setBackgroundColor(Color.WHITE);

        // 添加3个View，但是需要圆形
        mLeftView = getCircleView(context);
        mLeftView.exchangeColor(Color.BLUE);

        // 中间的圆
        mMiddleView = getCircleView(context) ;
        mMiddleView.exchangeColor(Color.RED);

        // 右边的圆
        mRightView = getCircleView(context) ;
        mRightView.exchangeColor(Color.GREEN);

        addView(mLeftView);
        addView(mMiddleView);
        addView(mRightView);

        // onResume()之后、View绘制流程执行完毕、布局实例化好之后再去开启动画
        post(new Runnable() {
            @Override
            public void run() {
                // 扩展动画
                expendAnimation() ;
            }
        }) ;


    }


    /**
     * 展开动画
     */
    private void expendAnimation() {
        // 左边跑  从0位置 到 -mTranslationDistance位置
        ObjectAnimator leftTranslationAnimator = ObjectAnimator.ofFloat(mLeftView, "translationX", 0, -mTranslationDistance);
        // 右边跑
        ObjectAnimator rightTransltionAnimator = ObjectAnimator.ofFloat(mRightView, "translationX", 0, mTranslationDistance);

        // 这里需要差值器 像荡秋千一样的弹性效果 刚开始越来越慢  DecelerateInterpolator

        // android.animator包下的
        AnimatorSet set = new AnimatorSet() ;
        // 设置组合动画时长
        set.setDuration(ANIMATION_TIME) ;
        // 一起播放动画
        set.playTogether(leftTranslationAnimator , rightTransltionAnimator);
        // 设置差值器 刚开始越来越慢
        set.setInterpolator(new DecelerateInterpolator());
        // 监听往外边的动画执行完毕后，然后让动画又往里边跑
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 往里边跑的动画
                innerAnimation() ;
            }
        });

        // 开启动画
        set.start();


    }


    /**
     * 往里边跑的动画 , 和往外边跑的动画位移都是相反的
     */
    private void innerAnimation() {
        // 左边跑
        ObjectAnimator leftTranslationAnimator = ObjectAnimator.ofFloat(mLeftView, "translationX", -mTranslationDistance, 0);
        // 右边跑
        ObjectAnimator rightTranslationAnimator = ObjectAnimator.ofFloat(mRightView, "translationX", mTranslationDistance, 0);

        // 组合动画
        AnimatorSet set = new AnimatorSet() ;
        // 设置时长
        set.setDuration(ANIMATION_TIME) ;
        // 设置差值器
        set.setInterpolator(new AccelerateInterpolator());
        // 让两个动画一起执行
        set.playTogether(leftTranslationAnimator , rightTranslationAnimator) ;

        // 监听往里边的动画执行完毕后，然后继续让其往外边跑，循环跑，
        // 往里边的动画执行完毕后就执行往外边的动画，外边的执行完就又执行往里边的动画
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 切换颜色的顺序  左边的给中间 中间的给右边 右边的给左边

                // 获取3个圆的颜色
                int leftColor = mLeftView.getColor() ;
                int middleColor = mMiddleView.getColor() ;
                int rightColor = mRightView.getColor() ;


                mLeftView.exchangeColor(rightColor);
                mRightView.exchangeColor(middleColor);
                mMiddleView.exchangeColor(leftColor);

                // 再重新去展开动画
                expendAnimation();
            }
        });

        set.start();

    }


    /**
     * 获取圆
     * @param context
     * @return
     */
    private CircleView getCircleView(Context context) {
        // 实例化圆的自定义View
        CircleView circleView = new CircleView(context) ;
        // 布局参数  圆的宽和高都是 10dp - 转为 px
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dp2px(10) , dp2px(10)) ;
        // 给参数设置 添加规则 ，正中心位置
        params.addRule(CENTER_IN_PARENT);
        // 给圆设置布局参数
        circleView.setLayoutParams(params);
        // 返回圆
        return circleView;

    }


    /**
     * dp - px
     * @param dip
     * @return
     */
    private int dp2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP , dip , getResources().getDisplayMetrics());
    }


    /**
     * 针对于动画涉及到内存泄露的处理情况
     * @param visibility
     */
    @Override
    public void setVisibility(int visibility) {
        // 不要再去摆放和计算，少走一些系统源码
        super.setVisibility(View.INVISIBLE);

        // 清理动画
        mLeftView.clearAnimation();
        mMiddleView.clearAnimation();
        mRightView.clearAnimation();

        // 把LoadingView从父布局中移除
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null){
            parent.removeView(this);
            removeAllViews();
        }

    }
}
