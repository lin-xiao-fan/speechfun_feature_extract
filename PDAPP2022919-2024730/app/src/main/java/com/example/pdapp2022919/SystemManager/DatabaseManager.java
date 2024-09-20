package com.example.pdapp2022919.SystemManager;

import android.content.Context;

import androidx.room.Room;

import com.example.pdapp2022919.Database.Clock.ClockDao;
import com.example.pdapp2022919.Database.Correction.CorrectionDao;
import com.example.pdapp2022919.Database.Game.GameDao;
import com.example.pdapp2022919.Database.KeepLong.KeepLongDao;
import com.example.pdapp2022919.Database.PdDatabase;
import com.example.pdapp2022919.Database.Questionnaire.QuestionnaireDao;
import com.example.pdapp2022919.Database.ShortLine.ShortLineDao;
import com.example.pdapp2022919.Database.User.UserDao;

public class DatabaseManager {

    public static final String DB_NAME = "pd.db";//資料庫名稱

    private static volatile DatabaseManager instance;
    private static volatile PdDatabase database;

    private DatabaseManager() {}

    public static synchronized DatabaseManager getInstance(Context context){
        if(instance == null){
            instance = create(context);//創立新的資料庫
        }
        return instance;
    }

    private static DatabaseManager create(final Context context){
        // TODO use rxjava instead allow main thread method
        database = Room.databaseBuilder(
                context.getApplicationContext(), PdDatabase.class, DB_NAME
        ).allowMainThreadQueries().build();
        return new DatabaseManager();
    }

    public UserDao userDao() {
        return database.userDao();
    }

    public ClockDao clockDao() {
        return database.clockDao();
    }

    public QuestionnaireDao questionnaireDao() {
        return database.questionnaireDao();
    }
    public GameDao gameDao() {
        return database.gameDao();
    }

    public ShortLineDao shortLineDao() {
        return database.shortLineDao();
    }

    public CorrectionDao correctionDao(){
        return database.correctionDao();
    }

    public KeepLongDao keepLongDao(){return database.keeplongDao();}
}
