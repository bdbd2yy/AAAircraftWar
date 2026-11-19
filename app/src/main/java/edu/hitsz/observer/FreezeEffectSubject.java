package edu.hitsz.observer;

public class FreezeEffectSubject extends SupplyEffectSubject {

    @Override
    public void notifyObservers() {
        for (SupplyEffectObserver observer : getObservers()) {
            observer.onFreeze();
        }
    }
}
