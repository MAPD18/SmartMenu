package ca.mapd.capstone.smartmenu.customer;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ca.mapd.capstone.smartmenu.R;

public class AboutPageActivity extends AppCompatActivity implements View.OnClickListener {
    // this is the About Page. Not much goes on here
    Button m_BackButton; // this button will cause the user to go back to the previous activity (i.e. the main menu)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);
        m_BackButton = (Button) findViewById(R.id.aboutGoBackButton);
        m_BackButton.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
