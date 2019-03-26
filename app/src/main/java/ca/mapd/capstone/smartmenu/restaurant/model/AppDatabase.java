package ca.mapd.capstone.smartmenu.restaurant.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {CachedRestaurant.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CachedRestaurantDao cachedRestaurantDao();

    private static volatile AppDatabase INSTANCE;

    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "smart_menu_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
