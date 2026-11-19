package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.factory.SupplyType;
import edu.hitsz.strategy.StraightShootStrategy;

import java.util.List;

/**
 * 精英敌机
 * 可射击，坠毁后按概率掉落基础道具
 * @author Yinsh
 */
public class EliteEnemy extends AbstractEnemy {

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp, 20);
        this.power = 15;
        this.supplyRate = 0.80;
        this.supplyTypes = new SupplyType[]{
                SupplyType.BLOOD,
                SupplyType.FIRE,
                SupplyType.FIRE_PLUS,
                SupplyType.FREEZE
        };
        setShootStrategy(new StraightShootStrategy());
    }
}
