package com.example.workwithai.Activities; // REPLACE WITH YOUR ACTUAL PACKAGE NAME

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workwithai.R;

public class AISubmissionActivity extends AppCompatActivity {

    private Spinner spinnerSubject;
    private EditText etQuestionText;
    private Button btnUploadImage;
    private ImageView ivImagePreview;
    private Button btnSubmit;

    private Uri selectedImageUri = null;

    // Subjects derived from the requirements image
    private final String[] subjects = {
            "Mathematics",
            "Science",
            "Programming",
            "History",
            "Languages"
    };

    // Modern way to handle returning a result from another activity (Gallery)
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivImagePreview.setImageURI(uri);
                    ivImagePreview.setVisibility(View.VISIBLE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_submission); // Ensure this matches your XML file name

        // Initialize Views
        spinnerSubject = findViewById(R.id.spinnerSubject);
        etQuestionText = findViewById(R.id.etQuestionText);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        ivImagePreview = findViewById(R.id.ivImagePreview);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Populate the Spinner with subjects
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                subjects
        );
        spinnerSubject.setAdapter(adapter);

        // Handle Image Upload Click
        btnUploadImage.setOnClickListener(v -> {
            // Opens the device's file picker to select an image
            mGetContent.launch("image/*");
        });

        // Handle Submit Click
        btnSubmit.setOnClickListener(v -> submitQuestion());
    }

    private void submitQuestion() {
        String selectedSubject = spinnerSubject.getSelectedItem().toString();
        String questionText = etQuestionText.getText().toString().trim();

        // Validation: Ensure at least text or an image is provided
        if (questionText.isEmpty() && selectedImageUri == null) {
            Toast.makeText(this, "Please provide a question (text or image).", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Send data to your backend or AI service here
        // You have access to:
        // 1. selectedSubject
        // 2. questionText
        // 3. selectedImageUri (convert to File, Base64, or Multipart depending on your API)

        String successMessage = "Submitting to " + selectedSubject + " AI...";
        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();

        // Optional: clear form after successful submission
        // etQuestionText.setText("");
        // selectedImageUri = null;
        // ivImagePreview.setVisibility(View.GONE);
        // Add this inside the submitQuestion() method of AISubmissionActivity:
        Intent intent = new Intent(AISubmissionActivity.this, AIAnswerActivity.class);
        // You can pass the question ID or text using intent.putExtra("question", questionText);
        startActivity(intent);


    }
}