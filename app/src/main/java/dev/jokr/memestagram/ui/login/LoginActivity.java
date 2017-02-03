package dev.jokr.memestagram.ui.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.models.User;
import dev.jokr.memestagram.ui.main.MainActivity;
import dev.jokr.memestagram.util.ProjectConst;

public class LoginActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private FirebaseAuth firebaseAuth;

    @BindView(R.id.etxt_uname)
    EditText txtUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(this);
    }

    public void loginAsAnon(View view) {
        String uname = txtUsername.getText().toString();
        if (uname.length() < 4) {
            Toast.makeText(this, "At least 4 chars, please", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("USER", "signInAnonymously", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
//                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                            LoginActivity.this.startActivity(i);
                        }
                    }
                });
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // When he sings in
            Log.d("USER", "onAuthStateChanged:signed_in:" + user.getUid());

            saveNewUserToDatabase(user);

            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(i);
        }
    }

    private void saveNewUserToDatabase(FirebaseUser user) {
        String uname = txtUsername.getText().toString();

        User thisUser = new User(user.getUid(), uname, ProjectConst.ANON);

        FirebaseDatabase fireDb = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = fireDb.getReference("users");

        Map<String, Object> update = new HashMap<>();
        update.put("/" + user.getUid(), thisUser.toMap());
        usersRef.updateChildren(update);
    }


    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(this);
    }
}
