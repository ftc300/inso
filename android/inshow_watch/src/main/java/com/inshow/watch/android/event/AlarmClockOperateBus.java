package com.inshow.watch.android.event;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chendong on 2017/2/14.
 */

public class AlarmClockOperateBus implements Parcelable{
    public boolean isAdd;
    public int id;
    public int seconds;
    public String repeatType;
    public boolean isOn;

    public AlarmClockOperateBus(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public AlarmClockOperateBus(boolean isAdd, int id, int seconds, String repeatType, boolean isOn) {
        this.isAdd = isAdd;
        this.id = id;
        this.seconds = seconds;
        this.repeatType = repeatType;
        this.isOn = isOn;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isAdd ? (byte) 1 : (byte) 0);
        dest.writeInt(this.id);
        dest.writeInt(this.seconds);
        dest.writeString(this.repeatType);
        dest.writeByte(this.isOn ? (byte) 1 : (byte) 0);
    }

    protected AlarmClockOperateBus(Parcel in) {
        this.isAdd = in.readByte() != 0;
        this.id = in.readInt();
        this.seconds = in.readInt();
        this.repeatType = in.readString();
        this.isOn = in.readByte() != 0;
    }

    public static final Creator<AlarmClockOperateBus> CREATOR = new Creator<AlarmClockOperateBus>() {
        @Override
        public AlarmClockOperateBus createFromParcel(Parcel source) {
            return new AlarmClockOperateBus(source);
        }

        @Override
        public AlarmClockOperateBus[] newArray(int size) {
            return new AlarmClockOperateBus[size];
        }
    };

    @Override
    public String toString() {
        return isAdd + "," + id + "," + seconds + "," + repeatType + "," + isOn + ",";
    }
}
