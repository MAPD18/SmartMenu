package ca.mapd.capstone.smartmenu;

import org.junit.Test;

import ca.mapd.capstone.smartmenu.customer.models.Customer;

import static org.junit.Assert.assertTrue;

public class CustomerProfileUnitTest {

    @Test
    public void testUpdateCustomerProfileFulfilled() {
        String name = "Customer 1";
        String address = "Test address";
        String phoneNumber = "4165644770";
        assertTrue(updateCustomerProfile(name, address, phoneNumber));
    }

    private boolean updateCustomerProfile(String name, String address,
                                            String phoneNumber) {
        boolean formValid = true;
        Customer customer = new Customer();
        // Validate all the required fields in the form
        if (name.trim().isEmpty()) {
            formValid = false;
        }
        if (address.trim().isEmpty()) {
            formValid = false;
        }
        if (phoneNumber.trim().isEmpty()) {
            formValid = false;
        }
        if (formValid) {
            customer.setM_Name(name.trim());
            customer.setM_Address(address.trim());
            customer.setM_PhoneNumber(phoneNumber.trim());
        }
        return formValid;
    }
}
