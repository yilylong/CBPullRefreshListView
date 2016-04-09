package com.zhl.CBPullRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public  class CBRefreshHeaderView extends LinearLayout implements CBRefreshState {
    private Context context;
    public CBRefreshHeaderView(Context context) {
        this(context, null);
    }

    public CBRefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CBRefreshHeaderView(Context context, AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * set the current state
     * @param state
     */
    @Override
    public void setState(int state) {

    }

    /**
     * pull to refresh listener
     */
    @Override
    public void pullToRefresh() {

    }

    /**
     * relase to fresh listener
     */
    @Override
    public void releaseToRefresh() {

    }


    /**
     * pull up to loadmore
     */
    @Override
    public void pullUpToLoadmore() {

    }

    /**
     * relase to loadmore
     */
    @Override
    public void releaseToLoadmore() {

    }

    /**
     * onrefreshing
     */
    @Override
    public void onRefreshing() {

    }

    /**
     * onloading
     */
    @Override
    public void onLoading() {

    }

    /**
     * set the header height
     * @param height
     */
    @Override
    public void setVisiableHeight(int height) {

    }

    /**
     * get the header visiable height
     * @return
     */
    @Override
    public int getVisiableHeight() {
        return 0;
    }

    /**
     * on draging
     * @param deltaY
     */
    @Override
    public void onDragSlide(float deltaY) {

    }
    /**
     * get the pull up(footer) height
     */
    @Override
    public int getLoadMorePullUpDistance() {
        return 0;
    }
    /**
     * set the pull up(footer) heith
     */
    @Override
    public void setLoadMorePullUpDistance(int deltaY) {

    }
    /**
     * show  footerview
     */
    @Override
    public void footerViewShow() {

    }
    /**
     * hidden footer view
     */
    @Override
    public void footerViewHide() {

    }
    /**
     * get the header's real height
     */
    @Override
    public int getRealHeaderContentHeight() {
        return 0;
    }
    /**
     * set pull refresh enable
     */
    @Override
    public void setPullRefreshEnable(boolean enable) {

    }
    /**
     * set loadmore enable
     */
    @Override
    public void setLoadMoreEnable(boolean enable) {

    }

    /**
     * set the refresh time
     * @param refreshTime
     */
    @Override
    public void setRefreshTime(String refreshTime) {

    }

    /**
     * set the header icon
     * @param resId
     */
    @Override
    public void setHeaderIcon(int resId) {

    }

    /**
     * on style change
     * @param state
     */
    @Override
    public void onStyleChange(int state) {

    }

    protected String getString(int redID){
       return context.getString(redID);
    }


}
