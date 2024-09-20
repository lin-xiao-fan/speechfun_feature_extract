package com.example.pdapp2022919.HealthManager.History;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.example.pdapp2022919.Database.Game.Game;
import com.example.pdapp2022919.Database.Game.GameDao;
import com.example.pdapp2022919.Database.KeepLong.KeepLong;
import com.example.pdapp2022919.Database.KeepLong.KeepLongDao;
import com.example.pdapp2022919.Database.ShortLine.ShortLine;
import com.example.pdapp2022919.Database.ShortLine.ShortLineDao;
import com.example.pdapp2022919.HealthManager.HealthMangerList;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.example.pdapp2022919.SystemManager.FileManager2;
import com.example.pdapp2022919.SystemManager.MediaManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;
import com.example.pdapp2022919.net.Client;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Calendar extends ScreenSetting {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    private final HistoryListAdapter adapter = new HistoryListAdapter();
    private String selectedDate = "";
    private CalendarView CalendarView;
//    private ProgressDialog dialog;
    private HashMap<String, EventDay> events = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_calendar);
//        dialog = ProgressDialog.show(this, "", "請稍候");

        Button backHome = findViewById(R.id.back_main_page_button2);
        backHome.setOnClickListener(view -> {
            startActivity(new Intent(this, HealthMangerList.class));
        });

        Button button_today = findViewById(R.id.button_Today);
        button_today.setOnClickListener(view -> {
            Date now = new Date();
            CalendarView.setDate(now);
            selectedDate = DATE_FORMAT.format(now);
            adapter.setDate(selectedDate);
        });

        CalendarView = findViewById(R.id.calendarView);
        markRecordDate();

        RecyclerView historyList = findViewById(R.id.historylist);
        historyList.setLayoutManager(new LinearLayoutManager(this));
        historyList.setNestedScrollingEnabled(false);
        historyList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        historyList.setAdapter(adapter);

        selectedDate = DATE_FORMAT.format(new Date());
        adapter.setDate(selectedDate);

        CalendarView.setOnDayClickListener(eventDay -> {
            String date = DATE_FORMAT.format(eventDay.getCalendar().getTime());
            if (selectedDate.equals(date)) {
                return;
            }
            MediaManager.stopPlayer();
            selectedDate = date;
            adapter.setDate(selectedDate);
        });

    }

    private void markRecordDate() {
        new Thread(() -> {
            Drawable res = ContextCompat.getDrawable(this, R.drawable.star);
            HashSet<String> markedDay = new HashSet<>();

            // get all date and filter duplicate date
            GameDao dao = DatabaseManager.getInstance(this).gameDao();
            List<Long> games = dao.getAllTime(Client.getUuid().toString());
            for (Long time : games) {
                markedDay.add(DATE_FORMAT.format(time));
            }
            ShortLineDao dao2 = DatabaseManager.getInstance(this).shortLineDao();
            List<Long> shortLines = dao2.getAllTime(Client.getUuid().toString());
            for (Long time : shortLines) {
                markedDay.add(DATE_FORMAT.format(time));
            }

            // mark date
            events.clear();
            for (String s : markedDay) {
                java.util.Calendar instance = java.util.Calendar.getInstance();
                try {
                    instance.setTime(Objects.requireNonNull(DATE_FORMAT.parse(s)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                events.put(s, new EventDay(instance, res));
            }
            updateCalender();
        }).start();
    }

    private void updateCalender() {
        ArrayList<EventDay> days = new ArrayList<>(events.values());
        runOnUiThread(() -> {
            CalendarView.setEvents(days);
//            dialog.dismiss();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.stopPlayer();
    }

    private class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {

            public TextView timeText;
            public TextView typeText;
            public Button garbage;
            public Button upload;
            public Context ctx;
            public ViewHolder(@NonNull View itemView, Context ctx) {
                super(itemView);
                garbage = itemView.findViewById(R.id.garbageButton);
                upload = itemView.findViewById(R.id.uploadButton);
                timeText = itemView.findViewById(R.id.timeText);
                typeText = itemView.findViewById(R.id.typeText);
                this.ctx = ctx;
            }

        }

        private List<HistoryItem> historyList = new ArrayList<>();

        public void setDate(String date) {
            java.util.Calendar instance = java.util.Calendar.getInstance();
            try {
                instance.setTime(Objects.requireNonNull(DATE_FORMAT.parse(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long from = instance.getTime().getTime();
            instance.add(java.util.Calendar.DATE, 1);
            long to = instance.getTime().getTime();

            new Thread(() -> {
                historyList.clear();
                getData(from, to);
                runOnUiThread(this::notifyDataSetChanged);
            }).start();

        }
        private void getData(long from, long to) {
            GameDao dao = DatabaseManager.getInstance(Calendar.this).gameDao();
            List<Game> games = dao.getGames(Client.getUuid().toString(), from, to);
            historyList.addAll(games);

            ShortLineDao dao2 = DatabaseManager.getInstance(Calendar.this).shortLineDao();
            List<ShortLine> shortLines = dao2.getShortLines(Client.getUuid().toString(), from, to);
            historyList.addAll(shortLines);

            KeepLongDao dao3 = DatabaseManager.getInstance(Calendar.this).keepLongDao();
            List<KeepLong> keepLongs = dao3.getKeepLong(Client.getUuid().toString(),from,to);
            historyList.addAll(keepLongs);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_list_view, parent, false);
            return new ViewHolder(view, parent.getContext());
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (historyList == null) return;
            HistoryItem data = historyList.get(position);
            String time = TIME_FORMAT.format(data.getTime());
            holder.typeText.setText(data.getHistoryName());
            holder.timeText.setText(time);
            holder.itemView.setOnClickListener((view -> data.onItemClick(holder.ctx)));

//            case SHORT_LINE:
//            holder.typeText.setText(R.string.short_line_game);
//            holder.itemView.setOnClickListener((view) -> {
//                String audioName = FileManager.getShortLinePath(data.date);
//                MediaManager.playAudio(audioName, null);
//            });
//            break;

            holder.garbage.setOnClickListener(view -> {
                new AlertDialog.Builder(holder.ctx)
                        .setTitle(R.string.make_sure_del)
                        .setNegativeButton(R.string.ok, (dialog, which) -> {
                            deleteFile(holder.ctx, position);
                        }).setPositiveButton(R.string.no, (dialog, which) -> {})
                        .show();
            });
            holder.upload.setOnClickListener(view -> {
                new AlertDialog.Builder(holder.ctx)
                        .setTitle(R.string.make_sure_upload)
                        .setNegativeButton(R.string.ok, (dialog, which) -> {
                            new Thread(() -> {
                                File[] files = data.getContentFiles();
                                Client.upload(holder.ctx, files, isSucceed -> {
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        if (isSucceed) {
                                            Toast.makeText(holder.ctx, R.string.upload_success, Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(holder.ctx, R.string.upload_failed, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                });
                            }).start();
                        }).setPositiveButton(R.string.no, (dialog, which) -> {})
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            if (historyList == null) return 0;
            return historyList.size();
        }

        private void deleteFile(Context context, int position) {
            HistoryItem data = historyList.get(position);
            new Thread(() -> {
                if (!FileManager2.deleteFile(data.getContentFiles())) {
                    runOnUiThread(() ->
                            Toast.makeText(context, R.string.file_is_not_exist, Toast.LENGTH_SHORT).show());
                }
                historyList.remove(position);
                if (historyList.size() == 0) {
                    historyList = null;
                    events.remove(selectedDate);
                    updateCalender();
                }
                runOnUiThread(() -> {
                    Toast.makeText(context, R.string.del_success, Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                });
            }).start();
        }

    }
}