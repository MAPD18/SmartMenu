package ca.mapd.capstone.smartmenu.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.customer.customer_activity.RestaurantListActivity;

public class MainActivity extends AuthAbstractActivity implements View.OnClickListener {
    /*
    * Starting point of the application, has buttons which lead to places
    * Note that most essential activity will implement AuthAbstractActivity*/
    Button m_CourierButton;
    Button m_CustomerButton;
    SignInButton m_signInButton;
    Button m_signOutButton;
    TextView m_WelcomeText;
    LinearLayout m_LoggedInMenu;
    Button m_AboutPageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_LoggedInMenu = (LinearLayout) findViewById(R.id.loggedInLayout);
        m_LoggedInMenu.setVisibility(View.GONE);

        m_CustomerButton = (Button) findViewById(R.id.customerTypeButton);
        m_signOutButton = (Button) findViewById(R.id.signOutButton);
        m_signInButton = (SignInButton) findViewById(R.id.signInButton);
        m_WelcomeText = (TextView) findViewById(R.id.welcomeTextView);
        m_AboutPageButton = (Button) findViewById(R.id.aboutPageButton);

        m_signInButton.setOnClickListener(this);
        m_signOutButton.setOnClickListener(this);
        m_AboutPageButton.setOnClickListener(this);
        m_CustomerButton.setOnClickListener(this);

    }

    @Override
    public void onLogin() {
        // see documentation, changes the view to represent the login state
        // in this case, the "Main Menu" layout is revealed to the user, while hiding the sign-in button
        super.onLogin();
        m_LoggedInMenu.setVisibility(View.VISIBLE);
        m_signInButton.setVisibility(View.GONE);
        String displayName = m_Auth.getCurrentUser().getDisplayName(); // i mean if it's null then something's seriously wrong
        m_WelcomeText.setText(getString(R.string.welcome_text_on_login, displayName));
    }

    @Override
    public void onNotLoggedIn() {
        // same as onLogin, except that the process is reversed it doesn't call its super (it overrides it)
        m_LoggedInMenu.setVisibility(View.GONE);
        m_signInButton.setVisibility(View.VISIBLE);
        m_WelcomeText.setText(getString(R.string.welcome_text));
    }


    @Override
    public void onClick(View v) {
        Intent i = null;
        switch(v.getId()){
            case R.id.customerTypeButton:
                Intent i1 = new Intent(this, RestaurantListActivity.class);
                startActivity(i1);
            case R.id.signInButton:
                googleSignIn();
                break;
            case R.id.signOutButton:
                googleSignOut();
                break;
            case R.id.aboutPageButton:
                i = new Intent(this, AboutPageActivity.class);
                break;
        }
        if (i != null) {
            startActivity(i);
        }
    }
}
