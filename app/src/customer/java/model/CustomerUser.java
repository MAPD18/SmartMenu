package model;

import javax.inject.Inject;

import ca.mapd.capstone.smartmenu.interfaces.IUser;


public class CustomerUser implements IUser {

    @Inject
    public CustomerUser() {
    }

    @Override
    public String getFlavor() {
        return "CUSTOMER";
    }
}
