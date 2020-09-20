package com.sigon.piman.game

import com.badlogic.gdx.math.Rectangle

data class Weapon(val player: Player, val type: WeaponType, val startTime: Long, val cellX: Int, val cellY: Int, val rectangle: Rectangle) {
   private lateinit var gameMap: Array<Array<Array<String>>>
    var detonate = false

    fun blast(map: Array<Array<Array<String>>>): MutableList<Array<Int>>{
        gameMap = map
        println("Weapon type:  ")
        println(type.toString())
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

    private fun recurCellFind(rangeCellsList: MutableList<Array<Int>> , explosiveCellsSet: MutableSet<Array<Int>>, currentX: Int, currentY: Int): MutableList<Array<Int>>{
        val tmp = arrayOf(0, 0, -1, 1)
        var cellType: String
        var isInList: Boolean
        var isInRange = false


        for (q in 0..3){
            isInList = false
            cellType = gameMap[currentY + tmp[q]][currentX + tmp.reversedArray()[q]][1]

            val explosionCell = Array(3) { 0 }
            explosionCell[0] = currentY + tmp[q]
            explosionCell[1] = currentX + tmp.reversedArray()[q]
            explosionCell[2] = type.damage
           // explosiveCellsSet.stream().filter{ o -> o[0].equals(explosionCell[0]) }.findFirst().isPresent

            for (rangeCell in rangeCellsList){
                if (rangeCell[0] == explosionCell[0] && rangeCell[1] == explosionCell[1])
                    isInRange = true
            }
            if (!isInRange)
                continue

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
                        recurCellFind(rangeCellsList, explosiveCellsSet, currentX + tmp.reversedArray()[q],currentY + tmp[q])
                    }
                }
            }

            if (type == WeaponType.FLAME_THROWER){
                if (cellType == SpriteName.BACKGROUND.letter){
                    for (expCell in explosiveCellsSet){
                        if (expCell[0] == explosionCell[0] && expCell[1] == explosionCell[1]){
                            isInList = true
                            break
                        }
                    }
                    if (!isInList){
                        explosiveCellsSet.add(explosionCell)
                        recurCellFind(rangeCellsList, explosiveCellsSet, currentX + tmp.reversedArray()[q],currentY + tmp[q])
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
         //   WeaponType.MINE -> cellFind(intArrayOf(0,1,0))

            WeaponType.DIGGER_BOMB -> {
                val explosions: MutableList<Array<Int>> = mutableListOf()
                val blastRangeArray = cellFind(intArrayOf(0,1,2,3,4,3,2,1,0))

                explosions.add(arrayOf(cellY, cellX, 0))
                explosions.addAll(recurCellFind(blastRangeArray, explosions.toMutableSet(), cellX, cellY))

                return explosions
            }

            WeaponType.FLAME_THROWER ->{
                val ta: MutableList<Array<Int>> = mutableListOf()
                val matrix = intArrayOf(0,1,2,3,3,2,0)
                var bombCellY: Int
                var bombCellX: Int
                var firstCellY = cellY
                var firstCellX = cellX
                var isFirst = true

                println("ANGLE: ${player.moveAngle}")
                for (i in matrix.indices){
                    for (j in matrix[i] * -1..matrix[i]) {
                       when (player.moveAngle) {
                                0f -> {
                                    bombCellY = (cellY - 2) - i
                                    bombCellX = cellX + j
                                }
                                180f -> {
                                    bombCellY = cellY + (i + 2)
                                    bombCellX = cellX + j
                                }
                                90f -> {
                                    bombCellY = cellY + j
                                    bombCellX = (cellX - 2) - i
                                }
                                270f -> {
                                    bombCellY = cellY + j
                                    bombCellX = cellX + (i + 2)
                                }
                               else -> {
                                   bombCellY = 0
                                   bombCellX = 0
                               }
                       }
                        if ((bombCellY >= 1 && bombCellY <= gameMap.size - 1) && (bombCellX >= 1 && bombCellX <= gameMap[0].size - 1)) {
                            ta.add(arrayOf(bombCellY, bombCellX, type.damage))

                            if (isFirst) {
                                println("player Y_X:[${player.yCell}, ${player.xCell}] - bomb Y_X: [$bombCellY, $bombCellX]")
                                firstCellX = bombCellX
                                firstCellY = bombCellY
                                isFirst = false
                            }
                        }
                    }
                }
                ////
             //   player.moveAngle += 1

                println("directCellFind: ")
                for (recCell in ta){
                    print("[${recCell[0]}, ${recCell[1]}], ")
                }
                println(" - ")

                val q = recurCellFind(ta, mutableSetOf(), firstCellX, firstCellY)

                val iterator = q.iterator()
                while (iterator.hasNext()){
                    val recurseCell = iterator.next()
                    var isInList = false

                    for (rangeCell in ta){
                        if (rangeCell[0] == recurseCell[0] && rangeCell[1] == recurseCell[1]){
                            isInList = true
                        }
                    }
                    if (!isInList)
                       // println("remove_Cell: [${}] ")
                        iterator.remove()
                }

                println("recurCellFind: ")
                for (recCell in q){
                    print("[${recCell[0]}, ${recCell[1]}], ")
                }
                println(" - ")

                return q
            }
            else -> mutableListOf()
        }
    }
}