package edu.hitsz.application;

public class EasyGame extends GameEngine {

    public EasyGame() {
        super(GameDifficulty.EASY);
        enemyMaxNumber = 4;
        mobEnemyRate = 0.70;
        eliteEnemyRate = 0.25;
        elitePlusEnemyRate = 0.05;
        enemySpawnCycle = 24;
        heroShootCycle = 5;
        enemyShootCycle = 28;
        bossScoreThreshold = Integer.MAX_VALUE;
    }

    @Override
    protected void difficultyIncreaseAction() {
    }

    @Override
    protected boolean canSpawnBoss() {
        return false;
    }
}
