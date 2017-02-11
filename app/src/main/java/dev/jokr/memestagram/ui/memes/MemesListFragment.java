package dev.jokr.memestagram.ui.memes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.events.ShowCreateNewMemeEvent;
import dev.jokr.memestagram.misc.LoggedUserManager;
import dev.jokr.memestagram.models.Meme;

/**
 * Created by jokr on 27.12.16..
 */

public class MemesListFragment extends Fragment implements ChildEventListener {

    @BindView(R.id.list_memes)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private List<Meme> memes;
    private MemesAdapter adapter;
    private List<String> likes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memes_list, container, false);
        ButterKnife.bind(this, v);

        memes = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        StorageReference sRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://memestagram-1767b.appspot.com/memes");
        adapter = new MemesAdapter(getActivity(), sRef);
        recyclerView.setAdapter(adapter);

        DatabaseReference memesRef = FirebaseDatabase.getInstance().getReference("memes");

        LoggedUserManager.getInstance().getLoggedUser(user -> {
            if (user.likes != null) this.likes = new ArrayList<>(user.likes.values());
            memesRef.addChildEventListener(this);
        });



        fab.setOnClickListener(view -> EventBus.getDefault().post(new ShowCreateNewMemeEvent()));

        return v;
    }

    private boolean isElementOf(String el, List<String> list) {
        if (list==null) return false;

        for (int i=0; i<list.size(); i++) {
            if (el.equals(list.get(i)))
                return true;
        }
        return false;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d("USER", "onChildAdded:" + dataSnapshot.getKey());

        Meme meme = dataSnapshot.getValue(Meme.class);
        meme.$key = dataSnapshot.getKey();

        meme.liked = isElementOf(meme.$key, likes);

        memes.add(meme);
        adapter.setMemes(memes);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        // TODO: 11.02.17. meme like is not updated to other screens
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
