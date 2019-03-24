package ca.mapd.capstone.smartmenu.customer.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.LocaleData;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.customer.customer_activity.MenuListActivity;
import ca.mapd.capstone.smartmenu.customer.models.MenuItem;
import ca.mapd.capstone.smartmenu.customer.models.Restaurant;

import static ca.mapd.capstone.smartmenu.customer.customer_activity.MenuListActivity.KEY_RESTAURANT_ID;
import static ca.mapd.capstone.smartmenu.customer.customer_activity.MenuListActivity.KEY_RESTAURANT_NAME;


public class RestaurantRecyclerAdapter extends RecyclerView.Adapter<RestaurantRecyclerAdapter.RestaurantHolder> {
    private ArrayList<Restaurant> m_RestaurantList; // the complete data set of Restaurants


    // recycler adapter for restaurants

    public static class RestaurantHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CardView m_cRestaurantCardView;
        private TextView m_cRestaurantNameView;
        private TextView m_cRestaurantAddressView;
        private Restaurant m_cRestaurant;

        public RestaurantHolder(View v){
            super(v);
            m_cRestaurantCardView = (CardView) v.findViewById(R.id.cardview);
            m_cRestaurantNameView = (TextView) v.findViewById(R.id.recyclerRestaurantShortTextView);
            m_cRestaurantAddressView = (TextView) v.findViewById(R.id.recyclerAddressShortTextView);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // when a RestaurantHolder (e.g. a row or a cell in the RecyclerView) is clicked
            // view that Restaurant's detail through ViewRestaurantActivity
            Context context = itemView.getContext();
            Intent intent = new Intent(itemView.getContext(), MenuListActivity.class);
            intent.putExtra(KEY_RESTAURANT_ID, m_cRestaurant.m_key);
            intent.putExtra(KEY_RESTAURANT_NAME, m_cRestaurant.getName());
            itemView.getContext().startActivity(intent);

        }

        public void bindRestaurant(Restaurant Restaurant){
            // bind a Restaurant object (and thus its data) into the Restaurant holder
            // the binded Restaurant's detail will be displayed in the cell/row of the RecyclerView
            // :param Restaurant: a Restaurant which will be displayed on the RecyclerView
            m_cRestaurant = Restaurant;
            if (Restaurant.isAvailable)
                m_cRestaurantCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary));
            else
                m_cRestaurantCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorGray));
            m_cRestaurantNameView.setText(Restaurant.m_Name);
            m_cRestaurantAddressView.setText(Restaurant.m_Address);

        }
    }

    public RestaurantRecyclerAdapter(ArrayList<Restaurant> RestaurantList){
        // initialize the adapter with its data set
        // :param RestaurantList: the data source for the adapter
        m_RestaurantList = RestaurantList;
    }

    @Override
    public RestaurantRecyclerAdapter.RestaurantHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the recyclerview_item_row layout and pass it onto the RestaurantHolder
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_restaurant_row, parent, false);
        return new RestaurantHolder(inflatedView);
    }



    @Override
    public void onBindViewHolder(RestaurantRecyclerAdapter.RestaurantHolder holder, int position) {
        // called when the system wants to bind a Restaurant to a particular view
        Restaurant Restaurant = m_RestaurantList.get(position);
        holder.bindRestaurant(Restaurant);
    }

    @Override
    public int getItemCount() {
        // indicates how many items will be displayed on the RecyclerView
        return m_RestaurantList.size();
    }

    public void updateRestaurant(Restaurant item, int position) {
        m_RestaurantList.set(position, item);
        notifyItemChanged(position);
    }

    public void removeRestaurant(int position) {
        m_RestaurantList.remove(position);
        notifyItemChanged(position);
    }

}
