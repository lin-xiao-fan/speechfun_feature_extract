//package com.example.pdapp2022919.Recode;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.example.pdapp2022919.SystemManager.MediaManager;
//
//public class RecordData implements Parcelable {
//
//    public int level_difficulty, success;
//    public double pretest_db,post_test_db;
//    public long play_how_long,start_play_time,stop_play_time;
//    public String files_path;
//
//    public RecordData() {}
//    protected RecordData(Parcel in) {
//        level_difficulty = in.readInt();
//        pretest_db = in.readDouble();
//        post_test_db = in.readDouble();
//        play_how_long = in.readLong();
//        start_play_time = in.readLong();
//        stop_play_time = in.readLong();
//        success = in.readInt();
//        files_path = in.readString();
//    }
//
//    public static final Creator<RecordData> CREATOR = new Creator<RecordData>() {
//        @Override
//        public RecordData createFromParcel(Parcel in) {
//            return new RecordData(in);
//        }
//
//        @Override
//        public RecordData[] newArray(int size) {
//            return new RecordData[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeInt(level_difficulty);
//        parcel.writeDouble(pretest_db);
//        parcel.writeDouble(post_test_db);
//        parcel.writeLong(play_how_long);
//        parcel.writeLong(start_play_time);
//        parcel.writeLong(stop_play_time);
//        parcel.writeInt(success);
//        parcel.writeString(files_path);
//    }
//
//    public String getPlayTimeText() {
//        return MediaManager.milliTimeToText(play_how_long);
//    }
//
//    public String getDifficultText(){
//        switch (level_difficulty){
//            case 1:
//                return "簡單";
//            case 2:
//                return "中等";
//            case 3:
//                return "困難";
//            default:
//                return "";
//        }
//    }
//
//    public void addFilePath(String filePath) {
//        String[] str = filePath.split("/");
//        if (files_path == null) files_path = str[str.length - 1];
//        else files_path += "," + str[str.length - 1];
//    }
//
//}
