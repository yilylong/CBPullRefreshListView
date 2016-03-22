package com.zhl.CBPullRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhl.CBPullRefresh.titanic.Titanic;
import com.zhl.CBPullRefresh.titanic.TitanicTextView;


public class CBRefreshHeader extends CBRefreshHeaderView {
    private LinearLayout mContainer;
    private ImageView mArrowImageView;
    private ProgressBar mProgressBar;
    private TextView mHintTextView;
    private int mState = STATE_NORMAL;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    private final int ROTATE_ANIM_DURATION = 200;

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;
    private TitanicTextView headrAnimView;
    private Titanic titanic;

    public CBRefreshHeader(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public CBRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        // 初始情况，设置下拉刷新view高度为0
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.cblistview_header, null);
        addView(mContainer, lp);
        setGravity(Gravity.BOTTOM);

        mArrowImageView = (ImageView) findViewById(R.id.cbrefresh_header_arrow);
        mHintTextView = (TextView) findViewById(R.id.cbrefresh_header_hint_textview);
        mProgressBar = (ProgressBar) findViewById(R.id.cbrefresh_header_progressbar);
        headrAnimView = (TitanicTextView) findViewById(R.id.cbrefresh_header_anim);
        titanic = new Titanic();
        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    public void setState(int state) {
        if (state == mState)
            return;

        if (state == STATE_REFRESHING) { // 显示进度
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else { // 显示箭头图片
            mArrowImageView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        switch (state) {
            case STATE_NORMAL:
                if (mState == STATE_READY) {
                    mArrowImageView.startAnimation(mRotateDownAnim);
                }
                if (mState == STATE_REFRESHING) {
                    mArrowImageView.clearAnimation();
                }
                mHintTextView.setText("下拉刷新");
                break;
            case STATE_READY:
                if (mState != STATE_READY) {
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(mRotateUpAnim);
                    mHintTextView.setText("松开刷新数据");
                }
                break;
            case STATE_REFRESHING:
                mHintTextView.setText("正在加载...");
                break;
            default:
        }

        mState = state;
    }

    public void setVisiableHeight(int height) {
        if (height < 0)
            height = 0;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisiableHeight() {
        return mContainer.getHeight();
    }

    /**
     * 设置顶部刷新图标
     *
     * @param resName
     */
    public void setHeaderRefreshIcon(int resName) {
        mArrowImageView.setBackgroundResource(resName);
    }

    @Override
    public void onAnimStart() {
        headrAnimView.setVisibility(View.VISIBLE);
        titanic.start(headrAnimView);
    }

    @Override
    public void onAnimCancel() {
        headrAnimView.setVisibility(View.GONE);
        titanic.cancel();
    }

    @Override
    public void setHeaderAnimTextColor(int color) {
        headrAnimView.setTextColor(color);
    }

    @Override
    public void setState() {

    }

    @Override
    public void pullToRefresh() {

    }

    @Override
    public void releaseToRefresh() {

    }

    @Override
    public void setRefreshing() {

    }

    @Override
    public void dropToLoadmore() {

    }

    @Override
    public void releaseToLoadmore() {

    }
}