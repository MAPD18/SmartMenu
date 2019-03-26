package ca.mapd.capstone.smartmenu.customer.customer_activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.customer.models.Customer;
import com.google.firebase.database.*;

public class CustomerProfileActivity extends AppCompatActivity {

    private EditText txtEmail;
    private EditText txtName;
    private EditText txtAddress;
    private EditText txtPhoneNumber;

    private DatabaseReference databaseCustomer;

    private String userEmail;
    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        userEmail = getIntent().getStringExtra("userEmail");

        databaseCustomer = FirebaseDatabase.getInstance().getReference(Customer.CUSTOMER_KEY);

        Query query = databaseCustomer
                .orderByChild("m_Email")
                .equalTo(userEmail);

        query.addListenerForSingleValueEvent(valueEventListener);

        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        Button btnSave = findViewById(R.id.btnSaveCustomerProfile);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCustomerProfile();
            }
        });

    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    customer = snapshot.getValue(Customer.class);
                    if (customer != null && customer.toString() != null) {
                        //FIXME: Research and fix the async problem.
                        txtEmail.setText(customer.getM_Email());
                        txtName.setText(customer.getM_Name());
                        txtAddress.setText(customer.getM_Address());
                        txtPhoneNumber.setText(customer.getM_PhoneNumber());
                    }
                }
            } else {
                Customer customer = new Customer();
                customer.setM_Id(databaseCustomer.push().getKey());
                customer.setM_Email(userEmail);
                saveCustomer(customer, null);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void updateCustomerProfile() {
        boolean formValid = true;

        // Validate all the required fields in the form
        if (TextUtils.isEmpty(txtName.getText().toString().trim())) {
            txtName.setError("Name is required!");
            formValid = false;
        }
        if (TextUtils.isEmpty(txtAddress.getText().toString().trim())) {
            txtAddress.setError("Address is required!");
            formValid = false;
        }
        if (TextUtils.isEmpty(txtPhoneNumber.getText().toString().trim())) {
            txtPhoneNumber.setError("Phone number is required!");
            formValid = false;
        }
        if (formValid) {
            customer.setM_Name(txtName.getText().toString());
            customer.setM_Address(txtAddress.getText().toString());
            customer.setM_PhoneNumber(txtPhoneNumber.getText().toString());
            saveCustomer(customer, userEmail);
            Toast.makeText(this, "Customer profile updated!", Toast.LENGTH_LONG).show();
        }
    }

    //TODO: Improve this method here with id, not e-mail!
    private void saveCustomer(Customer customer, String email) {
        if (email != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Customer.CUSTOMER_KEY).child(customer.getM_Id());
            databaseReference.setValue(customer);
        } else {
            databaseCustomer.child(customer.getM_Id()).setValue(customer);
        }
    }
}