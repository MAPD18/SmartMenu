package ca.mapd.capstone.smartmenu.customer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;


@IgnoreExtraProperties
public class Restaurant implements Parcelable {
    public static final String RESTAURANT_KEY = "RESTAURANT";
    public String m_Id;
    public String m_Name;
    public String m_Address;
    public String m_PhoneNumber;
    public Double m_Latitude;
    public Double m_Longitude;
    public String m_Email;
    public ArrayList<MenuItem> m_Menu;
    public boolean isAvailable;
    public String m_key;

    public Restaurant() {

    }

    public Restaurant(String name, String address, String phoneNumber) {
        m_Name = name;
        m_Address = address;
        m_PhoneNumber = phoneNumber;
        m_Menu = new ArrayList<MenuItem>();
    }

    public ArrayList<MenuItem> getMenu() {
        return m_Menu;
    }

    public void addItem(MenuItem menuItem) {
        this.m_Menu.add(menuItem);
    }


    public String getM_Id() {
        return m_Id;
    }

    public void setM_Id(String m_Id) {
        this.m_Id = m_Id;
    }

    public String getM_Name() {
        return m_Name;
    }

    public void setM_Name(String m_Name) {
        this.m_Name = m_Name;
    }

    public String getM_Address() {
        return m_Address;
    }

    public void setM_Address(String m_Address) {
        this.m_Address = m_Address;
    }

    public String getM_PhoneNumber() {
        return m_PhoneNumber;
    }

    public void setM_PhoneNumber(String m_PhoneNumber) {
        this.m_PhoneNumber = m_PhoneNumber;
    }

    public Double getM_Latitude() {
        return m_Latitude;
    }

    public void setM_Latitude(Double m_Latitude) {
        this.m_Latitude = m_Latitude;
    }

    public Double getM_Longitude() {
        return m_Longitude;
    }

    public void setM_Longitude(Double m_Longitude) {
        this.m_Longitude = m_Longitude;
    }

    public String getM_Email() {
        return m_Email;
    }

    public void setM_Email(String m_Email) {
        this.m_Email = m_Email;
    }

    public void setId(String m_key) {
        this.m_key = m_key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Restaurant(Parcel in) {
        m_Name = in.readString();
        m_Address = in.readString();
        m_Menu = new ArrayList<>();
        in.readTypedList(m_Menu, MenuItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(m_Name);
        dest.writeString(m_Address);
        dest.writeTypedList(m_Menu);
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {

        @Override
        public Restaurant createFromParcel(Parcel source) {
            return new Restaurant(source);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public String toString() {
        return getM_Name();
    }


    public boolean isPofileFormValid() {
        return m_Name != null && !m_Name.trim().isEmpty() &&
                m_Address != null && !m_Address.trim().isEmpty() &&
                m_PhoneNumber != null && !m_PhoneNumber.trim().isEmpty();

    }

}
