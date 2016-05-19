package com.zhl.cbpullrefreshlistview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.zhl.CBPullRefresh.CBPullRefreshListView;
import com.zhl.CBPullRefresh.SwipeMenu.SwipeMenu;
import com.zhl.CBPullRefresh.SwipeMenu.SwipeMenuCreator;
import com.zhl.CBPullRefresh.SwipeMenu.SwipeMenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TestAdapter mAdatper;
    private ArrayList<String> DataList = new ArrayList<String>();
    private CBPullRefreshListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initView() {
        mListView = (CBPullRefreshListView) findViewById(R.id.listview);
//        mListView.setRefreshHeader(new CBRefreshHeader(this));
        mListView.setAdapter(mAdatper = new TestAdapter());
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadMoreEnable(true);
//        mListView.setSwipeEnable(false);
        mListView.showTobSearchBar(true);
        mListView.setOnSearchBarClickListener(new CBPullRefreshListView.OnSearchClickListener() {
            @Override
            public void onSearchBarClick() {
                Toast.makeText(MainActivity.this, "点击了搜索栏", Toast.LENGTH_SHORT).show();
            }
        });
        mListView.setOnPullRefreshListener(new CBPullRefreshListView.OnPullRefreshListener() {
            @Override
            public void onRefresh() {
                mListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.stopRefresh();
                    }
                }, 3000);
            }

            @Override
            public void onLoadMore() {
                mListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.stopLoadMore();
                    }
                }, 3000);
            }

            @Override
            public void onUpdateRefreshTime(long time) {

            }

        });
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem collectionItem = new SwipeMenuItem(getApplicationContext());
                collectionItem.setBackground(R.color.green);
                collectionItem.setWidth(dp2px(MainActivity.this, 90));
                collectionItem.setTitle("收藏");
                collectionItem.setTitleSize(18);
                collectionItem.setTitleColor(Color.WHITE);
                collectionItem.setIcon(R.drawable.icon_collection);
                menu.addMenuItem(collectionItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(R.color.red);
                deleteItem.setWidth(dp2px(MainActivity.this, 90));
                deleteItem.setTitle("删除");
                deleteItem.setIcon(R.drawable.icon_delete);
                deleteItem.setTitleSize(18);
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(new CBPullRefreshListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                Toast.makeText(MainActivity.this, "点击了item swipe 菜单的第" + index, Toast.LENGTH_SHORT).show();
            }
        });
        mListView.setOnItemClickListener(new CBPullRefreshListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,"点击了"+position,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        for (int i = 0; i < 30; i++) {
            DataList.add(new String("this is a cbpullrefreshlistview test" + i));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(this,DragbackActivity.class);
                startActivity(intent);
                break;
            case R.id.action_custom_header:
                Intent intent2 = new Intent(this,CustomRefreshHeaderActivity.class);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class TestAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return DataList == null ? 0 : DataList.size();
        }

        @Override
        public Object getItem(int position) {
            return DataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_listview, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(R.id.item_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.title.setText(DataList.get(position));
            return convertView;
        }
    }

    class ViewHolder {
        TextView title;
    }

    public int dp2px(Context context,int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
