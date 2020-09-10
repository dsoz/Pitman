package com.sigon.piman.game

import com.badlogic.gdx.math.Rectangle

data class Weapon(val player: Player, val type: WeaponType, val startTime: Long, val cellX: Int, val cellY: Int, val rectangle: Rectangle) {
   private lateinit var gameMap: Array<Array<Array<String>>>

    fun blast(map: Array<Array<Array<String>>>): MutableList<Array<Int>>{
        gameMap = map

        return calculateBlastCells(type)
    }

    private fun cellFind(blastPattern: IntArray): MutableList<Array<Int>>{
        val xRange = (blastPattern.size / 2)
        val explosions: MutableList<Array<Int>> = mutableListOf()
        var array: Array<Int>

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

        return explosions
    }

    private fun recurCellFind(explosiveCellsSet: MutableSet<Array<Int>>, currentX: Int, currentY: Int): MutableList<Array<Int>>{
        val tmp = arrayOf(0, 0, -1, 1)
        var cellType: String
        var isInList: Boolean


        for (q in 0..3){
            isInList = false
            cellType = gameMap[currentY + tmp[q]][currentX + tmp.reversedArray()[q]][1]

            val explosionCell = Array(3) { 0 }
            explosionCell[0] = currentY + tmp[q]
            explosionCell[1] = currentX + tmp.reversedArray()[q]
            explosionCell[2] = type.damage
           // explosiveCellsSet.stream().filter{ o -> o[0].equals(explosionCell[0]) }.findFirst().isPresent

            if (type == WeaponType.DIGGER_BOMB){
                    if ((cellType == SpriteName.ROCK_1.letter || cellType == SpriteName.ROCK_2.letter || cellType == SpriteName.ROCK_3.letter) ||
                        (q == 0 && (cellType == SpriteName.ROCK_TURN_2.letter || cellType == SpriteName.ROCK_TURN_3.letter)) ||
                        (q == 1 && (cellType == SpriteName.ROCK_TURN_1.letter || cellType == SpriteName.ROCK_TURN_4.letter)) ||
                        (q == 2 && (cellType == SpriteName.ROCK_TURN_1.letter || cellType == SpriteName.ROCK_TURN_2.letter)) ||
                        (q == 3 && (cellType == SpriteName.ROCK_TURN_3.letter || cellType == SpriteName.ROCK_TURN_4.letter)) ) {
                    for (expCell in explosiveCellsSet){
                        if (expCell[0] == explosionCell[0] && expCell[1] == explosionCell[1]){
                            isInList = true
                            break
                        }
                    }
                    if (!isInList){
                        explosiveCellsSet.add(explosionCell)
                        recurCellFind(explosiveCellsSet, currentX + tmp.reversedArray()[q],currentY + tmp[q])
                    }
                }
            }
        }
        return explosiveCellsSet.toMutableList()
    }

    private fun calculateBlastCells(type: WeaponType): MutableList<Array<Int>>{
        return when(type){
            WeaponType.SHELL_SMALL -> cellFind(intArrayOf(0,1,0))
            WeaponType.SHELL_LARGE -> cellFind(intArrayOf(0,1,2,1,0))
            WeaponType.DYNAMITE -> cellFind(intArrayOf(1,2,2,2,1))
            WeaponType.NUCLEAR_BOMB -> cellFind(intArrayOf(2,5,7,8,9,10,11,12,12,13,13,13,14,14,14,14,14,13,13,13,12,12,11,10,9,8,7,5,2))
            WeaponType.CRUCIFIX_BOMB_SMALL -> cellFind(intArrayOf(0,0,0,0,0,0,0,7,0,0,0,0,0,0,0))
            WeaponType.CRUCIFIX_BOMB_LARGE -> {
                val array = IntArray(GameMap.mapWidth - 2)
                array[cellX] = GameMap.mapHeight * 2
                return cellFind(array)
            }
            WeaponType.MINE -> cellFind(intArrayOf(0,1,0))

            WeaponType.DIGGER_BOMB -> {
                val explosions: MutableList<Array<Int>> = mutableListOf()
                val blastArray = cellFind(intArrayOf(1,2,3,4,3,2,1))

                /*
                val array = Array(3) { 0 }
                array[0] = cellY
                array[1] = cellX
                array[2] = 0

                 */

                explosions.add(arrayOf(cellX, cellY, 0))
                explosions.addAll(recurCellFind(explosions.toMutableSet(), cellX, cellY))

                

                return explosions
            }

            else -> mutableListOf()
        }
    }
}