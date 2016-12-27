package dev.jokr.memestagram.screens.main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dev.jokr.memestagram.R;
import dev.jokr.memestagram.screens.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserAuth();
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
