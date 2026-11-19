package edu.hitsz.application;

public enum GameDifficulty {
    EASY("EASY", "easy"),
    MEDIUM("MEDIUM", "medium"),
    HARD("HARD", "hard");

    private final String displayName;
    private final String fileName;

    GameDifficulty(String displayName, String fileName) {
        this.displayName = displayName;
        this.fileName = fileName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFileName() {
        return fileName;
    }
}
