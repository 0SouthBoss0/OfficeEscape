package com.officescape.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.officescape.GameConstants;
import com.officescape.GameProgress;
import com.officescape.unit.Character;

public class Camera extends Item {
    private final GameProgress gameProgress;
    private final Sprite turnedOff;
    private final float blinkInterval = 1.0f;
    private float blinkTimer = 0f;
    private boolean showRed = true;

    public Camera(float x, float y, Character.Direction direction, GameProgress gameProgress) {
        super(GameConstants.CAMERA_RED_FILE_PATH, x, y, GameConstants.CAMERA_GRAY_SCALE, direction);
        this.gameProgress = gameProgress;

        this.turnedOff = new Sprite(new Texture(Gdx.files.internal(GameConstants.CAMERA_GRAY_FILE_PATH)));
        turnedOff.setSize(sprite.getWidth() * scale, sprite.getHeight() * scale);
        turnedOff.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        switch (direction) {
            case UP:
                break;
            case DOWN:
                turnedOff.rotate(180);
                break;
            case LEFT:
                turnedOff.rotate(90);
                break;
            case RIGHT:
                turnedOff.rotate(-90);
                break;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!gameProgress.getQuestStatus(1)) {
            blinkTimer += Gdx.graphics.getDeltaTime();

            if (blinkTimer >= blinkInterval) {
                blinkTimer = 0f;
                showRed = !showRed;
            }

            if (showRed) {
                sprite.draw(batch);
            } else {
                turnedOff.draw(batch);
            }
        } else {
            turnedOff.draw(batch);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        turnedOff.getTexture().dispose();
    }
}
