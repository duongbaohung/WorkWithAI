package com.example.workwithai.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workwithai.Adapters.QuizAdapter;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizFragment extends Fragment {

    private RecyclerView rvQuiz;
    private Button btnGenerateQuiz;
    private LinearLayout llLoading;
    private final String API_KEY = "AIzaSyDMvTMw8K4UNBjLyZ3JO5yP4qpu62adV4c";

    public QuizFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quiz, container, false);

        rvQuiz = v.findViewById(R.id.rvQuiz);
        btnGenerateQuiz = v.findViewById(R.id.btnGenerateQuiz);
        llLoading = v.findViewById(R.id.llLoading);

        btnGenerateQuiz.setOnClickListener(view -> generateAIQuiz());

        return v;
    }

    private void generateAIQuiz() {
        QuestionRepository repo = new QuestionRepository(requireContext());
        List<QuestionModel> history = repo.getAllHistory("", "All");

        if (history.size() < 1) {
            Toast.makeText(getContext(), "Ask at least one question first to generate a quiz!", Toast.LENGTH_LONG).show();
            return;
        }

        llLoading.setVisibility(View.VISIBLE);

        // Prepare context from history
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < Math.min(history.size(), 5); i++) {
            context.append("Topic: ").append(history.get(i).getSubject())
                    .append(". Question: ").append(history.get(i).getQuestionText()).append("\n");
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + API_KEY);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String prompt = "Based on these previous study topics:\n" + context.toString() +
                        "\nGenerate a 3-question quiz. Use JSON format only: " +
                        "{\"questions\": [{\"type\": \"MCQ\", \"question\": \"...\", \"options\": [\"A\", \"B\", \"C\", \"D\"], \"answer\": \"Correct Option\", \"explanation\": \"...\"}]}";

                JSONObject body = new JSONObject();
                JSONArray contents = new JSONArray();
                contents.put(new JSONObject().put("parts", new JSONArray().put(new JSONObject().put("text", prompt))));
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
                    String rawJson = res.getJSONArray("candidates").getJSONObject(0)
                            .getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");

                    rawJson = rawJson.replace("```json", "").replace("```", "").trim();
                    JSONArray quizArray = new JSONObject(rawJson).getJSONArray("questions");

                    handler.post(() -> {
                        llLoading.setVisibility(View.GONE);
                        displayQuiz(quizArray);
                    });
                }
            } catch (Exception e) {
                handler.post(() -> {
                    llLoading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to generate quiz.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void displayQuiz(JSONArray questions) {
        QuizAdapter adapter = new QuizAdapter(questions, getContext());
        rvQuiz.setLayoutManager(new LinearLayoutManager(getContext()));
        rvQuiz.setAdapter(adapter);
    }
}