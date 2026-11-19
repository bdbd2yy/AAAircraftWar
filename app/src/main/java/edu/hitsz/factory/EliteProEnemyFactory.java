package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.aircraft.EliteProEnemy;

public class EliteProEnemyFactory implements EnemyFactory {

    private static final int SIDE_SPEED = 3;

    @Override
    public AbstractEnemy createEnemy(int locationX, int locationY) {
        int speedX = Math.random() < 0.5 ? -SIDE_SPEED : SIDE_SPEED;
        return new EliteProEnemy(locationX, locationY, speedX, 6, 100);
    }
}
