package dev.jokr.memestagram.ui.newmeme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.misc.FragmentCreatedListener;
import dev.jokr.memestagram.ui.main.MainActivity;
import dev.jokr.memestagram.views.MemeDrawable;
import dev.jokr.memestagram.views.MemeView;

public class MemeEditorActivity extends AppCompatActivity implements MemeEditorFragment.FinishedEditListener, FragmentCreatedListener {

    private static final int PICK_TEMPLATE = 0;
    private static final int TOP_CAPTION = 1;
    private static final int BOTTOM_CAPTION = 2;
    private static final int PUBLISH = 3;

    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;

    private MemeEditorFragment memeCaptionFragment;
    private PublishMemeFragment publishMemeFragment;
    private int step = PICK_TEMPLATE;
    private float initialScaleForSecondFragment = 1.0f;
    private Bitmap intermediateBitmap;
    private MemeView.Metadata topCaptionMeta;
    private MemeView.Metadata botCaptionMeta;
    private MemeDrawable memeDrawable;
    private int drawableId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_editor);
        ButterKnife.bind(this);
        this.drawableId = getIntent().getIntExtra("drawable_id", 0);

        step = TOP_CAPTION;
        memeCaptionFragment = new MemeEditorFragment();
        memeCaptionFragment.setFinishedEditListener(MemeEditorActivity.this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, memeCaptionFragment)
                .commit();

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, PickerActivity.class);
        startActivity(i);
    }

    @Override
    public void onFinishedEdit(Bitmap bitmap, MemeView.Metadata meta, float scale) {
        if (step == TOP_CAPTION) {
            this.topCaptionMeta = meta;
            this.intermediateBitmap = bitmap;
            step = BOTTOM_CAPTION;
            memeCaptionFragment = new MemeEditorFragment();
            memeCaptionFragment.setFinishedEditListener(this);
            initialScaleForSecondFragment = scale;

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, memeCaptionFragment)
                    .commit();
        } else {
            this.botCaptionMeta = meta;
            step = PUBLISH;

            // create drawable
            this.intermediateBitmap = null;
            memeDrawable = new MemeDrawable(this, getBaseBitmap(), topCaptionMeta, botCaptionMeta);
//            this.intermediateBitmap = drawableToBitmap(d);

            // create publish fragment
            this.memeCaptionFragment = null; // gtfo
            this.publishMemeFragment = new PublishMemeFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, this.publishMemeFragment)
                    .commit();
        }
    }

    private Bitmap getBaseBitmap() {
        Log.d("USER", "getBitmap id: " + this.drawableId);
        Drawable d = ContextCompat.getDrawable(this, this.drawableId);
//        Drawable d = ContextCompat.getDrawable(this, R.drawable.temp_brace_yourselves);
        return ((BitmapDrawable) d).getBitmap();
    }

    @Override
    public void onFragmentCreated() {
        if (step == TOP_CAPTION) {
            memeCaptionFragment.setBitmapAndInitialize(getBaseBitmap(), true);
        } else if (step == BOTTOM_CAPTION) {
            memeCaptionFragment.setBitmapAndInitialize(intermediateBitmap, false);
            if (initialScaleForSecondFragment != 1.0)
                memeCaptionFragment.setInitialScale(initialScaleForSecondFragment);
        } else if (step == PUBLISH) {
            publishMemeFragment.setMemeDrawable(memeDrawable);
        }
    }
}
