package edu.hitsz.application;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.bullet.HeroBullet;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class GameEngineTest {
    private TestGame game;

    private static final class TestGame extends GameEngine {
        TestGame() {
            super(GameDifficulty.EASY);
            enemyMaxNumber = 0;
            heroShootCycle = Double.POSITIVE_INFINITY;
            enemyShootCycle = Double.POSITIVE_INFINITY;
        }
        @Override protected void difficultyIncreaseAction() { }
        @Override protected boolean canSpawnBoss() { return false; }
    }

    @Before
    public void setUp() {
        ImageManager.initialize(InstrumentationRegistry.getInstrumentation().getTargetContext());
        game = new TestGame();
    }

    @Test
    public void bulletKillShouldScoreOnceAndRemoveBothObjects() {
        game.getEnemies().add(new MobEnemy(200, 200, 0, 0, 8));
        game.getHeroBullets().add(new HeroBullet(200, 200, 0, 0, 8));
        game.update();
        assertEquals(10, game.getScore());
        assertTrue(game.getEnemies().isEmpty());
        assertTrue(game.getHeroBullets().isEmpty());
        game.update();
        assertEquals(10, game.getScore());
    }

    @Test
    public void aircraftCollisionShouldEndGameWithoutAnyHeroBullets() {
        game.getHero().setLocation(256, 500);
        game.getEnemies().add(new MobEnemy(256, 500, 0, 0, 30));
        game.update();
        assertTrue(game.isGameOver());
        assertEquals(0, game.getHero().getHp());
    }

    @Test
    public void escapedEnemyAndBulletShouldDisappearWithoutScoring() {
        game.getEnemies().add(new MobEnemy(100, 767, 0, 10, 30));
        game.getHeroBullets().add(new HeroBullet(300, 1, 0, -14, 8));
        game.update();
        assertTrue(game.getEnemies().isEmpty());
        assertTrue(game.getHeroBullets().isEmpty());
        assertEquals(0, game.getScore());
    }

    @Test
    public void temporaryFireModeShouldExpireOnlyWhenSimulationAdvances() {
        game.getHero().activateScatterShootForDuration(80);
        assertEquals(3, game.getHero().shoot().size());
        game.update();
        assertEquals(3, game.getHero().shoot().size());
        game.update();
        assertEquals(1, game.getHero().shoot().size());
        assertEquals(-14, game.getHero().shoot().get(0).getSpeedY());
    }

    @Test
    public void restartShouldResetHeroAndScore() {
        game.getHero().decreaseHp(100);
        game.update();
        assertTrue(game.isGameOver());
        game = new TestGame();
        assertFalse(game.isGameOver());
        assertEquals(100, game.getHero().getHp());
        assertEquals(0, game.getScore());
        assertEquals(1, game.getHero().shoot().size());
    }

    @Test
    public void easyModeShouldSpawnAndShootAfterOriginalIntervals() {
        GameEngine easy = new EasyGame();
        for (int i = 0; i < 24; i++) { easy.update(); }
        assertEquals(1, easy.getEnemies().size());
        assertFalse(easy.getHeroBullets().isEmpty());
        assertEquals(24, easy.getBackgroundTop());
    }
}
