package com.officescape.item;

import com.officescape.unit.Player;

public interface HideableItem {
    void onHide(Player player);

    void onUnhide(Player player);

    boolean isPlayerHidden();
}
