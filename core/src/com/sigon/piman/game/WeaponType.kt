package com.sigon.piman.game

enum class WeaponType(var price: Int, var fuseDelay: Long, var damage: Int, var letter: String) {

    SHELL_SMALL(1, 2_000000000, 5, "F"),
    SHELL_LARGE(3, 3_000000000, 10, "J"),
    DYNAMITE(12, 4_000000000, 12, "K"),
    NUCLEAR_BOMB(805, 15_000000000, 99, "K"),
    CRUCIFIX_BOMB_SMALL(43, 6_000000000, 25, "K"),
    CRUCIFIX_BOMB_LARGE(179, 6_000000000, 50, "K"),
    MINE(40, -1, 5, "K"),
    /*
    FLAME_THROWER(619),
    NAPALM_BARREL(98),
    POLY_URETHANE(18),
    GRENADE(371),
    C4_EXPLOSIVE(98),
    CRACKER_BARREL(111),
    DIGGER_BOMB(148),
    REMOTE_BOMB_SMALL(18),
    REMOTE_BOMB_LARGE(80),
    TELEPORT(86),

     */
}