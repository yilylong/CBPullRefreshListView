package com.zhl.CBPullRefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;

import com.zhl.CBPullRefresh.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CBPullRefreshListView extends ListView implements OnScrollListener {
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
	private boolean mPullLoading;// 正在上拉
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
	private int topSearchBarHeight=0;
	private OnSearchClickListener searchClickListener;
	private long refreshTime;
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
		mScroller = new Scroller(context, new DecelerateInterpolator());
		super.setOnScrollListener(this);
		// init header view
		mHeaderView = new CBRefreshHeader(context);
		addHeaderView(mHeaderView);
		headersCount++;

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
		headersCount++;
		initTopsearchViewHeight();

		// init footer view
		mFooterView = new CBRefreshFooter(context);
		// headerview 和footer 与内容之间无分割线
		setHeaderDividersEnabled(false);
	}

	private void initTopsearchViewHeight() {
		topSearchView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				topSearchBarHeight = topSearchView.getRealHeaderContentHeight();
				if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
					getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}else{
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
				if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
					getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}else{
					getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			}
		});
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// 确保加载更多的布局是最后一个并且只加载一次
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
			setFooterDividersEnabled(false);
		}
		setHeaderDividersEnabled(false);
		setRefreshTime(null);
		super.setAdapter(adapter);
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
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
			resetTopSearchBarHeight();
		}
	}
	
	public void startRefresh() {
		mPullRefreshing = true;
		((CBRefreshHeader) mHeaderView).setState(CBRefreshHeader.STATE_REFRESHING);
		mScrollBack = SCROLLBACK_HEADER;
		((CBRefreshHeader) mHeaderView).setVisiableHeight(mHeaderViewHeight);
		resetHeaderHeight();
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
	 * 设置刷新时间
	 * @param time
	 */
	public void setRefreshTime(long time){
		if(time<=0){
			setRefreshTime(null);
		}else{
			refreshTime = time;
			setRefreshTime(Utils.getTimeDifferent(time));
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
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
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
		if(!mEnablePullLoad){
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
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			// 下拉
			if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				// the first item is showing, header has shown or pull down.
				if(showTopSearchBar&&topSearchView.getVisiableHeight()<topSearchBarHeight){
					updateTopSearchBarHeight(deltaY);
				}else{
					if(mPullRefreshListener != null){
						mPullRefreshListener.setUpdateTime();
					}
					setRefreshTime(System.currentTimeMillis());
					updateHeaderHeight(deltaY / OFFSET_RADIO);
					mHeaderView.onPullDown((int) Math.abs(deltaY / OFFSET_RADIO));
					invokeOnScrolling();
				}
				
			} else if (getLastVisiblePosition() == mTotalItemCount - 1 && (mFooterView.getLoadMorePullUpDistance() > 0 || deltaY < 0)) {// 上拉
				// last item, already pulled up or want to pull up.
				updateFooterHeight(-deltaY / OFFSET_RADIO);
				mFooterView.onPullUp((int) (-deltaY / OFFSET_RADIO));
			}
			break;
		default:
			mLastY = -1; // reset
			if (getFirstVisiblePosition() == 0) {
				// invoke refresh
				if (mEnablePullRefresh &&mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
					mPullRefreshing = true;
					mHeaderView.onRefreshing();
					mHeaderView.setState(CBRefreshState.STATE_REFRESHING);
					if (mPullRefreshListener != null) {
						mPullRefreshListener.onRefresh();
					}
				}
				resetHeaderHeight();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				// invoke load more.
				if (mEnablePullLoad && mFooterView.getLoadMorePullUpDistance() > PULL_LOAD_MORE_DELTA) {
					startLoadMore();
				}
				resetFooterHeight();
			}else{
				resetTopSearchBarHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	private void resetTopSearchBarHeight() {
		if(showTopSearchBar && topSearchView != null){
			topSearchView.setVisiableHeight(0);
		}
	}
	
	public void setTopSearchBarHeight() {
		if(topSearchView != null){
			topSearchView.setVisiableHeight(0);
		}
	}

	private void updateTopSearchBarHeight(float deltaY) {
		if(deltaY>20){
			topSearchView.setVisiableHeight(topSearchBarHeight);
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setLoadMorePullUpDistance(mScroller.getCurrY());
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
	 * 设置顶部刷新图标
	 * @param resName
	 */
	public void setHeaderRefreshIcon(int resName){
		if(mHeaderView!=null){
			mHeaderView.setHeaderIcon(resName);
		}
	}
	
	/**
	 * 设置底部加载更多背景
	 * @param resName
	 */
	public void setFooterBg(int resName){
		if(mFooterView!=null){
			((CBRefreshFooter)mFooterView).setFooterBg(resName);
		}
	}
	/**
	 * 是否显示顶部搜索栏
	 * @param show
	 */
	public void showTobSearchBar(boolean show){
		this.showTopSearchBar = show;
	}
	
	public void setOnPullRefreshListener(OnPullRefreshListener l) {
		mPullRefreshListener = l;
	}
	
	public void setOnItemClickListener(final CBPullRefreshListView.OnItemClickListener listener) {
		this.setOnItemClickListener(new AbsListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(listener!=null){
					//因为这里加了header 必须要减去header数目才是真实的内容position　
					listener.onItemClick(parent, view, position-headersCount, id);
				}
			}
		});
	}
	
	public void setOnSearchBarClickListener(OnSearchClickListener searchClickListener){
		this.searchClickListener = searchClickListener;
	}

	/**
	 * you can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	public void setTopSearchDrawable(int resid){
		topSearchView.setHeaderIcon(resid);
	}
	/**
	 * implements this interface to get refresh/load more event.
	 */
	public interface OnPullRefreshListener {
		public void onRefresh();
		public void onLoadMore();
		public void setUpdateTime();
	}
	
	public interface OnItemClickListener{
		public void onItemClick(AdapterView<?> parent, View view,
								int position, long id);
	}
	public interface OnSearchClickListener{
		public void onSearchBarClick();
	}

	public <T extends CBRefreshHeaderView> void setRefreshHeader(T header){
		if(header==null){
			return;
		}
		removeHeaderView(mHeaderView);
		removeHeaderView(topSearchView);
		mHeaderView = header;
		addHeaderView(mHeaderView);
		addHeaderView(topSearchView);
		initHeaderHeight();
		initTopsearchViewHeight();
	}

	public <T extends CBRefreshHeaderView> void setLoadMoreFooter(T footer){
		if(footer==null){
			return;
		}
		this.removeFooterView(mFooterView);
		this.mFooterView = footer;
		addFooterView(mFooterView);
	}
	public <T extends CBRefreshHeaderView> void setTopSearchBar(T searchBar){
		if(searchBar==null){
			return;
		}
		this.removeFooterView(topSearchView);
		this.topSearchView = searchBar;
		addHeaderView(topSearchView);
		initTopsearchViewHeight();
	}
}
