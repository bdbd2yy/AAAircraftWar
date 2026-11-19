package edu.hitsz.aircraft;

import edu.hitsz.application.GameConfig;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.factory.SupplyFactory;
import edu.hitsz.factory.SupplyType;
import edu.hitsz.prop.AbstractSupply;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.observer.SupplyEffectObserver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 敌机抽象父类
 * @author Yinsh
 */
public abstract class AbstractEnemy extends AbstractAircraft implements SupplyEffectObserver {

    private static final int DEFAULT_SUPPLY_SPEED = 5;

    protected double supplyRate = 0;

    protected SupplyType[] supplyTypes = new SupplyType[0];

    private final int score;

    public AbstractEnemy(int locationX, int locationY, int speedX, int speedY, int hp, int score) {
        super(locationX, locationY, speedX, speedY, hp);
        this.score = score;
    }

    @Override
    public void forward() {
        super.forward();
        if (locationY >= GameConfig.WINDOW_HEIGHT) {
            vanish();
        }
    }

    public int getScore() {
        return score;
    }

    public AbstractSupply generateSupply() {
        if (supplyTypes.length == 0 || supplyRate <= 0) {
            return null;
        }
        if (Math.random() > supplyRate) {
            return null;
        }

        SupplyType supplyType = supplyTypes[(int) (Math.random() * supplyTypes.length)];
        return SupplyFactory.createSupply(
                supplyType,
                getLocationX(),
                getLocationY(),
                0,
                DEFAULT_SUPPLY_SPEED
        );
    }

    public List<AbstractSupply> generateSupplies() {
        List<AbstractSupply> generatedSupplies = new ArrayList<>();
        AbstractSupply supply = generateSupply();
        if (supply != null) {
            generatedSupplies.add(supply);
        }
        return generatedSupplies;
    }

    @Override
    public BaseBullet createBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        return new EnemyBullet(locationX, locationY, speedX, speedY, power);
    }

    @Override
    public void onBomb() {
        vanish();
    }

    @Override
    public void onFreeze() {
        freezeFor(4000);
    }
}
