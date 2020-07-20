package com.sigon.piman.game

import com.badlogic.gdx.math.Rectangle

data class Weapon(val type: WeaponType, val startTime: Long, val cellX: Int, val cellY: Int, val rectangle: Rectangle) {

    fun blast(map: Array<Array<Array<String>>>): MutableList<Array<Int>>{
        return calculateBlastCells(getBlastPattern(), map)
    }

    private fun calculateBlastCells(qty: IntArray, map: Array<Array<Array<String>>>): MutableList<Array<Int>>{
        val xRange = (qty.size / 2)
        val explosions: MutableList<Array<Int>> = mutableListOf()
        var array: Array<Int>

        for((tmp, x) in ((xRange * -1)..xRange).withIndex()){
            for (y in (qty[tmp])* -1..qty[tmp]){
                array =  Array(4) {0}
                if (cellX + x < 1 || cellX + x >= GameMap.mapWidth - 2 || cellY + y < 1 || cellY + y >= GameMap.mapHeight - 2){
                    continue
                }
                else{
                    array[0] = y + cellY
                    array[1] = x + cellX
                    array[2] = type.damage

                    explosions.add(array)
                }
            }
        }

        return explosions
    }

    private fun getBlastPattern(): IntArray{
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
        }
    }
}