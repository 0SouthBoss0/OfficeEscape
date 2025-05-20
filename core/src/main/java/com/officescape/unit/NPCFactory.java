package com.officescape.unit;

import com.badlogic.gdx.utils.Array;
import com.officescape.GameConstants;

public class NPCFactory {
    private static NPCFactory instance;
    private Array<NPC> npcs;
    private Player player;

    private NPCFactory() {
        npcs = new Array<>();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public static synchronized NPCFactory getInstance() {
        if (instance == null) {
            instance = new NPCFactory();
        }
        return instance;
    }

    public NPC createBabka(int x, int y, Character.Direction direction) {
        NPC babka = new Babka(GameConstants.BABKA_FILE_PATH, x, y);
        babka.setCurrentDirection(direction);
        npcs.add(babka);
        return babka;
    }

    public NPC createBoss(int x, int y, Character.Direction direction) {
        NPC boss = new Boss(GameConstants.BOSS_FILE_PATH, x, y);
        boss.setCurrentDirection(direction);
        npcs.add(boss);
        return boss;
    }

    public NPC createCat(int x, int y, Character.Direction direction) {
        NPC cat = new Cat(GameConstants.CAT_FILE_PATH, x, y);
        cat.setCurrentDirection(direction);
        npcs.add(cat);
        return cat;
    }

    public NPC createColleague(int x, int y, Character.Direction direction) {
        NPC colleague = new Colleague(GameConstants.COLLEAGUE_FILE_PATH, x, y);
        colleague.setCurrentDirection(direction);
        npcs.add(colleague);
        return colleague;
    }

    public NPC createItshnik(int x, int y, Character.Direction direction) {
        NPC itshnik = new Itshnik(GameConstants.ITSHNIK_FILE_PATH, x, y);
        itshnik.setCurrentDirection(direction);
        npcs.add(itshnik);
        return itshnik;
    }

    public Array<NPC> getAllNPCs() {
        return npcs;
    }

    public void clearAllNPCs() {
        npcs.clear();
    }
}
