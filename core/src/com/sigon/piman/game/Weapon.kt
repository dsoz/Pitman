package com.sigon.piman.game

import com.badlogic.gdx.math.Rectangle

data class Weapon(val player: Player, val type: WeaponType, val startTime: Long, val cellX: Int, val cellY: Int, val rectangle: Rectangle) {
   private lateinit var gameMap: Array<Array<Array<String>>>

    fun blast(map: Array<Array<Array<String>>>): MutableList<Array<Int>>{
        gameMap = map
        return calculateBlastCells(map)
    }

    private fun calculateBlastCells(map: Array<Array<Array<String>>>): MutableList<Array<Int>>{
        val blastPattern = getBlastPattern(type)
        val xRange = (blastPattern.size / 2)
        var explosions: MutableList<Array<Int>> = mutableListOf()
        var array: Array<Int>



        if (blastPattern.isNotEmpty()) {
            for ((tmp, x) in ((xRange * -1)..xRange).withIndex()) {
                for (y in (blastPattern[tmp]) * -1..blastPattern[tmp]) {
                    array = Array(3) { 0 }
                    if (cellX + x < 1 || cellX + x >= GameMap.mapWidth - 1 || cellY + y < 1 || cellY + y >= GameMap.mapHeight - 1) {
                        continue
                    } else {
                        array[0] = y + cellY
                        array[1] = x + cellX
                        array[2] = type.damage

                        explosions.add(array)
                    }
                }
            }
        }
        else {
            val angle = player.moveAngle

            when(type){
                WeaponType.FLAME_THROWER -> {

                }

                WeaponType.DIGGER_BOMB -> {
                    val blastArray = intArrayOf(1,2,3,4,3,2,1)

                    array = Array(3) { 0 }
                    array[0] = cellY
                    array[1] = cellX
                    array[2] = 0

                    explosions.add(array)
                    explosions.addAll(recurCellFind(explosions, cellX, cellY))
                    
                }
            }
        }

        return explosions
    }

    private fun recurCellFind(explosiveCellsList: MutableList<Array<Int>>, currentX: Int, currentY: Int): MutableList<Array<Int>>{
        val tmp = arrayOf(0, 0, -1, 1)
        val explosionCell = Array(3) { 0 }
        var cellType: String
        var isInList: Boolean
        var expCellsList = explosiveCellsList
        var w = mutableSetOf<Array<Int>>()


        for (q in 0..3){
            isInList = false
            cellType = gameMap[currentY + tmp[q]][currentX + tmp.reversedArray()[q]][1]

            explosionCell[0] = currentY + tmp[q]
            explosionCell[1] = currentX + tmp.reversedArray()[q]
            explosionCell[2] = type.damage

            println("  ")
            println("======================")
            println("current cell Y_X: [$currentY, $currentX]")
            println("  ")
            println("q: $q, Y_X_tmp: [${currentY + tmp[q]}, ${currentX + tmp.reversedArray()[q]}], cellType: '${spriteProcessing(cellType)}.'")
            for (z in expCellsList)
                print("${z.contentToString()}, ")
            println("  ")

            if ((cellType == "O" || cellType == "P" || cellType == "Q") ) {
                for (expCell in expCellsList){
                    if (expCell[0] == currentY  + tmp[q] && expCell[1] == currentX + tmp.reversedArray()[q]){
                        println("is_InList")

                        isInList = true
                        break
                    }
                }
                if (!isInList){
                    println("NOT_InList")

                    expCellsList.add(explosionCell)
                    recurCellFind(expCellsList, currentX + tmp.reversedArray()[q],currentY + tmp[q])
                }
            }
        }
        return expCellsList
    }

    private fun getBlastPattern(type: WeaponType): IntArray{
        return when(type){
            WeaponType.SHELL_SMALL -> intArrayOf(0,1,0)
            WeaponType.SHELL_LARGE -> intArrayOf(0,1,2,1,0)
            WeaponType.DYNAMITE -> intArrayOf(1,2,2,2,1)
            WeaponType.NUCLEAR_BOMB -> intArrayOf(2,5,7,8,9,10,11,12,12,13,13,13,14,14,14,14,14,13,13,13,12,12,11,10,9,8,7,5,2)
            WeaponType.CRUCIFIX_BOMB_SMALL -> intArrayOf(0,0,0,0,0,0,0,7,0,0,0,0,0,0,0)
            WeaponType.CRUCIFIX_BOMB_LARGE -> {
                val array = IntArray(GameMap.mapWidth - 2)
                array[cellX] = GameMap.mapHeight * 2
                return array
            }
            WeaponType.MINE -> intArrayOf(0,1,0)


            else -> intArrayOf()
        }
    }

    private fun spriteProcessing(label: String): SpriteName{
        /*
        for (sprite in SpriteName.values()){
            if (sprite.fileName == label)
                return sprite
        }

         */

        return when (label){
            "A" -> SpriteName.SAND_1
            "B" -> SpriteName.SAND_2
            "C" -> SpriteName.SAND_3

            "O" -> SpriteName.ROCK_1
            "P" -> SpriteName.ROCK_2
            "Q" -> SpriteName.ROCK_3

            "2" -> SpriteName.ROCK_TURN_1
            "3" -> SpriteName.ROCK_TURN_2
            "4" -> SpriteName.ROCK_TURN_3
            "5" -> SpriteName.ROCK_TURN_4
            "8" -> SpriteName.STEEL_PLATE

            "D" -> SpriteName.SAND_ROCK_1
            "E" -> SpriteName.SAND_ROCK_2
            "R" -> SpriteName.ROCK_DAMAGED_1
            "S" -> SpriteName.ROCK_DAMAGED_2

            "T" -> SpriteName.BLAST
            "U" -> SpriteName.FOG_1
            "V" -> SpriteName.FOG_2

            "Z" -> SpriteName.BACKGROUND

            "G" -> SpriteName.BLAST
            "H" -> SpriteName.FOG_1
            "I" -> SpriteName.FOG_2

            "F" -> SpriteName.MEDKIT
            "J" -> SpriteName.GIFTBOX
            "K" -> SpriteName.DRILL
            "L" -> SpriteName.SHELL_SMALL
            "M" -> SpriteName.SHELL_LARGE
            "N" -> SpriteName.DYNAMITE
            "W" -> SpriteName.NUCLEAR_BOMB
            "X" -> SpriteName.CRUCIFIX_BOMB_SMALL
            "Y" -> SpriteName.CRUCIFIX_BOMB_LARGE
            "6" -> SpriteName.MINE
            "7" -> SpriteName.DIGGER_BOMB
            "9" -> SpriteName.NAPALM_BARREL

            "z" -> SpriteName.DEATH

            "f" -> SpriteName.BRASS_BRACELET
            "g" -> SpriteName.ANCIENT_SHIELD
            "h" -> SpriteName.PILE_OF_COINS
            "i" -> SpriteName.GOLDEN_EGG
            "j" -> SpriteName.GOLDEN_BAR
            "k" -> SpriteName.GOLDEN_CROSS
            "l" -> SpriteName.SCEPTRE
            "m" -> SpriteName.RUBIN
            "n" -> SpriteName.CROWN

            "a" -> SpriteName.ROCK_BLAST_UP
            "b" -> SpriteName.ROCK_BLAST_DOWN
            "c" -> SpriteName.ROCK_BLAST_LEFT
            "d" -> SpriteName.ROCK_BLAST_RIGHT
            "e" -> SpriteName.ROCK_DIGG_UP
            "o" -> SpriteName.ROCK_DIGG_DOWN
            "p" -> SpriteName.ROCK_DIGG_LEFT
            "q" -> SpriteName.ROCK_DIGG_RIGHT

            "r" -> SpriteName.SAND_BLAST_UP
            "s" -> SpriteName.SAND_BLAST_DOWN
            "t" -> SpriteName.SAND_BLAST_LEFT
            "u" -> SpriteName.SAND_BLAST_RIGHT
            "v" -> SpriteName.SAND_DIGG_UP
            "w" -> SpriteName.SAND_DIGG_DOWN
            "x" -> SpriteName.SAND_DIGG_LEFT
            "y" -> SpriteName.SAND_DIGG_RIGHT

            else -> SpriteName.BACKGROUND
        }
    }
}