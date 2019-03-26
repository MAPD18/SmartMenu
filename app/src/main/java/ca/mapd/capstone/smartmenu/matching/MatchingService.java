package ca.mapd.capstone.smartmenu.matching;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import java.nio.charset.Charset;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.util.Constants;

public class MatchingService extends Service {
    private BluetoothManager mBluetoothManager;

    public static final String USE_BLUETOOTH = "match_use_bluetooth";
    public static final String START_BLUETOOTH_FORCE = "start_bluetooth_force";
    public static final String STOP_SERVICE = "stop_service";
    public static boolean isRestaurantApp = false;
    public static String restaurantId = "";

    public static void startMatchingWithBluetooth(Context context, boolean startBluetoothForce) {
        isRestaurantApp = context.getResources().getBoolean(R.bool.is_restaurant_app);
        Log.d(Constants.TAG, "startMatchingWithBluetooth: " +isRestaurantApp);
        Intent intent = new Intent(context, MatchingService.class);
        intent.putExtra(MatchingService.USE_BLUETOOTH, true);
        intent.putExtra(MatchingService.START_BLUETOOTH_FORCE, startBluetoothForce);
        context.startService(intent);
    }

    public static void stopMatchingWithBluetooth(Context context) {
        Log.d(Constants.TAG, "stopMatchingWithBluetooth: " +isRestaurantApp);
        Intent intent = new Intent(context, MatchingService.class);
        context.stopService(intent);
    }

    @Override
    public void onCreate() {
        Log.d(Constants.TAG, "MatchingService: onCreate");
        isRestaurantApp = getApplicationContext().getResources().getBoolean(R.bool.is_restaurant_app);
        restaurantId = getApplicationContext().getSharedPreferences(Constants.MY_PREFS, Context.MODE_PRIVATE).getString(Constants.MY_PREFS_RESTAURANT_ID, "");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Constants.TAG, "MatchingService: onStartCommand");

        if (intent.hasExtra(USE_BLUETOOTH)) {
            boolean useBluetooth = intent.getBooleanExtra(USE_BLUETOOTH, false);
            boolean startBluetoothForce = intent.getBooleanExtra(START_BLUETOOTH_FORCE, false);

            if (useBluetooth && mBluetoothManager == null) {
                // On Android, the default charset is UTF-8.
                byte[] menuIdBytes = restaurantId.getBytes();

                mBluetoothManager = new BluetoothManager(this);
                boolean result = mBluetoothManager.start(menuIdBytes, startBluetoothForce, isRestaurantApp);
                if (!result) {
                    mBluetoothManager.stop();
                    mBluetoothManager = null;
                }
            } else if (!useBluetooth && mBluetoothManager != null) {
                mBluetoothManager.stop();
                mBluetoothManager = null;
            }
        }

        if (intent.getBooleanExtra(STOP_SERVICE, false)) {
            stopForeground(true);
            stopSelf();
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.d(Constants.TAG, "MatchingService: onDestroy");

        if (mBluetoothManager != null) mBluetoothManager.stop();
        mBluetoothManager = null;

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(Constants.TAG, "MatchingService: onBind");
        return null;
    }
}