package dev.jokr.memestagram.ui.editor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import dev.jokr.memestagram.R;
import dev.jokr.memestagram.ui.main.MainActivity;

public class PickerActivity extends AppCompatActivity implements TemplatePickFragment.OnPickedTemplateListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);

        TemplatePickFragment fragment = new TemplatePickFragment();
        fragment.setPickedTemplateListener(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


    @Override
    public void onPickedTemplate(int drawableId) {
        Intent i = new Intent(this, MemeEditorActivity.class);
        i.putExtra("drawable_id", drawableId);
        startActivity(i);
    }

}
