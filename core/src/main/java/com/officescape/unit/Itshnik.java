package com.officescape.unit;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.officescape.GameConstants;

import static com.officescape.GameConstants.*;
import static com.officescape.GameConstants.SMALL_MEET_CENTER;

public class Itshnik extends NPC{
    public Itshnik(String texturePath, int x, int y) {
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
            ITSHNIK_START, SERVER_TOP, COFFEE_MACHINE
        };
    }
}
