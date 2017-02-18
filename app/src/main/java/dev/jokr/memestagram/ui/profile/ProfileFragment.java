package dev.jokr.memestagram.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.misc.LoggedUserManager;
import dev.jokr.memestagram.models.Meme;
import dev.jokr.memestagram.ui.login.LoginActivity;
import dev.jokr.memestagram.ui.memes.MemesAdapter;

/**
 * Created by jokr on 03.01.17..
 */

public class ProfileFragment extends Fragment implements ChildEventListener {

    MemesAdapter adapter;

    @BindView(R.id.txt_username)
    TextView txtUsername;
    @BindView(R.id.list_memes)
    RecyclerView memes;

    private Query hisMemes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);

        LoggedUserManager.getInstance().getLoggedUser(user -> {
            txtUsername.setText(user.username);
            hisMemes = FirebaseDatabase.getInstance().getReference("memes")
                    .orderByChild("user/key")
                    .equalTo(user.getKey());

            hisMemes.addChildEventListener(this);
        });

        StorageReference sRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://memestagram-1767b.appspot.com/memes");
        adapter = new MemesAdapter(getContext(), sRef);

        memes.setLayoutManager(new LinearLayoutManager(getContext()));
        memes.setNestedScrollingEnabled(false);
        memes.setAdapter(adapter);
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hisMemes.removeEventListener(this);
    }

    @OnClick(R.id.btn_logout)
    public void logout() {
        LogoutDialogFragment dialog = new LogoutDialogFragment();
        dialog.setListener(() -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(getContext(), LoginActivity.class);
            getContext().startActivity(i);
        });
        dialog.show(getFragmentManager(), "logout_dialog");
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
    public void onCancelled(DatabaseError databaseError) {

    }
}
