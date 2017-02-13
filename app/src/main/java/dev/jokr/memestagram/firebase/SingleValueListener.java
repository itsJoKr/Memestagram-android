package dev.jokr.memestagram.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by jokr on 13.02.17..
 */

public class SingleValueListener implements ValueEventListener {

    private LambdaValueListener listener;
    private DatabaseReference dbRef;
    private Query query;

    private SingleValueListener(DatabaseReference dbRef, LambdaValueListener listener) {
        this.listener = listener;
        this.dbRef = dbRef;
        this.dbRef.addValueEventListener(this);
    }

    private SingleValueListener(Query query, LambdaValueListener listener) {
        this.listener = listener;
        this.query = query;
        this.query.addValueEventListener(this);
    }

    public static SingleValueListener make(DatabaseReference dbRef, LambdaValueListener listener) {
        return new SingleValueListener(dbRef, listener);
    }

    public static SingleValueListener make(Query query, LambdaValueListener listener) {
        return new SingleValueListener(query, listener);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        listener.onDataChange(dataSnapshot);
        if (dbRef != null) dbRef.removeEventListener(this);
        if (query != null) query.removeEventListener(this);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public interface LambdaValueListener {
        public void onDataChange(DataSnapshot snap);
    }
}
