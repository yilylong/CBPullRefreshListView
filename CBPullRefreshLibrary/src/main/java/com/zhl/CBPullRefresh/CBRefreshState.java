package com.zhl.cbpullrefresh;

/**
 * 描述：刷新状态的接口
 * Created by zhaohl on 2016-3-22.
 */
public interface CBRefreshState {
    /**
     * state pull to refresh
     */
    public final static int STATE_PULL_TO_REFRESH = 0;
    /**
     * state relase to refresh
     */
    public final static int STATE_RELEASE_TO_REFRESH = 1;
    /**
     * state on refreshing
     */
    public final static int STATE_REFRESHING = 2;
    /**
     * state pull to loadmore
     */
    public final static int STATE_PULL_UP_TO_LOADMORE = 3;
    /**
     * state relase to loadmore
     */
    public final static int STATE_RELEASE_TO_LOADMORE = 4;

    /**
     * set the state
     */
    public void setState(int state);
    public void pullToRefresh();

    public void releaseToRefresh();

    public void pullUpToLoadmore();

    public void releaseToLoadmore();

    public void onRefreshing();

    public void onLoading();

    public void setVisiableHeight(int height);

    public int getVisiableHeight();

    public void onDragSlide(float deltaY);

    public int getLoadMorePullUpDistance();

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
