package com.officescape.item;

import com.badlogic.gdx.graphics.Texture;
import com.officescape.GameConstants;
import com.officescape.unit.Character;
import com.officescape.unit.Player;

public class CoffeeMachine extends Item implements BreakableItem{
    public CoffeeMachine(float x, float y, Character.Direction direction) {
        super(GameConstants.COFFEE_MACHINE_FILE_PATH, x, y, GameConstants.COFFEE_MACHINE_SCALE, direction);
    }

    @Override
    public void onBreak(Player player) {
        sprite.setTexture(new Texture(GameConstants.CRASHED_COFFEE_MACHINE_FILE_PATH));
    }

    @Override
    public void onFix(Character character) {
        sprite.setTexture(new Texture(GameConstants.COFFEE_MACHINE_FILE_PATH));
    }
}
