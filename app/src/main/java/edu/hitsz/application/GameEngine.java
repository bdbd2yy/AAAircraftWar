package edu.hitsz.application;

import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.factory.BossEnemyFactory;
import edu.hitsz.factory.EliteEnemyFactory;
import edu.hitsz.factory.ElitePlusEnemyFactory;
import edu.hitsz.factory.EliteProEnemyFactory;
import edu.hitsz.factory.EnemyFactory;
import edu.hitsz.factory.MobEnemyFactory;
import edu.hitsz.prop.AbstractSupply;
import edu.hitsz.prop.BombSupply;
import edu.hitsz.prop.FreezeSupply;
import edu.hitsz.observer.BombEffectSubject;
import edu.hitsz.observer.FreezeEffectSubject;
import edu.hitsz.observer.SupplyEffectSubject;

import java.util.LinkedList;
import java.util.List;

public abstract class GameEngine {

    protected double mobEnemyRate = 0.40;
    protected double eliteEnemyRate = 0.25;
    protected double elitePlusEnemyRate = 0.20;
    protected int bossScoreThreshold = 300;

    private int backGroundTop = 0;

    private final HeroAircraft heroAircraft;
    private final List<AbstractEnemy> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<AbstractSupply> supplies;

    protected int enemyMaxNumber = 5;

    protected double enemySpawnCycle = 20;
    private int enemySpawnCounter = 0;

    protected double heroShootCycle = 20;
    protected double enemyShootCycle = 20;
    private int heroShootCounter = 0;
    private int enemyShootCounter = 0;

    protected double enemySpeedFactor = 1.0;
    protected int enemyHpBonus = 0;
    protected int bossHpBonus = 0;

    private int score = 0;
    private int nextBossScore;
    private boolean bossActive = false;

    private boolean gameOverFlag = false;

    private final EnemyFactory mobEnemyFactory;
    private final EnemyFactory eliteEnemyFactory;
    private final EnemyFactory elitePlusEnemyFactory;
    private final EnemyFactory eliteProEnemyFactory;
    private final EnemyFactory bossEnemyFactory;
    private final GameDifficulty difficulty;

    protected GameEngine(GameDifficulty difficulty) {
        this.difficulty = difficulty;

        heroAircraft = HeroAircraft.getInstance();
        heroAircraft.reset();
        nextBossScore = bossScoreThreshold;

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        supplies = new LinkedList<>();

        mobEnemyFactory = new MobEnemyFactory();
        eliteEnemyFactory = new EliteEnemyFactory();
        elitePlusEnemyFactory = new ElitePlusEnemyFactory();
        eliteProEnemyFactory = new EliteProEnemyFactory();
        bossEnemyFactory = new BossEnemyFactory();

    }

    /** One simulation step, called only by the SurfaceView game thread. */
    public final void update() {
        if (gameOverFlag) {
            return;
        }
        GameClock.advance();
        heroAircraft.forward();
        difficultyIncreaseAction();
        enemySpawnAction();
        shootAction();
        bulletsMoveAction();
        aircraftsMoveAction();
        supplyMoveAction();
        crashCheckAction();
        postProcessAction();
        checkResultAction();
        backGroundTop = (backGroundTop + 1) % GameConfig.WINDOW_HEIGHT;
    }

    private void enemySpawnAction() {
        enemySpawnCounter++;
        if (enemySpawnCounter < enemySpawnCycle) {
            return;
        }
        enemySpawnCounter = 0;

        if (shouldSpawnBoss()) {
            enemyAircrafts.add(createBossEnemy());
            bossActive = true;
            nextBossScore += bossScoreThreshold;

            return;
        }

        if (enemyAircrafts.size() >= enemyMaxNumber) {
            return;
        }

        enemyAircrafts.add(createEnemy());
    }

    private AbstractEnemy createEnemy() {
        int maxEnemyWidth = Math.max(
                Math.max(ImageManager.MOB_ENEMY_IMAGE.getWidth(), ImageManager.ELITE_ENEMY_IMAGE.getWidth()),
                Math.max(ImageManager.ELITE_PLUS_ENEMY_IMAGE.getWidth(), ImageManager.ELITE_PRO_ENEMY_IMAGE.getWidth())
        );
        int locationX = maxEnemyWidth / 2 + (int) (Math.random() * (GameConfig.WINDOW_WIDTH - maxEnemyWidth));
        int locationY = (int) (Math.random() * GameConfig.WINDOW_HEIGHT * 0.05);
        EnemyFactory enemyFactory = selectEnemyFactory();
        AbstractEnemy enemy = enemyFactory.createEnemy(locationX, locationY);
        configureEnemy(enemy);
        return enemy;
    }

    private AbstractEnemy createBossEnemy() {
        int bossWidth = ImageManager.BOSS_ENEMY_IMAGE.getWidth();
        int bossHeight = ImageManager.BOSS_ENEMY_IMAGE.getHeight();
        int locationX = bossWidth / 2 + (int) (Math.random() * (GameConfig.WINDOW_WIDTH - bossWidth));
        AbstractEnemy bossEnemy = bossEnemyFactory.createEnemy(locationX, bossHeight / 2);
        configureBossEnemy(bossEnemy);
        System.out.println(difficulty.getDisplayName() + " Boss appears, hp=" + bossEnemy.getHp());
        return bossEnemy;
    }

    private boolean shouldSpawnBoss() {
        return canSpawnBoss() && !bossActive && score >= nextBossScore;
    }

    private EnemyFactory selectEnemyFactory() {
        double random = Math.random();
        if (random < mobEnemyRate) {
            return mobEnemyFactory;
        }
        if (random < mobEnemyRate + eliteEnemyRate) {
            return eliteEnemyFactory;
        }
        if (random < mobEnemyRate + eliteEnemyRate + elitePlusEnemyRate) {
            return elitePlusEnemyFactory;
        }
        return eliteProEnemyFactory;
    }

    private void shootAction() {
        heroShootCounter++;
        if (heroShootCounter >= heroShootCycle) {
            heroShootCounter = 0;
            heroBullets.addAll(heroAircraft.shoot());
        }

        enemyShootCounter++;
        if (enemyShootCounter >= enemyShootCycle) {
            enemyShootCounter = 0;
            for (AbstractEnemy enemyAircraft : enemyAircrafts) {
                if (!enemyAircraft.notValid()) {
                    enemyBullets.addAll(enemyAircraft.shoot());
                }
            }
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void supplyMoveAction() {
        for (AbstractSupply supply : supplies) {
            supply.forward();
        }
    }

    private void crashCheckAction() {
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractEnemy enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    continue;
                }

                if (enemyAircraft.crash(bullet)) {
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();

                    if (enemyAircraft.notValid()) {
                        handleEnemyDestroyed(enemyAircraft);
                    }
                    break;
                }

            }
        }

        for (AbstractEnemy enemy : enemyAircrafts) {
            if (!enemy.notValid() && !heroAircraft.notValid() && enemy.crash(heroAircraft)) {
                enemy.vanish();
                heroAircraft.decreaseHp(Integer.MAX_VALUE);
            }
        }

        for (AbstractSupply supply : supplies) {
            if (supply.notValid()) {
                continue;
            }
            if (heroAircraft.crash(supply) || supply.crash(heroAircraft)) {

                if (supply instanceof BombSupply) {
                    activateBombSupply();

                } else if (supply instanceof FreezeSupply) {
                    activateFreezeSupply();
                    supply.activate(heroAircraft);
                } else {
                    supply.activate(heroAircraft);
                }
                supply.vanish();
            }
        }
    }

    private void activateBombSupply() {
        List<AbstractEnemy> aliveEnemies = getAliveEnemies();
        SupplyEffectSubject bombEffectSubject = new BombEffectSubject();
        registerObservers(bombEffectSubject);
        bombEffectSubject.notifyObservers();

        for (AbstractEnemy enemyAircraft : aliveEnemies) {
            if (enemyAircraft.notValid()) {
                handleEnemyDestroyed(enemyAircraft, false);
            }
        }
    }

    private void activateFreezeSupply() {
        SupplyEffectSubject freezeEffectSubject = new FreezeEffectSubject();
        registerObservers(freezeEffectSubject);
        freezeEffectSubject.notifyObservers();
    }

    private void registerObservers(SupplyEffectSubject subject) {
        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            if (!enemyAircraft.notValid()) {
                subject.addObserver(enemyAircraft);
            }
        }
        for (BaseBullet enemyBullet : enemyBullets) {
            if (!enemyBullet.notValid()) {
                subject.addObserver(enemyBullet);
            }
        }
    }

    private List<AbstractEnemy> getAliveEnemies() {
        List<AbstractEnemy> aliveEnemies = new LinkedList<>();
        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            if (!enemyAircraft.notValid()) {
                aliveEnemies.add(enemyAircraft);
            }
        }
        return aliveEnemies;
    }

    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        supplies.removeIf(AbstractFlyingObject::notValid);
    }

    private void checkResultAction() {
        if (!gameOverFlag && heroAircraft.getHp() <= 0) {
            gameOverFlag = true;

        }
    }

    private void handleEnemyDestroyed(AbstractEnemy enemyAircraft) {
        handleEnemyDestroyed(enemyAircraft, true);
    }

    private void handleEnemyDestroyed(AbstractEnemy enemyAircraft, boolean dropSupply) {
        score += enemyAircraft.getScore();
        if (dropSupply) {
            supplies.addAll(enemyAircraft.generateSupplies());
        }
        if (enemyAircraft instanceof BossEnemy) {
            bossActive = false;
            if (!gameOverFlag) {

            }
        }
    }

    private void configureEnemy(AbstractEnemy enemy) {
        enemy.adjustSpeed(enemySpeedFactor);
        if (enemyHpBonus > 0) {
            enemy.increaseMaxHp(enemyHpBonus);
        }
    }

    private void configureBossEnemy(AbstractEnemy bossEnemy) {
        if (bossHpBonus > 0) {
            bossEnemy.increaseMaxHp(bossHpBonus);
        }
        onBossCreated();
    }

    protected abstract void difficultyIncreaseAction();

    protected boolean canSpawnBoss() {
        return true;
    }

    protected void onBossCreated() {
    }

    public HeroAircraft getHero() { return heroAircraft; }
    public List<AbstractEnemy> getEnemies() { return enemyAircrafts; }
    public List<BaseBullet> getHeroBullets() { return heroBullets; }
    public List<BaseBullet> getEnemyBullets() { return enemyBullets; }
    public List<AbstractSupply> getSupplies() { return supplies; }
    public int getScore() { return score; }
    public int getBackgroundTop() { return backGroundTop; }
    public boolean isGameOver() { return gameOverFlag; }
}
