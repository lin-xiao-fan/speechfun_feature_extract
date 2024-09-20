package com.example.pdapp2022919.Database.Clock;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.pdapp2022919.net.Client;

@Entity(primaryKeys = {"uuid", "hour", "minute"})
public class Clock implements Parcelable {

    @NonNull
    public String uuid;
    public int hour;
    public int minute;
    public byte clock_state;

    public Clock(int hour, int minute, byte clock_state) {
        this.uuid = Client.getUuid().toString();;
        this.hour = hour;
        this.minute = minute;
        this.clock_state = clock_state;
    }

    protected Clock(Parcel in) {
        uuid = in.readString();
        hour = in.readInt();
        minute = in.readInt();
        clock_state = in.readByte();
    }

    public static final Creator<Clock> CREATOR = new Creator<Clock>() {
        @Override
        public Clock createFromParcel(Parcel in) {
            return new Clock(in);
        }

        @Override
        public Clock[] newArray(int size) {
            return new Clock[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeInt(hour);
        parcel.writeInt(minute);
        parcel.writeByte(clock_state);
    }
}
