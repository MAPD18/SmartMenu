package ca.mapd.capstone.smartmenu.restaurant;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.activities.AuthAbstractActivity;
import ca.mapd.capstone.smartmenu.activities.LoginActivity;
import ca.mapd.capstone.smartmenu.customer.AboutPageActivity;
import ca.mapd.capstone.smartmenu.matching.MatchingService;
import ca.mapd.capstone.smartmenu.restaurant.activities.RestaurantProfileActivity;

import static ca.mapd.capstone.smartmenu.util.Constants.MY_PREFS;
import static ca.mapd.capstone.smartmenu.util.Constants.MY_PREFS_RESTAURANT_BROADCAST_ON;
import static ca.mapd.capstone.smartmenu.util.Constants.MY_PREFS_RESTAURANT_ID;

public class RestaurantMainActivity extends AuthAbstractActivity {

    private static final int REQUEST_ENABLE_BT = 3456;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 46193;
    private BluetoothAdapter bluetoothAdapter;
    private SharedPreferences sharedPref;
    private EditText restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);

        sharedPref = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        ToggleButton broadcastToggle = findViewById(R.id.broadcastToggle);
        restaurantId = findViewById(R.id.restaurantId);
        restaurantId.setText("item1");

        broadcastToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleBroadcast(isChecked);
                sharedPref.edit().putBoolean(MY_PREFS_RESTAURANT_BROADCAST_ON, isChecked).apply();
            }
        });

        broadcastToggle.setChecked(sharedPref.getBoolean(MY_PREFS_RESTAURANT_BROADCAST_ON, false));
    }

    private void toggleBroadcast(boolean isChecked) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        } else {
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
                if (isChecked) {
                    updateRestaurantId();
                    MatchingService.startMatchingWithBluetooth(this, false);
                } else
                    MatchingService.stopMatchingWithBluetooth(this);
            }
        }
    }

    private void updateRestaurantId() {
        sharedPref.edit().putString(MY_PREFS_RESTAURANT_ID, restaurantId.getText().toString()).apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toggleBroadcast(true);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restaurant_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.about_page:
                startActivity(new Intent(this, AboutPageActivity.class));
                return true;
            case R.id.log_out:
                m_Auth.signOut();
                super.googleSignOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            case R.id.my_profile:
                startActivity(new Intent(this, RestaurantProfileActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
