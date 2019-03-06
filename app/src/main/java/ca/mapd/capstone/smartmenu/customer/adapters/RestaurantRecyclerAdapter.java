package ca.mapd.capstone.smartmenu.customer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.customer.models.Restaurant;



public class RestaurantRecyclerAdapter extends RecyclerView.Adapter<RestaurantRecyclerAdapter.RestaurantHolder> implements Filterable {
    private ArrayList<Restaurant> m_RestaurantList; // the complete data set of Restaurants
    private ArrayList<Restaurant> m_cDisplayedRestaurantList; // the data set which will be displayed to the users
    // recycler adapter for restaurants

    public static class RestaurantHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView m_cRestaurantNameView;
        private Restaurant m_cRestaurant;

        public RestaurantHolder(View v){
            super(v);
            m_cRestaurantNameView = (TextView) v.findViewById(R.id.recyclerRestaurantShortTextView);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // when a RestaurantHolder (e.g. a row or a cell in the RecyclerView) is clicked
            // view that Restaurant's detail through ViewRestaurantActivity
            Context context = itemView.getContext();

        }

        public void bindRestaurant(Restaurant Restaurant){
            // bind a Restaurant object (and thus its data) into the Restaurant holder
            // the binded Restaurant's detail will be displayed in the cell/row of the RecyclerView
            // :param Restaurant: a Restaurant which will be displayed on the RecyclerView
            m_cRestaurant = Restaurant;
            m_cRestaurantNameView.setText(Restaurant.toString());
        }
    }

    public RestaurantRecyclerAdapter(ArrayList<Restaurant> RestaurantList){
        // initialize the adapter with its data set
        // :param RestaurantList: the data source for the adapter
        m_RestaurantList = RestaurantList;
        m_cDisplayedRestaurantList = RestaurantList;
    }

    public Filter getFilter(){
        /* this method is where the filtering of the Restaurant objects based on a criteria will
        occur */
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                /* performs the filtering on the results/data set
                * :param constraint: the text query that is entered into the Search Bar. this will
                 * be used to filter the data */
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0){
                    results.values = m_RestaurantList;
                    results.count = m_RestaurantList.size();
                }
                else{
                    ArrayList<Restaurant> filterResultsData = new ArrayList<Restaurant>();
                    for(int i = 0; i < m_RestaurantList.size(); i++){
                        //this is where we filter stuffs
                        if (m_RestaurantList.get(i).toString().toLowerCase()
                                .contains(constraint.toString().toLowerCase())){
                            filterResultsData.add(m_RestaurantList.get(i));
                        }
                    }
                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // publish the results or do whatever we want to do with it once we obtain the
                // filtered data set
                m_cDisplayedRestaurantList = (ArrayList<Restaurant>)results.values;
                notifyDataSetChanged();
            }
        };
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
        Restaurant Restaurant = m_cDisplayedRestaurantList.get(position);
        holder.bindRestaurant(Restaurant);
    }

    @Override
    public int getItemCount() {
        // indicates how many items will be displayed on the RecyclerView
        return m_cDisplayedRestaurantList.size();
    }
}
