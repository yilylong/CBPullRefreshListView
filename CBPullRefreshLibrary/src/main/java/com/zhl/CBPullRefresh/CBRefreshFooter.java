package com.zhl.cbpullrefresh;

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
    private int state;

    public CBRefreshFooter(Context context) {
        super(context);
        initView(context);
    }

    public CBRefreshFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    @Override
    public void setState(int state) {
        this.state = state;
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
     * set the footer bg
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
        mHintView.setText(getString(R.string.refresh_footer_tip_pullup_loadmore));
    }

    @Override
    public void releaseToLoadmore() {
        mProgressBar.setVisibility(View.GONE);
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(getString(R.string.refresh_footer_tip_release_loadmore));
    }

    @Override
    public void onLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(getString(R.string.refresh_footer_tip_loading));
    }

    @Override
    public void onStyleChange(int state) {
        super.onStyleChange(state);
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
