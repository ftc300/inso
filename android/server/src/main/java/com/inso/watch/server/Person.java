package com.inso.watch.server;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/4/4
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */
public class Person implements Parcelable {

    private String mUserName;
    private String mUserAge;

    @Override
    public String toString() {
        return "Person{" +
                "mUserName='" + mUserName + '\'' +
                ", mUserAge='" + mUserAge + '\'' +
                '}';
    }

    public Person(String username, String userage) {
        mUserName = username;
        mUserAge = userage;
    }

    public String getmUserName() {
        return mUserName;
    }

    public String getmUserAge() {
        return mUserAge;
    }

    protected Person(Parcel in) {
        mUserName = in.readString();
        mUserAge = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUserName);
        dest.writeString(mUserAge);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

}