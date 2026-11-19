package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.factory.SupplyType;
import edu.hitsz.strategy.StraightShootStrategy;

import java.util.List;

/**
 * 精锐敌机
 * 当前迭代仅完成类定义与基础行为骨架
 * @author Yinsh
 */
public class ElitePlusEnemy extends AbstractEnemy {

    public ElitePlusEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp, 30);
        this.power = 20;
        this.shootNum = 2;
        this.supplyRate = 0.90;
        this.supplyTypes = new SupplyType[]{
                SupplyType.BLOOD,
                SupplyType.FIRE,
                SupplyType.FIRE_PLUS,
                SupplyType.BOMB,
                SupplyType.FREEZE
        };
        setShootStrategy(new StraightShootStrategy());
    }

    @Override
    public void onFreeze() {
        freezeFor(3000);
    }
}
