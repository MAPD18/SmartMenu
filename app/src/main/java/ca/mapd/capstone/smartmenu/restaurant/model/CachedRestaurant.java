package ca.mapd.capstone.smartmenu.restaurant.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class CachedRestaurant {

    @PrimaryKey
    @NonNull
    public String restaurantId;

}
