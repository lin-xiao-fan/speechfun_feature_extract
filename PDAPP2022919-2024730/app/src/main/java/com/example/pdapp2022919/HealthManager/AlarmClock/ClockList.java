package com.example.pdapp2022919.HealthManager.AlarmClock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pdapp2022919.Database.Clock.Clock;
import com.example.pdapp2022919.Database.Clock.ClockDao;
import com.example.pdapp2022919.HealthManager.HealthMangerList;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;
import com.example.pdapp2022919.net.Client;

import java.util.Calendar;
import java.util.List;


public class ClockList extends ScreenSetting {

    RecyclerView clockRecyclerView;
    private final ClockListAdapter adapter = new ClockListAdapter();
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_list);
        Button addButton = findViewById(R.id.addButton);
        Button back_main_page_button6 = findViewById(R.id.back_main_page_button6);
        clockRecyclerView = findViewById(R.id.clock_recyclerView);
        clockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        clockRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        clockRecyclerView.setAdapter(adapter);
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, EditClock.class);
            intent.putExtra(NameManager.IS_EDIT_MODE, false);
            calendar.setTimeInMillis(System.currentTimeMillis());
            int Hour= calendar.get(Calendar.HOUR_OF_DAY);
            int Minute= calendar.get(Calendar.MINUTE);
            intent.putExtra(NameManager.CLOCK_DATA,new Clock(Hour, Minute, (byte) 0XFF));
            startActivity(intent);
        });
        back_main_page_button6.setOnClickListener(view -> {
            startActivity(new Intent(this, HealthMangerList.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            ClockDao dao = DatabaseManager.getInstance(ClockList.this).clockDao();
            List<Clock> list = dao.getAllClock(Client.getUuid().toString());
            runOnUiThread(() -> adapter.setList(list));
        }).start();
    }

    private static class ClockListAdapter extends RecyclerView.Adapter<ClockListAdapter.ViewHolder> {

        private static final String[] day_text = new String[] {"一", "二", "三", "四", "五", "六", "日"};
        private List<Clock> clockList;

        static class ViewHolder extends RecyclerView.ViewHolder {

            public Context context;
            public TextView timeText;
            public TextView dateText;
            public Switch switch1;

            public ViewHolder(@NonNull View itemView, Context context) {
                super(itemView);
                timeText = itemView.findViewById(R.id.time_text);
                dateText = itemView.findViewById(R.id.date_text);
                switch1 = itemView.findViewById(R.id.switch1);
                this.context = context;
            }

        }
        public void setList(List<Clock> list) {
            clockList = list;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.clock_list_view, parent, false);
            return new ViewHolder(view, parent.getContext());
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (clockList == null) return;
            Clock clock = clockList.get(position);
            byte state = clock.clock_state;

            holder.timeText.setText(
                    holder.context.getString(R.string.clock_format, clock.hour, clock.minute)
            );
            holder.switch1.setOnCheckedChangeListener((compoundButton, b) -> {
                new Thread(() -> {
                    ClockDao dao = DatabaseManager.getInstance(holder.context).clockDao();
                    if (b) clock.clock_state |= 0x80;
                    else clock.clock_state &= 0x7F;
                    dao.updateClock(clock);
                }).start();
            });
            holder.switch1.setChecked((state & 0X80)== 0x80);

            if ((state & 0x7F) == 0x7F) holder.dateText.setText("每天");
            else {
                holder.dateText.setText("");
                for (int i = 0; i < 7; i++) {
                    if ((state & (0X40>>i)) > 0) {
                        holder.dateText.append(day_text[i]);
                    }
                }
            }
            holder.itemView.setOnClickListener((view) -> {
                Intent intent = new Intent(holder.context, EditClock.class);
                intent.putExtra(NameManager.IS_EDIT_MODE, true);
                intent.putExtra(NameManager.CLOCK_DATA, clock);
                holder.context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            if (clockList == null) return 0;
            return clockList.size();
        }
    }
}

