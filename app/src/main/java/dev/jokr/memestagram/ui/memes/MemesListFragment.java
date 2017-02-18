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
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.events.ShowCreateNewMemeEvent;
import dev.jokr.memestagram.firebase.SingleValueListener;
import dev.jokr.memestagram.misc.LoggedUserManager;
import dev.jokr.memestagram.models.Meme;

/**
 * Created by jokr on 27.12.16..
 */

public class MemesListFragment extends Fragment implements ChildEventListener {

    public static final int DANK = 1;
    public static final int FRESH = 2;
    public static final int SUBBED = 3;
    public static final int RANDOM = 4;
    private static final String TYPE = "type";

    @BindView(R.id.list_memes)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private int type;
    private MemesAdapter adapter;
    private List<String> likes;
    private Query query;

    public static MemesListFragment newInstance(int listType) {
        Bundle args = new Bundle();
        args.putInt(TYPE, listType);
        MemesListFragment fragment = new MemesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memes_list, container, false);
        ButterKnife.bind(this, v);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setStackFromEnd(true);
        llm.setReverseLayout(true);
        recyclerView.setLayoutManager(llm);
        StorageReference sRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://memestagram-1767b.appspot.com/memes");
        adapter = new MemesAdapter(getActivity(), sRef);
        recyclerView.setAdapter(adapter);

        type = getArguments().getInt(TYPE);
        FirebaseDatabase db = FirebaseDatabase.getInstance();


        LoggedUserManager.getInstance().getLoggedUser(user -> {
            if (user.likes != null) this.likes = new ArrayList<>(user.likes.values());

            if (type == DANK || type == FRESH) {
                query = getQueryForType(db, type);
                query.addChildEventListener(this);
            } else if (type == RANDOM)
                getRandomMemes(db);
            else if (type == SUBBED)
                getSubbedMemes(db);
        });

        fab.setOnClickListener(view -> EventBus.getDefault().post(new ShowCreateNewMemeEvent()));

        return v;
    }

    private static Query getQueryForType(FirebaseDatabase db, int type) {
        if (type == DANK)
            return db.getReference("memes").orderByChild("likes");
        else if (type == FRESH)
            return db.getReference("memes").orderByChild("timestamp");
        else
            return db.getReference("memes");
    }

    private void getSubbedMemes(FirebaseDatabase db) {
        LoggedUserManager.getInstance().getLoggedUser(user -> {
            if (user.followed == null) return;

            List<String> memeKeys = new ArrayList<>(user.followed.keySet());
            for (int i=0; i<memeKeys.size(); i++) {
                query = db.getReference("memes")
                        .orderByChild("user/key")
                        .equalTo(memeKeys.get(i));

                query.addChildEventListener(this);
            }
        });
    }

    private void getRandomMemes(FirebaseDatabase db) {
        SingleValueListener.make(db.getReference("misc/count"), countSnap -> {
            long count = (long) countSnap.getValue();


            int numberOfMemes = count > 10 ? 10 : (int) (count - 2);
            int[] randArray = new int[numberOfMemes];
            fillWithRandoms(randArray, (int) count);

            for (int i = 0; i < randArray.length; i++) {
                int finalI = i;
                SingleValueListener.make(db.getReference("memekeys"), snap -> {
                    List<String> memesKeys = new ArrayList<String>(((HashMap) snap.getValue()).keySet());
                    String key = memesKeys.get(randArray[finalI]);
                    SingleValueListener.make(db.getReference("memes/" + key), memeSnap -> {
                        Meme meme = memeSnap.getValue(Meme.class);
                        meme.$key = memeSnap.getKey();

                        meme.liked = isElementOf(meme.$key, likes);
                        adapter.addMeme(meme);
                    });
                });
            }
        });
    }

    private static void fillWithRandoms(int[] randArray, int maxVal) {
        Random rand = new Random();
        Arrays.fill(randArray, -1);

        for (int i = 0; i < randArray.length; i++) {
            boolean notUnique;
            do {
                int randNum = rand.nextInt((int) maxVal);
                notUnique = false;

                for (int j = 0; j < randArray.length; j++) {
                    if (randArray[j] == randNum) {
                        notUnique = true;
                    }
                }

                if (!notUnique) randArray[i] = randNum;
            } while (notUnique);
        }
    }

    private boolean isElementOf(String el, List<String> list) {
        if (list == null) return false;

        for (int i = 0; i < list.size(); i++) {
            if (el.equals(list.get(i)))
                return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (query != null)
            query.removeEventListener(this);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d("USER", "onChildAdded:" + dataSnapshot.getKey());

        Meme meme = dataSnapshot.getValue(Meme.class);
        meme.$key = dataSnapshot.getKey();

        meme.liked = isElementOf(meme.$key, likes);

        adapter.addMeme(meme);
//        adapter.setMemes(memes);
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
