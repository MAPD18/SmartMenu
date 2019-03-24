package ca.mapd.capstone.smartmenu.restaurant.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import ca.mapd.capstone.smartmenu.R;

public class RestaurantProfileActivity extends AppCompatActivity {

    private EditText txtName;
    private EditText txtAddress;
    private EditText txtPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);

        txtName = findViewById(R.id.txtName);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);

    }

    public void onClickSave(View view) {
        //TODO: Save data here.
    }
}
