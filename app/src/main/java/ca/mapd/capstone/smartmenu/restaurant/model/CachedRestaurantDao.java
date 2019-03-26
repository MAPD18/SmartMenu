package ca.mapd.capstone.smartmenu.restaurant.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CachedRestaurantDao {

    @Insert
    public void insert(CachedRestaurant cachedRestaurant);

    @Query("SELECT * from CachedRestaurant")
    public List<CachedRestaurant> getALl();

    @Query("DELETE from CachedRestaurant")
    public void deleteAll();

}
