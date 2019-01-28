package com.ansen.leftrightmoveimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.ansen.leftrightmoveimageview.lib.R;

/**
 * 左右移动图片背景
 * 参考:http://www.apkbus.com/thread-240378-1-1.html
 * @author ansen
 * @create time 2019/1/26
 */
public class LeftRightMoveImageView extends AppCompatImageView {
    private int animationDuration;
    private int resourceId;

    public LeftRightMoveImageView(Context context) {
        this(context,null);
    }

    public LeftRightMoveImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LeftRightMoveImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取自定义属性的值
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.LeftRightMoveImageView);

        final TypedValue srcValue=new TypedValue();
        typedArray.getValue(R.styleable.LeftRightMoveImageView_imageSrc,srcValue);
        resourceId=srcValue.resourceId;

        setImageResource(srcValue.resourceId);

        animationDuration=typedArray.getInt(R.styleable.LeftRightMoveImageView_animationDuration,0);

        typedArray.recycle();

        //view加载完成时回调
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);//需要移除监听  不然进去死循环

                Log.i("ansen","onGlobalLayout:");
                startAnimation(resourceId,animationDuration);//加载完成开启动画
            }
        });
    }

    public void startAnimation(int src, int animationDuration){
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),src);
        int imageWidht=bitmap.getWidth();//图片本身宽度

        ViewGroup.LayoutParams params=getLayoutParams();
        params.width=imageWidht;
        setLayoutParams(params);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        runAnimation(imageWidht-dm.widthPixels-20,animationDuration);
    }

    private void runAnimation(float toXValue,int animationDuration) {
        Log.i("ansen"," toXValue:"+toXValue);
        final TranslateAnimation right = new TranslateAnimation(0f,
                -toXValue,
                0f,
                0f);
        final TranslateAnimation left = new TranslateAnimation(-toXValue, 0f,
                0f,  0f);
        right.setDuration(animationDuration);// 背景右移
        left.setDuration(animationDuration);// 背景左边移动

        right.setFillAfter(true);
        left.setFillAfter(true);

        right.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startAnimation(left);
            }
        });

        left.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                startAnimation(right);
            }
        });
        startAnimation(right);
    }

    //在Act destroy的时候被调用的，也就是act对应的window被删除的时候，
    // 且每个view只会被调用一次，父view的调用在后，
    // 也不论view的visibility状态都会被调用，
    // 适合做最后的清理操作；
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        clearAnimation();
    }
}
