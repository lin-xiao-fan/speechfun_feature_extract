package com.example.pdapp2022919.Database.User;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user ORDER BY lastLogin DESC LIMIT 1")
    User getUser();

    //@Query 找
    @Query("SELECT * FROM user WHERE uuid LIKE :uuid LIMIT 1")
    User findByUuid(String uuid);

    //新增
    @Insert
    void addUser(User user);

    //更新
    @Update
    void updateUser(User user);

}