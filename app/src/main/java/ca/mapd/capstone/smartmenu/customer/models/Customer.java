package ca.mapd.capstone.smartmenu.customer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class Customer implements Parcelable{
    public static final String USER_PROFILE_KEY = "USER_PROFILE";
    public String m_Id;
    public String m_Name;
    public String m_Address;
    public String m_PhoneNumber;

    public Customer(){

    }

    public Customer(String name, String address, String phoneNumber){
        m_Name = name;
        m_Address = address;
        m_PhoneNumber = phoneNumber;
    }

    public String getM_Id() { return m_Id; }

    public void setM_Id(String m_Id) { this.m_Id = m_Id; }

    public String getM_Name(){
        return m_Name;
    }

    public void setM_Name(String m_Name) { this.m_Name = m_Name; }

    public String getM_Address(){
        return m_Address;
    }

    public void setM_Address(String m_Address) { this.m_Address = m_Address; }

    public String getM_PhoneNumber() { return m_PhoneNumber; }

    public void setM_PhoneNumber(String m_PhoneNumber) { this.m_PhoneNumber = m_PhoneNumber; }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Customer(Parcel in){
        m_Name = in.readString();
        m_Address = in.readString();
        m_PhoneNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(m_Name);
        dest.writeString(m_Address);
        dest.writeString(m_PhoneNumber);
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>(){

        @Override
        public Customer createFromParcel(Parcel source) {
            return new Customer(source);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

    public String toString(){
        return getM_Name();
    }
}
