package com.example.workwithai.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workwithai.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {

    private JSONArray questions;
    private Context context;

    public QuizAdapter(JSONArray questions, Context context) {
        this.questions = questions;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_quiz_question, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject q = questions.getJSONObject(position);
            holder.tvQuestion.setText((position + 1) + ". " + q.getString("question"));

            JSONArray options = q.getJSONArray("options");
            holder.rgOptions.removeAllViews();
            holder.tvFeedback.setVisibility(View.GONE);

            for (int i = 0; i < options.length(); i++) {
                RadioButton rb = new RadioButton(context);
                rb.setText(options.getString(i));
                rb.setPadding(10, 10, 10, 10);
                holder.rgOptions.addView(rb);
            }

            holder.rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
                try {
                    RadioButton selected = group.findViewById(checkedId);
                    String answer = q.getString("answer");
                    holder.tvFeedback.setVisibility(View.VISIBLE);

                    if (selected.getText().toString().equals(answer)) {
                        holder.tvFeedback.setText("Correct! " + q.optString("explanation"));
                        holder.tvFeedback.setTextColor(Color.parseColor("#2E7D32"));
                    } else {
                        holder.tvFeedback.setText("Incorrect. The correct answer was: " + answer);
                        holder.tvFeedback.setTextColor(Color.RED);
                    }
                } catch (Exception ignored) {}
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return questions.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvFeedback;
        RadioGroup rgOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuizQuestion);
            tvFeedback = itemView.findViewById(R.id.tvFeedback);
            rgOptions = itemView.findViewById(R.id.rgOptions);
        }
    }
}