package ca.mapd.capstone.smartmenu.activities;

import android.content.Intent;
import android.os.Bundle;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.customer.customer_activity.CustomerMainActivity;
import ca.mapd.capstone.smartmenu.restaurant.RestaurantMainActivity;

public class MainActivity extends AuthAbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (m_Auth.getCurrentUser() != null) {
            boolean isRestaurantApp = getResources().getBoolean(R.bool.is_restaurant_app);
            if (isRestaurantApp)
                startActivity(new Intent(this, RestaurantMainActivity.class));
            else
                startActivity(new Intent(this, CustomerMainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }

}
