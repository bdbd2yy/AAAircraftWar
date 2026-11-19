package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;

/**
 * 火力道具
 * @author Yinsh
 */
public class FireSupply extends AbstractSupply {

    private static final int EFFECT_DURATION_MILLIS = 10000;

    public FireSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {
        heroAircraft.activateScatterShootForDuration(EFFECT_DURATION_MILLIS);
    }
}
