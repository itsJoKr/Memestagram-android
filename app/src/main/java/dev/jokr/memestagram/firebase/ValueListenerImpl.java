package dev.jokr.memestagram.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by jokr on 09.02.17..
 */

public class ValueListenerImpl implements ValueEventListener {

    LambdaValueListener l;

    private ValueListenerImpl(LambdaValueListener l) {
        this.l = l;
    }

    public ValueListenerImpl(){}

    public void setListener(LambdaValueListener listener) {
        this.l = listener;
    }

    public static ValueListenerImpl make(LambdaValueListener l) {
        return new ValueListenerImpl(l);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        l.onDataChange(dataSnapshot);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public interface LambdaValueListener {
        public void onDataChange(DataSnapshot snap);
    }
}
