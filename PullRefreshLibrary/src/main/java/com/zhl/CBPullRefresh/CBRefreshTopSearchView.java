package com.zhl.CBPullRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;


public class CBRefreshTopSearchView extends LinearLayout {
	private LinearLayout container;

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
		// 初始情况，设置view高度为0
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
		container = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.cbpullrefrsh_topsearch, null);
		addView(container, lp);
	}

	
	
	public void setHeight(int height) {
		if (height < 0)
			height = 0;
		LayoutParams lp = (LayoutParams) container.getLayoutParams();
		lp.height = height;
		container.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		return container.getHeight();
	}
	
	
}