class PowerUpInventory {
    int lightOrbs;
    int traceOrbs;

    boolean collect(PowerUp powerUp) {
        if (powerUp.type == PowerUp.LIGHT) {
            if (lightOrbs == 2) {
                return false;
            }
            lightOrbs++;
        } else {
            if (traceOrbs == 1) {
                return false;
            }
            traceOrbs++;
        }
        return true;
    }

    boolean useLightOrb() {
        if (lightOrbs == 0) {
            return false;
        }
        lightOrbs--;
        return true;
    }

    boolean useTraceOrb() {
        if (traceOrbs == 0) {
            return false;
        }
        traceOrbs--;
        return true;
    }

    void clear() {
        lightOrbs = 0;
        traceOrbs = 0;
    }
}
