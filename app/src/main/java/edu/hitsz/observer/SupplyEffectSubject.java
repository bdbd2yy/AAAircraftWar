package edu.hitsz.observer;

import java.util.ArrayList;
import java.util.List;

public abstract class SupplyEffectSubject {

    private final List<SupplyEffectObserver> observers = new ArrayList<>();

    public void addObserver(SupplyEffectObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(SupplyEffectObserver observer) {
        observers.remove(observer);
    }

    protected List<SupplyEffectObserver> getObservers() {
        return new ArrayList<>(observers);
    }

    public abstract void notifyObservers();
}
