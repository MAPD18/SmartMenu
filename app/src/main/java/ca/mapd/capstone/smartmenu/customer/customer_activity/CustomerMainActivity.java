package ca.mapd.capstone.smartmenu.customer.customer_activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.activities.AuthAbstractActivity;
import ca.mapd.capstone.smartmenu.customer.adapters.RestaurantRecyclerAdapter;
import ca.mapd.capstone.smartmenu.customer.models.Restaurant;
import ca.mapd.capstone.smartmenu.matching.MatchingService;

public class CustomerMainActivity extends AuthAbstractActivity {
    private ArrayList<Restaurant> m_RestaurantList; /*this holds the list of Restaurants which will be displayed*/
    private HashMap<String, Integer> m_RestaurantKeyMap; //neat little hack to keep track of where our restaurant resides in the restaurantlist
    private RecyclerView m_RecyclerView; // our RecyclerView instance
    private LinearLayoutManager m_LinearLayoutManager; //the LayoutManager used by the RecyclerView
    private RestaurantRecyclerAdapter m_Adapter; // our custom RecyclerAdapter for Restaurant objects
    private DatabaseReference m_BaseRestaurantRef;
    private ArrayList<DatabaseReference> m_RefList = new ArrayList<>();
    private HashMap<String, Integer> m_RefListMap = new HashMap<>();
    private LinearLayout m_LinearLayout;
    private ProgressBar m_ProgressBar;
    private TextView m_StatusMessageView;

    private static final int REQUEST_ENABLE_BT = 3456;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 46193;
    private BluetoothAdapter bluetoothAdapter;
    private MenuBroadcastReceiver menuBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Choose which restaurant you'd like to order from");
        setContentView(R.layout.activity_restaurant_list);


        //init vars
        m_RecyclerView = (RecyclerView) findViewById(R.id.restaurantRecyclerView);
        m_StatusMessageView = findViewById(R.id.statusMessageView);
        m_RecyclerView.setHasFixedSize(true);
        m_LinearLayoutManager = new LinearLayoutManager(this);
        m_RecyclerView.setLayoutManager(m_LinearLayoutManager);
        m_RestaurantList = new ArrayList<>();
        m_RestaurantKeyMap = new HashMap<>();
        m_LinearLayout = (LinearLayout) findViewById(R.id.restaurantListLayout);
        m_ProgressBar = (ProgressBar) findViewById(R.id.loadingBar);
        m_ProgressBar.setIndeterminate(true);

        //Firebase restaurants
        m_BaseRestaurantRef = FirebaseDatabase.getInstance().getReference(Restaurant.RESTAURANT_KEY);

        //set adapter
        m_Adapter = new RestaurantRecyclerAdapter(m_RestaurantList);
        m_RecyclerView.setAdapter(m_Adapter);

        FirebaseUser user = m_Auth.getCurrentUser();
        if (user != null)
            this.setTitle(user.getDisplayName());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        } else
            initBluetooth();

        menuBroadcastReceiver = new MenuBroadcastReceiver();
    }

    private ValueEventListener myListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
            String key = dataSnapshot.getKey();
            if (key != null && restaurant == null) { // Restaurant removed
                Integer position = m_RestaurantKeyMap.get(key);
                if (position != null) {
                    m_RestaurantList.remove((int) position);
                    m_RestaurantKeyMap.remove(key);
                }
            } else if (restaurant != null) {
                restaurant.setId(key);
                Integer position = m_RestaurantKeyMap.get(key);
                if (position == null) {
                    m_RestaurantList.add(restaurant);
                    position = m_RestaurantList.size() - 1;
                    m_RestaurantKeyMap.put(key, position);
                } else {
                    m_RestaurantList.set(position, restaurant);
                }
            }
            m_Adapter.notifyDataSetChanged();
            m_ProgressBar.setVisibility(View.GONE);
            m_LinearLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            m_ProgressBar.setVisibility(View.GONE);
            m_LinearLayout.setVisibility(View.VISIBLE);
        }
    };

    public void onResume(){
        //Firebase data restore
        if (m_RefList.isEmpty()) {
            m_StatusMessageView.setText("No Restaurants Nearby.");
            m_ProgressBar.setVisibility(View.GONE);
        } else {
            m_StatusMessageView.setVisibility(View.GONE);
            m_ProgressBar.setVisibility(View.VISIBLE);
            m_LinearLayout.setVisibility(View.GONE);
            for (DatabaseReference ref : m_RefList) {
                ref.addValueEventListener(myListener);
            }
        }
        IntentFilter filter = new IntentFilter(MenuBroadcastReceiver.MENU_INTENT_FILTER);
        registerReceiver(menuBroadcastReceiver, filter);
        super.onResume();
    }

    public void onPause(){
        for (DatabaseReference ref : m_RefList) {
            ref.removeEventListener(myListener);
        }
        m_RestaurantList.clear();
        m_RestaurantKeyMap.clear();
        unregisterReceiver(menuBroadcastReceiver);
        super.onPause();
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
            String restaurantId = intent.getStringExtra(KEY_MENU_ID);
            Integer position = m_RefListMap.get(restaurantId);
            if (position == null) {
                Log.d("TEST", "New MenuId discovered: " + restaurantId);
                m_RefList.add(m_BaseRestaurantRef.child(restaurantId));
                position = m_RefList.size() - 1;
                m_RefListMap.put(restaurantId, position);
            }
        }
    }
}