package com.officescape.item;

import com.officescape.GameConstants;
import com.officescape.unit.Player;

public class Printer extends Item implements BreakableItem{
    public Printer(float x, float y) {
        super(GameConstants.PRINTER_FILE_PATH, x, y, GameConstants.PRINTER_FILE_SCALE);
    }

    @Override
    public void onBreak(Player player) {

    }

    @Override
    public void onFix(Character character) {

    }
}
