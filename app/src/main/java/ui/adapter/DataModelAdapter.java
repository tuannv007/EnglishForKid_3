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
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import data.model.DataModel;

/**
 * Created by tuanbg on 12/21/2016.
 * <></>
 */
public class DataModelAdapter extends RecyclerView.Adapter<DataModelAdapter.DataModelHolder> {
    private List<DataModel> mListDataModel;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public DataModelAdapter(Context context, List<DataModel> listSongs) {
        mListDataModel = listSongs;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
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
                .load(songs.getImage())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mImage);
        }
    }
}
