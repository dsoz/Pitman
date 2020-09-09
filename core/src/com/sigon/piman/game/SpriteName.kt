package com.sigon.piman.game

enum class SpriteName(var fileName: String, var letter: String) {
    CROWN("crown", "n"),
    RUBIN("rubin", "m"),
    SCEPTRE("sceptre", "l"),
    GOLDEN_CROSS("golden_cross", "k"),
    GOLDEN_BAR("golden_bar", "j"),
    GOLDEN_EGG("golden_egg", "i"),
    PILE_OF_COINS("pile_of_coins", "h"),
    ANCIENT_SHIELD("ancient_shield", "g"),
    BRASS_BRACELET("brass_bracelet", "f"),

    BACKGROUND("background", "Z"),
    STEEL_PLATE("steelPlate", "8"),

    SAND_1("sand_1", "A"),
    SAND_2("sand_2","B"),
    SAND_3("sand_3", "C"),

    ROCK_1("rock_1", "O"),
    ROCK_2("rock_2", "P"),
    ROCK_3("rock_3", "Q"),

    ROCK_TURN_1("rockTurn_1", "2"),
    ROCK_TURN_2("rockTurn_2", "3"),
    ROCK_TURN_3("rockTurn_3", "4"),
    ROCK_TURN_4("rockTurn_4", "5"),

    ROCK_DAMAGED_1("rockDamaged_1", "R"),
    ROCK_DAMAGED_2("rockDamaged_2", "S"),

    SAND_ROCK_1("sandRock_1", "D"),
    SAND_ROCK_2("sandRock_2", "E"),

    BLAST("blast_1", "G"),
    FOG_1("fog_1", "H"),
    FOG_2("fog_2", "I"),

    ROCK_BLAST_UP("rockMask_blast_up", "a"),
    ROCK_BLAST_DOWN("rockMask_blast_down", "b"),
    ROCK_BLAST_LEFT("rockMask_blast_left", "c"),
    ROCK_BLAST_RIGHT("rockMask_blast_right", "d"),

    ROCK_DIGG_UP("rockMask_digg_up", "e"),
    ROCK_DIGG_DOWN("rockMask_digg_down", "o"),
    ROCK_DIGG_LEFT("rockMask_digg_left", "p"),
    ROCK_DIGG_RIGHT("rockMask_digg_right", "q"),

    SAND_BLAST_UP("sandMask_blast_up", "r"),
    SAND_BLAST_DOWN("sandMask_blast_down", "s"),
    SAND_BLAST_LEFT("sandMask_blast_left", "t"),
    SAND_BLAST_RIGHT("sandMask_blast_right", "u"),

    SAND_DIGG_UP("sandMask_digg_up", "v"),
    SAND_DIGG_DOWN("sandMask_digg_down", "w"),
    SAND_DIGG_LEFT("sandMask_digg_left", "x"),
    SAND_DIGG_RIGHT("sandMask_digg_right", "y"),

    PLAYER_USER("player1", ""),
    PLAYER_2("player2", ""),
    PLAYER_3("player3", ""),
    PLAYER_4("player4", ""),
    DEATH("death", "z"),

    SHELL_SMALL("bomb_small", "L"),
    SHELL_LARGE("bomb_large", "M"),
    DYNAMITE("dynamite", "N"),
    NUCLEAR_BOMB("atomicBomb", "W"),
    CRUCIFIX_BOMB_SMALL("crucifixBomb_small", "X"),
    CRUCIFIX_BOMB_LARGE("crucifixBomb_large", "Y"),
    MINE("mine", "6"),
    DIGGER_BOMB("diggerBomb", "7"),
    NAPALM_BARREL("napalmBarrel", "9"),

    MEDKIT("medKit", "F"),
    DRILL("drill", "K"),
    GIFTBOX("giftBox", "J")

}