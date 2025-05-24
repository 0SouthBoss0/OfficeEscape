package com.officescape.item;

import com.officescape.unit.Player;

public interface BreakableItem {

    void onBreak(Player player);

    void onFix(Character character);
}
