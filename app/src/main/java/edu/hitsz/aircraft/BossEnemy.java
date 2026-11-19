package edu.hitsz.aircraft;

import edu.hitsz.application.GameConfig;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.factory.SupplyType;
import edu.hitsz.prop.AbstractSupply;
import edu.hitsz.strategy.CircularShootStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Boss敌机
 * 当前迭代仅完成类定义与基础行为骨架
 * @author Yinsh
 */
public class BossEnemy extends AbstractEnemy {

    private static final int DROP_SUPPLY_COUNT = 3;

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp, 100);
        this.power = 30;
        this.shootNum = 20;
        this.supplyTypes = new SupplyType[]{
                SupplyType.BLOOD,
                SupplyType.FIRE,
                SupplyType.FIRE_PLUS,
                SupplyType.BOMB,
                SupplyType.FREEZE
        };
        setShootStrategy(new CircularShootStrategy());
    }

    @Override
    public void forward() {
        locationX += speedX;
        if (locationX <= 0 || locationX >= GameConfig.WINDOW_WIDTH) {
            speedX = -speedX;
        }
    }

    @Override
    public List<AbstractSupply> generateSupplies() {
        List<AbstractSupply> generatedSupplies = new ArrayList<>();
        for (int i = 0; i < DROP_SUPPLY_COUNT; i++) {
            SupplyType supplyType = supplyTypes[(int) (Math.random() * supplyTypes.length)];
            generatedSupplies.add(edu.hitsz.factory.SupplyFactory.createSupply(
                    supplyType,
                    getLocationX(),
                    getLocationY(),
                    0,
                    5
            ));
        }
        return generatedSupplies;
    }

    @Override
    public void onBomb() {
    }

    @Override
    public void onFreeze() {
    }
}
