package novotek.com.monuments.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import novotek.com.monuments.Monuments;
import novotek.com.monuments.R;
import novotek.com.monuments.events.MonumentCreatedEvent;
import novotek.com.monuments.fragment.MonumentListFragment;
import novotek.com.monuments.model.Monument;

/**
 * Created by BX on 5/23/2016.
 */
public class MainActivity extends AppCompatActivity {
    private static final int CACHED_FRAGMENT_PAGES = 2;
    public static final String EXTRA_USER_NAME = "userName";
    public static final String EXTRA_USER_ID = "userId";

    FragmentPagerAdapter adapterViewPager;
    ViewPager fragmentPager;
    MonumentListFragment myMonumentFragment;
    MonumentListFragment allMomumentFragment;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.floating_action_button) FloatingActionButton mFloatingActionButton;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.logout) ImageView logoutBtn;
    @Bind(R.id.logged_user_name) TextView loggedUserName;

    private int mFragmentActive;
    VectorDrawableCompat addMonumentIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Logger.d("MainActivity.onCreate");

        if (toolbar != null)
            setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.monuments_logo);
        }
        //actionBar.setDisplayHomeAsUpEnabled(showHamburgerIcon);
        setSupportActionBar(toolbar);

        fragmentPager = (ViewPager) findViewById(R.id.forFragment);
        fragmentPager.setOffscreenPageLimit(CACHED_FRAGMENT_PAGES);
        adapterViewPager = new MainActivityPagerAdapter(getSupportFragmentManager(), this);
        fragmentPager.setAdapter(adapterViewPager);
        fragmentPager.setCurrentItem(0);
        fragmentChanged(0);
        //fragmentPager.setPagingEnabled(false);
        // Attach the page change listener inside the activity
        fragmentPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageSelected(int position) {
                fragmentChanged(position);
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(fragmentPager);
        //tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        Intent myIntent = getIntent(); // gets the previously created intent
        String userName = myIntent.getStringExtra(EXTRA_USER_NAME);
        if (!TextUtils.isEmpty(userName)) {
            Logger.d("Logged as " + userName);
        }

        long userId = Long.valueOf(myIntent.getLongExtra(EXTRA_USER_ID, 0l));


        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureMonument();
            }
        });

        addMonumentIcon = VectorDrawableCompat.create(getResources(), R.drawable.ic_add_black_24dp, null);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        loggedUserName.setText(Monuments.instance.getLoggedUser().getUserName());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_types:
                gotoActionTypes();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoActionTypes() {
        Intent intent = new Intent(this, MonumentTypeActivity.class);
        startActivity(intent);
    }

    private void logout() {
        Monuments.instance.setLoggedUser(null);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void fragmentChanged(int position) {
        mFragmentActive = position;

        if (mFloatingActionButton != null) {
            if (position == 0) {
                mFloatingActionButton.setVisibility(View.VISIBLE);
                mFloatingActionButton.setImageDrawable(addMonumentIcon);
            } else if (position == 1) {
                mFloatingActionButton.setVisibility(View.GONE);
                mFloatingActionButton.setImageDrawable(null);
            }
        }
    }
    public void setMyMonumentsFragment(MonumentListFragment fragment) {
        myMonumentFragment = fragment;
    }

    public void setOtherMonumentsFragment(MonumentListFragment fragment) {
        allMomumentFragment = fragment;
    }

    private void captureMonument() {
        Intent intent = new Intent(this, CaptureMonumentActivity.class);
        startActivity(intent);
    }

    private void reinitFragmentData(Monument monumentChanged) {
        if (myMonumentFragment != null) {
            myMonumentFragment.initializeListAdapter(monumentChanged);
            Logger.d("myMonumentFragment");
        }
        if (allMomumentFragment != null) {
            allMomumentFragment.initializeListAdapter(monumentChanged);
            Logger.d("allMomumentFragment");
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(final MonumentCreatedEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        // after activity loading stabilize
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                reinitFragmentData(event.monument);
            }
        }, 250);

        showSnackbar("Monument added.");
        Logger.d("created");
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }



}
