package com.officescape.unit;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.officescape.GameConstants;

public class Player extends Character {
    private boolean visible = true;
    private boolean canMove = true;
    public boolean isPlayerHidden = false;

    public Player(String texturePath) {
        super(texturePath, GameConstants.PLAYER_START_X, GameConstants.PLAYER_START_Y);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (visible) {
            super.draw(batch);
        }
    }


    @Override
    public void update(float deltaTime, Array<Rectangle> walls) {
        if (canMove) {
            super.update(deltaTime, walls);
        }
    }
}

