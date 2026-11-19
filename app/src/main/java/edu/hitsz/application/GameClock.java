package edu.hitsz.application;

/** Simulation time, owned exclusively by the game thread. */
public final class GameClock {
    private static long millis;
    private GameClock() { }
    public static long now() { return millis; }
    public static void advance() { millis += GameConfig.STEP_MILLIS; }
}
