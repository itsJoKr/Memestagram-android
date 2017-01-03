package dev.jokr.memestagram.ui.memes;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.models.Meme;

/**
 * Created by jokr on 27.12.16..
 */

public class MemesAdapter extends RecyclerView.Adapter<MemesAdapter.MemeHolder>{

    private final StorageReference memesRef;
    private Context ctx;
    private LayoutInflater inflater;
    private List<Meme> memes;

    public MemesAdapter(Context ctx, StorageReference memesRef) {
        this.ctx = ctx;
        this.inflater = ((Activity)ctx).getLayoutInflater();
        this.memesRef = memesRef;
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
        loadImage(meme.$key, holder.imgMeme);
    }

    @Override
    public int getItemCount() {
        return memes == null ? 0 : memes.size();
    }

    public void setMemes(List<Meme> memes) {
        this.memes = memes;
        notifyDataSetChanged();
    }

    private void loadImage(String key, final ImageView imageView) {
        StorageReference imageRef = memesRef.child(key);
        try {
            final File localFile = File.createTempFile(key, "jpg");
            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Picasso.with(ctx).load(localFile).into(imageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("USER", "image Failure", exception.fillInStackTrace());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class MemeHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_meme_title)
        TextView memeTitle;
        @BindView(R.id.img_meme)
        ImageView imgMeme;

        public MemeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
