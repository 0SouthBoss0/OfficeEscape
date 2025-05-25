package com.officescape.item;

import com.officescape.GameProgress;
import com.officescape.unit.Player;

public interface TakeableItem {
    void onTake(Player player, GameProgress gameProgress);
}
