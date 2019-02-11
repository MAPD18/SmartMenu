package model;

import javax.inject.Inject;

import ca.mapd.capstone.smartmenu.interfaces.IUser;

public class RestaurantUser implements IUser {

    @Inject
    public RestaurantUser() {
    }

    @Override
    public String getFlavor() {
        return "RESTAURANT";
    }
}
