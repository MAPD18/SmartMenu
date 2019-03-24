package ca.mapd.capstone.smartmenu.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ca.mapd.capstone.smartmenu.restaurant.activities.RestaurantProfileActivity;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.customer.customer_activity.RestaurantListActivity;
import ca.mapd.capstone.smartmenu.customer.models.Restaurant;
import ca.mapd.capstone.smartmenu.matching.MatchingService;

public class MainActivity extends AuthAbstractActivity {

    private static final int REQUEST_ENABLE_BT = 3456;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 46193;
    private BluetoothAdapter bluetoothAdapter;
    private MenuBroadcastReceiver menuBroadcastReceiver;
    TextView welcomeMessage;
    Button seeRestaurantListButton;
    Button myProfileButton;
    private ArrayList<String> m_RestaurantIds; /*this holds the list of Restaurant Ids which will be displayed*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeMessage = findViewById(R.id.welcomeMessage);
        myProfileButton = findViewById(R.id.myProfileButton);
        seeRestaurantListButton = findViewById(R.id.seeRestaurantListButton);
        boolean isRestaurantApp = getResources().getBoolean(R.bool.is_restaurant_app);
        myProfileButton.setVisibility(isRestaurantApp ? View.VISIBLE : View.GONE);
        seeRestaurantListButton.setVisibility(isRestaurantApp ? View.GONE : View.VISIBLE);

        final FirebaseUser user = m_Auth.getCurrentUser();
        if (user != null)
            welcomeMessage.setText(getString(R.string.welcome_text_on_login, user.getDisplayName()));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        } else
            initBluetooth();


        menuBroadcastReceiver = new MenuBroadcastReceiver();

        m_RestaurantIds = new ArrayList<>();

        myProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RestaurantProfileActivity.class);
                intent.putExtra("userEmail", user.getEmail());
                startActivity(intent);
            }
        });

        seeRestaurantListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RestaurantListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("RESTAURANT_ID_LIST", m_RestaurantIds);
                startActivity(intent);
            }
        });

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
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(MenuBroadcastReceiver.MENU_INTENT_FILTER);
        registerReceiver(menuBroadcastReceiver, filter);
    }

    @Override
    public void onPause() {
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
            Log.d("TEST", "MenuId discovered: " + intent.getStringExtra(KEY_MENU_ID));
            m_RestaurantIds.add(intent.getStringExtra(KEY_MENU_ID));
        }
    }
}
