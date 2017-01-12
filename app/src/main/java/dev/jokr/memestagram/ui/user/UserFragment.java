package dev.jokr.memestagram.ui.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.models.User;
import dev.jokr.memestagram.util.ProjectConst;

/**
 * Created by jokr on 12.01.17..
 */

public class UserFragment extends Fragment {

    @BindView(R.id.txt_username)
    TextView txtUsername;
    @BindView(R.id.list_memes)
    RecyclerView memes;

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

        User u = (User) getArguments().getSerializable("user");
        if (u.type == ProjectConst.ANON)
            txtUsername.setText("[PLEB]" + u.username);
        else
            txtUsername.setText(u.username);

        return v;
    }


}
