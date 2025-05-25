package com.officescape.item;

import com.badlogic.gdx.graphics.Texture;
import com.officescape.GameConstants;
import com.officescape.unit.Character;
import com.officescape.unit.Player;

public class Trash extends Item implements BreakableItem {
    public Trash(float x, float y, Character.Direction direction) {
        super(GameConstants.TRASH_FILE_PATH, x, y, GameConstants.TRASH_FILE_SCALE, direction);
    }


    @Override
    public void onBreak(Player player) {
        sprite.setTexture(new Texture(GameConstants.FIRE_TRASH_FILE_PATH));
    }

    @Override
    public void onFix(Character character) {
        sprite.setTexture(new Texture(GameConstants.TRASH_FILE_PATH));
    }
}
