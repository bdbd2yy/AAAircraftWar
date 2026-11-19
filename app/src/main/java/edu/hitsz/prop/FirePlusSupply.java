package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;

/**
 * 超级火力道具
 * @author Yinsh
 */
public class FirePlusSupply extends AbstractSupply {

    private static final int EFFECT_DURATION_MILLIS = 10000;

    public FirePlusSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {
        heroAircraft.activateCircularShootForDuration(EFFECT_DURATION_MILLIS);
    }
}
