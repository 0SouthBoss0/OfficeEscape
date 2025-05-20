package com.officescape.unit;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.officescape.GameConstants;


public class Babka extends NPC {
    public Babka(String texturePath, int x, int y, GameConstants.Position[] waypoints) {
        super(texturePath, x, y, waypoints);
    }

    @Override
    protected void onCustomUpdate(float deltaTime, Array<Rectangle> walls) {
        isPlayerNearby(walls);
    }

}
