package com.sigon.piman.game

enum class WeaponType(var price: Int, var fuseDelay: Long, var damage: Int, var letter: String) {

    SHELL_SMALL(1, 2_000000000, 5, "L"),
    SHELL_LARGE(3, 3_000000000, 10, "M"),
    DYNAMITE(12, 4_000000000, 12, "N"),
    NUCLEAR_BOMB(805, 15_000000000, 99, "W"),
    CRUCIFIX_BOMB_SMALL(43, 6_000000000, 25, "X"),
    CRUCIFIX_BOMB_LARGE(179, 6_000000000, 50, "Y"),
    MINE(40, -1, 5, "6"),
    DIGGER_BOMB(148, 1_000000000, 15, "7"),
    NAPALM_BARREL(98, 10_000000000, 5, "K"),

    FLAME_THROWER(619, 0, 10, "K"),
    POLY_URETHANE(18, 10_000000000, 0, "K"),
    C4_EXPLOSIVE(98, 10_000000000, 10, "K"),
    CRACKER_BARREL(111, 10_000000000, 14, "K"),
    GRENADE(371, 0, 5, "K"),
    REMOTE_BOMB_SMALL(18, -1, 10, "K"),
    REMOTE_BOMB_LARGE(80, -1, 12, "K"),
    /*
    TELEPORT(86),

     */
}