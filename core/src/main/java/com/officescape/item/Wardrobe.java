package com.officescape.item;

import com.officescape.GameConstants;
import com.officescape.unit.Player;

public class Wardrobe extends Item implements HideableItem {


    public Wardrobe(float x, float y) {
        super(GameConstants.WARDROBE_FILE_PATH, x, y, GameConstants.WARDROBE_SCALE);
    }

    @Override
    public void onHide(Player player) {
        if (!player.isHidden) {
            player.setVisible(false);
            player.setCanMove(false);
            player.isHidden = true;
            System.out.println("Player hidden in wardrobe!");
        }
    }

    @Override
    public void onUnhide(Player player) {
        if (player.isHidden) {
            player.setVisible(true);
            player.setCanMove(true);
            player.isHidden = false;
            System.out.println("Player left the wardrobe!");
        }
    }

    @Override
    public boolean isPlayerHidden(Player player) {
        return player.isHidden;
    }
}
