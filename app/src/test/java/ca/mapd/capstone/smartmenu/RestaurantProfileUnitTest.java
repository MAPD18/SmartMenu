package ca.mapd.capstone.smartmenu;

import ca.mapd.capstone.smartmenu.customer.models.Restaurant;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RestaurantProfileUnitTest {

    @Test
    public void testRestaurantProfileUpdate() {
        Restaurant restaurant =  new Restaurant("Restaurant 1", "Test avenue", "647 561 7767");
        assertTrue(restaurant.isPofileFormValid());
    }

    @Test
    public void testRestaurantProfileUpdateWithEmptyFields(){
        Restaurant restaurant =  new Restaurant("  ", "", "");
        Assert.assertFalse(restaurant.isPofileFormValid());

        Restaurant restaurant2 =  new Restaurant("Restaurant 1", "", "");
        Assert.assertFalse(restaurant2.isPofileFormValid());

        Restaurant restaurant3 =  new Restaurant("", "Test ave", "");
        Assert.assertFalse(restaurant3.isPofileFormValid());

        Restaurant restaurant4 =  new Restaurant("", "", "647 561 7767");
        Assert.assertFalse(restaurant4.isPofileFormValid());
    }
}
