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

                   explosions = recurCellFind(explosions, cellX, cellY)
                    
                }
            }
        }

        return explosions
    }

    private fun recurCellFind(explosionCellsList: MutableList<Array<Int>>, currentX: Int, currentY: Int): MutableList<Array<Int>>{
        val tmp = arrayOf(0, 0, -1, 1)
        val explosionCell = Array(3) { 0 }

        for (q in 0..3){
            val cellType = gameMap[currentY + tmp[q]][currentX + tmp.reversedArray()[q]][1]
            explosionCell[0] = currentY
            explosionCell[1] = currentX
            explosionCell[2] = type.damage

            if (cellType == "O" || cellType == "P" || cellType == "Q" && !explosionCellsList.contains(explosionCell)) {
                explosionCellsList.add(explosionCell)

                recurCellFind(explosionCellsList, currentX + tmp.reversedArray()[q],currentY + tmp[q])
            }
        }

        return explosionCellsList
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
}