package ca.mapd.capstone.smartmenu.customer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.customer.models.MenuItem;
import ca.mapd.capstone.smartmenu.customer.models.Restaurant;


public class MenuItemRecyclerAdapter
        extends RecyclerView.Adapter<MenuItemRecyclerAdapter.MenuItemHolder> {
    // adapter class for displaying MenuItem objects, this will specify how our data is going to be
    // presented in the RecyclerView

    private ArrayList<MenuItem> m_MenuItemList; // the complete data set of MenuItems
    private ArrayList<MenuItemHolder> m_ViewHolderList;

    public static class MenuItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView m_cMenuItemNameView;
        private TextView m_CountItemView;
        private MenuItem m_cMenuItem;
        private Button m_AddButton;
        private Button m_SubButton;
        private int m_ItemCount;

        private MenuItemHolder(View v){
            super(v);
            m_ItemCount = 0;
            m_cMenuItemNameView = (TextView) v.findViewById(R.id.recyclerMenuItemShortTextView);
            m_CountItemView = (TextView) v.findViewById(R.id.recyclerMenuItemCountTextView);
            m_CountItemView.setText(this.countToString());
            m_AddButton = (Button) v.findViewById(R.id.addMenuItemButton);
            m_SubButton = (Button) v.findViewById(R.id.subMenuItemButton);
            m_AddButton.setOnClickListener(this);
            m_SubButton.setOnClickListener(this);
        }

        private String countToString(){
            // getItemCount but as a string
            return "x" + Integer.toString(m_ItemCount);
        }

        public int getItemCount(){
            // get the number of items in here
            return m_ItemCount;
        }
        public MenuItem getItem(){
            return m_cMenuItem;
        }

        @Override
        public void onClick(View v) {
            // when a MenuItemHolder (e.g. a row or a cell in the RecyclerView) is clicked
            // view that MenuItem's detail through ViewMenuItemActivity
            switch(v.getId()){
                case R.id.addMenuItemButton:
                    m_ItemCount += 1;
                    break;
                case R.id.subMenuItemButton:
                    if(m_ItemCount > 0) {
                        m_ItemCount -= 1;
                    }
                    break;
            }
            m_CountItemView.setText(this.countToString());
        }

        public void bindMenuItem(MenuItem MenuItem){
            // bind a MenuItem object (and thus its data) into the MenuItem holder
            // the binded MenuItem's detail will be displayed in the cell/row of the RecyclerView
            // :param MenuItem: a MenuItem which will be displayed on the RecyclerView
            m_cMenuItem = MenuItem;
            m_cMenuItemNameView.setText(MenuItem.toString());
        }
    }

    public MenuItemRecyclerAdapter(Restaurant restaurant){
        // initialize the adapter with its data set
        m_MenuItemList = restaurant.getMenu(); // retrieve the menu from the restaurant
        m_ViewHolderList = new ArrayList<>(); // keeps track of all of our view holders
    }

    @Override
    public MenuItemRecyclerAdapter.MenuItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the recyclerview_item_row layout and pass it onto the MenuItemHolder
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_menuitem_row, parent, false);
        return new MenuItemHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(MenuItemRecyclerAdapter.MenuItemHolder holder, int position) {
        // called when the system wants to bind a MenuItem to a particular view
        MenuItem MenuItem = m_MenuItemList.get(position);
        holder.bindMenuItem(MenuItem);
        m_ViewHolderList.add(holder);
    }

    @Override
    public int getItemCount() {
        // indicates how many items will be displayed on the RecyclerView
        return m_MenuItemList.size();
    }

    public ArrayList<MenuItem> getMenuItemList(){
        // returns an arraylist of menuitems based on the MenuItems in the adapter
        // and the amount indicated in each viewholder
        ArrayList<MenuItem> output = new ArrayList<>();
        for(MenuItemRecyclerAdapter.MenuItemHolder holder : m_ViewHolderList){
            for(int i = 0; i < holder.getItemCount(); i++) {
                output.add(holder.getItem());
            }
        }
        return output;
    }
}
