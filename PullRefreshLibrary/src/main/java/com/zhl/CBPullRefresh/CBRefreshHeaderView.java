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

    public void setHeaderAnimTextColor(int color){

    };

    /**
     * 设置当前状态
     * @param state
     */
    @Override
    public void setState(int state) {

    }

    /**
     * 下拉刷新的回调
     */
    @Override
    public void pullToRefresh() {

    }

    /**
     * 释放刷新的回调
     */
    @Override
    public void releaseToRefresh() {

    }


    /**
     * 上拉加载更多
     */
    @Override
    public void pullUpToLoadmore() {

    }

    /**
     * 释放加载更多
     */
    @Override
    public void releaseToLoadmore() {

    }

    /**
     * 正在刷新
     */
    @Override
    public void onRefreshing() {

    }

    @Override
    public void onLoading() {

    }

    /**
     * 设置头部或底部的可见高度
     * @param height
     */
    @Override
    public void setVisiableHeight(int height) {

    }

    /**
     * 获取可见高度
     * @return
     */
    @Override
    public int getVisiableHeight() {
        return 0;
    }

    /**
     * 正在下拉
     * @param deltaY
     */
    @Override
    public void onPullDown(int deltaY) {

    }
    /**
     * 正在上拉
     * @param deltaY
     */
    @Override
    public void onPullUp(int deltaY) {

    }
    /**
     * 获取上拉底部的拉动距离
     */
    @Override
    public int getLoadMorePullUpDistance() {
        return 0;
    }
    /**
     * 设置上拉底部的拉动距离
     */
    @Override
    public void setLoadMorePullUpDistance(int deltaY) {

    }
    /**
     * 显示底部view
     */
    @Override
    public void footerViewShow() {

    }
    /**
     * 隐藏底部view
     */
    @Override
    public void footerViewHide() {

    }
    /**
     * 获取头部的真实内容高度
     */
    @Override
    public int getRealHeaderContentHeight() {
        return 0;
    }
    /**
     * 设置下拉是否可用
     */
    @Override
    public void setPullRefreshEnable(boolean enable) {

    }
    /**
     * 设置上拉是否可用
     */
    @Override
    public void setLoadMoreEnable(boolean enable) {

    }

    @Override
    public void setRefreshTime(String refreshTime) {

    }

    @Override
    public void setHeaderIcon(int resId) {

    }

    protected String getString(int redID){
       return context.getString(redID);
    }
}
