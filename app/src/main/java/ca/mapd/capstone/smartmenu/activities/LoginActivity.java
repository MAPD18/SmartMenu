package ca.mapd.capstone.smartmenu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

import ca.mapd.capstone.smartmenu.R;

public class LoginActivity extends AuthAbstractActivity implements View.OnClickListener {
    /*
    * Starting point of the application, has buttons which lead to places
    * Note that most essential activity will implement AuthAbstractActivity */
    SignInButton googleSignInButton;
    Button loginButton, signUpButton;
    TextInputEditText login, password;
    TextView errorMessage;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (m_Auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        init();
    }

    private void init() {
        googleSignInButton = findViewById(R.id.signInButton);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        errorMessage = findViewById(R.id.errorMessage);
        progressBar = findViewById(R.id.progressBar);

        googleSignInButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    @Override
    public void onLoginSuccessful() {
        progressBar.setVisibility(View.GONE);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onNotLoggedIn() {

    }

    @Override
    protected void onLoginFailed() {
        errorMessage.setText(R.string.login_or_password_invalid_message);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.loginButton:
                signInWithEmailAndPassword(login.getText() != null ? login.getText().toString() : "",
                        password.getText() != null? password.getText().toString() : "");
                break;
            case R.id.signUpButton:
                startActivity(new Intent(this, NewUserActivity.class));
                finish();
            case R.id.signInButton:
                googleSignIn();
                break;
        }
        progressBar.setVisibility(View.VISIBLE);
    }
}
