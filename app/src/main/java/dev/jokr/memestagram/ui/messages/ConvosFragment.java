package dev.jokr.memestagram.ui.messages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

/**
 * Created by jokr on 10.01.17..
 */

public class ConvosFragment extends Fragment implements ChildEventListener {

    @BindView(R.id.list_convos)
    RecyclerView convos;
    private DatabaseReference convosRef;
    private ConvosAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_convos, container, false);
        ButterKnife.bind(this, v);

        adapter = new ConvosAdapter(getContext());
        convos.setLayoutManager(new LinearLayoutManager(getContext()));
        convos.setAdapter(adapter);

        LoggedUserManager.getInstance().getLoggedUser(user -> {
            convosRef = FirebaseDatabase.getInstance().getReference("users/" + user.getKey() + "/convos");
            convosRef.addChildEventListener(this);
        });

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        convosRef.removeEventListener(this);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Conversation c = dataSnapshot.getValue(Conversation.class);
        adapter.addConvo(c);
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
