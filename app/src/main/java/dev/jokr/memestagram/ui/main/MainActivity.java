package dev.jokr.memestagram.ui.main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.events.ShowCreateNewMeme;
import dev.jokr.memestagram.ui.login.LoginActivity;
import dev.jokr.memestagram.ui.newmeme.MemeEditorActivity;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.content_frame) LinearLayout contentFrame;
    @Bind(R.id.viewpager_main) ViewPager viewPager;
    @Bind(R.id.tab_layout_main) TabLayout tabLayout;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserAuth();

        viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);



    }

    private void checkUserAuth() {
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("USER", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(i);
                    Log.d("USER", "onAuthStateChanged:signed_out");
                }
            }
        });
    }

    public void logoutUser(View view) {
        firebaseAuth.signOut();
    }

    @Subscribe
    public void onMessageEvent(ShowCreateNewMeme event) {
        Log.d("USER", "show create new meme");


        Intent i = new Intent(this, MemeEditorActivity.class);
        this.startActivity(i);
    }
}
