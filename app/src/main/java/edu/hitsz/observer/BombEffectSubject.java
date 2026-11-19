package edu.hitsz.observer;

public class BombEffectSubject extends SupplyEffectSubject {

    @Override
    public void notifyObservers() {
        for (SupplyEffectObserver observer : getObservers()) {
            observer.onBomb();
        }
    }
}
