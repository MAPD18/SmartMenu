package di;

import android.app.Application;

import ca.mapd.capstone.smartmenu.interfaces.IUser;
import model.RestaurantUser;
import toothpick.smoothie.module.SmoothieApplicationModule;

public class MyAppModule extends SmoothieApplicationModule {

    public MyAppModule(Application application) {
        super(application);
        bind(IUser.class).to(RestaurantUser.class);
    }

}
