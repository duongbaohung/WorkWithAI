package com.example.workwithai.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.workwithai.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuizFragment extends Fragment {

    private RecyclerView rvQuiz;
    private Button btnGenerateQuiz;
    private LinearLayout llLoading;

    public QuizFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quiz, container, false);

        rvQuiz = v.findViewById(R.id.rvQuiz);
        btnGenerateQuiz = v.findViewById(R.id.btnGenerateQuiz);
        llLoading = v.findViewById(R.id.llLoading);

        // When the button is clicked, start the simulation instead of the real API
        btnGenerateQuiz.setOnClickListener(view -> simulateQuizGeneration());

        return v;
    }

    private void simulateQuizGeneration() {
        // Show the loading overlay
        llLoading.setVisibility(View.VISIBLE);

        // Simulate a 2-second network delay to act like the AI is "thinking"
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                // Create a mock JSON array of questions that perfectly matches
                // the format the QuizAdapter is expecting
                JSONArray quizArray = new JSONArray();

                // Question 1
                JSONObject q1 = new JSONObject();
                q1.put("question", "What is the derivative of x²?");
                q1.put("options", new JSONArray(new String[]{"x", "2x", "x²", "2"}));
                q1.put("answer", "2x");
                q1.put("explanation", "Using the power rule, you multiply by the exponent and subtract 1 from the exponent.");
                quizArray.put(q1);

                // Question 2
                JSONObject q2 = new JSONObject();
                q2.put("question", "In programming, what does 'OOP' stand for?");
                q2.put("options", new JSONArray(new String[]{"Object-Oriented Programming", "Only Output Prints", "Overly Obscure Processes", "Optical Output Protocol"}));
                q2.put("answer", "Object-Oriented Programming");
                q2.put("explanation", "OOP is a programming paradigm based on the concept of 'objects', which can contain data and code.");
                quizArray.put(q2);

                // Question 3
                JSONObject q3 = new JSONObject();
                q3.put("question", "Which planet is known as the Red Planet?");
                q3.put("options", new JSONArray(new String[]{"Venus", "Jupiter", "Mars", "Saturn"}));
                q3.put("answer", "Mars");
                q3.put("explanation", "Mars is often called the Red Planet because of iron oxide (rust) on its surface.");
                quizArray.put(q3);

                // Hide the loading overlay and display the generated quiz list
                llLoading.setVisibility(View.GONE);
                displayQuiz(quizArray);
                Toast.makeText(getContext(), "Mock Quiz Generated!", Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
                llLoading.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error creating mock quiz.", Toast.LENGTH_SHORT).show();
            }
        }, 2000); // Wait for 2 seconds
    }

    private void displayQuiz(JSONArray questions) {
        QuizAdapter adapter = new QuizAdapter(questions, getContext());
        rvQuiz.setLayoutManager(new LinearLayoutManager(getContext()));
        rvQuiz.setAdapter(adapter);
    }
}