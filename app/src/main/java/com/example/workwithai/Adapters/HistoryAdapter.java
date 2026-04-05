package com.example.workwithai.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workwithai.Models.QuestionModel;
import com.example.workwithai.R;
import com.example.workwithai.Repositories.QuestionRepository;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<QuestionModel> historyList;
    private Context context;
    private QuestionRepository repo;

    public HistoryAdapter(List<QuestionModel> historyList, Context context) {
        this.historyList = historyList;
        this.context = context;
        this.repo = new QuestionRepository(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionModel item = historyList.get(position);

        holder.tvSubject.setText(item.getSubject());
        holder.tvQuestionSnippet.setText(item.getQuestionText() != null && !item.getQuestionText().isEmpty()
                ? item.getQuestionText() : "[Image Question]");
        holder.tvAnswerSnippet.setText(item.getFinalAnswer());

        // Handle Bookmark Icon State
        if (item.getIsBookmarked() == 1) {
            holder.btnBookmark.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.btnBookmark.setImageResource(android.R.drawable.btn_star_big_off);
        }

        holder.btnBookmark.setOnClickListener(v -> {
            int newStatus = (item.getIsBookmarked() == 1) ? 0 : 1;
            repo.toggleBookmark(item.getId(), newStatus);
            item.setIsBookmarked(newStatus);
            notifyItemChanged(position);
        });

        // Optional: Add click listener to the whole card to re-open the AIAnswerActivity with this data
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubject, tvQuestionSnippet, tvAnswerSnippet;
        ImageButton btnBookmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tvHistorySubject);
            tvQuestionSnippet = itemView.findViewById(R.id.tvHistoryQuestion);
            tvAnswerSnippet = itemView.findViewById(R.id.tvHistoryAnswer);
            btnBookmark = itemView.findViewById(R.id.btnHistoryBookmark);
        }
    }
}