package com.zhl.CBPullRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public abstract class CBRefreshHeaderView extends LinearLayout implements CBPullRefreshListView.OnHeaderAnimationListenr,CBRefreshState {
    public CBRefreshHeaderView(Context context) {
        this(context, null);
    }

    public CBRefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CBRefreshHeaderView(Context context, AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void setHeaderAnimTextColor(int color);

    @Override
    public void onAnimStart() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimCancel() {
        // TODO Auto-generated method stub

    }

}
