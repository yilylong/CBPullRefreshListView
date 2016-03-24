package com.zhl.CBPullRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CBRefreshFooter extends CBRefreshHeaderView {

    private Context mContext;
    private View mContentView;
    private View mProgressBar;
    private TextView mHintView;

    public CBRefreshFooter(Context context) {
        super(context);
        initView(context);
    }

    public CBRefreshFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    @Override
    public void setHeaderAnimTextColor(int color) {

    }

    @Override
    public void setState(int state) {
        //mHintView.setVisibility(View.INVISIBLE);
//        mProgressBar.setVisibility(View.GONE);
//        //mHintView.setVisibility(View.INVISIBLE);
//        if (state == STATE_RELEASE_TO_LOADMORE) {
//            mHintView.setVisibility(View.VISIBLE);
//            mHintView.setText("松开载入更多");
//        } else if (state == STATE_REFRESHING) {
//            mProgressBar.setVisibility(View.VISIBLE);
//            mHintView.setVisibility(View.VISIBLE);
//            mHintView.setText("正在加载...");
//        } else {
//            mHintView.setVisibility(View.VISIBLE);
//            mHintView.setText("加载更多");
//        }
    }


    /**
     * normal status
     */
    public void normal() {
        mHintView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * loading status
     */
    public void loading() {
        mHintView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }


    private void initView(Context context) {
        mContext = context;
        LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.cblistview_footer, null);
        addView(moreView);
        moreView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        mContentView = moreView.findViewById(R.id.cbrefresh_footer_content);
        mProgressBar = moreView.findViewById(R.id.cbrefresh_footer_progressbar);
        mHintView = (TextView) moreView.findViewById(R.id.cbrefresh_footer_hint_textview);
    }

    /**
     * 设置底部加载更多背景
     *
     * @param resName
     */
    public void setFooterBg(int resName) {
        mContentView.setBackgroundResource(resName);
    }

    @Override
    public void pullUpToLoadmore() {
        mProgressBar.setVisibility(View.GONE);
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText("加载更多");
    }

    @Override
    public void releaseToLoadmore() {
        mProgressBar.setVisibility(View.GONE);
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText("松开载入更多");
    }

    @Override
    public void onLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText("正在加载...");
    }

    @Override
    public void onPullDown(int deltaY) {

    }

    @Override
    public void onPullUp(int deltaY) {

    }

    @Override
    public int getLoadMorePullUpDistance() {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        return lp.bottomMargin;
    }

    @Override
    public void setLoadMorePullUpDistance(int deltaY) {
        if (deltaY < 0)
            return;
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        lp.bottomMargin = deltaY;
        mContentView.setLayoutParams(lp);
    }

    @Override
    public void footerViewShow() {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        mContentView.setLayoutParams(lp);
    }

    @Override
    public void footerViewHide() {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        lp.height = 0;
        mContentView.setLayoutParams(lp);
    }

}
