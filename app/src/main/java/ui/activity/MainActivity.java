package ui.activity;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.framgia.englishforkid_3.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import data.local.SQLiteCommon;
import ui.fragment.DataModelFragment;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private final String TAG = getClass().getSimpleName();
    private List<DataModelFragment> mListFragment = new ArrayList<>();
    private ViewPagerAdapter mViewPagerAdapter;
    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        SearchView mSearchView =
            (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_swap_ui) swapUI();
        return super.onOptionsItemSelected(item);
    }

    private void swapUI() {
        for (DataModelFragment fragment : mListFragment) {
            fragment.swapUI();
        }
    }

    private void initData() {
        setSupportActionBar(mToolbar);
        setTitle(R.string.title_main_activity);
        String[] titles = getResources().getStringArray(R.array.array_main_activity);
        mListFragment.add(DataModelFragment.newInstance(SQLiteCommon.TYPE_TABLE_SONGS));
        mListFragment.add(DataModelFragment.newInstance(SQLiteCommon.TYPE_TABLE_SHORT_STORIES));
        mViewPagerAdapter =
            new ViewPagerAdapter(getSupportFragmentManager(), titles, mListFragment);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        for (DataModelFragment fragment : mListFragment) {
            fragment.filter(newText);
        }
        return true;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private String[] mTitles;
        private List<DataModelFragment> mListFragment;

        public ViewPagerAdapter(FragmentManager fm, String[] titles,
                                List<DataModelFragment> fragments) {
            super(fm);
            mTitles = titles;
            mListFragment = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mListFragment.get(position);
        }

        @Override
        public int getCount() {
            return mListFragment != null ? mListFragment.size() : 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }
}
