package dev.jokr.memestagram.ui.newmeme;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.misc.FragmentCreatedListener;
import dev.jokr.memestagram.views.MemeDrawable;

/**
 * Created by jokr on 31.12.16..
 */

public class PublishMemeFragment extends Fragment {

    @Bind(R.id.img_meme)
    ImageView imgMeme;

    private FragmentCreatedListener fragmentCreatedListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_publish_meme, container, false);
        ButterKnife.bind(this, v);

        fragmentCreatedListener.onFragmentCreated();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.fragmentCreatedListener = (FragmentCreatedListener) context;
    }

    public void setMemeDrawable(MemeDrawable drawable) {
        imgMeme.setImageDrawable(drawable);
    }
}
