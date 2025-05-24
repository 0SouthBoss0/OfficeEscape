package com.officescape.item;

import com.badlogic.gdx.graphics.Texture;
import com.officescape.GameConstants;
import com.officescape.unit.Player;

public class Printer extends Item implements BreakableItem{
    public Printer(float x, float y) {
        super(GameConstants.PRINTER_FILE_PATH, x, y, GameConstants.PRINTER_FILE_SCALE);
    }

    @Override
    public void onBreak(Player player) {
        sprite.setTexture(new Texture(GameConstants.CRASHED_PRINTER_FILE_PATH));
    }

    @Override
    public void onFix(Character character) {
        sprite.setTexture(new Texture(GameConstants.PRINTER_FILE_PATH));
    }
}
