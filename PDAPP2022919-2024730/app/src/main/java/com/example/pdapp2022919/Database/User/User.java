package com.example.pdapp2022919.Database.User;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.pdapp2022919.Database.DataConvertor;
import com.example.pdapp2022919.Database.Vegetable.Vegetable;

import java.util.ArrayList;
import java.util.Random;

@Entity
public class User {

    private static Random random = new Random();

    @NonNull
    @PrimaryKey
    public String uuid;
    public String account;
    public String password;
    public int star_count;
    public int coin_count;
    public int checked_vegetable;
    @TypeConverters({DataConvertor.class})
    private boolean[] unlocked_vegetable;
    public long lastLogin;

    public User(@NonNull String uuid, String account, String password) {
        this.uuid = uuid;
        this.account = account;
        this.password = password;
        this.lastLogin = System.currentTimeMillis();
    }

    @Ignore // 建構子
    public User(String uuid, String account, String password, int star_count, int coin_count) {
        this(uuid, account, password);
        this.star_count = star_count;
        this.coin_count = coin_count;
    }

    public int unlockVegetable() {
        getUnlocked_vegetable();
        // 紀錄沒有解鎖的蔬菜
        ArrayList<Integer> locked = new ArrayList<>();
        for (int i = 0; i < unlocked_vegetable.length; i++) {
            if (!unlocked_vegetable[i]) locked.add(i);
        }
        // 如果都解鎖了 跳出
        if (locked.size() == 0) return -1;
        // 解鎖沒拿過的蔬菜
        int unlock = locked.get(random.nextInt(locked.size()));
        unlocked_vegetable[unlock] = true;
        return unlock;
    }

    public void setUnlocked_vegetable(boolean[] vegetable) {
        unlocked_vegetable = vegetable;
    }

    public boolean[] getUnlocked_vegetable() {
        if (unlocked_vegetable == null) {
            unlocked_vegetable = new boolean[Vegetable.values().length];
            unlocked_vegetable[0] = true;
//            [true, false, false, false, false, false, false, false, false, false, false, false]
        }
        return unlocked_vegetable;
    }

}

