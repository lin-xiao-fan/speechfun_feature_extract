package com.example.pdapp2022919.Questionnaire;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pdapp2022919.Database.Questionnaire.Questionnaire;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.NameManager;

import java.text.SimpleDateFormat;
import java.util.List;

public class QRecoderAdapter extends RecyclerView.Adapter<QRecoderAdapter.ViewHolder> {
    private List<Questionnaire> questionnaireList;

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView qdate_text, qname_text, qscore_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            qdate_text = itemView.findViewById(R.id.qdate_text);
            qname_text = itemView.findViewById(R.id.qname_text);
            qscore_text = itemView.findViewById(R.id.qscore_text);

        }
    }

    public void setList(List<Questionnaire> list) {
        questionnaireList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.qrecoder_list_view, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (questionnaireList == null) return;
        Questionnaire questionnaire = questionnaireList.get(position);
        String L = new SimpleDateFormat("yyyy-MM-dd").format(questionnaire.time);
        holder.qdate_text.setText(L);
        holder.qname_text.setText(questionnaire.q_type);
        switch (questionnaire.q_type) {
            case NameManager.VHI:
                holder.qname_text.setTextColor(0xFFFB5800);
                break;
            case NameManager.VOS:
                holder.qname_text.setTextColor(0xFF05941F);
                break;
            case NameManager.SUS:
                holder.qname_text.setTextColor(0xFF0072E3);
                break;
        }
        holder.qscore_text.setText(Integer.toString(questionnaire.q_score));

    }

    @Override
    public int getItemCount() {
        if (questionnaireList == null) return 0;
        return questionnaireList.size();
    }

}