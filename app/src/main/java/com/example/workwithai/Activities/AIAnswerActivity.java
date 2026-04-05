package com.example.workwithai.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.workwithai.Models.QuestionModel;
import com.example.workwithai.R;
import com.example.workwithai.Repositories.QuestionRepository;

public class AIAnswerActivity extends AppCompatActivity {

    private TextView tvSubjectTag;
    private TextView tvDifficultyTag;
    private TextView tvConcepts;
    private TextView tvSteps;
    private TextView tvFinalAnswer;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_generation);

        // Initialize Views
        tvSubjectTag = findViewById(R.id.tvSubjectTag);
        tvDifficultyTag = findViewById(R.id.tvDifficultyTag);
        tvConcepts = findViewById(R.id.tvConcepts);
        tvSteps = findViewById(R.id.tvSteps);
        tvFinalAnswer = findViewById(R.id.tvFinalAnswer);
        progressBar = findViewById(R.id.progressBar);

        // Hide answer fields initially to show loading state
        setAnswerVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        // Retrieve the data passed from the QuestionFragment
        String questionText = getIntent().getStringExtra("QUESTION_TEXT");
        String imageUriString = getIntent().getStringExtra("IMAGE_URI");

        // Use the simulation instead of the real Gemini API for now
        simulateAIFetching(questionText, imageUriString);
    }

    private void simulateAIFetching(String questionText, String imageUriString) {
        // Simulate a 2-second network delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            String contextText = (questionText != null && !questionText.isEmpty()) ? questionText : "an image problem";

            // Mock structured data
            AIResponse mockResponse = new AIResponse(
                    "Mathematics",
                    "High School",
                    "• Pythagorean Theorem: a² + b² = c²\n• Helps find the hypotenuse of a right-angled triangle.",
                    "Step 1: Analyzed your question about: " + contextText + ".\n\n" +
                            "Step 2: Square both sides: 3² = 9, 4² = 16.\n\n" +
                            "Step 3: Add them together: 9 + 16 = 25.\n\n" +
                            "Step 4: Take the square root of the result: √25 = 5.",
                    "The hypotenuse (c) is 5."
            );

            displayAnswer(mockResponse);
            progressBar.setVisibility(View.GONE);
            setAnswerVisibility(View.VISIBLE);

        }, 2000);
    }

    private void displayAnswer(AIResponse response) {
        tvSubjectTag.setText(response.subject);
        tvDifficultyTag.setText("Level: " + response.difficulty);
        tvConcepts.setText(response.concepts);
        tvSteps.setText(response.steps);
        tvFinalAnswer.setText(response.finalAnswer);

        // --- AUTO-SAVE TO DATABASE ---
        try {
            QuestionRepository repo = new QuestionRepository(this);
            QuestionModel q = new QuestionModel();
            q.setSubject(response.subject);
            q.setDifficulty(response.difficulty);

            String qText = getIntent().getStringExtra("QUESTION_TEXT");
            String iUri = getIntent().getStringExtra("IMAGE_URI");
            q.setQuestionText(qText != null ? qText : "");
            q.setImageUri(iUri != null ? iUri : "");

            q.setConcepts(response.concepts);
            q.setSteps(response.steps);
            q.setFinalAnswer(response.finalAnswer);

            repo.saveQuestion(q);
            Toast.makeText(this, "Answer Generated & Saved to History!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("DatabaseError", "Failed to save to database: " + e.getMessage());
        }
    }

    private void setAnswerVisibility(int visibility) {
        tvSubjectTag.setVisibility(visibility);
        tvDifficultyTag.setVisibility(visibility);
        tvConcepts.setVisibility(visibility);
        tvSteps.setVisibility(visibility);
        tvFinalAnswer.setVisibility(visibility);

        View conceptsHeading = findViewById(R.id.tvConcepts);
        if(conceptsHeading != null) conceptsHeading.setVisibility(visibility);
    }

    private static class AIResponse {
        String subject, difficulty, concepts, steps, finalAnswer;
        public AIResponse(String s, String d, String c, String st, String f) {
            this.subject = s; this.difficulty = d; this.concepts = c; this.steps = st; this.finalAnswer = f;
        }
    }
}