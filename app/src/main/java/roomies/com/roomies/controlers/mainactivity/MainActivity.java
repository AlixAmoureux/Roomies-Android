package roomies.com.roomies.controlers.mainactivity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import roomies.com.roomies.R;
import roomies.com.roomies.controlers.mainactivity.connection.SignInFragment;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.top_bar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new SignInFragment()).commit();
        }
    }
}
