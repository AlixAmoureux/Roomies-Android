package roomies.com.roomies.controlers.secondactivity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import roomies.com.roomies.R;
import roomies.com.roomies.controlers.ManageObjects;
import roomies.com.roomies.controlers.mainactivity.managecoloc.createcoloc.CreateColocFragment;
import roomies.com.roomies.controlers.mainactivity.managecoloc.joincoloc.listcolocs.ListColocFragment;
import roomies.com.roomies.controlers.secondactivity.parameters.ParametersFragment;
import roomies.com.roomies.models.users.ConnectedUserInfo;

public class SecondActivity extends AppCompatActivity {

    private BottomNavigationView mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        ConnectedUserInfo user = ManageObjects.readUserInfosInPrefs("userInfo", this);
        Log.e("Second Activity", "onCreate 1 !");

        Toolbar toolbar = (Toolbar) findViewById(R.id.top_bar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new ListColocFragment()).commit();
        }
        Log.e("Second Activity", "onCreate 1 !");
        mBottomBar = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment f = null;
                switch (item.getItemId())
                {
                    case R.id.action_home:
                        f = new ListColocFragment();
                        break;
                    case R.id.action_dettes:
                        f = new CreateColocFragment();
                        break;
                    case R.id.action_sous_menu:
                        f = new ParametersFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, f)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                return false;
            }
        });
    }
}
