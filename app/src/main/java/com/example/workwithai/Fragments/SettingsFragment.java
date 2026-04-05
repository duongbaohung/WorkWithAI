package com.example.workwithai.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workwithai.Adapters.HistoryAdapter;
import com.example.workwithai.Models.QuestionModel;
import com.example.workwithai.R;
import com.example.workwithai.Repositories.QuestionRepository;

import java.util.List;

/**
 * A fragment that handles both app settings and the Personal Question Library.
 * It allows users to search and filter their past AI-generated answers.
 */
public class SettingsFragment extends Fragment {

    private RecyclerView rvHistory;
    private EditText etSearch;
    private Spinner spinnerFilter;
    private QuestionRepository repo;
    private HistoryAdapter adapter;
    private TextView tvEmptyMessage;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment (fragment_settings.xml)
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize Library components found in the updated fragment_settings layout
        rvHistory = view.findViewById(R.id.rvHistory);
        etSearch = view.findViewById(R.id.etSearch);
        spinnerFilter = view.findViewById(R.id.spinnerFilterSubject);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);

        // Initialize the database repository
        repo = new QuestionRepository(requireContext());

        setupFilterSpinner();

        // Setup Search Functionality: Listen for text changes in the search bar
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadHistoryFromDatabase();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Initial load of history
        loadHistoryFromDatabase();

        return view;
    }

    /**
     * Populates the subject filter dropdown and handles selection events.
     */
    private void setupFilterSpinner() {
        String[] filters = {"All Subjects", "Mathematics", "Science", "Programming", "History", "Languages"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, filters);
        spinnerFilter.setAdapter(spinnerAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadHistoryFromDatabase();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /**
     * Fetches questions from the SQLite database based on current search and filter criteria.
     */
    private void loadHistoryFromDatabase() {
        String query = etSearch.getText().toString().trim();
        String subject = spinnerFilter.getSelectedItem().toString();

        // Logic to handle "All Subjects" vs specific categories
        if (subject.equals("All Subjects")) {
            subject = "All";
        }

        // Fetch data using the Repository
        List<QuestionModel> data = repo.getAllHistory(query, subject);

        // Handle UI states for empty vs populated history
        if (data.isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);

            // Set up the RecyclerView with the HistoryAdapter
            adapter = new HistoryAdapter(data, requireContext());
            rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
            rvHistory.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the list every time the user navigates back to this tab
        // to ensure new questions are shown immediately.
        loadHistoryFromDatabase();
    }
}