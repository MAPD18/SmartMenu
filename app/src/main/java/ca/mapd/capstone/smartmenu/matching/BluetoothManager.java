package ca.mapd.capstone.smartmenu.matching;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import ca.mapd.capstone.smartmenu.util.Constants;

public class BluetoothManager implements BluetoothAdapter.LeScanCallback {
    private Context mContext;
    // for "client" mode only
    private Timer mScanTimer;
    private final MyBluetoothGattCallback mMyBluetoothGattCallback;
    private final long CLIENT_RESTART_PERIOD = 10 * 1000;

    private android.bluetooth.BluetoothManager mBluetoothManager;       // BluetoothManager:      added in API level 18
    private BluetoothAdapter mBluetoothAdapter;       // BluetoothAdapter:      added in API level 5
    private Object mBluetoothLeAdvertiser;            // BluetoothLeAdvertiser: added in API level 21
    private Object mAdvertiseCallback;                // AdvertiseCallback:     added in API level 21
    private BluetoothGattServer mBluetoothGattServer; // BluetoothGattServer:   added in API level 18

    public BluetoothManager(Context context) {
        mContext = context;
        mMyBluetoothGattCallback = new MyBluetoothGattCallback(context);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(Constants.TAG, "BLE is not supported.");
            return;
        }

        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        mBluetoothManager = (android.bluetooth.BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager == null) {
            Log.e(Constants.TAG, "Unable to initialize BluetoothManager.");
            return;
        }

        // For API level 18 and above.
        // Checks if Bluetooth is supported on the device.
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(Constants.TAG, "Unable to obtain a BluetoothAdapter.");
            return;
        }

        // For API level 21 and above, get a reference to BluetoothLeAdvertiser through
        // BluetoothAdapter.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
            if (mBluetoothLeAdvertiser == null) {
                Log.e(Constants.TAG, "Unable to obtain a BluetoothLeAdvertiser.");
            }
        }
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        mMyBluetoothGattCallback.processDevice(device, rssi);
    }

    public boolean start(byte[] menuId, boolean startForce, boolean isRestaurantApp) {
        if (!isInitialized()) return false;
        if (!mBluetoothAdapter.isEnabled() && !startForce) return false;
        if (!mBluetoothAdapter.enable()) return false;

        Log.d(Constants.TAG, "start isRestaurantApp: " + isRestaurantApp);
        if (isRestaurantApp)
            startServer(menuId);
        else
            startClient();

        return true;
    }

    public void stop() {
        stopServer();
        stopClient();
    }

    private boolean isInitialized() {
        return isInitializedAsServer() || isInitializedAsClient();
    }

    private boolean isInitializedAsServer() {
        return mBluetoothManager != null && mBluetoothAdapter != null && mBluetoothLeAdvertiser != null;
    }

    private boolean isInitializedAsClient() {
        return mBluetoothManager != null && mBluetoothAdapter != null;
    }

    private void startServer(byte[] menuId) {
        Log.d(Constants.TAG, "startServer");

        if (!isInitializedAsServer()) return;

        if (addServiceToGattServer(menuId))
            startAdvertising();
    }

    private void stopServer() {
        Log.d(Constants.TAG, "stopServer");

        if (!isInitializedAsServer()) return;

        stopAdvertising();
        removeServiceFromGattServer();
    }

    private void startClient() {
        Log.d(Constants.TAG, "startClient");
        final UUID[] uuid = new UUID[]{Constants.SERVICE_UUID.getUuid()};
        if (isInitializedAsClient()) startClientHelper(uuid);
    }

    private void stopClient() {
        Log.d(Constants.TAG, "stopClient");
        if (isInitializedAsClient()) stopClientHelper();
    }

    private void startClientHelper(final UUID[] uuid) {
        if (mScanTimer != null) mScanTimer.cancel();
        else mScanTimer = new Timer();

        mScanTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(Constants.TAG, "startClientHelper : stop-start");

                mBluetoothAdapter.stopLeScan(BluetoothManager.this);
                boolean result = mBluetoothAdapter.startLeScan(uuid, BluetoothManager.this);

                if (!result) {
                    Handler handler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message message) {
                            Toast.makeText(mContext, "Cant start Bluetooth", Toast.LENGTH_LONG).show();
                        }
                    };
                    handler.obtainMessage().sendToTarget();
                }
            }
        }, 0, CLIENT_RESTART_PERIOD);
    }

    private void stopClientHelper() {
        if (mScanTimer != null) mScanTimer.cancel();

        mBluetoothAdapter.stopLeScan(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startAdvertising() {
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .build();

        AdvertiseData data = new AdvertiseData.Builder()
                .addServiceUuid(Constants.SERVICE_UUID)
                .build();

        AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.d(Constants.TAG, "LE Advertise Started.");
            }

            @Override
            public void onStartFailure(int errorCode) {
                if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                    Log.d(Constants.TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
                } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
                    Log.d(Constants.TAG, "Failed to start advertising because no advertising instance is available.");
                } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
                    Log.d(Constants.TAG, "Failed to start advertising as the advertising is already started.");
                } else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
                    Log.d(Constants.TAG, "Operation failed due to an internal error.");
                } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
                    Log.d(Constants.TAG, "This feature is not supported on this platform.");
                } else {
                    Log.d(Constants.TAG, "There was unknown error.");
                }
            }
        };

        ((BluetoothLeAdvertiser) mBluetoothLeAdvertiser).startAdvertising(settings, data, advertiseCallback);
        mAdvertiseCallback = advertiseCallback;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void stopAdvertising() {
        if (mBluetoothLeAdvertiser != null && mAdvertiseCallback != null)
            ((BluetoothLeAdvertiser) mBluetoothLeAdvertiser).stopAdvertising((AdvertiseCallback) mAdvertiseCallback);
    }

    private boolean addServiceToGattServer(final byte[] menuId) {
        boolean result = false;

        if (mBluetoothGattServer == null) {
            mBluetoothGattServer = mBluetoothManager.openGattServer(
                    mContext,
                    new BluetoothGattServerCallback() {
                        @Override
                        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                            if (mBluetoothGattServer != null) {
                                mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, menuId);
                            }
                        }
                    });

            if (mBluetoothGattServer != null) {
                BluetoothGattService bluetoothGattService = mBluetoothGattServer.getService(Constants.SERVICE_UUID.getUuid());
                if (bluetoothGattService == null) {
                    BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                            Constants.CHARACTERISTIC_UUID.getUuid(),
                            BluetoothGattCharacteristic.PROPERTY_READ |
                                    BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_BROADCAST,
                            BluetoothGattCharacteristic.PERMISSION_READ |
                                    BluetoothGattCharacteristic.PERMISSION_WRITE);

                    bluetoothGattService = new BluetoothGattService(Constants.SERVICE_UUID.getUuid(), BluetoothGattService.SERVICE_TYPE_PRIMARY);

                    if (bluetoothGattService.addCharacteristic(characteristic)) {
                        Log.d(Constants.TAG, "gatt_characteristic added successfully");
                        if (mBluetoothGattServer.addService(bluetoothGattService))
                            Log.d(Constants.TAG, "gatt_service added successfully");
                        else Log.d(Constants.TAG, "gatt_service is not added");
                    } else Log.d(Constants.TAG, "gatt_characteristic is not added");
                }

                result = true;
            }
        }

        return result;
    }

    private void removeServiceFromGattServer() {
        if (mBluetoothGattServer != null) {
            BluetoothGattService btGattService = mBluetoothGattServer.getService(Constants.SERVICE_UUID.getUuid());
            if (btGattService != null)
                mBluetoothGattServer.removeService(btGattService);

            mBluetoothGattServer.close();
            mBluetoothGattServer = null;
        }
    }
}