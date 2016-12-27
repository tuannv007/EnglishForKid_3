package ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.framgia.englishforkid_3.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import data.model.DataModel;

/**
 * Created by tuanbg on 12/21/2016.
 * <></>
 */
public class DataModelAdapter extends RecyclerView.Adapter<DataModelAdapter.DataModelHolder> {
    private final String TAG = getClass().getSimpleName();
    private List<DataModel> mListDataModel;
    private List<DataModel> mListDataSearch = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mTypeWatch;
    private OnItemClick mOnItemClick;

    public DataModelAdapter(Context context, List<DataModel> dataModels, int type) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mTypeWatch = type;
        mListDataModel = dataModels;
        mListDataSearch.addAll(dataModels);
    }

    public void setOnClickItem(OnItemClick onItemClick) {
        mOnItemClick = onItemClick;
    }

    @Override
    public DataModelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DataModelHolder(mLayoutInflater.inflate(R.layout.item_songs, parent, false));
    }

    @Override
    public void onBindViewHolder(DataModelHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mListDataModel != null ? mListDataModel.size() : 0;
    }

    public void filter(String keySearch) {
        if (keySearch.trim().isEmpty()) {
            mListDataModel.clear();
            mListDataModel.addAll(mListDataSearch);
            notifyDataSetChanged();
            return;
        }
        List<DataModel> dataModels = new ArrayList<>();
        for (DataModel item : mListDataSearch) {
            if (item.getName().toLowerCase().contains(keySearch.toLowerCase())) dataModels.add(item);
        }
        mListDataModel.clear();
        mListDataModel.addAll(dataModels);
        notifyDataSetChanged();
    }

    public class DataModelHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_item_songs)
        ImageView mImage;
        @BindView(R.id.text_item_title)
        TextView mTitle;

        public DataModelHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(int position) {
            DataModel songs = mListDataModel.get(position);
            mTitle.setText(songs.getName() != null ? songs.getName() : "");
            Glide.with(mContext)
                .load(songs.getImageUrl())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mImage);
        }

        @OnClick(R.id.card_view)
        void onClickItem() {
            DataModel dataModel = mListDataModel.get(getAdapterPosition());
            if (mOnItemClick != null) mOnItemClick.onClickItem(dataModel, mTypeWatch);
        }
    }

    public interface OnItemClick {
        void onClickItem(DataModel dataModel, int type);
    }
}
