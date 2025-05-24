package com.officescape.item;

import com.officescape.GameConstants;
import com.officescape.unit.Character;
import com.officescape.unit.Player;

public class KeyCard extends Item implements TakeableItem {
    public KeyCard(float x, float y, Character.Direction direction) {
        super(GameConstants.KEY_CARD_FILE_PATH, x, y, GameConstants.KEY_CARD_SCALE, direction);
    }


    @Override
    public void onTake(Player player) {
        take(player);
    }
}
