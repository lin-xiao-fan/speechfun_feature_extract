package com.example.pdapp2022919.Game.GameFeedback;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.pdapp2022919.Database.User.User;
import com.example.pdapp2022919.Database.User.UserDao;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.example.pdapp2022919.net.Client;

public class AwardPopUP extends PopupWindow {

    private final Context context;
    private final Button[] star = new Button[3];
    private final ImageView treasure;
    private final TextView scoreText;
    private final Button okButton;
    private final View token;

    private boolean isTen = false;

    public AwardPopUP(Context context, View token) {
        this.context = context;
        this.token = token;
        View view = LayoutInflater.from(context).inflate(R.layout.activity_award_pop_up, null);
        setContentView(view);

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        star[0] = view.findViewById(R.id.star1);
        star[1] = view.findViewById(R.id.star2);
        star[2] = view.findViewById(R.id.star3);
        treasure = view.findViewById(R.id.treasure);
        scoreText = view.findViewById(R.id.score_text);
        okButton = view.findViewById(R.id.ok_button);

        okButton.setOnClickListener((view1) -> {
            if (isTen) exchangeCoin();
            else dismiss();
        });
        treasure.setOnClickListener((view1) -> {
            if (isTen) exchangeCoin();
        });
    }

    public void setStarCount(int starCount) {
        for (int i = 0; i < starCount; i++) {
            star[i].setEnabled(true);
        }
        new Thread(() -> {
            UserDao dao = DatabaseManager.getInstance(context).userDao();
            User user = dao.getUser();
            user.star_count += starCount;
            isTen = user.star_count >= 10;
            dao.updateUser(user);
            new Handler(Looper.getMainLooper()).post(() -> {
                if (isTen) okButton.setText("開啟");
                scoreText.setText(Integer.toString(user.star_count));
            });
        }).start();
    }

    private void exchangeCoin() {
        CoinPopUp popupWindow = new CoinPopUp(context);
        popupWindow.showAtLocation(token, Gravity.CENTER, 0, 0);
        new Thread(() -> {
            UserDao dao = DatabaseManager.getInstance(context).userDao();
            User user = dao.getUser();
            user.star_count -= 10;
            user.coin_count += 1;
            dao.updateUser(user);
            isTen = false;
            new Handler(Looper.getMainLooper()).post(() -> {
                okButton.setText("知道了");
                scoreText.setText(Integer.toString(user.star_count));
            });
        }).start();
    }
}