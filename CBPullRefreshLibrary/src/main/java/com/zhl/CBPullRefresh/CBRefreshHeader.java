package com.zhl.cbpullrefresh;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhl.cbpullrefresh.titanic.Titanic;
import com.zhl.cbpullrefresh.titanic.TitanicTextView;


public class CBRefreshHeader extends CBRefreshHeaderView {
    private LinearLayout mContainer;
    private RelativeLayout mHeaderViewContent;
    private ImageView mArrowImageView;
    private ProgressBar mProgressBar;
    private TextView mHintTextView;
    private TextView mHeaderTimeView;
    // the curent state
    private int mState = STATE_PULL_TO_REFRESH;
    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;
    private final int ROTATE_ANIM_DURATION = 200;
    private TitanicTextView headrAnimView;
    private Titanic titanic;


    public CBRefreshHeader(Context context) {
        this(context, null);
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
        // in the first set the Header height=0
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.cblistview_header, null);
        mHeaderViewContent = (RelativeLayout) mContainer.findViewById(R.id.cbrefresh_header_content);
        mHeaderTimeView = (TextView) mContainer.findViewById(R.id.cbrefresh_header_time);
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

    @Override
    public void setState(int state) {
        this.mState = state;
    }
    /**
     * @param resName
     */
    @Override
    public void setHeaderIcon(int resName) {
        mArrowImageView.setBackgroundResource(resName);
    }

    @Override
    public void pullToRefresh() {
        mHintTextView.setText(getString(R.string.refresh_header_tip_pull2refresh));
        mArrowImageView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        if (mState == STATE_RELEASE_TO_REFRESH) {
            mArrowImageView.startAnimation(mRotateDownAnim);
        }
        if (mState == STATE_REFRESHING) {
            mArrowImageView.clearAnimation();
        }
    }

    @Override
    public void releaseToRefresh() {
        mHintTextView.setText(getString(R.string.refresh_header_tip_release2refresh));
        if(mState != STATE_RELEASE_TO_REFRESH){
            mArrowImageView.clearAnimation();
            mArrowImageView.startAnimation(mRotateUpAnim);
        }
    }

    @Override
    public void onRefreshing() {
        mHintTextView.setText(getString(R.string.refresh_header_tip_refreshing));
        mArrowImageView.clearAnimation();
        mArrowImageView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStyleChange(int state) {
        super.onStyleChange(state);
    }

    @Override
    public void setVisiableHeight(int height) {
        if (height < 0)
            height = 0;
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    @Override
    public int getVisiableHeight() {
        return mContainer.getHeight();
    }

    @Override
    public int getRealHeaderContentHeight() {
        // beacuse container height =0 at the first ,so return mHeaderViewContent height
        if(mHeaderViewContent!=null){
            return mHeaderViewContent.getHeight();
        }
        return 0;
    }

    @Override
    public void setPullRefreshEnable(boolean enable) {
        if (!enable) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setRefreshTime(String refreshTime) {
        mHeaderTimeView.setText(refreshTime);
    }
}