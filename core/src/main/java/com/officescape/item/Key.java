package com.officescape.item;

// KeyCard.java

import com.officescape.unit.Player;

public class Key extends Item {
    public Key(float x, float y) {
        super("items/key.png", x, y, 0.03f);
    }

    @Override
    public void onPickUp(Player player) {
        // Логика при подборе ключ-карты
        System.out.println("Key picked up!");
    }
}
