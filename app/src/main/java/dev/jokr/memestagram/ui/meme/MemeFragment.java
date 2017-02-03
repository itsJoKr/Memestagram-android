package dev.jokr.memestagram.ui.meme;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

/**
 * Created by jokr on 03.01.17..
 */

public class MemeFragment extends Fragment implements ChildEventListener {

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

        btnSend.setOnClickListener(v1 -> sendComment());

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
//        lm.setReverseLayout(true);
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

    @OnClick(R.id.card_user)
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
