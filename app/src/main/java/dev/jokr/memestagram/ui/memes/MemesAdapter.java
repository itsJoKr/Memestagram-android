package dev.jokr.memestagram.ui.memes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.events.ShowMemeEvent;
import dev.jokr.memestagram.misc.LoggedUserManager;
import dev.jokr.memestagram.models.Meme;
import dev.jokr.memestagram.util.ChildAddListenerImpl;
import dev.jokr.memestagram.util.PlebUsernameTag;
import dev.jokr.memestagram.util.ValueListenerImpl;

/**
 * Created by jokr on 27.12.16..
 */

public class MemesAdapter extends RecyclerView.Adapter<MemesAdapter.MemeHolder> {

    private final StorageReference memesRef;
    private Context ctx;
    private LayoutInflater inflater;
    private List<Meme> memes;

    public MemesAdapter(Context ctx, StorageReference memesRef) {
        this.ctx = ctx;
        this.inflater = ((Activity) ctx).getLayoutInflater();
        this.memesRef = memesRef;
    }

    @Override
    public MemeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_meme, parent, false);

        return new MemeHolder(v);
    }

    @Override
    public void onBindViewHolder(MemeHolder holder, int position) {
        Meme meme = memes.get(position);
        holder.memeTitle.setText(meme.title);
        holder.byUser.setText(meme.user.username);
        holder.byUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pleb_tag, 0, 0, 0);
        holder.imgMeme.setOnClickListener(v -> EventBus.getDefault().post(new ShowMemeEvent(meme, holder.imgMeme.getDrawable())));
        holder.containerLikes.setOnClickListener(v -> toggleLike(meme, position));
        holder.nofLikes.setText("" + meme.likes);
        holder.tintLikeImage(meme.liked);

        // TODO: temp load local images
//        holder.imgMeme.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.temp_philosoraptor));
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
        notifyItemInserted(memes.size() - 1);
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

        imageRef.getFile(tempFile).addOnSuccessListener(taskSnapshot -> {
            // Local temp file has been created
            Picasso.with(ctx).load(tempFile).into(imageView);
        }).addOnFailureListener(exception -> Log.e("USER", "image Failure", exception.fillInStackTrace()));
    }

    private void toggleLike(Meme meme, int position) {
        String memeKey = meme.$key;
        final boolean[] updatePhase = {true};

        LoggedUserManager.getInstance().getLoggedUser(user -> {
            DatabaseReference userLikesRef = FirebaseDatabase.getInstance().getReference("users/" + user.getKey() + "/likes");
            DatabaseReference memeRef = FirebaseDatabase.getInstance().getReference("memes/" + memeKey);

            ValueListenerImpl nofLikesListener = new ValueListenerImpl();
            ValueListenerImpl isLikedListener = new ValueListenerImpl();

            isLikedListener.setListener(snap -> {
                userLikesRef.child(memeKey).removeEventListener(isLikedListener);
                if (snap.getValue() == null) {
                    nofLikesListener.setListener(lSnap -> {
                        long likes = (long) lSnap.getValue();

                        if (!updatePhase[0]) {
                            memeRef.child("likes").removeEventListener(nofLikesListener);
                            meme.likes = likes;
                            meme.liked = !meme.liked;
                            notifyItemChanged(position);
                        } else {
                            updatePhase[0] = false;
                            HashMap<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/users/" + user.getKey() + "/likes/" + memeKey, memeKey);
                            childUpdates.put("/memes/" + memeKey + "/likes", likes + 1);
                            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
                        }
                    });
                    memeRef.child("likes").addValueEventListener(nofLikesListener);
                } else {
                    nofLikesListener.setListener(lSnap -> {
                        long likes = (long) lSnap.getValue();

                        if (!updatePhase[0]) {
                            memeRef.child("likes").removeEventListener(nofLikesListener);
                            meme.likes = likes;
                            meme.liked = !meme.liked;
                            notifyItemChanged(position);
                        } else {
                            updatePhase[0] = false;
                            HashMap<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/users/" + user.getKey() + "/likes/" + memeKey, null);
                            childUpdates.put("/memes/" + memeKey + "/likes", likes - 1);
                            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
                        }
                    });
                    memeRef.child("likes").addValueEventListener(nofLikesListener);
                }
            });
            userLikesRef.child(memeKey).addValueEventListener(isLikedListener);
        });
    }

    class MemeHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_meme_title)
        TextView memeTitle;
        @BindView(R.id.img_meme)
        ImageView imgMeme;
        @BindView(R.id.txt_by_username)
        TextView byUser;
        @BindView(R.id.txt_nof_likes)
        TextView nofLikes;
        @BindView(R.id.container_likes)
        LinearLayout containerLikes;
        @BindView(R.id.img_like)
        ImageView imgLike;

        public MemeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void tintLikeImage(boolean liked) {
            if (liked)
                imgLike.setColorFilter(ContextCompat.getColor(ctx,R.color.colorAccent));
            else
                imgLike.setColorFilter(Color.GRAY);
        }

    }
}
