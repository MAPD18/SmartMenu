package ca.mapd.capstone.smartmenu.matching;

import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;

import java.util.List;

import ca.mapd.capstone.smartmenu.customer.customer_activity.CustomerMainActivity;
import ca.mapd.capstone.smartmenu.util.Constants;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyBluetoothGattCallback extends BluetoothGattCallback {
    private final Context mContext;

    private final String SUBTAG = "MyBTGattCallback";
    private SparseBooleanArray deviceProcessed = new SparseBooleanArray();
    private SparseBooleanArray deviceServiceDiscovered = new SparseBooleanArray();
    private SparseBooleanArray deviceProcessCompleted = new SparseBooleanArray();

    private NotificationManager notificationMgr;
    private NotificationDecorator notificationDecorator;


    public MyBluetoothGattCallback(Context context) {
        mContext = context;
        notificationMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationDecorator = new NotificationDecorator(context, notificationMgr);
    }

    // BluetoothGattCallback
    @Override
    public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int status, int newState) {
        Log.d(Constants.TAG + SUBTAG,
                /*     */ "onConnectionStateChange received: status = " + String.valueOf(status)
                        + " state = " + String.valueOf(newState));

        boolean discoveryHasBeenStarted = false;
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            deviceProcessed.append(bluetoothGatt.getDevice().getAddress().hashCode(), true);
            if (deviceProcessCompleted.get(bluetoothGatt.getDevice().getAddress().hashCode())) return;
            discoveryHasBeenStarted = bluetoothGatt.discoverServices();
        }

        if (!discoveryHasBeenStarted) {
            connectGattInMainLoop(bluetoothGatt.getDevice());

            Log.d(Constants.TAG + SUBTAG, "onConnectionStateChange bluetoothGatt.close");
            bluetoothGatt.close();
        }
    }

    public void processDevice(final BluetoothDevice device, int rssi) {
        if (device == null) {
            Log.d(Constants.TAG + SUBTAG, "Device not found.  Unable to connect.");
            return;
        }

        if (TextUtils.isEmpty(device.getAddress())) {
            Log.d(Constants.TAG + SUBTAG, "nUnspecified address.");
            return;
        }

        if (deviceProcessCompleted.get(device.getAddress().hashCode())) return;
        if (deviceProcessed.get(device.getAddress().hashCode())) return;

        connectGattInMainLoop(device);
    }

    private void connectGattInMainLoop(final BluetoothDevice device) {
        if (device == null) return;

        Handler handler = new Handler(mContext.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // autoConnect = false - directly connect to the device
                Log.d(Constants.TAG + SUBTAG, "connectGattInMainLoop device.connectGatt");
                device.connectGatt(mContext, false, MyBluetoothGattCallback.this);
            }
        }, 500);
    }

    // BluetoothGattCallback
    @Override
    public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int status) {
        Log.d(Constants.TAG + SUBTAG, "onServicesDiscovered received: " + String.valueOf(status));
        if (status == BluetoothGatt.GATT_SUCCESS) {
            processServices(bluetoothGatt);
        } else {
            connectGattInMainLoop(bluetoothGatt.getDevice());

            Log.d(Constants.TAG + SUBTAG, "onServicesDiscovered bluetoothGatt.close");
            bluetoothGatt.close();
        }
    }

    private void processServices(BluetoothGatt bluetoothGatt) {
        List<BluetoothGattService> gattServices = bluetoothGatt.getServices();

        if (gattServices == null || gattServices.isEmpty()) return;

        for (BluetoothGattService gattService : gattServices) {
            String uuidService = gattService.getUuid().toString().toUpperCase();
            Log.d(Constants.TAG + SUBTAG, "uuidService: " + uuidService);

            if (Constants.SERVICE_UUID.toString().toUpperCase().equals(uuidService)) {
                Log.d(Constants.TAG + SUBTAG, "processService: " + uuidService);
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    String uuidCharacteristic = gattCharacteristic.getUuid().toString().toUpperCase();
                    if (Constants.CHARACTERISTIC_UUID.toString().toUpperCase().equals(uuidCharacteristic)) {
                        Log.d(Constants.TAG + SUBTAG, "readCharacteristic: " + uuidCharacteristic);
                        deviceServiceDiscovered.append(bluetoothGatt.getDevice().getAddress().hashCode(), true);
                        bluetoothGatt.readCharacteristic(gattCharacteristic);
                    }
                }
            }
        }
    }

    // BluetoothGattCallback
    @Override
    public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.d(Constants.TAG + SUBTAG, "onCharacteristicRead received: " + String.valueOf(status));
        if (status == BluetoothGatt.GATT_SUCCESS) {
            processCharacteristic(bluetoothGatt, characteristic);
        } else {
            connectGattInMainLoop(bluetoothGatt.getDevice());

            Log.d(Constants.TAG + SUBTAG, "onCharacteristicRead bluetoothGatt.close");
            bluetoothGatt.close();
        }
    }

    // BluetoothGattCallback
    @Override
    public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic characteristic) {
        Log.d(Constants.TAG + SUBTAG, "onCharacteristicChanged");
        processCharacteristic(bluetoothGatt, characteristic);
    }

    private void processCharacteristic(final BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic characteristic) {
        // ...as early as possible...
        Log.d(Constants.TAG + SUBTAG, "processCharacteristic bluetoothGatt.close");
        bluetoothGatt.close();

        // For all other profiles, writes the data formatted in HEX.
        byte[] data = characteristic.getValue();

        if (data != null && data.length > 0) {
            String menuId = new String(data);
            Log.d(Constants.TAG, "Characteristics: " + menuId);
            Intent intent = new Intent(CustomerMainActivity.MenuBroadcastReceiver.MENU_INTENT_FILTER);
            intent.putExtra(CustomerMainActivity.MenuBroadcastReceiver.KEY_MENU_ID, menuId);
            mContext.sendBroadcast(intent);
            deviceProcessCompleted.append(bluetoothGatt.getDevice().getAddress().hashCode(), true);
            notificationDecorator.displaySimpleNotification("New Menu Discovered nearby!");
        }
    }

}
