package dev.jokr.memestagram.ui.meme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.models.Meme;

/**
 * Created by jokr on 03.01.17..
 */

public class MemeFragment extends Fragment {

    @BindView(R.id.img_meme)
    ImageView imgMeme;
    @BindView(R.id.txt_by_username)
    TextView txtUsername;
    @BindView(R.id.txt_meme_title)
    TextView txtMemeTitle;
    private Meme meme;

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


        return v;
    }
}
