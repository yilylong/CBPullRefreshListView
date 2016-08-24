package com.zhl.cbpullrefreshlistview;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rodolfonavalon.shaperipplelibrary.ShapeRipple;
import com.wang.avi.AVLoadingIndicatorView;
import com.zhl.CBPullRefresh.CBRefreshHeaderView;
import com.zhl.CBPullRefresh.CBRefreshState;


/**
 * 描述：
 * Created by zhaohl on 2016-4-6.
 */
public class MyCustomRrefreshHeaderRippleColor extends CBRefreshHeaderView {
    private LinearLayout mHeaderContanier;
    private FrameLayout mHeaderContent;
    private AVLoadingIndicatorView indicatorView;
    private ShapeRipple shapeRipple;
    private ImageView rotateView;
    private int State;
    private Context context;
    private int mScreenW,mScreenH;
    private int duration = 1500;
    private ObjectAnimator colorAnimator;


    public MyCustomRrefreshHeaderRippleColor(Context context) {
        this(context, null);
    }

    public MyCustomRrefreshHeaderRippleColor(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MyCustomRrefreshHeaderRippleColor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mScreenW = metrics.widthPixels;
        mScreenH = metrics.heightPixels;
        // 初始情况，设置下拉刷新view高度为0
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        mHeaderContanier = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.my_custom_header_ripplecolor,null);
        mHeaderContent = (FrameLayout) mHeaderContanier.findViewById(R.id.header_content);
        indicatorView = (AVLoadingIndicatorView) mHeaderContanier.findViewById(R.id.avloadingIndicatorView);
        rotateView = (ImageView) mHeaderContanier.findViewById(R.id.rotate_icon);
        shapeRipple = (ShapeRipple) mHeaderContanier.findViewById(R.id.shape_ripple);
        shapeRipple.setEnableColorTransition(true);
        shapeRipple.setEnableRandomPosition(true);
        shapeRipple.setEnableRandomColor(true);
//        shapeRipple.setRippleShape(new Square());
        shapeRipple.setRippleInterval(1);
        shapeRipple.setRippleDuration(duration);
        shapeRipple.stopRipple();
//        initColorAnim();
        addView(mHeaderContanier,lp);
        setGravity(Gravity.BOTTOM);
    }

    private void initColorAnim() {
        colorAnimator = ObjectAnimator.ofObject(mHeaderContent,"backgroundColor",new ArgbEvaluator(),0xEFFAFAFA,0x55DE8DAB);
        colorAnimator.setDuration(8000);
        colorAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        colorAnimator.setRepeatCount(ObjectAnimator.INFINITE);
    }

    @Override
    public void setState(int state) {
        this.State = state;
    }

    @Override
    public void onDragSlide(float deltaY) {
        Log.i("MyTag", "--------deltaY===========" + deltaY);
        if(deltaY>0&&State!= CBRefreshState.STATE_REFRESHING){
            startRotae(deltaY);
        }
    }

    private void startRotae(float deltaY) {
        rotateView.setRotation(deltaY);
    }

    @Override
    public void pullToRefresh() {
        rotateView.setVisibility(View.VISIBLE);
        indicatorView.setVisibility(View.INVISIBLE);
        shapeRipple.stopRipple();
    }

    @Override
    public void releaseToRefresh() {
        shapeRipple.stopRipple();
    }

    @Override
    public void onRefreshing() {
        rotateView.setVisibility(View.INVISIBLE);
        indicatorView.setVisibility(View.VISIBLE);
        shapeRipple.startRipple();
    }

    @Override
    public void setVisiableHeight(int height) {
        if(height<0){
            height = 0;
        }
        LayoutParams params = (LayoutParams) mHeaderContanier.getLayoutParams();
        params.height = height;
        mHeaderContanier.setLayoutParams(params);
    }

    @Override
    public int getVisiableHeight() {
        return mHeaderContanier.getHeight();
    }

    @Override
    public int getRealHeaderContentHeight() {
        // 因为container刚开始设置的高度为0所以这里要获取mHeaderContent的高度为真实高度
        if(mHeaderContent!=null){
            return mHeaderContent.getHeight();
        }
        return 0;
    }

    @Override
    public void setPullRefreshEnable(boolean enable) {
        if(enable){
            mHeaderContanier.setVisibility(View.VISIBLE);
        }else{
            mHeaderContanier.setVisibility(View.INVISIBLE);
        }
    }




}
