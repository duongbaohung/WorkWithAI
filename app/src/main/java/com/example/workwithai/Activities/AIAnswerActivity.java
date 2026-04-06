package com.example.workwithai.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.workwithai.Models.QuestionModel;
import com.example.workwithai.R;
import com.example.workwithai.Repositories.QuestionRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIAnswerActivity extends AppCompatActivity {

    private TextView tvSubjectTag, tvDifficultyTag, tvConcepts, tvSteps, tvFinalAnswer;
    private ProgressBar progressBar;

    /**
     * API KEY:
     * Using your Google AI Studio Free Tier key.
     */
    private final String apiKey = "AIzaSyC7t-TsIWIhulvKYiJnsmFhfgqhXu_NRK8";

    /**
     * MODEL NAME:
     * Using 'gemini-1.5-flash' for production compatibility.
     */
    private final String modelName = "gemini-2.5-flash";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_generation);

        tvSubjectTag = findViewById(R.id.tvSubjectTag);
        tvDifficultyTag = findViewById(R.id.tvDifficultyTag);
        tvConcepts = findViewById(R.id.tvConcepts);
        tvSteps = findViewById(R.id.tvSteps);
        tvFinalAnswer = findViewById(R.id.tvFinalAnswer);
        progressBar = findViewById(R.id.progressBar);

        setAnswerVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        String questionText = getIntent().getStringExtra("QUESTION_TEXT");
        String imageUriString = getIntent().getStringExtra("IMAGE_URI");

        fetchAIAnswer(questionText, imageUriString);
    }

    private void fetchAIAnswer(String questionText, String imageUriString) {
        executorService.execute(() -> {
            String base64Image = null;
            if (imageUriString != null) {
                base64Image = convertUriToBase64(Uri.parse(imageUriString));
            }

            // Using v1beta for advanced JSON schema support
            String result = performApiRequestWithBackoff(questionText, base64Image, 0);

            runOnUiThread(() -> {
                if (result != null) {
                    processAIResponse(result);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "AI Connection Error. Check Logcat (AI_DEBUG)", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private String convertUriToBase64(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int width = 1024;
            int height = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, height, true);

            scaled.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] bytes = baos.toByteArray();
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e("AI_DEBUG", "Image Conversion Error: " + e.getMessage());
            return null;
        }
    }

    private String performApiRequestWithBackoff(String userQuery, String base64Image, int retryCount) {
        HttpURLConnection conn = null;
        try {
            // v1beta is MANDATORY for response_schema and system_instruction
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 1. Build the Parts Array
            JSONArray parts = new JSONArray();

            JSONObject textPart = new JSONObject();
            textPart.put("text", userQuery != null ? userQuery : "Analyze this academic problem.");
            parts.put(textPart);

            if (base64Image != null) {
                JSONObject imagePart = new JSONObject();
                JSONObject inlineData = new JSONObject();
                inlineData.put("mimeType", "image/jpeg");
                inlineData.put("data", base64Image);
                imagePart.put("inlineData", inlineData);
                parts.put(imagePart);
            }

            // 2. Build Content
            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            // 3. Build System Instruction (CRITICAL: MUST USE snake_case)
            JSONObject sysInstruction = new JSONObject();
            JSONArray sysParts = new JSONArray();
            sysParts.put(new JSONObject().put("text", "You are an AI Study Mentor. Solve academic problems step-by-step. Respond strictly in the JSON format requested."));
            sysInstruction.put("parts", sysParts);

            // 4. Build Generation Config & Schema (CRITICAL: MUST USE snake_case)
            JSONObject genConfig = new JSONObject();
            genConfig.put("response_mime_type", "application/json");

            JSONObject schema = new JSONObject();
            schema.put("type", "object");
            JSONObject props = new JSONObject();
            props.put("subject", new JSONObject().put("type", "string"));
            props.put("difficulty", new JSONObject().put("type", "string"));
            props.put("concepts", new JSONObject().put("type", "string"));
            props.put("steps", new JSONObject().put("type", "string"));
            props.put("finalAnswer", new JSONObject().put("type", "string"));
            schema.put("properties", props);
            genConfig.put("response_schema", schema);

            // 5. Assemble Payload (CRITICAL: MUST USE snake_case)
            JSONObject payload = new JSONObject();
            payload.put("contents", contents);
            payload.put("system_instruction", sysInstruction);
            payload.put("generation_config", genConfig);

            OutputStream os = conn.getOutputStream();
            os.write(payload.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                return sb.toString();
            } else {
                InputStream errorStream = conn.getErrorStream();
                if (errorStream != null) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(errorStream));
                    StringBuilder errorSb = new StringBuilder();
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) errorSb.append(errorLine);
                    Log.e("AI_DEBUG", "Error " + responseCode + ": " + errorSb.toString());
                }

                if (retryCount < 3 && responseCode != 404) {
                    Thread.sleep(2000);
                    return performApiRequestWithBackoff(userQuery, base64Image, retryCount + 1);
                }
            }
        } catch (Exception e) {
            Log.e("AI_DEBUG", "Network Error: " + e.getMessage());
        } finally {
            if (conn != null) conn.disconnect();
        }
        return null;
    }

    private void processAIResponse(String jsonResponse) {
        try {
            JSONObject responseObj = new JSONObject(jsonResponse);
            String rawText = responseObj.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            JSONObject aiData = new JSONObject(rawText);

            tvSubjectTag.setText(aiData.optString("subject", "General"));
            tvDifficultyTag.setText("Level: " + aiData.optString("difficulty", "N/A"));
            tvConcepts.setText(aiData.optString("concepts", "N/A"));
            tvSteps.setText(aiData.optString("steps", "N/A"));
            tvFinalAnswer.setText(aiData.optString("finalAnswer", "N/A"));

            progressBar.setVisibility(View.GONE);
            setAnswerVisibility(View.VISIBLE);

            saveToHistory(aiData);

        } catch (Exception e) {
            Log.e("AI_DEBUG", "JSON Error: " + e.getMessage());
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error processing AI response.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToHistory(JSONObject data) {
        executorService.execute(() -> {
            try {
                QuestionRepository repo = new QuestionRepository(this);
                QuestionModel q = new QuestionModel();
                q.setSubject(data.optString("subject"));
                q.setDifficulty(data.optString("difficulty"));
                q.setQuestionText(getIntent().getStringExtra("QUESTION_TEXT"));
                q.setImageUri(getIntent().getStringExtra("IMAGE_URI"));
                q.setConcepts(data.optString("concepts"));
                q.setSteps(data.optString("steps"));
                q.setFinalAnswer(data.optString("finalAnswer"));
                repo.saveQuestion(q);
            } catch (Exception e) {
                Log.e("AI_DEBUG", "History Save Failed: " + e.getMessage());
            }
        });
    }

    private void setAnswerVisibility(int visibility) {
        tvSubjectTag.setVisibility(visibility);
        tvDifficultyTag.setVisibility(visibility);
        tvConcepts.setVisibility(visibility);
        tvSteps.setVisibility(visibility);
        tvFinalAnswer.setVisibility(visibility);
        findViewById(R.id.tvConcepts).setVisibility(visibility);
    }
}