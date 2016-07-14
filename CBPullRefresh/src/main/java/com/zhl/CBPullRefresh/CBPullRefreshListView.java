package com.zhl.CBPullRefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;

import com.zhl.CBPullRefresh.SwipeMenu.SwipeMenu;
import com.zhl.CBPullRefresh.SwipeMenu.SwipeMenuAdapter;
import com.zhl.CBPullRefresh.SwipeMenu.SwipeMenuCreator;
import com.zhl.CBPullRefresh.SwipeMenu.SwipeMenuLayout;
import com.zhl.CBPullRefresh.SwipeMenu.SwipeMenuView;
import com.zhl.CBPullRefresh.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CBPullRefreshListView extends ListView implements OnScrollListener {
    private static final int TOUCH_STATE_NONE = 0;
    private static final int TOUCH_STATE_X = 1;
    private static final int TOUCH_STATE_Y = 2;
    private float mLastY = -1; // save event y
    private Scroller mScroller; // used for scroll back
    private OnScrollListener mScrollListener; // user's scroll listener
    // the interface to trigger refresh and load more.
    private OnPullRefreshListener mPullRefreshListener;
    private int headersCount;
    // -- header view
    private CBRefreshHeaderView mHeaderView;
    // -- footer view
    private CBRefreshHeaderView mFooterView;
    private int mHeaderViewHeight; // header view's height
    private boolean mEnablePullRefresh = true;

    private boolean mPullRefreshing = false; // is refreashing.
    private boolean mEnablePullLoad;
    private boolean mPullLoading;// isLoading
    private boolean mIsFooterReady = false;
    // total list items, used to detect is at the bottom of listview.
    private int mTotalItemCount = 0;
    // for mScroller, scroll back from header or footer.
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;
    private final static int SCROLL_DURATION = 200; // scroll back duration
    private final static int PULL_LOAD_MORE_DELTA = 150; // when pull up >= 150px
    private final static float OFFSET_RADIO = 2.3f; // support iOS like pull
    // feature.
    private boolean showTopSearchBar = false;
    private CBRefreshHeaderView topSearchView;
    private int topSearchBarHeight = 0;
    private OnSearchClickListener searchClickListener;
    private OnMenuItemClickListener mMenuItemClickListener;
    private SwipeMenuLayout mTouchView;
    private long refreshTime;
    private SwipeMenuCreator mMenuCreator;
    private Interpolator mSwipeInterpolator;
    private int mTouchPosition;
    private int mTouchState = TOUCH_STATE_NONE;
    private float mDownX;
    private float mDownY;
    private float disX, disY;
    private int MAX_Y = 5;
    private int MAX_X = 3;
    private OnSwipeListener mOnSwipeListener;
    private Context context;
    private boolean swipeEnable = false;
    private int touchSlop;
    private int MIN_FLING;
    private int MAX_VELOCITYX;
    private GestureDetectorCompat mGestureDetector;
    private GestureDetector.OnGestureListener mGestureListener;
    private boolean isFling;

    /**
     * @param context
     */
    public CBPullRefreshListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public CBPullRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public CBPullRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    private void initWithContext(Context context) {
        this.context = context;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        MIN_FLING = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        MAX_VELOCITYX = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mScroller = new Scroller(context, new DecelerateInterpolator());
        super.setOnScrollListener(this);
        // init header view
        mHeaderView = new CBRefreshHeader(context);
        addHeaderView(mHeaderView);

        // init header height
        initHeaderHeight();

        topSearchView = new CBRefreshTopSearchView(context);
        topSearchView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchClickListener != null) {
                    searchClickListener.onSearchBarClick();
                }
            }
        });
        addHeaderView(topSearchView);
        initTopsearchViewHeight();

        // init footer view
        mFooterView = new CBRefreshFooter(context);
        mFooterView.footerViewHide();
        mFooterView.setOnClickListener(null);
        // headerview and footer divider
        setHeaderDividersEnabled(false);
        mGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                isFling = false;
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(e1==null){
                    return false;
                }
                if ((e1.getX() - e2.getX()) > MIN_FLING && velocityX < MAX_VELOCITYX) {
                    isFling = true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        };
        mGestureDetector = new GestureDetectorCompat(getContext(), mGestureListener);
    }

    @Override
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        super.addHeaderView(v, data, isSelectable);
        headersCount++;
    }

    private void initTopsearchViewHeight() {
        topSearchView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                topSearchBarHeight = topSearchView.getRealHeaderContentHeight();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void initHeaderHeight() {
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHeaderViewHeight = mHeaderView.getRealHeaderContentHeight();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        // ensure footer view is the last child view
        if (mIsFooterReady == false) {
            mIsFooterReady = true;
            addFooterView(mFooterView);
            setFooterDividersEnabled(false);
        }
        setHeaderDividersEnabled(false);
        setRefreshTime(null);
//		super.setAdapter(adapter);
        super.setAdapter(new SwipeMenuAdapter(getContext(), adapter) {
            @Override
            public void createMenu(SwipeMenu menu) {
                if (mMenuCreator != null) {
                    mMenuCreator.create(menu);
                }
            }

            @Override
            public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
                if (mMenuItemClickListener != null) {
                    mMenuItemClickListener.onMenuItemClick(view.getPosition(), menu, index);
                }
                if (mTouchView != null) {
                    mTouchView.smoothCloseMenu();
                }
            }
        });
    }

    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        mHeaderView.setPullRefreshEnable(mEnablePullRefresh);
    }

    public void setStyleChange(int state) {
        mHeaderView.onStyleChange(state);
        mFooterView.onStyleChange(state);
    }

    /**
     * enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadMoreEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            mFooterView.footerViewHide();
            mFooterView.setOnClickListener(null);
        } else {
            mPullLoading = false;
            mFooterView.footerViewShow();
            mFooterView.setState(CBRefreshState.STATE_PULL_UP_TO_LOADMORE);
            mFooterView.pullUpToLoadmore();
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * set Swipe enable
     *
     * @param swipeEnable
     */
    public void setSwipeEnable(boolean swipeEnable) {
        this.swipeEnable = swipeEnable;
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            resetHeaderHeight();
            resetTopSearchBarHeight();
            mHeaderView.setState(CBRefreshState.STATE_PULL_TO_REFRESH);
        }
    }


    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading == true) {
            mPullLoading = false;
            mFooterView.pullUpToLoadmore();
            mFooterView.setState(CBRefreshState.STATE_PULL_UP_TO_LOADMORE);
        }
    }

    /**
     * set last refresh time
     *
     * @param time
     */
    @SuppressLint("SimpleDateFormat")
    public void setRefreshTime(String time) {
        if (null == time) {
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            mHeaderView.setRefreshTime(dateFormat.format(now));
        } else {
            mHeaderView.setRefreshTime(time);
        }
    }

    /**
     * set refreshTIme
     *
     * @param time
     */
    public void setRefreshTime(long time) {
        if (time <= 0) {
            setRefreshTime(null);
        } else {
            refreshTime = time;
            setRefreshTime(Utils.getTimeDifferent(context, time));
        }
    }

    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());
        if (mEnablePullRefresh && !mPullRefreshing) {
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.releaseToRefresh();
                mHeaderView.setState(CBRefreshState.STATE_RELEASE_TO_REFRESH);
            } else {
                mHeaderView.pullToRefresh();
                mHeaderView.setState(CBRefreshState.STATE_PULL_TO_REFRESH);
            }
        }
        setSelection(0); // scroll to top each time
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        int height = mHeaderView.getVisiableHeight();
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    private void updateFooterHeight(float delta) {
        if (!mEnablePullLoad) {
            return;
        }
        int height = mFooterView.getLoadMorePullUpDistance() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load more.
                mFooterView.releaseToLoadmore();
                mFooterView.setState(CBRefreshState.STATE_RELEASE_TO_LOADMORE);
            } else {
                mFooterView.pullUpToLoadmore();
                mFooterView.setState(CBRefreshState.STATE_PULL_UP_TO_LOADMORE);
            }
        }
        mFooterView.setLoadMorePullUpDistance(height);

        // setSelection(mTotalItemCount - 1); // scroll to bottom
    }

    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getLoadMorePullUpDistance();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            invalidate();
        }
    }

    private void startLoadMore() {
        mPullLoading = true;
        mFooterView.onLoading();
        mFooterView.setState(CBRefreshState.STATE_REFRESHING);
        if (mPullRefreshListener != null) {
            mPullRefreshListener.onLoadMore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("MyTag", "按下-x=" + ev.getX());
                mLastY = ev.getRawY();
                mDownX = ev.getX();
                mDownY = ev.getY();
                int oldPos = mTouchPosition;
                mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
                View view = getChildAt(mTouchPosition - getFirstVisiblePosition());
                if (swipeEnable && mTouchView != null && (mTouchView.isOpen() || mTouchView.isActive()) && oldPos != mTouchPosition) {
                    mTouchView.smoothCloseMenu();
                    mTouchView = null;
                    mTouchState = TOUCH_STATE_NONE;
                }
                if (view instanceof SwipeMenuLayout) {
                    mTouchView = (SwipeMenuLayout) view;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("MyTag", "移动downx=" + mDownX);
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                disX = ev.getX() - mDownX;
                disY = ev.getY() - mDownY;

                if (Math.abs(disX) > touchSlop && Math.abs(disX) > Math.abs(disY)) {// 左右滑动
                    mTouchState = TOUCH_STATE_X;
                    if (swipeEnable && mTouchView != null) {
                        Log.d("MyTag", "侧滑菜单移动=" + disX);
                        mTouchView.swipeMenuSlide(-(int) disX);
                        if (mTouchView.isActive()) {
                            return true;
                        }
                    }
                } else if (Math.abs(disY) > touchSlop && Math.abs(disY) > Math.abs(disX)) {// 上下滑动
                    if ((swipeEnable && mTouchView != null && mTouchView.isOpen()) || (swipeEnable && mTouchView != null && mTouchView.isActive())) {
                        mTouchView.smoothCloseMenu();
                        mTouchView = null;
                        mTouchState = TOUCH_STATE_NONE;
                    }
                    mTouchState = TOUCH_STATE_Y;
                    if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
                        if (showTopSearchBar && topSearchView.getVisiableHeight() < topSearchBarHeight) {
                            updateTopSearchBarHeight(deltaY);
                        } else {
                            updateHeaderHeight(deltaY / OFFSET_RADIO);
                            mHeaderView.onDragSlide((float) mHeaderView.getVisiableHeight() + Math.abs(deltaY / OFFSET_RADIO));
                            invokeOnScrolling();
                        }

                    } else if (getLastVisiblePosition() == mTotalItemCount - 1 && (mFooterView.getLoadMorePullUpDistance() > 0 || deltaY < 0)) {// 上拉
                        // last item, already pulled up or want to pull up.
                        updateFooterHeight(-deltaY / OFFSET_RADIO);
                        mFooterView.onDragSlide((float) mFooterView.getLoadMorePullUpDistance() + (-deltaY / OFFSET_RADIO));
                    }
                }
                break;
            default:
                Log.d("MyTag", "抬起");
                mLastY = -1; // reset
                switch (mTouchState) {
                    case TOUCH_STATE_X:
                        if (swipeEnable && mTouchView != null) {
                            mTouchView.swipeMenuFling(isFling, -(int) disX);
                            if (mTouchView.isActive()) {
                                return true;
                            }
                        }
                        break;
                    case TOUCH_STATE_Y:
                        if (getFirstVisiblePosition() == 0) {
                            // invoke refresh
                            if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                                mPullRefreshing = true;
                                mHeaderView.onRefreshing();
                                mHeaderView.setState(CBRefreshState.STATE_REFRESHING);
                                if (mPullRefreshListener != null) {
                                    mPullRefreshListener.onRefresh();
                                    mPullRefreshListener.onUpdateRefreshTime(System.currentTimeMillis());
                                }
                                setRefreshTime(System.currentTimeMillis());
                            }
                            resetHeaderHeight();
                        } else if (getLastVisiblePosition() == mTotalItemCount - 1) {
                            // invoke load more.
                            if (mEnablePullLoad && mFooterView.getLoadMorePullUpDistance() > PULL_LOAD_MORE_DELTA) {
                                startLoadMore();
                            }
                            resetFooterHeight();
                        } else {
                            resetTopSearchBarHeight();
                        }
                        break;
                }
                mTouchState = TOUCH_STATE_NONE;
                break;
        }
        return super.onTouchEvent(ev);
    }


    private void resetTopSearchBarHeight() {
        if (showTopSearchBar && topSearchView != null) {
            topSearchView.setVisiableHeight(0);
        }
    }

    public void setTopSearchBarHeight() {
        if (topSearchView != null) {
            topSearchView.setVisiableHeight(0);
        }
    }

    private void updateTopSearchBarHeight(float deltaY) {
        if (deltaY > 20) {
            topSearchView.setVisiableHeight(topSearchBarHeight);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
                mHeaderView.onDragSlide(mScroller.getCurrY());
            } else {
                mFooterView.setLoadMorePullUpDistance(mScroller.getCurrY());
                mFooterView.onDragSlide(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }


    /**
     * set the refresh header icon
     *
     * @param resName
     */
    public void setHeaderRefreshIcon(int resName) {
        if (mHeaderView != null) {
            mHeaderView.setHeaderIcon(resName);
        }
    }

    /**
     * set footer view background
     *
     * @param resName
     */
    public void setFooterBg(int resName) {
        if (mFooterView != null) {
            ((CBRefreshFooter) mFooterView).setFooterBg(resName);
        }
    }

    /**
     * set topsearchbar show or not
     *
     * @param show
     */
    public void showTobSearchBar(boolean show) {
        this.showTopSearchBar = show;
    }

    public void setOnPullRefreshListener(OnPullRefreshListener l) {
        mPullRefreshListener = l;
    }

    public void setOnItemClickListener(final OnItemClickListener listener) {
        this.setOnItemClickListener(new AbsListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (listener != null) {
                    //must subtract the headers count
                    listener.onItemClick(parent, view, position - headersCount, id);
                }
            }
        });
    }

    public void setOnSearchBarClickListener(OnSearchClickListener searchClickListener) {
        this.searchClickListener = searchClickListener;
    }

    /**
     * you can listen ListView.OnScrollListener or this one. it will invoke
     * onXScrolling when header/footer scroll back.
     */
    public interface OnXScrollListener extends OnScrollListener {
        public void onXScrolling(View view);
    }

    public void setTopSearchDrawable(int resid) {
        topSearchView.setHeaderIcon(resid);
    }

    public <T extends CBRefreshHeaderView> void setRefreshHeader(T header) {
        if (header == null) {
            return;
        }
        removeHeaderView(mHeaderView);
        headersCount--;
        removeHeaderView(topSearchView);
        headersCount--;
        mHeaderView = header;
        addHeaderView(mHeaderView);
        addHeaderView(topSearchView);
        initHeaderHeight();
        initTopsearchViewHeight();
    }

    public <T extends CBRefreshHeaderView> void setLoadMoreFooter(T footer) {
        if (footer == null) {
            return;
        }
        this.removeFooterView(mFooterView);
        this.mFooterView = footer;
        addFooterView(mFooterView);
        mFooterView.footerViewHide();
        mFooterView.setOnClickListener(null);
    }

    public <T extends CBRefreshHeaderView> void setTopSearchBar(T searchBar) {
        if (searchBar == null) {
            return;
        }
        this.removeFooterView(topSearchView);
        headersCount--;
        this.topSearchView = searchBar;
        addHeaderView(topSearchView);
        initTopsearchViewHeight();
    }

    /**
     * 返回当前触摸的item view
     *
     * @return
     */
    public SwipeMenuLayout getTouchView() {
        return mTouchView;
    }

    /**
     * swipe item is open?
     *
     * @return
     */
    public boolean isSwipeMenuOpen() {
        return mTouchView == null ? false : mTouchView.isOpen() || mTouchView.isActive();
    }

    public void setMenuCreator(SwipeMenuCreator menuCreator) {
        this.mMenuCreator = menuCreator;
    }

    public void setSwipeMenuInterpolator(Interpolator interpolator) {
        this.mSwipeInterpolator = interpolator;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener itemClickListener) {
        this.mMenuItemClickListener = itemClickListener;
    }

    /**
     * the refresh listener
     */
    public interface OnPullRefreshListener {
        public void onRefresh();

        public void onLoadMore();

        public void onUpdateRefreshTime(long time);
    }

    public interface OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id);
    }

    public interface OnSearchClickListener {
        public void onSearchBarClick();
    }

    public static interface OnMenuItemClickListener {
        void onMenuItemClick(int position, SwipeMenu menu, int index);
    }

    public static interface OnSwipeListener {
        void onSwipeStart(int position);

        void onSwipeEnd(int position);
    }
}
