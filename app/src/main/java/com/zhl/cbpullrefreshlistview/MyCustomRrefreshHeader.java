package com.zhl.cbpullrefreshlistview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wang.avi.AVLoadingIndicatorView;
import com.zhl.cbpullrefresh.CBRefreshHeaderView;
import com.zhl.cbpullrefresh.CBRefreshState;
import com.zhl.cbpullrefresh.utils.Blur;
import com.zhl.cbpullrefresh.utils.BlurUtil;


/**
 * 描述：
 * Created by zhaohl on 2016-4-6.
 */
public class MyCustomRrefreshHeader extends CBRefreshHeaderView {
    private LinearLayout mHeaderContanier;
    private RelativeLayout mHeaderContent;
    private ImageView blurView;
    private View blurContentView;
    private AVLoadingIndicatorView indicatorView;
    private ImageView rotateView;
    private int State;
    private Context context;
    private int mScreenW,mScreenH;


    public MyCustomRrefreshHeader(Context context) {
        this(context, null);
    }

    public MyCustomRrefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MyCustomRrefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mScreenW = metrics.widthPixels;
        mScreenH = metrics.heightPixels;
        // 初始情况，设置下拉刷新view高度为0
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
        mHeaderContanier = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.my_custom_header,null);
        mHeaderContent = (RelativeLayout) mHeaderContanier.findViewById(R.id.header_content);
        indicatorView = (AVLoadingIndicatorView) mHeaderContanier.findViewById(R.id.avloadingIndicatorView);
        rotateView = (ImageView) mHeaderContanier.findViewById(R.id.rotate_icon);
        startBlur(0);
        addView(mHeaderContanier,lp);
        setGravity(Gravity.BOTTOM);
    }

    @Override
    public void setState(int state) {
        this.State = state;
    }

    @Override
    public void onDragSlide(float deltaY) {
        Log.i("mytag", "--------deltaY===========" + deltaY);
        if(deltaY>0f){
            if(State!= CBRefreshState.STATE_REFRESHING){
                startRotae(deltaY);
                startBlur(deltaY/1000);
            }
        }else{
            clearBlurImage();
        }
    }

    private void startRotae(float deltaY) {
        rotateView.setRotation(deltaY);
    }

    @Override
    public void pullToRefresh() {
        rotateView.setVisibility(View.VISIBLE);
        indicatorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void releaseToRefresh() {
        super.releaseToRefresh();
    }

    @Override
    public void onRefreshing() {
        rotateView.setVisibility(View.INVISIBLE);
        indicatorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setVisiableHeight(int height) {
        if(height<0){
            height = 0;
        }
        LinearLayout.LayoutParams params = (LayoutParams) mHeaderContanier.getLayoutParams();
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


    private void clearBlurImage() {
        if(blurView!=null){
            blurView.setVisibility(View.GONE);
            blurView.setImageBitmap(null);
        }
    }

    private void startBlur(float slideOffset) {
        if(slideOffset>1){
            return;
        }
        if(blurContentView==null||blurView==null){
            return;
        }
        if (blurView.getVisibility() != View.VISIBLE) {
            blurView.setImageBitmap(null);
            blurView.setVisibility(View.VISIBLE);
            Bitmap downScaled = BlurUtil.drawViewToBitmap(blurContentView,
                    mScreenW, mScreenH, 5);
            Bitmap blurred = Blur.apply(context, downScaled, 20);
            blurView.setImageBitmap(blurred);
            downScaled.recycle();
        }
        BlurUtil.setAlpha(blurView, 1-slideOffset);
    }

    public void setBlurView(View contentView,ImageView BlurView){
        this.blurContentView = contentView;
        this.blurView = BlurView;
    }
}
