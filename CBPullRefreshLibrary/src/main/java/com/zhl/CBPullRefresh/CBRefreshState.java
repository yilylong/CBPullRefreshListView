package com.zhl.CBPullRefresh;

/**
 * 描述：刷新状态的接口
 * Created by zhaohl on 2016-3-22.
 */
public interface CBRefreshState {
    /**
     * 下拉刷新
     */
    public final static int STATE_PULL_TO_REFRESH = 0;
    /**
     * 释放刷新
     */
    public final static int STATE_RELEASE_TO_REFRESH = 1;
    /**
     * 正在刷新
     */
    public final static int STATE_REFRESHING = 2;
    /**
     * 上拉加载
     */
    public final static int STATE_PULL_UP_TO_LOADMORE = 3;
    /**
     * 释放加载更多
     */
    public final static int STATE_RELEASE_TO_LOADMORE = 4;

    /**
     * 设置状态
     */
    public void setState(int state);

    public void pullToRefresh();

    public void releaseToRefresh();

    /**
     * 上拉加载更多
     */
    public void pullUpToLoadmore();

    public void releaseToLoadmore();

    public void onRefreshing();

    public void onLoading();

    public void setVisiableHeight(int height);

    public int getVisiableHeight();

    public void onDragSlide(float deltaY);

    /**
     * 上拉距离
     * @return
     */
    public int getLoadMorePullUpDistance();

    /**
     * 设置下拉距离
     * @return
     */
    public void setLoadMorePullUpDistance(int deltaY);

    public void footerViewShow();

    public void footerViewHide();

    public int getRealHeaderContentHeight();

    public void setPullRefreshEnable(boolean enable);

    public void setLoadMoreEnable(boolean enable);

    public void setRefreshTime(String refreshTime);

    public void setHeaderIcon(int resId);

    public void onStyleChange(int state);
}
