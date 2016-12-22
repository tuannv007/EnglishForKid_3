package ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.framgia.englishforkid_3.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import data.model.Songs;

/**
 * Created by tuanbg on 12/21/2016.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> {
    private List<Songs> mListSongs = new ArrayList<>();
    private Context mContext;

    public SongsAdapter(Context context, List<Songs> listSongs) {
        mListSongs = listSongs;
        mContext = context;
    }
    @Override
    public SongsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_songs, parent, false);
        return new SongsViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(SongsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mListSongs != null ? mListSongs.size() : 0;
    }

    public class SongsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_item_songs)
        ImageView mImage;
        @BindView(R.id.text_item_title)
        TextView mTitle;

        public SongsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        private void bind(int position) {
            Songs songs = mListSongs.get(position);
            mTitle.setText(songs.getTitle() != null ? songs.getTitle() : "");
            Glide.with(mContext)
                    .load(songs.getLinkImage())
                    .into(mImage);
        }
    }
}
