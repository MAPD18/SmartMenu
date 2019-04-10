package ca.mapd.capstone.smartmenu.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ca.mapd.capstone.smartmenu.R;

import com.google.firebase.auth.FirebaseAuth;

public class NewUserActivity extends Activity {

    protected FirebaseAuth m_Auth;
    EditText userEmail, userPassword;
    Button createUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        userEmail = findViewById(R.id.txtEmail);
        userPassword = findViewById(R.id.txtPassword);
        createUserButton = findViewById(R.id.btnCreateUser);
        m_Auth = FirebaseAuth.getInstance();

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Auth.createUserWithEmailAndPassword(userEmail.getText().toString(), userPassword.getText().toString());
            }
        });
    }
}