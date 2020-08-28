package com.sigon.piman.game

enum class WeaponType(var price: Int, var fuseDelay: Long, var damage: Int, var isRemote: Boolean, var letter: String) {
    SHELL_SMALL(1, 2_000000000, 5, false, "L"),
    SHELL_LARGE(3, 3_000000000, 10, false,"M"),
    DYNAMITE(12, 4_000000000, 12, false,"N"),
    NUCLEAR_BOMB(805, 15_000000000, 99, false,"W"),
    CRUCIFIX_BOMB_SMALL(43, 6_000000000, 25, false,"X"),
    CRUCIFIX_BOMB_LARGE(179, 6_000000000, 50, false,"Y"),
    MINE(40, -1, 5, false,"6"),
    DIGGER_BOMB(148, 1_000000000, 15, false,"7"),
    NAPALM_BARREL(98, 10_000000000, 5, false, "K"),

    FLAME_THROWER(619, 0, 10, false,"K"),
    POLY_URETHANE(18, 10_000000000, 0, false,"K"),
    C4_EXPLOSIVE(98, 10_000000000, 10, false,"K"),
    CRACKER_BARREL(111, 10_000000000, 14,  false,"K"),
    GRENADE(371, 0, 5, false,"K"),
    REMOTE_BOMB_SMALL(18, -1, 10, true,"K"),
    REMOTE_BOMB_LARGE(80, -1, 12,true,"K");


    companion object {
        var weaponLetters = mutableListOf<String>()
        init {
            for (w in values()) {
                weaponLetters.add(w.letter)
            }
        }
    }


}