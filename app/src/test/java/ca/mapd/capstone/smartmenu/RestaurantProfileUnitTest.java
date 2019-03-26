package ca.mapd.capstone.smartmenu;

import android.text.Editable;
import android.text.TextUtils;
import ca.mapd.capstone.smartmenu.customer.models.Restaurant;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RestaurantProfileUnitTest {

    @Test
    public void testUpdateRestaurantProfileFullfilled() {
        String name = "Restaurant 1";
        String address = "Test avenue";
        String phoneNumber = "647 6617767";
        assertTrue(updateRestaurantProfile(name, address, phoneNumber));
    }

    private boolean updateRestaurantProfile(String name, String address,
                                            String phoneNumber) {
        boolean formValid = true;
        Restaurant restaurant = new Restaurant();
        // Validate all the required fields in the form
        if (name.trim().isEmpty()) {
            //txtName.setError("Name is required!");
            formValid = false;
        }
        if (address.trim().isEmpty()) {
            //txtAddress.setError("Address size is required!");
            formValid = false;
        }
        if (phoneNumber.trim().isEmpty()) {
            //txtPhoneNumber.setError("Phone number is required!");
            formValid = false;
        }
        if (formValid) {
            restaurant.setM_Name(name.trim());
            restaurant.setM_Address(address.trim());
            restaurant.setM_PhoneNumber(phoneNumber.trim());
            //saveRestaurant(restaurant, userEmail);
            //Toast.makeText(this, "Restaurant profile updated!", Toast.LENGTH_LONG).show();
        }
        return formValid;
    }
}
