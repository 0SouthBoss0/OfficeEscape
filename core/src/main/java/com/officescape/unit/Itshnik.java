package com.officescape.unit;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.officescape.GameConstants;

import static com.officescape.GameConstants.*;
import static com.officescape.GameConstants.SMALL_MEET_CENTER;

public class Itshnik extends NPC{
    public Itshnik(String texturePath, int x, int y, GameConstants.Position[] waypoints) {
        super(texturePath, x, y, waypoints);
    }

    @Override
    protected void onCustomUpdate(float deltaTime, Array<Rectangle> walls) {
        walkThrough(deltaTime);
        isPlayerNearby(walls);
    }

}
