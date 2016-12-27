package ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.framgia.englishforkid_3.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import data.local.SQLiteCommon;
import data.model.DataModel;
import ui.activity.PlayVideoActivity;
import ui.adapter.DataModelAdapter;
import util.Constant;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataModelFragment extends Fragment implements DataModelAdapter.OnItemClick {
    private final String TAG = getClass().getSimpleName();
    private SQLiteCommon mSqLiteCommon;
    private DataModelAdapter mAdapter;
    private List<DataModel> mListDataModel = new ArrayList<>();
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private int mTypeWatch = SQLiteCommon.TYPE_TABLE_SONGS;

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
        if (bundle != null) {
            mTypeWatch = bundle.getInt(Constant.BUNDLE_TYPE_WATCH);
        }
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
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Constant.SPAN_COUNT));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.invalidate();
        mListDataModel.addAll(mSqLiteCommon.getListDataModel(mTypeWatch));
        mAdapter = new DataModelAdapter(getActivity(), mListDataModel, mTypeWatch);
        mAdapter.setOnClickItem(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClickItem(DataModel dataModel, int type) {
        startActivity(PlayVideoActivity.getProfileIntent(getActivity(), dataModel, type));
    }
}
