package edu.hitsz.bullet;

import edu.hitsz.application.GameConfig;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.observer.SupplyEffectObserver;

/**
 * 子弹基类
 * @author Yinsh
 */
public abstract class BaseBullet extends AbstractFlyingObject implements SupplyEffectObserver {

    private int power = 0;

    public BaseBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY);
        this.power = power;
    }

    @Override
    public void forward() {
        super.forward();

        // 判定 x 轴出界
        if (locationX <= 0 || locationX >= GameConfig.WINDOW_WIDTH) {
            vanish();
        }

        // 判定 y 轴出界
        if (speedY > 0 && locationY >= GameConfig.WINDOW_HEIGHT ) {
            // 向下飞行出界
            vanish();
        }else if (locationY <= 0){
            // 向上飞行出界
            vanish();
        }
    }

    public int getPower() {
        return power;
    }

    @Override
    public void onBomb() {
        vanish();
    }

    @Override
    public void onFreeze() {
        freezeFor(5000);
    }
}
