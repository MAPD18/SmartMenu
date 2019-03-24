package ca.mapd.capstone.smartmenu.util;

import android.os.ParcelUuid;

public abstract class Constants {

    public static final ParcelUuid SERVICE_UUID = ParcelUuid.fromString("C39813F0-D356-45DE-976D-1F8BC3A898A5");
    public static final ParcelUuid CHARACTERISTIC_UUID = ParcelUuid.fromString("C32F7F7E-3E79-4A79-B04C-A392876A9959");
    public static final String TAG = "jabdia";

    public static final String CHANNEL_ID = "smart_menu_channel";
    public static final String CHANNEL_NAME = "SmartMenu Notification";
    public static final String CHANNEL_DESCRIPTION = "Notification for Smart Menu";

    public static final String MY_PREFS = "myApp.prefs";
    public static final String MY_PREFS_RESTAURANT_ID = "myApp.prefs.restaurantId";


}
