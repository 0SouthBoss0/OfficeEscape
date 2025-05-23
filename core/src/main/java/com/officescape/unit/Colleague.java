package com.officescape.unit;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.officescape.GameConstants;

public class Colleague extends NPC {
    public Colleague(String texturePath, int x, int y, GameConstants.Position[] waypoints) {
        super(texturePath, x, y, waypoints);
    }

    @Override
    protected void onCustomUpdate(float deltaTime, Array<Rectangle> walls) {
        walkThrough(deltaTime);
        isPlayerNearby(walls);
    }


}
