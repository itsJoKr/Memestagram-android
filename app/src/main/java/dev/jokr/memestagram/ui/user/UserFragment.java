package dev.jokr.memestagram.ui.user;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.events.ShowConversationEvent;
import dev.jokr.memestagram.firebase.SingleValueListener;
import dev.jokr.memestagram.misc.LoggedUserManager;
import dev.jokr.memestagram.models.Conversation;
import dev.jokr.memestagram.models.Meme;
import dev.jokr.memestagram.models.User;
import dev.jokr.memestagram.ui.memes.MemesAdapter;
import dev.jokr.memestagram.util.ProjectConst;

/**
 * Created by jokr on 12.01.17..
 */

public class UserFragment extends Fragment implements ChildEventListener, ValueEventListener {

    @BindView(R.id.txt_username)
    TextView txtUsername;
    @BindView(R.id.list_memes)
    RecyclerView memes;
    @BindView(R.id.btn_follow)
    ImageView btnFollow;

    private MemesAdapter adapter;
    private User user;

    public static UserFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, v);

        user = (User) getArguments().getSerializable("user");
        if (user.type == ProjectConst.ANON)
            txtUsername.setText(user.username);
        else
            txtUsername.setText(user.username);

        StorageReference sRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://memestagram-1767b.appspot.com/memes");
        adapter = new MemesAdapter(getContext(), sRef);
        memes.setLayoutManager(new LinearLayoutManager(getContext()));
        memes.setNestedScrollingEnabled(false);
        memes.setAdapter(adapter);

        Query hisMemes = FirebaseDatabase.getInstance().getReference("memes")
                .orderByChild("user/key")
                .equalTo(user.getKey());

        hisMemes.addChildEventListener(this);
        setIsFollowingUser();
        return v;
    }

    @OnClick(R.id.btn_convo)
    public void openConvo() {
        LoggedUserManager.getInstance().getLoggedUser(u -> {
            Query convoQuery =
                    FirebaseDatabase.getInstance()
                            .getReference("users/" + u.getKey() + "/convos")
                    .orderByKey()
                    .equalTo(user.getKey());

            convoQuery.addListenerForSingleValueEvent(this);
        });
    }

    @OnClick(R.id.btn_follow)
    public void followUser() {
        LoggedUserManager.getInstance().getLoggedUser(u -> {
            DatabaseReference itRef = FirebaseDatabase.getInstance()
                    .getReference("users/" + u.getKey() + "/followed/" + user.key);
            DatabaseReference followRef = FirebaseDatabase.getInstance()
                    .getReference("users/" + u.getKey() + "/followed");

            SingleValueListener.make(itRef, followingSnap -> {
                HashMap<String, Object> update = new HashMap<>();
                if (followingSnap.getValue() == null) {
                    // follow user
                    update.put("/" + user.key, user.key);
                    btnFollow.setColorFilter(getContext().getResources().getColor(R.color.darkOne),
                            PorterDuff.Mode.SRC_ATOP);
                } else {
                    // unfollow user
                    update.put("/" + user.key, null);
                    btnFollow.setColorFilter(getContext().getResources().getColor(android.R.color.darker_gray),
                            PorterDuff.Mode.SRC_ATOP);
                }
                followRef.updateChildren(update);
            });
        });
    }

    private void setIsFollowingUser() {
        LoggedUserManager.getInstance().getLoggedUser(u -> {
            DatabaseReference itRef = FirebaseDatabase.getInstance()
                    .getReference("users/" + u.getKey() + "/followed/" + user.key);
            SingleValueListener.make(itRef, followingSnap -> {
                if (followingSnap.getValue() == null) {
                    btnFollow.setColorFilter(getContext().getResources().getColor(android.R.color.darker_gray),
                            PorterDuff.Mode.SRC_ATOP);
                } else {
                    btnFollow.setColorFilter(getContext().getResources().getColor(R.color.darkOne),
                            PorterDuff.Mode.SRC_ATOP);
                }
            });
        });
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Meme m = dataSnapshot.getValue(Meme.class);
        m.$key = dataSnapshot.getKey();
        adapter.addMeme(m);
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
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            openConversation();
        } else {
            HashMap<String, String> m = (HashMap<String, String>)
                    ((HashMap) (dataSnapshot.getValue())).get(user.getKey());
            Conversation c = new Conversation(m.get("otherGuy"), m.get("convoKey"));
            EventBus.getDefault().post(new ShowConversationEvent(c));
        }
    }

    public void openConversation() {
        LoggedUserManager.getInstance().getLoggedUser(u -> {
            String path = "users/" + u.getKey() + "/convos/";
            DatabaseReference userConvosRef = FirebaseDatabase.getInstance().getReference(path);
            String key = u.getKey() + user.getKey();
            Conversation c = new Conversation(user.username, key);

            Map<String, Object> update = new HashMap<>();
            update.put("/" + key, c.toMap());
            userConvosRef.updateChildren(update, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    Log.e("USER", "Publish meme: " + databaseError.getMessage());
                } else {
                    EventBus.getDefault().post(new ShowConversationEvent(c));
                }
            });
        });
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
