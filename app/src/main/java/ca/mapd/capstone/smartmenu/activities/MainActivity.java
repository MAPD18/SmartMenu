package ca.mapd.capstone.smartmenu.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.interfaces.IUser;
import ca.mapd.capstone.smartmenu.matching.MatchingService;
import toothpick.Toothpick;

public class MainActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 234;
    private static final int REQUEST_ENABLE_BT = 3456;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 46193;
    private TextView hello;
    private BluetoothAdapter bluetoothAdapter;
    private MenuBroadcastReceiver menuBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toothpick.inject(this, Toothpick.openScopes(getApplication(), this));


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        } else
            initBluetooth();


        menuBroadcastReceiver = new MenuBroadcastReceiver();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        hello = findViewById(R.id.hello);
        hello.setText("Hello, " + user.getDisplayName());

    }

    private void initBluetooth() {
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            MatchingService.startMatchingWithBluetooth(this, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(MenuBroadcastReceiver.MENU_INTENT_FILTER);
        registerReceiver(menuBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(menuBroadcastReceiver);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initBluetooth();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                }
            }
        }
    }

    public class MenuBroadcastReceiver extends BroadcastReceiver {

        public static final String KEY_MENU_ID = "KEY_MENU_ID";
        public static final String MENU_INTENT_FILTER = "MENU_INTENT_FILTER";

        @Override
        public void onReceive(Context context, Intent intent) {
            String currentText = hello.getText().toString();
            hello.setText(currentText
                    .concat("\n")
                    .concat("MenuId discovered: ")
                    .concat(intent.getStringExtra(KEY_MENU_ID)));
        }
    }

}
