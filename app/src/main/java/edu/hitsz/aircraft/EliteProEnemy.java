package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.factory.SupplyType;
import edu.hitsz.strategy.ScatterShootStrategy;

import java.util.List;

/**
 * 王牌敌机
 * 当前迭代仅完成类定义与基础行为骨架
 * @author Yinsh
 */
public class EliteProEnemy extends AbstractEnemy {

    public EliteProEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp, 40);
        this.power = 25;
        this.shootNum = 3;
        this.supplyRate = 0.95;
        this.supplyTypes = new SupplyType[]{
                SupplyType.BLOOD,
                SupplyType.FIRE,
                SupplyType.FIRE_PLUS,
                SupplyType.BOMB,
                SupplyType.FREEZE
        };
        setShootStrategy(new ScatterShootStrategy(2));
    }

    @Override
    public void onBomb() {
        decreaseHp(50);
    }

    @Override
    public void onFreeze() {
        slowFor(0.5, 5000);
    }
}
