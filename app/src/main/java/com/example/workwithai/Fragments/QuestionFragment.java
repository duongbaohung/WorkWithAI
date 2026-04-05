package com.example.workwithai.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.workwithai.R;
// If AIAnswerActivity is in your main package, you might need this import:
import com.example.workwithai.Activities.AIAnswerActivity;

public class QuestionFragment extends Fragment {

    // Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // --- AI Feature Variables ---
    private Spinner spinnerSubject;
    private EditText etQuestionText;
    private Button btnUploadImage;
    private ImageView ivImagePreview;
    private Button btnSubmit;

    private Uri selectedImageUri = null;

    private final String[] subjects = {
            "Mathematics", "Science", "Programming", "History", "Languages"
    };

    // Modern way to handle returning a result from the gallery inside a Fragment
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivImagePreview.setImageURI(uri);
                    ivImagePreview.setVisibility(View.VISIBLE);
                }
            });
    // ----------------------------

    public QuestionFragment() {
        // Required empty public constructor
    }

    public static QuestionFragment newInstance(String param1, String param2) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        // Initialize Views using the inflated 'view'
        spinnerSubject = view.findViewById(R.id.spinnerSubject);
        etQuestionText = view.findViewById(R.id.etQuestionText);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        ivImagePreview = view.findViewById(R.id.ivImagePreview);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // Setup Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                subjects
        );
        spinnerSubject.setAdapter(adapter);

        // Handle Clicks
        btnUploadImage.setOnClickListener(v -> mGetContent.launch("image/*"));
        btnSubmit.setOnClickListener(v -> submitQuestion());

        return view;
    }

    private void submitQuestion() {
        String questionText = etQuestionText.getText().toString().trim();

        if (questionText.isEmpty() && selectedImageUri == null) {
            Toast.makeText(requireContext(), "Please provide a question (text or image).", Toast.LENGTH_SHORT).show();
            return;
        }

        // Launch the AIAnswerActivity and PASS the actual data to it
        Intent intent = new Intent(requireContext(), com.example.workwithai.Activities.AIAnswerActivity.class);

        // Pass text
        intent.putExtra("QUESTION_TEXT", questionText);

        // Pass image if available, with permissions so the next Activity can read it
        if (selectedImageUri != null) {
            intent.putExtra("IMAGE_URI", selectedImageUri.toString());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        startActivity(intent);
    }
}