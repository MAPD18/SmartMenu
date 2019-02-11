package ca.mapd.capstone.smartmenu;

import android.app.Application;

import di.MyAppModule;
import toothpick.Scope;
import toothpick.Toothpick;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Scope scope = Toothpick.openScope(this);
        scope.installModules(new MyAppModule(this));
        Toothpick.inject(this, scope);
    }
}
