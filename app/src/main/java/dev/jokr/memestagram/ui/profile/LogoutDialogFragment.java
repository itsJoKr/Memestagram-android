package dev.jokr.memestagram.ui.profile;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import static android.support.v7.app.AlertDialog.*;

/**
 * Created by jokr on 18.02.17..
 */

public class LogoutDialogFragment extends DialogFragment {

    private OnLogoutListener listener;

    public void setListener(OnLogoutListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);

        Builder builder = new Builder(getActivity());
        builder.setTitle("Are you sure?")
                .setMessage("You are logged in as a causal pleb. This means you won't be able" +
                        " to return after you log out.")
                .setPositiveButton("YES", (dialog, which) -> {
                    if (listener != null) listener.onConfirmedLogout();
                }).setNegativeButton("CANCEL", null);

        return builder.create();
    }

    public interface OnLogoutListener {
        public void onConfirmedLogout();
    }
}
