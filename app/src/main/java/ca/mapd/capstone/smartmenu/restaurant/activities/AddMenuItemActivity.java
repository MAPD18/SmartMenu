package ca.mapd.capstone.smartmenu.restaurant.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.customer.models.MenuItem;

public class AddMenuItemActivity extends Activity {

    public static final String KEY_NEW_MENU_ITEM = "new_menu_item";
    public static final String KEY_NEW_MENU_ITEM_KEY = "new_menu_item_key";
    public static final String KEY_IS_EDITING = "is_editing";
    EditText itemName, itemDescription, itemPrice;
    Button addItemButton;
    String key;
    boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);

        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        itemPrice = findViewById(R.id.itemPrice);
        addItemButton = findViewById(R.id.addItemButton);

        MenuItem editMenuItem = getIntent().getParcelableExtra(KEY_NEW_MENU_ITEM);
        if (editMenuItem != null) {
            key = getIntent().getStringExtra(KEY_NEW_MENU_ITEM_KEY);
            itemName.setText(editMenuItem.getName());
            itemDescription.setText(editMenuItem.getDescription());
            itemPrice.setText(String.valueOf(editMenuItem.getPrice()));
            addItemButton.setText("SAVE");
            isEditing = true;
        }


        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuItem newItem = new MenuItem(itemName.getText().toString(),
                        itemDescription.getText().toString(),
                        Double.parseDouble(itemPrice.getText().toString()));
                Intent intent = new Intent();
                intent.putExtra(KEY_NEW_MENU_ITEM, newItem);
                intent.putExtra(KEY_IS_EDITING, isEditing);
                intent.putExtra(KEY_NEW_MENU_ITEM_KEY, key);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
