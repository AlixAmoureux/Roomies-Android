package roomies.com.roomies;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import roomies.com.roomies.mainactivityviews.managecoloc.createcoloc.CreateColocFragment;
import roomies.com.roomies.mainactivityviews.managecoloc.joincoloc.ListColocFragment;

public class SecondActivity extends AppCompatActivity {

    private BottomNavigationView mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

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
