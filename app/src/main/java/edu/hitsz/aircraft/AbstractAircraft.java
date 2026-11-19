package edu.hitsz.aircraft;

import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.NoShootStrategy;
import edu.hitsz.strategy.ShootStrategy;

import java.util.List;

/**
 * 所有种类飞机的抽象父类
 * @author Yinsh
 */
public abstract class AbstractAircraft extends AbstractFlyingObject {

    private ShootStrategy shootStrategy = new NoShootStrategy();

    // 最大生命值
    protected int maxHp;
    protected int hp;
    protected int shootNum = 1;
    protected int power = 10;
    protected int direction = 1;

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
    }

    public void decreaseHp(int decrease) {
        hp -= decrease;
        if (hp <= 0) {
            hp = 0;
            vanish();
        }
    }

    public void increaseHp(int increase) {
        hp += increase;
        if (hp > maxHp) {
            hp = maxHp;
        }
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void increaseMaxHp(int increase) {
        maxHp += increase;
        hp += increase;
    }

    /**
     * 飞机射击方法
     *
     * @return 可射击对象需实现，返回子弹列表；非可射击对象空实现，返回空列表
     */
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(this);
    }

    public void setShootStrategy(ShootStrategy shootStrategy) {
        this.shootStrategy = shootStrategy;
    }

    public int getShootNum() {
        return shootNum;
    }

    public void setShootNum(int shootNum) {
        this.shootNum = shootNum;
    }

    public int getPower() {
        return power;
    }

    public int getDirection() {
        return direction;
    }

    public abstract BaseBullet createBullet(int locationX, int locationY, int speedX, int speedY, int power);
}
