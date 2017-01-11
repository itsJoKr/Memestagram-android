package dev.jokr.memestagram.ui.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import dev.jokr.memestagram.events.ShowConversation;
import dev.jokr.memestagram.events.ShowCreateNewMeme;
import dev.jokr.memestagram.events.ShowMeme;
import dev.jokr.memestagram.misc.FragmentCreatedListener;
import dev.jokr.memestagram.misc.LoggedUserManager;
import dev.jokr.memestagram.ui.login.LoginActivity;
import dev.jokr.memestagram.ui.meme.MemeFragment;
import dev.jokr.memestagram.ui.memes.PagerFragment;
import dev.jokr.memestagram.ui.messages.ConvosFragment;
import dev.jokr.memestagram.ui.messages.MessagesFragment;
import dev.jokr.memestagram.ui.newmeme.PickerActivity;
import dev.jokr.memestagram.ui.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity implements FragmentCreatedListener {

    private final static int FRAG_MEMES = 1;
    private final static int FRAG_PROFILE = 2;
    private final static int FRAG_MESSAGES = 3;
    private final static int FRAG_SEARCH = 4;

    private final static int FRAG_MEME_DETAIL = 5;
    private final static int FRAG_MESSAGES_DETAIL = 6;

    @BindView(R.id.content_frame) LinearLayout contentFrame;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.txt_logged_as) TextView txtLoggedAs;

    private int currentFragmentState = 0;
    private Fragment currentFragment;
    private FirebaseAuth firebaseAuth;
    private Drawable tempDrawable;

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

    @OnClick(R.id.drawer_item_messages)
    public void openItemMessages(View v) {
        showRipple(v);
        updateFragment(FRAG_MESSAGES);
    }

    @Subscribe
    public void onMessageEvent(ShowCreateNewMeme event) {
        Intent i = new Intent(this, PickerActivity.class);
        this.startActivity(i);
    }

    @Subscribe
    public void onMessageEvent(ShowMeme event) {
        currentFragmentState = FRAG_MEME_DETAIL;
        currentFragment = MemeFragment.newInstance(event.getMeme());
        this.tempDrawable = event.getDrawable();
        setFragmentWithBackstack(currentFragment);
    }

    @Subscribe
    public void onMessageEvent(ShowConversation event) {
        currentFragmentState  = FRAG_MESSAGES_DETAIL;

        currentFragment = MessagesFragment.newInstance(event.getConversation());
        tempDrawable = null; // remove reference
        setFragmentWithBackstack(currentFragment);
    }


    @Override
    public void onFragmentCreated() {
        if (currentFragmentState == FRAG_MEME_DETAIL) {
           ((MemeFragment) currentFragment).setMemeImage(tempDrawable);
        }
    }

    private void updateFragment(int frag) {
        if (frag == currentFragmentState) return;

        currentFragment = null;
        tempDrawable = null; // remove references
        currentFragmentState = frag;
        drawer.closeDrawers();
        switch (currentFragmentState) {
            case FRAG_MEMES:
                PagerFragment f = new PagerFragment();
                setFragment(f);
                break;
            case FRAG_PROFILE:
                setFragment(new ProfileFragment());
                break;
            case FRAG_MESSAGES:
                setFragment(new ConvosFragment());
                break;
            default:
                Log.e("MainAcitvity", "Unknown fragment const: " + currentFragmentState);
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

    private void setFragmentWithBackstack(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("wtf")
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showRipple(View v) {
        RippleBackground rb = (RippleBackground) v.getParent();
        rb.startRippleAnimation();
        stopRipple(rb);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void stopRipple(final RippleBackground rb) {
        new Thread(() -> {
            try {
                Thread.sleep(120);
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
