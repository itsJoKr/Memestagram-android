package dev.jokr.memestagram.ui.main;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.models.Meme;

/**
 * Created by jokr on 27.12.16..
 */

public class MemesListFragment extends Fragment implements ChildEventListener {

    @Bind(R.id.memes_recycler)
    RecyclerView recyclerView;

    private List<Meme> memes;
    private MemesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memes_list, container, false);
        ButterKnife.bind(this, v);

        memes = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MemesAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        DatabaseReference memesRef = FirebaseDatabase.getInstance().getReference("memes");
        memesRef.addChildEventListener(this);

        return v;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d("USER", "onChildAdded:" + dataSnapshot.getKey());

        Meme meme = dataSnapshot.getValue(Meme.class);
        meme.$key = dataSnapshot.getKey();
        memes.add(meme);
        adapter.setMemes(memes);
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
