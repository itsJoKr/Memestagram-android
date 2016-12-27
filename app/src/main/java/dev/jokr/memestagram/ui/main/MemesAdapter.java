package dev.jokr.memestagram.ui.main;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.models.Meme;

/**
 * Created by jokr on 27.12.16..
 */

public class MemesAdapter extends RecyclerView.Adapter<MemesAdapter.MemeHolder>{

    private Context ctx;
    private LayoutInflater inflater;
    private List<Meme> memes;

    public MemesAdapter(Context ctx) {
        this.ctx = ctx;
        this.inflater = ((Activity)ctx).getLayoutInflater();
    }

    @Override
    public MemeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_meme, parent, false);
        MemeHolder holder = new MemeHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(MemeHolder holder, int position) {
        Meme meme = memes.get(position);
        holder.memeTitle.setText(meme.title + meme.$key);
    }

    @Override
    public int getItemCount() {
        return memes == null ? 0 : memes.size();
    }

    public void setMemes(List<Meme> memes) {
        this.memes = memes;
        notifyDataSetChanged();
    }

    class MemeHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.meme_title)
        TextView memeTitle;

        public MemeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
