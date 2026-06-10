package ru.yandex.practicum;

public class PlayerStatistic {

    private String nickname;

    private int wins;

    private int steps;

    private boolean usedHints;

    public PlayerStatistic() {
    }

    public PlayerStatistic(
            String nickname,
            int wins,
            int steps,
            boolean usedHints
    ) {
        this.nickname = nickname;
        this.wins = wins;
        this.steps = steps;
        this.usedHints = usedHints;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public boolean isUsedHints() {
        return usedHints;
    }

    public void setUsedHints(boolean usedHints) {
        this.usedHints = usedHints;
    }

    public void incrementWins() {
        wins++;
    }
}