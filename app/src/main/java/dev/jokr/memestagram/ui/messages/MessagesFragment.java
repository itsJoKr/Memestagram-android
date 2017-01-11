package dev.jokr.memestagram.ui.messages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.misc.LoggedUserManager;
import dev.jokr.memestagram.models.Conversation;
import dev.jokr.memestagram.models.Message;

/**
 * Created by jokr on 11.01.17..
 */

public class MessagesFragment extends Fragment implements ChildEventListener {

    @BindView(R.id.list_messages)
    RecyclerView messages;
    private MessagesAdapter adapter;
    private DatabaseReference msgRef;

    public static MessagesFragment newInstance(Conversation conversation) {
        Bundle args = new Bundle();
        args.putSerializable("convo", conversation);
        MessagesFragment fragment = new MessagesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messages, container, false);
        ButterKnife.bind(this, v);

        Conversation c = (Conversation) getArguments().getSerializable("convo");
        messages.setLayoutManager(new LinearLayoutManager(getContext()));

        LoggedUserManager.getInstance().getLoggedUser(user -> {
            adapter = new MessagesAdapter(getContext(), user.getKey());
            messages.setAdapter(adapter);
            msgRef = FirebaseDatabase.getInstance().getReference("convos/" + c.convoKey);
            msgRef.addChildEventListener(this);
        });

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        msgRef.removeEventListener(this);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Message m = dataSnapshot.getValue(Message.class);
        adapter.addMessage(m);
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
