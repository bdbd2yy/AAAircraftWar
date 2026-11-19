package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.aircraft.ElitePlusEnemy;

public class ElitePlusEnemyFactory implements EnemyFactory {

    private static final int SIDE_SPEED = 2;

    @Override
    public AbstractEnemy createEnemy(int locationX, int locationY) {
        int speedX = Math.random() < 0.5 ? -SIDE_SPEED : SIDE_SPEED;
        return new ElitePlusEnemy(locationX, locationY, speedX, 7, 80);
    }
}
