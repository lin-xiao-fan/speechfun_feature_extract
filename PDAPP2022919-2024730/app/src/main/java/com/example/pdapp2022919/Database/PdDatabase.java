package com.example.pdapp2022919.Database;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.pdapp2022919.Database.Clock.Clock;
import com.example.pdapp2022919.Database.Clock.ClockDao;
import com.example.pdapp2022919.Database.Correction.Correction;
import com.example.pdapp2022919.Database.Correction.CorrectionDao;
import com.example.pdapp2022919.Database.Game.Game;
import com.example.pdapp2022919.Database.Game.GameDao;
import com.example.pdapp2022919.Database.KeepLong.KeepLong;
import com.example.pdapp2022919.Database.KeepLong.KeepLongDao;
import com.example.pdapp2022919.Database.Questionnaire.Questionnaire;
import com.example.pdapp2022919.Database.Questionnaire.QuestionnaireDao;
import com.example.pdapp2022919.Database.ShortLine.ShortLine;
import com.example.pdapp2022919.Database.ShortLine.ShortLineDao;
import com.example.pdapp2022919.Database.User.User;
import com.example.pdapp2022919.Database.User.UserDao;

@Database(
        entities = {User.class, Clock.class, Questionnaire.class, Game.class, ShortLine.class, Correction.class, KeepLong.class},
        version = 2,
        autoMigrations = {
                @AutoMigration(from = 1, to = 2)
        }
)
public abstract class PdDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract ClockDao clockDao();

    public abstract QuestionnaireDao questionnaireDao();

    public abstract GameDao gameDao();

    public abstract ShortLineDao shortLineDao();

    public abstract CorrectionDao correctionDao();
    public abstract KeepLongDao keeplongDao();


}
