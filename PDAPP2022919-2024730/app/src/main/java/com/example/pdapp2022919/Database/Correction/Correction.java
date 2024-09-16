package com.example.pdapp2022919.Database.Correction;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.example.pdapp2022919.net.Client;

import java.util.Objects;

@Entity(primaryKeys = {"uuid", "Correction_time"})
public class Correction implements Parcelable {
    @NonNull
    public String uuid;
    public long Correction_time;
    public double Environment_dB;
    public double Distance_cm;

    public Correction(){
        this.uuid = Client.getUuid().toString();
        this.Correction_time = System.currentTimeMillis();
    }

    protected Correction(Parcel in) {
        uuid = Objects.requireNonNull(in.readString());
        Correction_time = in.readLong();
        Environment_dB = in.readDouble();
        Distance_cm = in.readDouble();
    }

    public static final Creator<Correction> CREATOR = new Creator<Correction>() {
        @Override
        public Correction createFromParcel(Parcel in) {
            return new Correction(in);
        }

        @Override
        public Correction[] newArray(int size) {
            return new Correction[size];
        }
    };

    public long getCorrection_time(){
        return  Correction_time;
    }

    public double getEnvironment_dB(){
        return  Environment_dB;
    }

    public double getDistance_cm(){
        return Distance_cm;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeLong(Correction_time);
        parcel.writeDouble(Environment_dB);
        parcel.writeDouble(Distance_cm);
    }
}

