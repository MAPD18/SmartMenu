package ca.mapd.capstone.smartmenu.customer.customer_activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.customer.adapters.RestaurantRecyclerAdapter;
import ca.mapd.capstone.smartmenu.customer.models.Restaurant;

public class RestaurantListActivity extends AppCompatActivity {
    private ArrayList<Restaurant> m_RestaurantList; /*this holds the list of Restaurants which will be displayed*/
    private HashMap<String, Integer> m_RestaurantKeyMap; //neat little hack to keep track of where our restaurant resides in the restaurantlist
    private RecyclerView m_RecyclerView; // our RecyclerView instance
    private LinearLayoutManager m_LinearLayoutManager; //the LayoutManager used by the RecyclerView
    private RestaurantRecyclerAdapter m_Adapter; // our custom RecyclerAdapter for Restaurant objects
    private DatabaseReference m_RestaurantRef;
    private DatabaseReference m_RestaurantRef2;
    private LinearLayout m_LinearLayout;
    private ProgressBar m_ProgressBar;
    private ArrayList<String> m_BlutoothIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Choose which restaurant you'd like to order from");
        setContentView(R.layout.activity_restaurant_list);


        //init vars
        m_RecyclerView = (RecyclerView) findViewById(R.id.restaurantRecyclerView);
        m_RecyclerView.setHasFixedSize(true);
        m_LinearLayoutManager = new LinearLayoutManager(this);
        m_RecyclerView.setLayoutManager(m_LinearLayoutManager);
        m_RestaurantList = new ArrayList<>();
        m_RestaurantKeyMap = new HashMap<>();
        m_LinearLayout = (LinearLayout) findViewById(R.id.restaurantListLayout);
        m_ProgressBar = (ProgressBar) findViewById(R.id.loadingBar);
        m_ProgressBar.setIndeterminate(true);

        //Get Restaurant IDs sent via Bluetooth from previous activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            m_BlutoothIDs = bundle.getStringArrayList("RESTAURANT_ID_LIST");
        }
        if (m_BlutoothIDs == null) {
            m_BlutoothIDs = new ArrayList<>();
        }

        //Firebase restaurants
        m_RestaurantRef = FirebaseDatabase.getInstance().getReference(Restaurant.RESTAURANT_KEY).child("ajdsb328udbaisd97udb192uwdb");
        m_RestaurantRef2 = FirebaseDatabase.getInstance().getReference(Restaurant.RESTAURANT_KEY).child("item1");

        //set adapter
        m_Adapter = new RestaurantRecyclerAdapter(m_RestaurantList);
        m_RecyclerView.setAdapter(m_Adapter);

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
                }

                m_ProgressBar.setVisibility(View.GONE);
                m_LinearLayout.setVisibility(View.VISIBLE);
            }
            m_Adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void onResume(){
        //Firebase data restore
        //m_RestaurantRef.addChildEventListener(m_RestaurantRefCEL);
        m_RestaurantRef.addValueEventListener(myListener);
        m_RestaurantRef2.addValueEventListener(myListener);
        super.onResume();
    }

    public void onPause(){
        m_RestaurantRef.removeEventListener(myListener);
        m_RestaurantRef2.removeEventListener(myListener);
        m_RestaurantList.clear();
        m_RestaurantKeyMap.clear();
        super.onPause();
    }
}
