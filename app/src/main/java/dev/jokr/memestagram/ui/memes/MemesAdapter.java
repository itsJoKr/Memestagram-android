package dev.jokr.memestagram.ui.memes;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.events.ShowMemeEvent;
import dev.jokr.memestagram.models.Meme;
import dev.jokr.memestagram.util.PlebUsernameTag;

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
        holder.memeTitle.setText(meme.title);
        holder.byUser.setText(meme.user.username);
        holder.byUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pleb_tag, 0, 0, 0);
        holder.imgMeme.setOnClickListener(v -> EventBus.getDefault().post(new ShowMemeEvent(meme, holder.imgMeme.getDrawable())));


        // TODO: temp load local images
        holder.imgMeme.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.temp_leo_cheers));
//        loadImage(meme.$key, holder.imgMeme);
    }

    @Override
    public int getItemCount() {
        return memes == null ? 0 : memes.size();
    }

    public void setMemes(List<Meme> memes) {
        this.memes = memes;
        notifyDataSetChanged();
    }

    public void addMeme(Meme meme) {
        if (memes == null)
            memes = new ArrayList<>();

        memes.add(meme);
        notifyItemInserted(memes.size()-1);
    }

    private void loadImage(String key, final ImageView imageView) {
        String temporaryDir = System.getProperty("java.io.tmpdir");
        File tempFile = new File(temporaryDir, key + ".jpg");

        Picasso.with(ctx)
                .load(tempFile)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() { }
                    @Override
                    public void onError() {
                        //No disk cache, proceed to firebase
                        MemesAdapter.this.loadFromFirebase(key, imageView);
                    }
                });
    }

    private void loadFromFirebase(String key, final ImageView imageView) {
        StorageReference imageRef = memesRef.child(key);
        String temporaryDir = System.getProperty("java.io.tmpdir");
        File tempFile = new File(temporaryDir, key + ".jpg");

        imageRef.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Picasso.with(ctx).load(tempFile).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("USER", "image Failure", exception.fillInStackTrace());
            }
        });
    }

    class MemeHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_meme_title)
        TextView memeTitle;
        @BindView(R.id.img_meme)
        ImageView imgMeme;
        @BindView(R.id.txt_by_username)
        TextView byUser;

        public MemeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
