package com.example.pdapp2022919.Profile;

public class ProfileData {
    public String patient_name ="", patient_number="",phone_number= "";
    public int sex=0, birthday_year, birthday_month, birthday_day;

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getPatient_number() {
        return patient_number;
    }

    public void setPatient_number(String patient_number) {
        this.patient_number = patient_number;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getBirthday_year() {
        return birthday_year;
    }

    public void setBirthday_year(int birthday_year) {
        this.birthday_year = birthday_year;
    }

    public int getBirthday_month() {
        return birthday_month;
    }

    public void setBirthday_month(int birthday_month) {
        this.birthday_month = birthday_month;
    }

    public int getBirthday_day() {
        return birthday_day;
    }

    public void setBirthday_day(int birthday_day) {
        this.birthday_day = birthday_day;
    }
}
