package ca.mapd.capstone.smartmenu.customer.customer_activity;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.customer.adapters.RestaurantRecyclerAdapter;
import ca.mapd.capstone.smartmenu.customer.models.MenuItem;
import ca.mapd.capstone.smartmenu.customer.models.Restaurant;

public class RestaurantListActivity extends AppCompatActivity {
    private ArrayList<Restaurant> m_RestaurantList; /*this holds the list of Restaurants which will be displayed*/
    private HashMap<String, Integer> m_RestaurantKeyMap; //neat little hack to keep track of where our restaurant resides in the restaurantlist
    private RecyclerView m_RecyclerView; // our RecyclerView instance
    private LinearLayoutManager m_LinearLayoutManager; //the LayoutManager used by the RecyclerView
    private RestaurantRecyclerAdapter m_Adapter; // our custom RecyclerAdapter for Restaurant objects
    private DatabaseReference m_RestaurantRef;
    private ChildEventListener m_RestaurantRefCEL;
    private LinearLayout m_LinearLayout;
    private ProgressBar m_ProgressBar;

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

        //Firebase restaurants
        m_RestaurantRef = FirebaseDatabase.getInstance().getReference(Restaurant.RESTAURANT_KEY);
        m_RestaurantRefCEL = new ChildEventListener() {
            // listener populates the restaurant list with data as it comes and goes
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // when a restaurant has been added into the database
                m_RestaurantList.add(dataSnapshot.getValue(Restaurant.class));
                m_RestaurantKeyMap.put(dataSnapshot.getKey(), m_RestaurantList.size() - 1);
                m_Adapter.notifyDataSetChanged();
                m_ProgressBar.setVisibility(View.GONE);
                m_LinearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //shouldn't be fired because Order's cannot change once it has been lodged
                // it can be removed though
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // when a restaurant has been removed from the database
                Log.d("Restaurant", "Removed");
                Integer index = m_RestaurantKeyMap.get(dataSnapshot.getKey());
                m_RestaurantList.remove(index.intValue());
                m_RestaurantKeyMap.remove(dataSnapshot.getKey());
                m_Adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // on connection failure
                String toastStr = "Failed to load the list of restaurants";
                Toast toast = Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_SHORT);
                toast.show();
            }
        };

        //set adapter
        m_Adapter = new RestaurantRecyclerAdapter(m_RestaurantList);
        m_RecyclerView.setAdapter(m_Adapter);



    }

    public void onResume(){
        //Firebase data restore
        m_RestaurantRef.addChildEventListener(m_RestaurantRefCEL);
        super.onResume();
    }

    public void onPause(){
        m_RestaurantRef.removeEventListener(m_RestaurantRefCEL);
        m_RestaurantList.clear();
        m_RestaurantKeyMap.clear();
        super.onPause();
    }
}
