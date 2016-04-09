package com.zhl.CBPullRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class CBRefreshTopSearchView extends CBRefreshHeaderView {
	private LinearLayout container;
	private ImageView searchImg;

	public CBRefreshTopSearchView(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CBRefreshTopSearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		// in the first set the header's height = 0
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
		container = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.cbpullrefrsh_topsearch, null);
		searchImg = (ImageView) container.findViewById(R.id.pull2reresh_top_search);
		addView(container, lp);
	}

	
	@Override
	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		LayoutParams lp = (LayoutParams) container.getLayoutParams();
		lp.height = height;
		container.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		return container.getHeight();
	}

	@Override
	public int getRealHeaderContentHeight() {
		return searchImg.getHeight();
	}
}