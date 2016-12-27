package dev.jokr.memestagram.ui.main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.viewpager_main) ViewPager viewPager;
    @Bind(R.id.tab_layout_main) TabLayout tabLayout;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
}
