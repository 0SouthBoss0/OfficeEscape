package com.officescape.unit;

import com.badlogic.gdx.utils.Array;

public class NPCFactory {
    private static NPCFactory instance;
    private Array<NPC> npcs;

    private NPCFactory() {
        npcs = new Array<>();
    }

    public static synchronized NPCFactory getInstance() {
        if (instance == null) {
            instance = new NPCFactory();
        }
        return instance;
    }

    public NPC createNPC(String texturePath, int x, int y) {
        NPC npc = new NPC(texturePath, x, y);
        npcs.add(npc);
        return npc;
    }

    public Array<NPC> getAllNPCs() {
        return npcs;
    }

    public void clearAllNPCs() {
        npcs.clear();
    }
}
