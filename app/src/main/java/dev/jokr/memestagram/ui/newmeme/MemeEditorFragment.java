package dev.jokr.memestagram.ui.newmeme;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.misc.FragmentCreatedListener;
import dev.jokr.memestagram.views.MemeScrollView;
import dev.jokr.memestagram.views.MemeView;

/**
 * Created by jokr on 28.12.16..
 */

public class MemeEditorFragment extends Fragment{

    @Bind(R.id.meme_view)
    MemeView memeView;
    @Bind(R.id.scroll_view_editor)
    MemeScrollView memeScrollView;
    @Bind(R.id.fab)
    FloatingActionButton btnNext;

    private FinishedEditListener finishedEditListener;
    private FragmentCreatedListener fragmentCreatedListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_meme_editor, container, false);
        ButterKnife.bind(this, v);
        memeScrollView.setScrollingEnabled(false);

        memeView.setFocusable(true);
        memeView.setFocusableInTouchMode(true);

        fragmentCreatedListener.onFragmentCreated();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.fragmentCreatedListener = (FragmentCreatedListener) context;
    }

    private void scrollToBottom() {
        memeScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                memeScrollView.post(new Runnable() {
                    public void run() {
                        memeScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
    }

    public void setFinishedEditListener(FinishedEditListener finishedEditListener) {
        this.finishedEditListener = finishedEditListener;
    }

    public void setBitmapAndInitialize(Bitmap bmp, boolean upperHalf) {
        memeView.setBitmapAndInitialize(bmp, upperHalf);
        if (!upperHalf) scrollToBottom();
    }

    public void setInitialScale(float scale) {
        memeView.setInitialScale(scale);
    }

    @OnClick(R.id.fab)
    public void finishEdit() {
        memeView.hideSoftKeyboard();
        finishedEditListener.onFinishedEdit(memeView.getFinalBitmap(), memeView.getMetadata(), memeView.getScale());
    }

    public interface FinishedEditListener {
        public void onFinishedEdit(Bitmap bitmap, MemeView.Metadata meta, float scale);
    }

}
