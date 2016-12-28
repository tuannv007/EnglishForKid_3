package ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.framgia.englishforkid_3.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import data.local.SQLiteCommon;
import data.model.DataModel;
import ui.activity.DisplayVideoActivity;
import ui.adapter.DataModelAdapter;
import util.Constant;
import util.JsoupParserHtml;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataModelFragment extends Fragment implements DataModelAdapter.OnItemClick,
    SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = getClass().getSimpleName();
    private final int SPAN_COUNT_LINEAR = 1;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsGridLayout;
    private int SPAN_COUNT_GRID;
    private GridLayoutManager mLayoutManager;
    private SQLiteCommon mSqLiteCommon;
    private DataModelAdapter mAdapter;
    private List<DataModel> mListDataModel = new ArrayList<>();
    private int mTypeWatch;
    private int mSpanCount;

    public static DataModelFragment newInstance(int typeWatch) {
        DataModelFragment dataModelFragment = new DataModelFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.BUNDLE_TYPE_WATCH, typeWatch);
        dataModelFragment.setArguments(bundle);
        return dataModelFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSqLiteCommon = new SQLiteCommon(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null) mTypeWatch = bundle.getInt(Constant.BUNDLE_TYPE_WATCH);
        SPAN_COUNT_GRID = getResources().getInteger(R.integer.span_count);
        mSpanCount = SPAN_COUNT_GRID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_model, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    public void filter(String keySearch) {
        mAdapter.filter(keySearch);
    }

    private void initViews() {
        mLayoutManager = new GridLayoutManager(getActivity(), mSpanCount);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.invalidate();
        mListDataModel.addAll(mSqLiteCommon.getListDataModel(mTypeWatch));
        swapGridLayout();
        mAdapter.setOnClickItem(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.color_grey_600);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClickItem(DataModel dataModel, int type) {
        startActivity(DisplayVideoActivity.getProfileIntent(getActivity(), dataModel, type));
    }

    public void swapUI() {
        if (mIsGridLayout) swapLinearLayout();
        else swapGridLayout();
    }

    private void swapGridLayout() {
        mSpanCount = SPAN_COUNT_GRID;
        mLayoutManager.setSpanCount(mSpanCount);
        mAdapter = new DataModelAdapter(getActivity(), mListDataModel, mTypeWatch,
            R.layout.item_data_model_grid);
        mRecyclerView.setAdapter(mAdapter);
        mIsGridLayout = true;
    }

    private void swapLinearLayout() {
        mSpanCount = SPAN_COUNT_LINEAR;
        mLayoutManager.setSpanCount(mSpanCount);
        mAdapter = new DataModelAdapter(getActivity(), mListDataModel, mTypeWatch,
            R.layout.item_data_model_linear);
        mRecyclerView.setAdapter(mAdapter);
        mIsGridLayout = false;
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        new JsoupAsyncTask().execute();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, List<DataModel>> {
        @Override
        protected List<DataModel> doInBackground(Void... voids) {
            try {
                List<DataModel> listDataModel = new ArrayList<>();
                listDataModel.addAll(mTypeWatch == SQLiteCommon.TYPE_TABLE_SONGS ?
                    JsoupParserHtml.parseSongs() : JsoupParserHtml.parseShortStories());
                mSqLiteCommon.saveListDataModel(listDataModel, mTypeWatch);
                return listDataModel;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<DataModel> dataModels) {
            super.onPostExecute(dataModels);
            if (dataModels != null) {
                mListDataModel.clear();
                mListDataModel.addAll(dataModels);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}
