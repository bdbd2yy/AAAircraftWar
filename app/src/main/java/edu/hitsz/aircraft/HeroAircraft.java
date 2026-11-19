package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.GameConfig;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.strategy.CircularShootStrategy;
import edu.hitsz.strategy.ScatterShootStrategy;
import edu.hitsz.strategy.StraightShootStrategy;

/**
 * 英雄飞机，游戏玩家操控
 * 采用单例模式，保证全局仅存在一个英雄机实例
 * @author Yinsh
 */
public class HeroAircraft extends AbstractAircraft {

    private static final int INITIAL_HP = 100;
    private static final int SCATTER_SHOOT_NUM = 3;
    private static final int CIRCULAR_SHOOT_NUM = 20;
    private static final int STRAIGHT_SHOOT_NUM = 1;
    private static final int HERO_BULLET_POWER = 8;
    private static final int HERO_BULLET_SPEED = 14;
    private static HeroAircraft instance;

    private long fireModeRecoverAt;

    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.power = HERO_BULLET_POWER;
        this.direction = -1;
        useStraightShoot();
    }

    public static synchronized HeroAircraft getInstance() {
        if (instance == null) {
            instance = new HeroAircraft(
                    GameConfig.WINDOW_WIDTH / 2,
                    GameConfig.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                    0,
                    0,
                    INITIAL_HP
            );
        }
        return instance;
    }

    public synchronized void reset() {
        fireModeRecoverAt = 0;
        this.setLocation(
                GameConfig.WINDOW_WIDTH / 2,
                GameConfig.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight()
        );
        this.speedX = 0;
        this.speedY = 0;
        this.hp = this.maxHp;
        this.isValid = true;
        useStraightShoot();
    }

    @Override
    public void forward() {
        // Touch controls movement; simulation time controls temporary fire modes.
        if (fireModeRecoverAt > 0 && edu.hitsz.application.GameClock.now() >= fireModeRecoverAt) {
            fireModeRecoverAt = 0;
            useStraightShoot();
        }
    }

    @Override
    public BaseBullet createBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        int[] bulletSpeed = boostBulletSpeed(speedX, speedY);
        return new HeroBullet(locationX, locationY, bulletSpeed[0], bulletSpeed[1], power);
    }

    private int[] boostBulletSpeed(int speedX, int speedY) {
        double currentSpeed = Math.hypot(speedX, speedY);
        if (currentSpeed == 0 || currentSpeed >= HERO_BULLET_SPEED) {
            return new int[] {speedX, speedY};
        }

        double scale = HERO_BULLET_SPEED / currentSpeed;
        return new int[] {
                (int) Math.round(speedX * scale),
                (int) Math.round(speedY * scale)
        };
    }

    public void useStraightShoot() {
        setShootNum(STRAIGHT_SHOOT_NUM);
        setShootStrategy(new StraightShootStrategy());
    }

    public void useScatterShoot() {
        setShootNum(SCATTER_SHOOT_NUM);
        setShootStrategy(new ScatterShootStrategy(2));
    }

    public void useCircularShoot() {
        setShootNum(CIRCULAR_SHOOT_NUM);
        setShootStrategy(new CircularShootStrategy());
    }

    public void activateScatterShootForDuration(int durationMillis) {
        activateShootModeForDuration(this::useScatterShoot, durationMillis);
    }

    public void activateCircularShootForDuration(int durationMillis) {
        activateShootModeForDuration(this::useCircularShoot, durationMillis);
    }

    private void activateShootModeForDuration(Runnable activateAction, int durationMillis) {
        activateAction.run();
        fireModeRecoverAt = edu.hitsz.application.GameClock.now() + durationMillis;
    }
}
