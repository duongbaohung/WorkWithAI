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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workwithai.Adapters.HistoryAdapter;
import com.example.workwithai.Models.QuestionModel;
import com.example.workwithai.R;
import com.example.workwithai.Repositories.QuestionRepository;

import java.util.List;

public class LibraryFragment extends Fragment {

    private RecyclerView rvHistory;
    private EditText etSearch;
    private Spinner spinnerFilter;
    private QuestionRepository repo;
    private HistoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_library, container, false);

        rvHistory = v.findViewById(R.id.rvHistory);
        etSearch = v.findViewById(R.id.etSearch);
        spinnerFilter = v.findViewById(R.id.spinnerFilterSubject);
        repo = new QuestionRepository(getContext());

        setupFilter();
        loadHistory();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { loadHistory(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        return v;
    }

    private void setupFilter() {
        String[] filters = {"All", "Mathematics", "Science", "Programming", "History", "Languages"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, filters);
        spinnerFilter.setAdapter(adapter);
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { loadHistory(); }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadHistory() {
        String query = etSearch.getText().toString();
        String subject = spinnerFilter.getSelectedItem().toString();
        List<QuestionModel> data = repo.getAllHistory(query, subject);

        adapter = new HistoryAdapter(data, getContext());
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setAdapter(adapter);
    }
}
