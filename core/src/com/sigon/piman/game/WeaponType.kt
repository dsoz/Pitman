package com.sigon.piman.game

enum class WeaponType(var price: Int, var fuseDelay: Long, var damage: Int, var letter: String) {

    SHELL_SMALL(1, 2_000000000, 5, "F"),
    SHELL_LARGE(3, 3_000000000, 10, "J"),
    DYNAMITE(12, 4_000000000, 12, "K"),
    NUCLEAR_BOMB(805, 15_000000000, 99, "K"),
    CRUCIFIX_BOMB_SMALL(43, 6_000000000, 25, "K"),
    CRUCIFIX_BOMB_LARGE(179, 6_000000000, 50, "K"),
    MINE(40, -1, 5, "K"),

    FLAME_THROWER(619, 0, 10, "K"),
    NAPALM_BARREL(98, 10_000000000, 5, "K"),
    POLY_URETHANE(18, 10_000000000, 0, "K"),
    C4_EXPLOSIVE(98, 10_000000000, 10, "K"),
    CRACKER_BARREL(111, 10_000000000, 14, "K"),
    GRENADE(371, 0, 5, "K"),
    DIGGER_BOMB(148, 1_000000000, 15, "K"),
    REMOTE_BOMB_SMALL(18, -1, 10, "K"),
    REMOTE_BOMB_LARGE(80, -1, 12, "K"),
    /*
    TELEPORT(86),

     */
}