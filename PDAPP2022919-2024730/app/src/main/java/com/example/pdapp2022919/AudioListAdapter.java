package com.example.pdapp2022919;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pdapp2022919.R;

import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {

    private final Context context;
    private final List<AudioFile> audioFiles;
    private final OnItemClickListener onItemClickListener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(AudioFile audioFile);
    }

    public AudioListAdapter(Context context, List<AudioFile> audioFiles, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.audioFiles = audioFiles;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.audio_list_item, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        AudioFile audioFile = audioFiles.get(position);
        holder.audioFileName.setText(audioFile.getName());
        holder.audioFileDate.setText(audioFile.getDate());

        holder.itemView.setOnClickListener(v -> {
            int previousSelectedPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelectedPosition);
            notifyItemChanged(selectedPosition);
            onItemClickListener.onItemClick(audioFile);
        });

        // 设置选中效果
        holder.itemView.setBackgroundColor(selectedPosition == position ?
                context.getResources().getColor(R.color.selected_item_background) :
                context.getResources().getColor(R.color.default_item_background));
    }

    @Override
    public int getItemCount() {
        return audioFiles.size();
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder {
        TextView audioFileName;
        TextView audioFileDate;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            audioFileName = itemView.findViewById(R.id.audio_file_name);
            audioFileDate = itemView.findViewById(R.id.audio_file_date);
        }
    }
}
