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

import java.util.ArrayList;
import java.util.HashMap;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.customer.adapters.MenuItemRecyclerAdapter;
import ca.mapd.capstone.smartmenu.customer.models.MenuItem;

public class MenuListActivity extends AppCompatActivity {
    private ArrayList<MenuItem> m_MenuList; /*this holds the list of Menus which will be displayed*/
    private HashMap<String, Integer> m_MenuKeyMap; //neat little hack to keep track of where our Menu resides in the Menulist
    private RecyclerView m_RecyclerView; // our RecyclerView instance
    private LinearLayoutManager m_LinearLayoutManager; //the LayoutManager used by the RecyclerView
    private MenuItemRecyclerAdapter m_Adapter; // our custom RecyclerAdapter for Menu objects
    private DatabaseReference m_MenuRef;
    private ChildEventListener m_MenuRefCEL;
    private LinearLayout m_LinearLayout;
    private ProgressBar m_ProgressBar;
    private String m_RestaurantID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Choose which Menu you'd like to order from");
        setContentView(R.layout.activity_menu_list);


        //init vars
        m_RecyclerView = (RecyclerView) findViewById(R.id.MenuRecyclerView);
        m_RecyclerView.setHasFixedSize(true);
        m_LinearLayoutManager = new LinearLayoutManager(this);
        m_RecyclerView.setLayoutManager(m_LinearLayoutManager);
        m_MenuList = new ArrayList<>();
        m_MenuKeyMap = new HashMap<>();
        m_LinearLayout = (LinearLayout) findViewById(R.id.MenuListLayout);
        m_ProgressBar = (ProgressBar) findViewById(R.id.loadingBar);
        m_ProgressBar.setIndeterminate(true);

        //get Restaurant ID from previous activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            m_RestaurantID = bundle.getString("RESTAURANT_ID", "");
        }

        //Firebase Menus
        m_MenuRef = FirebaseDatabase.getInstance().getReference("MENU").child(m_RestaurantID);
        m_MenuRefCEL = new ChildEventListener() {
            // listener populates the Menu list with data as it comes and goes
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                // when a menu has been added into the database
                MenuItem menu = dataSnapshot.getValue(MenuItem.class);
                String key = dataSnapshot.getKey();
                
                m_MenuList.add(menu);
                m_MenuKeyMap.put(key, m_MenuList.size() - 1);

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
                // when a Menu has been removed from the database
                Log.d("Menu", "Removed");
                Integer index = m_MenuKeyMap.get(dataSnapshot.getKey());
                m_MenuList.remove(index.intValue());
                m_MenuKeyMap.remove(dataSnapshot.getKey());
                m_Adapter.notifyDataSetChanged();
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



        //set adapter
        m_Adapter = new MenuItemRecyclerAdapter(m_MenuList);
        m_RecyclerView.setAdapter(m_Adapter);



    }

    public void onResume(){
        //Firebase data restore
        m_MenuRef.addChildEventListener(m_MenuRefCEL);
        super.onResume();
    }

    public void onPause(){
        m_MenuRef.removeEventListener(m_MenuRefCEL);
        m_MenuList.clear();
        m_MenuKeyMap.clear();
        super.onPause();
    }
}
