package com.example.workwithai.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workwithai.R;

public class DemoEventActivity extends AppCompatActivity {
    EditText edtData;
    Button btnGetData, btnCheckGender;
    CheckBox cbUnblock;
    RadioGroup radGroupGender;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_event);
        //find element in layout -View
        edtData = findViewById(R.id.edtData);
        btnGetData = findViewById(R.id.btnGetData);
        cbUnblock = findViewById(R.id.cbUnblock);
        btnCheckGender = findViewById(R.id.btnCheckGender);
        radGroupGender = findViewById(R.id.radGroupGender);
        //block elements
        edtData.setEnabled(false);
        btnGetData.setEnabled(false);
        //unblock elements
        cbUnblock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    edtData.setEnabled(true);
                    btnGetData.setEnabled(true);
                }else {
                    edtData.setEnabled(false);
                    btnGetData.setEnabled(false);
                }
            }
        });
        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = edtData.getText().toString().trim();
                if (TextUtils.isEmpty(data)){
                    edtData.setError("Data is required");
                    return;
                }
                Toast.makeText(DemoEventActivity.this, data, Toast.LENGTH_SHORT).show();

            }
        });
        /*
        btnCheckGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. Get the ID of the selected RadioButton in the group
                int selectedId = radGroupGender.getCheckedRadioButtonId();

                // 2. Check if any RadioButton is actually selected
                if (selectedId == -1) {
                    Toast.makeText(DemoEventActivity.this, "Please select a gender!", Toast.LENGTH_SHORT).show();
                } else {
                    // 3. Find the RadioButton view using the ID
                    RadioButton selectedRadio = findViewById(selectedId);

                    // 4. Get the text (e.g., "Male" or "Female") and show it
                    String gender = selectedRadio.getText().toString();
                    Toast.makeText(DemoEventActivity.this, "Selected Gender: " + gender, Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        btnCheckGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radGroupGender.getCheckedRadioButtonId();
                RadioButton radGender = (RadioButton) findViewById(selectedId);
                if (radGender == null){
                  //user did not choose gender
                  Toast.makeText(DemoEventActivity.this, "Gender is rrequired", Toast.LENGTH_SHORT).show();
                } else {
                  String gender = radGender.getText().toString().trim();
                  Toast.makeText(DemoEventActivity.this, gender, Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*
        radGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // The 'checkedId' tells us exactly which button was clicked
                RadioButton selectedRadio = findViewById(checkedId);

                if (selectedRadio != null) {
                    String gender = selectedRadio.getText().toString();
                    // Show the result immediately
                    Toast.makeText(DemoEventActivity.this, "Changed to: " + gender, Toast.LENGTH_SHORT).show();
                }
            }
        });
         */

    }
}
