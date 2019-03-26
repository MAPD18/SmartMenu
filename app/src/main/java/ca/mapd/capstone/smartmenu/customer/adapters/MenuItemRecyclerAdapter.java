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

    public static class MenuItemHolder extends RecyclerView.ViewHolder {
        private TextView m_cMenuItemNameView;
        private TextView m_cMenuItemDescriptionView;
        private TextView m_cMenuItemPriceView;
        private MenuItem m_cMenuItem;

        private MenuItemHolder(View v){
            super(v);
            m_cMenuItemNameView = (TextView) v.findViewById(R.id.menuItemName);
            m_cMenuItemDescriptionView = (TextView) v.findViewById(R.id.menuItemDescription);
            m_cMenuItemPriceView = (TextView) v.findViewById(R.id.menuItemPrice);
        }

        public void bindMenuItem(MenuItem menuItem){
            // bind a MenuItem object (and thus its data) into the MenuItem holder
            // the binded MenuItem's detail will be displayed in the cell/row of the RecyclerView
            // :param MenuItem: a MenuItem which will be displayed on the RecyclerView
            m_cMenuItem = menuItem;
            m_cMenuItemNameView.setText(menuItem.getName());
            m_cMenuItemDescriptionView.setText(menuItem.getDescription());
            m_cMenuItemPriceView.setText(menuItem.getPriceAsString());
        }
    }

    public MenuItemRecyclerAdapter(ArrayList<MenuItem> menu){
        // initialize the adapter with its data set
        m_MenuItemList = menu;
        m_ViewHolderList = new ArrayList<>(); // keeps track of all of our view holders
    }

    public void updateMenuItem(MenuItem item, int position) {
        m_MenuItemList.set(position, item);
        notifyItemChanged(position);
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

}
