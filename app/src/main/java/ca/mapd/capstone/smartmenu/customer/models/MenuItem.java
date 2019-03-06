package ca.mapd.capstone.smartmenu.customer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class MenuItem implements Parcelable{
    public String m_Name;
    public String m_Description;
    public int m_Price;

    public MenuItem(){

    }

    public MenuItem(String name, String description, int price){
        this.m_Name = name;
        this.m_Description = description;
        this.m_Price = price;
    }

    protected MenuItem(Parcel in) {
        m_Name = in.readString();
        m_Description = in.readString();
        m_Price = in.readInt();
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

    public String getDescription() {
        return m_Description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(m_Name);
        dest.writeString(m_Description);
        dest.writeInt(m_Price);
    }

    public int getPrice() {
        return m_Price;
    }
}
