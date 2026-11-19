package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.GameConfig;
import edu.hitsz.basic.AbstractFlyingObject;

/**
 * 道具抽象父类
 * @author Yinsh
 */
public abstract class AbstractSupply extends AbstractFlyingObject {

    public AbstractSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void forward() {
        super.forward();
        if (locationY >= GameConfig.WINDOW_HEIGHT) {
            vanish();
        }
    }

    public abstract void activate(HeroAircraft heroAircraft);
}
