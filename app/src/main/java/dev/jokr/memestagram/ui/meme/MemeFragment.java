package dev.jokr.memestagram.ui.meme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.events.ShowProfileEvent;
import dev.jokr.memestagram.events.ShowUserEvent;
import dev.jokr.memestagram.misc.FragmentCreatedListener;
import dev.jokr.memestagram.misc.LoggedUserManager;
import dev.jokr.memestagram.models.Comment;
import dev.jokr.memestagram.models.Meme;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by jokr on 03.01.17..
 */

public class MemeFragment extends Fragment implements ChildEventListener {

    public static final int RC_STORAGE = 33;

    @BindView(R.id.img_meme)
    ImageView imgMeme;
    @BindView(R.id.txt_by_username)
    TextView txtUsername;
    @BindView(R.id.txt_meme_title)
    TextView txtMemeTitle;
    @BindView(R.id.icon_send_it)
    ImageView btnSend;
    @BindView(R.id.etxt_comment)
    EditText txtComment;
    @BindView(R.id.list_comments)
    RecyclerView listComments;
    @BindView(R.id.txt_nof_likes)
    TextView txtNofLikes;

    private Meme meme;
    private FragmentCreatedListener fragmentCreatedListener;
    private DatabaseReference commentsRef;
    private CommentsAdapter adapter;
    private List<Comment> comments;

    public static MemeFragment newInstance(Meme m) {
        Bundle args = new Bundle();
        args.putSerializable("meme", m);
        MemeFragment fragment = new MemeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_meme, container, false);
        ButterKnife.bind(this, v);
        this.meme = (Meme) getArguments().getSerializable("meme");
        commentsRef = FirebaseDatabase.getInstance().getReference("comments/" + meme.$key);
        commentsRef.addChildEventListener(this);
        comments = new ArrayList<>();

        txtUsername.setText(meme.user.username);
        txtMemeTitle.setText(meme.title);
        txtNofLikes.setText("" + meme.likes);

        btnSend.setOnClickListener(v1 -> sendComment());

        String perms = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (!EasyPermissions.hasPermissions(getContext(), perms)){
            EasyPermissions.requestPermissions(this, "Rationale??", RC_STORAGE, perms);
        }

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        listComments.setLayoutManager(lm);
        adapter = new CommentsAdapter(getContext());
        adapter.setComments(comments);
        listComments.setAdapter(adapter);
        listComments.setNestedScrollingEnabled(false);

        fragmentCreatedListener.onFragmentCreated();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentCreatedListener.onFragmentCreated(); // trigger drawable send
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_STORAGE)
    private void checkStoragePermission() {
        String perms = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
        } else {
            Toast.makeText(getContext(), "Need storage permission!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentCreatedListener = (FragmentCreatedListener) context;
    }

    public void setMemeImage(Drawable d) {
        imgMeme.setImageDrawable(d);
    }

    private void sendComment() {
        String comment = txtComment.getText().toString();
        txtComment.setText("");
        LoggedUserManager.getInstance().getLoggedUser(user -> {
            Comment c = new Comment(comment, user.username, user.getKey());
            String key = commentsRef.push().getKey();
            Map<String, Object> update = new HashMap<>();
            update.put("/" + key, c.toMap());
            commentsRef.updateChildren(update, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    Log.e("USER", "Publish meme: " + databaseError.getMessage());
                }
            });
        });

    }

    @OnClick(R.id.img_share)
    public void shareMeme() {
        Drawable mDrawable = imgMeme.getDrawable();
        Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();

        String path = MediaStore.Images.Media
                .insertImage(getContext().getContentResolver(), mBitmap, "Image Description", null);
        Uri uri = Uri.parse(path);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Share Image"));
    }

    @OnClick(R.id.btn_to_profile)
    public void onUserClicked() {
        LoggedUserManager.getInstance().getLoggedUser(user -> {
            if (meme.user.getKey().equals(user.getKey()))
                Toast.makeText(getContext(), "That's you. Nice meme.", Toast.LENGTH_SHORT).show();
            else
                EventBus.getDefault().post(new ShowUserEvent(meme.user));
        });
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Comment c = dataSnapshot.getValue(Comment.class);
        comments.add(c);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
