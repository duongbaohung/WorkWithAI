package com.example.workwithai.Models;

public class QuestionModel {
    private int id;
    private String subject;
    private String difficulty;
    private String questionText;
    private String imageUri;
    private String concepts;
    private String steps;
    private String finalAnswer;
    private int isBookmarked; // 0 for false, 1 for true
    private long timestamp;

    public QuestionModel() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
    public String getConcepts() { return concepts; }
    public void setConcepts(String concepts) { this.concepts = concepts; }
    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }
    public String getFinalAnswer() { return finalAnswer; }
    public void setFinalAnswer(String finalAnswer) { this.finalAnswer = finalAnswer; }
    public int getIsBookmarked() { return isBookmarked; }
    public void setIsBookmarked(int isBookmarked) { this.isBookmarked = isBookmarked; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}