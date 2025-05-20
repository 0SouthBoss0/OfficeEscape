package com.officescape.unit;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.officescape.GameConstants;

import static com.officescape.GameConstants.*;

public class Colleague extends NPC {
    public Colleague(String texturePath, int x, int y) {
        super(texturePath, x, y);
    }

    @Override
    protected void onCustomUpdate(float deltaTime, Array<Rectangle> walls) {
        walkThrough(deltaTime);
        isPlayerNearby(walls);
    }

    @Override
    protected void setNPCWaypoints() {
        this.NPC_WAYPOINTS = new GameConstants.Position[]{
            COLLEAGUE1_START, COFFEE_EAT1, SMALL_MEET_RIGHT
        };
    }
}
