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

import butterknife.Bind;
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

    @Bind(R.id.fragment_container)
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

        final TemplatePickFragment templatePickFragment = new TemplatePickFragment();
        templatePickFragment.setPickedTemplateListener(new TemplatePickFragment.OnPickedTemplateListener() {
            @Override
            public void onPickedTemplate(int drawableId) {
                MemeEditorActivity.this.drawableId = drawableId;

                step = TOP_CAPTION;
                memeCaptionFragment = new MemeEditorFragment();
                memeCaptionFragment.setFinishedEditListener(MemeEditorActivity.this);
                getSupportFragmentManager().beginTransaction()
                        .remove(templatePickFragment).commitNow();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, memeCaptionFragment)
                        .commit();
            }
        });

        step = TOP_CAPTION;
        memeCaptionFragment = new MemeEditorFragment();
        memeCaptionFragment.setFinishedEditListener(MemeEditorActivity.this);
        getSupportFragmentManager().beginTransaction()
                .remove(templatePickFragment).commitNow();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, memeCaptionFragment)
                .commit();

//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_container, templatePickFragment)
//                .commit();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
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
//        Drawable d = ContextCompat.getDrawable(this, this.drawableId);
        Drawable d = ContextCompat.getDrawable(this, R.drawable.temp_brace_yourselves);
        return ((BitmapDrawable) d).getBitmap();
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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
