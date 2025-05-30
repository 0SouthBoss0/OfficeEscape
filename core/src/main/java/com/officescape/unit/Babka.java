package com.officescape.unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.officescape.GameConstants;


public class Babka extends NPC {
    private float sleepTimer = 0;
    private boolean isSleeping = false;
    private final Sprite sleepingTexture = new Sprite(new Texture(Gdx.files.internal(GameConstants.REACTION_SLEEP_FILE_PATH)));

    public Babka(String texturePath, int x, int y, GameConstants.Position[] waypoints) {
        super(texturePath, x, y, waypoints);
    }

    @Override
    protected void onCustomUpdate(float deltaTime, Array<Rectangle> walls) {
        sleepTimer += deltaTime;

        if (sleepTimer >= 5f) {
            isSleeping = !isSleeping;
            sleepTimer = 0;

        }
        if (!isSleeping) {
            isPlayerNearby(walls);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isSleeping) {
            float starsX = getX() - sleepingTexture.getWidth() / 2;
            float starsY = getY() + sprite.getHeight() * GameConstants.BABKA_SLEEP_OFFSET;
            sleepingTexture.setPosition(starsX, starsY);
            sleepingTexture.draw(batch);

        }
        super.draw(batch);
    }

    @Override
    protected void handleEmergencyResponse(float deltaTime, Array<Rectangle> walls) {
    }
}
