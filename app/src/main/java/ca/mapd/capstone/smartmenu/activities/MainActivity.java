/*
package ca.mapd.capstone.smartmenu.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import javax.inject.Inject;

import ca.mapd.capstone.smartmenu.interfaces.IUser;
import ca.mapd.capstone.smartmenu.R;
import toothpick.Toothpick;

public class MainActivity extends AppCompatActivity {

    private TextView hello;

    @Inject
    IUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toothpick.inject(this, Toothpick.openScopes(getApplication(), this));

        hello = findViewById(R.id.hello);
        hello.setText(user.getFlavor());

    }
}
*/
