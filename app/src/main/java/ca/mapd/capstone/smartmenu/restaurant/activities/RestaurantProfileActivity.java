package ca.mapd.capstone.smartmenu.restaurant.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.activities.AuthAbstractActivity;
import ca.mapd.capstone.smartmenu.customer.models.Restaurant;
import com.google.firebase.database.*;

public class RestaurantProfileActivity extends AuthAbstractActivity {

    private EditText txtEmail;
    private EditText txtName;
    private EditText txtAddress;
    private EditText txtPhoneNumber;

    private DatabaseReference databaseRestaurant;

    private String userId = "";
    private String userEmail;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);

        this.setTitle("Profile");
        if (m_Auth.getCurrentUser() != null)
            userId = m_Auth.getCurrentUser().getUid();
        userEmail = m_Auth.getCurrentUser().getEmail();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        databaseRestaurant = FirebaseDatabase.getInstance().getReference(Restaurant.RESTAURANT_KEY).child(userId);
        databaseRestaurant.addListenerForSingleValueEvent(valueEventListener);

        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        Button btnSave = findViewById(R.id.btnSaveRestaurantProfile);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRestaurantProfile();
            }
        });

    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                restaurant = dataSnapshot.getValue(Restaurant.class);
                if (restaurant != null) {
                    txtName.setText(restaurant.getM_Name());
                    txtAddress.setText(restaurant.getM_Address());
                    txtPhoneNumber.setText(restaurant.getM_PhoneNumber());
                }
            } else {
                Restaurant restaurant = new Restaurant();
                restaurant.setM_Id(databaseRestaurant.push().getKey());
                restaurant.setM_Email(userEmail);
                saveRestaurant(restaurant);
            }
            txtEmail.setText(userEmail);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void updateRestaurantProfile() {
        boolean formValid = true;

        // Validate all the required fields in the form
        if (TextUtils.isEmpty(txtName.getText().toString().trim())) {
            txtName.setError("Name is required!");
            formValid = false;
        }
        if (TextUtils.isEmpty(txtAddress.getText().toString().trim())) {
            txtAddress.setError("Address size is required!");
            formValid = false;
        }
        if (TextUtils.isEmpty(txtPhoneNumber.getText().toString().trim())) {
            txtPhoneNumber.setError("Phone number is required!");
            formValid = false;
        }
        if (formValid) {
            restaurant.setM_Name(txtName.getText().toString());
            restaurant.setM_Address(txtAddress.getText().toString());
            restaurant.setM_PhoneNumber(txtPhoneNumber.getText().toString());
            saveRestaurant(restaurant);
            Toast.makeText(this, "Restaurant profile updated!", Toast.LENGTH_LONG).show();

        }
    }

    private void saveRestaurant(Restaurant restaurant) {
        if (userId != null && !userId.isEmpty()) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Restaurant.RESTAURANT_KEY).child(userId);
            databaseReference.setValue(restaurant);
        } else {
            databaseRestaurant.child(restaurant.getM_Id()).setValue(restaurant);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
