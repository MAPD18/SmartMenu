package ca.mapd.capstone.smartmenu.restaurant;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.activities.AuthAbstractActivity;
import ca.mapd.capstone.smartmenu.activities.LoginActivity;
import ca.mapd.capstone.smartmenu.customer.AboutPageActivity;
import ca.mapd.capstone.smartmenu.customer.adapters.MenuItemRecyclerAdapter;
import ca.mapd.capstone.smartmenu.customer.adapters.SwipeToDeleteCallback;
import ca.mapd.capstone.smartmenu.customer.models.MenuItem;
import ca.mapd.capstone.smartmenu.matching.MatchingService;
import ca.mapd.capstone.smartmenu.restaurant.activities.AddMenuItemActivity;
import ca.mapd.capstone.smartmenu.restaurant.activities.RestaurantProfileActivity;

import static ca.mapd.capstone.smartmenu.restaurant.activities.AddMenuItemActivity.KEY_IS_EDITING;
import static ca.mapd.capstone.smartmenu.restaurant.activities.AddMenuItemActivity.KEY_NEW_MENU_ITEM;
import static ca.mapd.capstone.smartmenu.restaurant.activities.AddMenuItemActivity.KEY_NEW_MENU_ITEM_KEY;
import static ca.mapd.capstone.smartmenu.util.Constants.MY_PREFS;
import static ca.mapd.capstone.smartmenu.util.Constants.MY_PREFS_RESTAURANT_BROADCAST_ON;
import static ca.mapd.capstone.smartmenu.util.Constants.MY_PREFS_RESTAURANT_ID;

public class RestaurantMainActivity extends AuthAbstractActivity implements MenuItemRecyclerAdapter.MenuItemListListener {

    private static final int REQUEST_ENABLE_BT = 3456;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 46193;
    private BluetoothAdapter bluetoothAdapter;
    private SharedPreferences sharedPref;
    private RecyclerView menuItemListView;
    private MenuItemRecyclerAdapter adapter;
    private ArrayList<MenuItem> menuItemList = new ArrayList<>();
    private HashMap<String, Integer> menuItemKeyMap = new HashMap<>();
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private ConstraintLayout mainLayout;

    public static final int REQUEST_ADD_MENU_ITEM = 9138;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);

        sharedPref = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        mainLayout = findViewById(R.id.mainLayout);
        progressBar = findViewById(R.id.loadingBar);
        adapter = new MenuItemRecyclerAdapter(menuItemList, this);
        menuItemListView = findViewById(R.id.menuItemListView);
        menuItemListView.setAdapter(adapter);
        menuItemListView.setLayoutManager(new LinearLayoutManager(this));
        menuItemListView.setHasFixedSize(true);

        ToggleButton broadcastToggle = findViewById(R.id.broadcastToggle);
        broadcastToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleBroadcast(isChecked);
                sharedPref.edit().putBoolean(MY_PREFS_RESTAURANT_BROADCAST_ON, isChecked).apply();
            }
        });

        broadcastToggle.setChecked(sharedPref.getBoolean(MY_PREFS_RESTAURANT_BROADCAST_ON, false));
        FloatingActionButton addMenuItemFAB =  findViewById(R.id.addMenuItemFAB);
        addMenuItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAddMenuItemDialog();
            }
        });

        databaseReference= FirebaseDatabase.getInstance().getReference("MENU").child(m_Auth.getCurrentUser().getEmail().replace(".", ""));
        databaseReference.addChildEventListener(myListener);
        enableSwipeToDelete();
    }

    private void displayAddMenuItemDialog() {
        startActivityForResult(new Intent(this, AddMenuItemActivity.class), REQUEST_ADD_MENU_ITEM);
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
        sharedPref.edit().putString(MY_PREFS_RESTAURANT_ID, m_Auth.getCurrentUser().getEmail().replace(".", "")).apply();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_MENU_ITEM && resultCode == RESULT_OK) {
            MenuItem item = data.getParcelableExtra(KEY_NEW_MENU_ITEM);
            boolean isEditing = data.getBooleanExtra(KEY_IS_EDITING, false);
            if (isEditing) {
                String key = data.getStringExtra(KEY_NEW_MENU_ITEM_KEY);
                saveMenuItem(item, key);
            } else {
                addNewMenuItem(item);
            }
        }
    }

    private void addNewMenuItem(MenuItem newItem) {
        databaseReference.push().setValue(newItem);
    }

    private void saveMenuItem(MenuItem editedItem, String key) {
        databaseReference.child(key).setValue(editedItem);
    }

    private void removeMenuItem(int position) {
        String key = null;
        for(Map.Entry<String, Integer> entry : menuItemKeyMap.entrySet()) {
            if (entry.getValue() == position) {
                key = entry.getKey();
                break;
            }
        }
        if (key != null)
            databaseReference.child(key).removeValue();
    }

    private ChildEventListener myListener = new ChildEventListener() {
        // listener populates the Menu list with data as it comes and goes
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            // when a menu has been added into the database
            MenuItem menu = dataSnapshot.getValue(MenuItem.class);
            String key = dataSnapshot.getKey();

            menuItemList.add(menu);
            int position = menuItemList.size() - 1;
            menuItemKeyMap.put(key, position);
            adapter.updateMenuItem(menu, position);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            //shouldn't be fired because Order's cannot change once it has been lodged
            // it can be removed though
            MenuItem menu = dataSnapshot.getValue(MenuItem.class);
            String key = dataSnapshot.getKey();

            Integer position = menuItemKeyMap.get(key);
            if (position != null) {
                adapter.updateMenuItem(menu, position);
            }

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            // when a Menu has been removed from the database
            Log.d("Menu", "Removed");
            Integer index = menuItemKeyMap.get(dataSnapshot.getKey());
            menuItemList.remove(index.intValue());
            menuItemKeyMap.remove(dataSnapshot.getKey());
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // on connection failure
            String toastStr = "Failed to load the list of Menus";
            Toast toast = Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_SHORT);
            toast.show();
        }
    };

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
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
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

    @Override
    public void onMenuItemSelected(MenuItem item, int position) {
        Intent intent = new Intent(this, AddMenuItemActivity.class);
        intent.putExtra(KEY_NEW_MENU_ITEM, item);
        String key = null;
        for(Map.Entry<String, Integer> entry : menuItemKeyMap.entrySet()) {
            if (entry.getValue() == position) {
                key = entry.getKey();
                break;
            }
        }
        if (key != null)
            intent.putExtra(KEY_NEW_MENU_ITEM_KEY, key);
        startActivityForResult(intent, REQUEST_ADD_MENU_ITEM);
    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                removeMenuItem(position);
                Snackbar snackbar = Snackbar
                        .make(mainLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(menuItemListView);
    }


}
