package com.roomies.android;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.roomies.android.informations.users.ConnectedUserInfo;
import com.roomies.android.mainactivityviews.managecoloc.createcoloc.CreateColocFragment;
import com.roomies.android.mainactivityviews.managecoloc.joincoloc.ColocsListFragment;
import com.roomies.android.secondactivityviews.ParametersFragment;

public class SecondActivity extends AppCompatActivity {

    private BottomNavigationView mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Log.d("Second Activity", "onCreate 1 !");
        ConnectedUserInfo user = ManageObjects.readUserInfosInPrefs(this, "userInfo");

        Toolbar toolbar = (Toolbar) findViewById(R.id.top_bar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            // As a rule of thumb, always use replace instead of add to avoid fragment overlap.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ColocsListFragment())
                    .commit();
        }

        mBottomBar = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment f = null;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        f = new ColocsListFragment();
                        break;
                    case R.id.action_dettes:
                        f = new CreateColocFragment();
                        break;
                    case R.id.action_sous_menu:
                        f = new ParametersFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, f)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();

                return false;
            }
        });
    }
}
