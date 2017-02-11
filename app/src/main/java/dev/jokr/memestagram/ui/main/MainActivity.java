package dev.jokr.memestagram.ui.main;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.events.ShowConversationEvent;
import dev.jokr.memestagram.events.ShowCreateNewMemeEvent;
import dev.jokr.memestagram.events.ShowMemeEvent;
import dev.jokr.memestagram.events.ShowProfileEvent;
import dev.jokr.memestagram.events.ShowUserEvent;
import dev.jokr.memestagram.misc.FragmentCreatedListener;
import dev.jokr.memestagram.misc.LoggedUserManager;
import dev.jokr.memestagram.ui.login.LoginActivity;
import dev.jokr.memestagram.ui.meme.MemeFragment;
import dev.jokr.memestagram.ui.memes.PagerFragment;
import dev.jokr.memestagram.ui.messages.ConvosFragment;
import dev.jokr.memestagram.ui.messages.MessagesFragment;
import dev.jokr.memestagram.ui.editor.PickerActivity;
import dev.jokr.memestagram.ui.profile.ProfileFragment;
import dev.jokr.memestagram.ui.user.UserFragment;

public class MainActivity extends AppCompatActivity implements FragmentCreatedListener {

    private final static int FRAG_MEMES = 1;
    private final static int FRAG_PROFILE = 2;
    private final static int FRAG_MESSAGES = 3;
    private final static int FRAG_SEARCH = 4;

    private final static int FRAG_MEME_DETAIL = 5;
    private final static int FRAG_MESSAGES_DETAIL = 6;
    private final static int FRAG_USER_DETAIL = 7;

    @BindView(R.id.content_frame) LinearLayout contentFrame;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.txt_logged_as) TextView txtLoggedAs;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

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

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "Billabong.woff");
        toolbarTitle.setTypeface(tf);

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
        updateFragment(FRAG_MEMES);
    }

    @OnClick(R.id.drawer_item_profile)
    public void openItemProfile(View v) {
        updateFragment(FRAG_PROFILE);
    }

    @OnClick(R.id.drawer_item_messages)
    public void openItemMessages(View v) {
        updateFragment(FRAG_MESSAGES);
    }

    @Subscribe
    public void onMessageEvent(ShowCreateNewMemeEvent event) {
        Intent i = new Intent(this, PickerActivity.class);
        this.startActivity(i);
    }

    @Subscribe
    public void onMessageEvent(ShowMemeEvent event) {
        currentFragmentState = FRAG_MEME_DETAIL;
        currentFragment = MemeFragment.newInstance(event.getMeme());
        this.tempDrawable = event.getDrawable();
        setFragmentWithBackstack(currentFragment, "meme");
    }

    @Subscribe
    public void onMessageEvent(ShowConversationEvent event) {
        currentFragmentState  = FRAG_MESSAGES_DETAIL;
        currentFragment = MessagesFragment.newInstance(event.getConversation());
        setFragmentWithBackstack(currentFragment);
    }

    @Subscribe
    public void onMessageEvent(ShowUserEvent event) {
        currentFragmentState = FRAG_USER_DETAIL;
        currentFragment = UserFragment.newInstance(event.getUser());
        setFragmentWithBackstack(currentFragment);
    }

    @Subscribe
    public void onMessageEvent(ShowProfileEvent event) {
        currentFragmentState = FRAG_PROFILE;
        currentFragment = new ProfileFragment();
        tempDrawable = null; // remove reference
        setFragment(currentFragment);
    }

    @Override
    public void onFragmentCreated() {
        if (currentFragmentState == FRAG_MEME_DETAIL || currentFragmentState == FRAG_USER_DETAIL) {
            MemeFragment f = (MemeFragment) getSupportFragmentManager().findFragmentByTag("meme");
            f.setMemeImage(tempDrawable);
        }
    }

    private void updateFragment(int frag) {
        if (frag == currentFragmentState) return;

        currentFragment = null;
        tempDrawable = null; // remove reference
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

    // Needed so I can reference fragment later by tag
    private void setFragmentWithBackstack(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("wtf")
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (currentFragmentState == FRAG_MESSAGES_DETAIL) {
                int id = getSupportFragmentManager().getBackStackEntryAt(0).getId();
                getSupportFragmentManager().popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
