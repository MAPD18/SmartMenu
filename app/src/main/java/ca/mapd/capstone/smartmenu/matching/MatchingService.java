package ca.mapd.capstone.smartmenu.matching;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.nio.charset.Charset;

import ca.mapd.capstone.smartmenu.R;

public class MatchingService extends Service {
    private BluetoothManager mBluetoothManager;

    public static final String USE_BLUETOOTH = "match_use_bluetooth";
    public static final String START_BLUETOOTH_FORCE = "start_bluetooth_force";
    public static final String STOP_SERVICE = "stop_service";
    public static boolean isRestaurantApp = false;

    public static void startMatchingWithBluetooth(Context context, boolean startBluetoothForce) {
        isRestaurantApp = context.getResources().getBoolean(R.bool.is_restaurant_app);
        Intent intent = new Intent(context, MatchingService.class);
        intent.putExtra(MatchingService.USE_BLUETOOTH, true);
        intent.putExtra(MatchingService.START_BLUETOOTH_FORCE, startBluetoothForce);
        context.startService(intent);
    }

    public static void stopMatchingWithBluetooth(Context context) {
        isRestaurantApp = context.getResources().getBoolean(R.bool.is_restaurant_app);
        Intent intent = new Intent(context, MatchingService.class);
        context.stopService(intent);
    }

    @Override
    public void onCreate() {
        Log.d(Constants.TAG, "MatchingService: onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Constants.TAG, "MatchingService: onStartCommand");

        if (intent.hasExtra(USE_BLUETOOTH)) {
            boolean useBluetooth = intent.getBooleanExtra(USE_BLUETOOTH, false);
            boolean startBluetoothForce = intent.getBooleanExtra(START_BLUETOOTH_FORCE, false);

            if (useBluetooth && mBluetoothManager == null) {
                String menuId = "Menu Id: 174691283";
                // On Android, the default charset is UTF-8.
                byte[] menuIdBytes = menuId.getBytes();

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