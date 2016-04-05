package com.zhl.cbpullrefreshlistview;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;

public class BaseActivity extends FragmentActivity{
	protected DragBackLayout dragBacklayout;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dragBacklayout = (DragBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		dragBacklayout.attachToActivity(this);
	}

	
}
