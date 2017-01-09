package dev.jokr.memestagram.ui.main;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skyfishjy.library.RippleBackground;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.events.ShowCreateNewMeme;
import dev.jokr.memestagram.events.ShowMeme;
import dev.jokr.memestagram.misc.LoggedUserManager;
import dev.jokr.memestagram.ui.login.LoginActivity;
import dev.jokr.memestagram.ui.meme.MemeFragment;
import dev.jokr.memestagram.ui.memes.PagerFragment;
import dev.jokr.memestagram.ui.newmeme.PickerActivity;
import dev.jokr.memestagram.ui.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private final static int FRAG_MEMES = 1;
    private final static int FRAG_PROFILE = 2;
    private final static int FRAG_MESSAGES = 3;
    private final static int FRAG_SEARCH = 4;

    private final static int FRAG_MEME_DETAIL = 5;

    @BindView(R.id.content_frame) LinearLayout contentFrame;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.txt_logged_as) TextView txtLoggedAs;

    private int currentFragment = 0;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserAuth();

        // initially show memes
        updateFragment(FRAG_MEMES);
    }

    private void checkUserAuth() {
        firebaseAuth.addAuthStateListener(firebaseAuth1 -> {
            FirebaseUser user = firebaseAuth1.getCurrentUser();
            if (user != null) {
                Log.d("USER", "User signed in:" + user.getUid());
                LoggedUserManager.getInstance().loginUser(user.getUid());
                LoggedUserManager.getInstance().getLoggedUser(user1 -> setLoggedInfoDrawer(user1.username));
            } else {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(i);
                Log.d("USER", "onAuthStateChanged:signed_out");
            }
        });
    }

    public void logoutUser(View view) {
        firebaseAuth.signOut();
    }

    @OnClick(R.id.drawer_item_dank)
    public void openItemDank(View v) {
        showRipple(v);
        updateFragment(FRAG_MEMES);
    }

    @OnClick(R.id.drawer_item_profile)
    public void openItemProfile(View v) {
        showRipple(v);
        updateFragment(FRAG_PROFILE);
    }

    @Subscribe
    public void onMessageEvent(ShowCreateNewMeme event) {
        Intent i = new Intent(this, PickerActivity.class);
        this.startActivity(i);
    }

    @Subscribe
    public void onMessageEvent(ShowMeme event) {
        currentFragment = FRAG_MEME_DETAIL;
        MemeFragment frag = MemeFragment.newInstance(event.getMeme());
        setFragment(frag);
    }

    private void updateFragment(int frag) {
        if (frag == currentFragment) return;

        currentFragment = frag;
        drawer.closeDrawers();
        switch (currentFragment) {
            case FRAG_MEMES:
            {
                PagerFragment f = new PagerFragment();
                setFragment(f);
                break;
            }
            case FRAG_PROFILE: {
                setFragment(new ProfileFragment());
                break;
            }
            default:
                Log.e("MainAcitvity", "Unknown fragment const: " + currentFragment);
        }
    }

    private void setLoggedInfoDrawer(String uname) {
        txtLoggedAs.setText("Logged as " + uname);
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showRipple(View v) {
        RippleBackground rb = (RippleBackground) v.getParent();
        rb.startRippleAnimation();
        stopRipple(rb);
    }

    private void stopRipple(final RippleBackground rb) {
        new Thread(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rb.post(new Runnable() {
                public void run() {
                    rb.stopRippleAnimation();
                }
            });
        }).start();
    }
}
