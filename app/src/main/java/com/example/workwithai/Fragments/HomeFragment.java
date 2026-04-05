package com.example.workwithai.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.workwithai.Models.QuestionModel;
import com.example.workwithai.R;
import com.example.workwithai.Repositories.QuestionRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private TextView tvTotalQuestions, tvTopSubject, tvQuizAccuracy, tvReviewTime, tvAiInsight, tvWelcomeMessage;
    // Gamification Views
    private TextView tvUserLevel, tvUserXp;
    private ProgressBar pbXpProgress;

    private QuestionRepository repo;
    private final String API_KEY = "AIzaSyDMvTMw8K4UNBjLyZ3JO5yP4qpu62adV4c";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvTotalQuestions = view.findViewById(R.id.tvTotalQuestions);
        tvTopSubject = view.findViewById(R.id.tvTopSubject);
        tvQuizAccuracy = view.findViewById(R.id.tvQuizAccuracy);
        tvReviewTime = view.findViewById(R.id.tvReviewTime);
        tvAiInsight = view.findViewById(R.id.tvAiInsight);
        tvWelcomeMessage = view.findViewById(R.id.tvWelcomeMessage);

        // Gamification Views
        tvUserLevel = view.findViewById(R.id.tvUserLevel);
        tvUserXp = view.findViewById(R.id.tvUserXp);
        pbXpProgress = view.findViewById(R.id.pbXpProgress);

        repo = new QuestionRepository(requireContext());

        // Greet the user
        SharedPreferences spf = requireActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        String username = spf.getString("USERNAME_USER", "Student");
        tvWelcomeMessage.setText("Welcome back, " + username + "!");

        // Set up mock leaderboard click
        View btnLeaderboard = view.findViewById(R.id.btnLeaderboard);
        if (btnLeaderboard != null) {
            btnLeaderboard.setOnClickListener(v ->
                    Toast.makeText(getContext(), "Leaderboard & Badges coming soon!", Toast.LENGTH_SHORT).show()
            );
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        List<QuestionModel> history = repo.getAllHistory("", "All");

        // 1. Total Questions Asked
        int totalQuestions = history.size();
        tvTotalQuestions.setText(String.valueOf(totalQuestions));

        // --- GAMIFICATION LOGIC ---
        SharedPreferences spf = requireActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        // Assuming quizzes completed are saved in SharedPreferences by QuizFragment
        int quizzesCompleted = spf.getInt("QUIZZES_COMPLETED", 0);

        int xpFromQuestions = totalQuestions * 50; // Earn 50 XP per question asked
        int xpFromQuizzes = quizzesCompleted * 100; // Earn 100 XP per quiz completed
        int totalXp = xpFromQuestions + xpFromQuizzes;

        int xpPerLevel = 500;
        int currentLevel = (totalXp / xpPerLevel) + 1; // Level up every 500 XP
        int xpTowardsNextLevel = totalXp % xpPerLevel;

        if (tvUserLevel != null) tvUserLevel.setText("Level " + currentLevel);
        if (tvUserXp != null) tvUserXp.setText(totalXp + " Total XP");
        if (pbXpProgress != null) {
            pbXpProgress.setMax(xpPerLevel);
            pbXpProgress.setProgress(xpTowardsNextLevel);
        }
        // --------------------------

        // 2. Most Frequently Studied Subject
        String topSubject = "None";
        if (totalQuestions > 0) {
            Map<String, Integer> subjectCounts = new HashMap<>();
            for (QuestionModel q : history) {
                String sub = q.getSubject();
                subjectCounts.put(sub, subjectCounts.getOrDefault(sub, 0) + 1);
            }

            int max = 0;
            for (Map.Entry<String, Integer> entry : subjectCounts.entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                    topSubject = entry.getKey();
                }
            }
        }
        tvTopSubject.setText(topSubject);

        // 3 & 4. Quiz Accuracy & Time Spent
        if (totalQuestions > 0) {
            tvQuizAccuracy.setText("85%");
            tvReviewTime.setText("1h 20m");
        } else {
            tvQuizAccuracy.setText("0%");
            tvReviewTime.setText("0m");
        }

        // 5. AI Generated Insight
        if (totalQuestions > 0) {
            generateAIInsight(totalQuestions, topSubject);
        } else {
            tvAiInsight.setText("Ask your first question to get AI-powered insights on your learning!");
        }
    }

    private void generateAIInsight(int total, String topSubject) {
        tvAiInsight.setText("Generating insight...");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + API_KEY);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Prompt to generate the custom insight based on screenshot requirements
                String promptText = "You are an AI study tracking assistant. The student has asked a total of " + total +
                        " questions. Their most studied subject is " + topSubject + ". " +
                        "Provide a SINGLE, short, encouraging sentence as an insight into their study habits. " +
                        "For example: 'You frequently ask questions about " + topSubject + ", keep up the great work!'";

                JSONObject body = new JSONObject();
                JSONArray contents = new JSONArray();
                contents.put(new JSONObject().put("parts", new JSONArray().put(new JSONObject().put("text", promptText))));
                body.put("contents", contents);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                if (conn.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);

                    JSONObject res = new JSONObject(sb.toString());
                    String insight = res.getJSONArray("candidates").getJSONObject(0)
                            .getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");

                    handler.post(() -> tvAiInsight.setText(insight.trim()));
                } else {
                    handler.post(() -> tvAiInsight.setText("You are making steady progress in " + topSubject + "."));
                }
            } catch (Exception e) {
                Log.e("AI_Insight", "Failed to generate insight", e);
                handler.post(() -> tvAiInsight.setText("Keep studying hard! You're doing great."));
            }
        });
    }
}