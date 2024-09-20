package com.example.pdapp2022919.HealthManager.History;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pdapp2022919.Database.Game.Game;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.FileType;
import com.example.pdapp2022919.SystemManager.MediaManager;

import java.io.File;

public class gameHistoryListAdapter extends RecyclerView.Adapter<gameHistoryListAdapter.ViewHolder> {

    private final File[] audioFiles;
    private ViewHolder prevControl = null;

    public gameHistoryListAdapter(Game data) {
        audioFiles = data.getContentFiles();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView levelText;
        public TextView durationText;
        public Button play_button;
        public Button pause_button;
        public int totalTime;
        public boolean isPlaying = false;

        private Handler handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    int remain = msg.arg1 - 1000;
                    durationText.setText(MediaManager.milliTimeToText(remain));
                    if (remain < 1000) return;
                    Message message = new Message();
                    message.what = 0;
                    message.arg1 = remain;
                    handler.sendMessageDelayed(message, 1000);
                }
            }

        };

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            levelText = itemView.findViewById(R.id.levelText);
            durationText = itemView.findViewById(R.id.durationText);
            play_button = itemView.findViewById(R.id.play_button);
            pause_button = itemView.findViewById(R.id.pause_button);
            pause_button.setVisibility(View.INVISIBLE);
        }

        public void onPlay() {
            isPlaying = true;
            play_button.setVisibility(View.INVISIBLE);
            pause_button.setVisibility(View.VISIBLE);
            Message message = new Message();
            message.what = 0;
            message.arg1 = totalTime;
            handler.sendMessageDelayed(message, 1000);
        }

        public void onPause() {
            isPlaying = false;
            play_button.setVisibility(View.VISIBLE);
            pause_button.setVisibility(View.INVISIBLE);
            handler.removeMessages(0);
            durationText.setText(MediaManager.milliTimeToText(totalTime));
        }

        public void setDuration(int duration) {
            totalTime = duration;
            durationText.setText(MediaManager.milliTimeToText(duration));
        }

    }

    @NonNull
    @Override
    public gameHistoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_history_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull gameHistoryListAdapter.ViewHolder holder, int position) {
        if (audioFiles == null) return;
        File file = audioFiles[position];
        holder.setDuration(MediaManager.getMediaDuration(file));
        holder.levelText.setText(audioName(position));
        holder.itemView.setOnClickListener(view -> {
            if (holder.isPlaying) holder.onPause();
            else {
                if (prevControl != null && prevControl != holder) {
                    prevControl.onPause();
                }
                prevControl = holder;
                holder.onPlay();
            }
            MediaManager.playAudio(file.getAbsolutePath(), holder::onPause);
        });
        holder.play_button.setOnClickListener((view -> {
            if (prevControl != null && prevControl != holder) {
                prevControl.onPause();
            }
            prevControl = holder;
            holder.onPlay();
            MediaManager.playAudio(file.getAbsolutePath(), holder::onPause);
        }));
        holder.pause_button.setOnClickListener((view -> {
            holder.onPause();
            MediaManager.playAudio(file.getAbsolutePath(), holder::onPause);
        }));
    }

    @Override
    public int getItemCount() {
        return audioFiles.length;
    }

    private String audioName(int position) {
        String path = audioFiles[position].getName();
        path = path.substring(0, path.length() - 4);
        if (path.equals(FileType.PRETEST.fileName)) return "前測";
        if (path.equals(FileType.POSTTEST.fileName)) return "後測";
        if (path.equals(FileType.GAME_ONE.fileName)) return "1";
        if (path.equals(FileType.GAME_TWO.fileName)) return "2";
        if (path.equals(FileType.GAME_THREE.fileName)) return "3";
        return "";
    }

}