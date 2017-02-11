package dev.jokr.memestagram.util;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by jokr on 09.02.17..
 */

public class ChildAddListenerImpl implements ChildEventListener {

    private LambdaChildListener l;

    private ChildAddListenerImpl(LambdaChildListener l) {
        this.l = l;
    }

    public static ChildAddListenerImpl make(LambdaChildListener listener) {
        return new ChildAddListenerImpl(listener);
    }

    public interface LambdaChildListener {
        public void onChildAdded(DataSnapshot snap, String s);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        l.onChildAdded(dataSnapshot, s);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
