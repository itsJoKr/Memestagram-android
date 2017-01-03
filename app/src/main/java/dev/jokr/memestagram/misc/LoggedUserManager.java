package dev.jokr.memestagram.misc;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import dev.jokr.memestagram.models.User;

/**
 * Created by jokr on 03.01.17..
 */

public class LoggedUserManager {

    private static LoggedUserManager um;
    private User u;
    private String $key;

    private LoggedUserManager() { }

    public static LoggedUserManager getInstance() {
        if (um == null) um = new LoggedUserManager();
        return um;
    }


    public void loginUser(String $key) {
        this.$key = $key;
        fetchUserData($key);
    }

    public void loginUser(User u) {
        this.u = u;
    }

    public void getLoggedUser(LoggedUserInfoListener l) {
        if (u != null) {
            l.loggedUserInfo(u);
        } else {
            fetchUserData(this.$key, l);
        }
    }


    private void fetchUserData(String $key, final LoggedUserInfoListener l) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + $key);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
                value.$key = LoggedUserManager.this.$key;
                LoggedUserManager.this.u = value;
                l.loggedUserInfo(LoggedUserManager.this.u);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("LoggedUserManager", "Failed to read value.", databaseError.toException());

            }
        });
    }

    private void fetchUserData(String $key) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + $key);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
                value.$key = LoggedUserManager.this.$key;
                LoggedUserManager.this.u = value;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("LoggedUserManager", "Failed to read value.", databaseError.toException());

            }
        });
    }


    public interface LoggedUserInfoListener {
        public void loggedUserInfo(User user);
    }

}
