package ca.mapd.capstone.smartmenu.customer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class MenuItem implements Parcelable{

    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PRICE = "price";
    public static final String MENU_KEY = "MENU";
    @Exclude public String m_Name;
    @Exclude public String m_Description;
    @Exclude public double m_Price;

    public MenuItem(){

    }

    public MenuItem(String name, String description, double price){
        this.m_Name = name;
        this.m_Description = description;
        this.m_Price = price;
    }

    public MenuItem(Parcel in) {
        m_Name = in.readString();
        m_Description = in.readString();
        m_Price = in.readDouble();
    }

    public static final Creator<MenuItem> CREATOR = new Creator<MenuItem>() {
        @Override
        public MenuItem createFromParcel(Parcel in) {
            return new MenuItem(in);
        }

        @Override
        public MenuItem[] newArray(int size) {
            return new MenuItem[size];
        }
    };



    public String toString(){
        return this.m_Name;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(m_Name);
        dest.writeString(m_Description);
        dest.writeDouble(m_Price);
    }

    @Exclude
    public String getPriceAsString() {
        return String.format("$%.2f", m_Price);
    }

    public double getPrice() {
        return  m_Price;
    }

    public String getName() {
        return m_Name;
    }

    public String getDescription() {
        return m_Description;
    }

    public void setName(String m_Name) {
        this.m_Name = m_Name;
    }

    public void setDescription(String m_Description) {
        this.m_Description = m_Description;
    }

    public void setPrice(double m_Price) {
        this.m_Price = m_Price;
    }
}
