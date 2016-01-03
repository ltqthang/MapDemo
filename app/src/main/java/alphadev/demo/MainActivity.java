package alphadev.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import alphadev.demo.fragment.HomeMapFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new HomeMapFragment())
                    .commit();
        }
    }
}
