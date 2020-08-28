package com.sigon.piman.game

import com.badlogic.gdx.math.Rectangle
import kotlin.random.Random


class GameMap {
    companion object {
        private var chanceToStartAlive = 0.35f
        private var numberOfSteps = 2

        var mapHeight = 21   //21   26
        var mapWidth = 35     //35    45

        private var deathLimit = 2
        private var birthLimit = 3

        private var treasuresQuantity = 20
        private var sandRockQuantity = 20

        fun generateMap(): Array<Array<Array<String>>> {
            var map = Array(mapHeight) { Array(mapWidth) { Array(6) { "0" } } }

            map = initialiseMap(map)
            for (x in 0 until numberOfSteps) {
                map = doSimulationStep(map)
            }

            addRockTurns(map)
            addSandWithRock(map, sandRockQuantity)
            addTreasures(map, treasuresQuantity)
            addSandAndRockRandom(map)

            return map
        }

        private fun initialiseMap(map: Array<Array<Array<String>>>): Array<Array<Array<String>>> {

            for (y in 0 until mapHeight) {
                for (x in 0 until mapWidth) {
                    if (Random.nextFloat() < chanceToStartAlive)
                        map[y][x][1] = "1"
                }
            }
            return map
        }

        private fun doSimulationStep(oldMap: Array<Array<Array<String>>>): Array<Array<Array<String>>> {
            val newMap = Array(mapHeight) { Array(mapWidth) { Array(6) { "" } } }

            for (y in oldMap.indices) {
                for (x in oldMap[0].indices) {
                    val nbs = countAliveNeighbours(oldMap, y, x)

                    if (oldMap[y][x][1] == "1") {
                        if (nbs < deathLimit) {
                            newMap[y][x][1] = "0"
                        } else {
                            newMap[y][x][1] = "1"
                        }
                    } else {
                        if (nbs > birthLimit) {
                            newMap[y][x][1] = "1"
                        } else {
                            newMap[y][x][1] = "0"
                        }
                    }
                }
            }
            return newMap
        }

        private fun countAliveNeighbours(map: Array<Array<Array<String>>>, y: Int, x: Int): Int {
            var count = 0

            for (i in -1..1) {
                for (j in -1..1) {
                    val neighbourX = x + j
                    val neighbourY = y + i

                    if (i == 0 && j == 0) {
                        continue
                    } else if (neighbourX < 0 || neighbourY < 0 || neighbourY >= map.size || neighbourX >= map[0].size) {
                        //  count += 1
                        continue
                    } else if (map[neighbourY][neighbourX][1] == "1") {
                        count += 1
                    }
                }
            }
            return count
        }

        private fun addRockTurns(map: Array<Array<Array<String>>>): Array<Array<Array<String>>> {
            for (y in map.indices) {
                for (x in map[0].indices) {
                    if (x == 0 || y == 0 || x == map[0].size - 1 || y == map.size - 1) {
                        map[y][x][1] = "8"
                        map[y][x][0] = "-1"
                        continue
                    }
                    if (map[y][x][1] == "1") {
                        map[y][x][0] = "15"

                        if (map[y - 1][x][1] != "0" && map[y + 1][x][1] == "0") {
                            if (map[y][x - 1][1] != "0" && map[y][x + 1][1] == "0") {
                                map[y][x][1] = "4"
                                continue
                            }
                            if (map[y][x - 1][1] == "0" && map[y][x + 1][1] != "0") {
                                map[y][x][1] = "5"
                                continue
                            }
                        } else if (map[y - 1][x][1] == "0" && map[y + 1][x][1] != "0") {
                            if (map[y][x - 1][1] != "0" && map[y][x + 1][1] == "0") {
                                map[y][x][1] = "3"
                                continue
                            }
                            if (map[y][x - 1][1] == "0" && map[y][x + 1][1] != "0") {
                                map[y][x][1] = "2"
                                continue
                            }
                        }
                    } else{
                        map[y][x][0] = "1"
                    }
                }
            }
            return map
        }

        private fun addTreasures(map: Array<Array<Array<String>>>, quantity: Int): Array<Array<Array<String>>> {
            for (i in 1..quantity) {
                val treasureX: Int = Random.nextInt(1, mapWidth - 1)
                val treasureY: Int = Random.nextInt(1, mapHeight - 1)

                map[treasureY][treasureX][1] = when (PossibilityGenerator.getDistributedRandomNumber()) {
                    1 -> "f"
                    2 -> "g"
                    3 -> "h"
                    4 -> "i"
                    5 -> "j"
                    6 -> "k"
                    7 -> "l"
                    8 -> "m"
                    else -> "n"
                }
                map[treasureY][treasureX][0] = "1"

                for (j in 2..5)
                    map[treasureY][treasureX][j] = ""

                addMask(map, treasureX, treasureY, true)
            }
            return map
        }

        fun addMask(map: Array<Array<Array<String>>>, objectX: Int, objectY: Int, isDig: Boolean): Array<Array<Array<String>>>{
            var cnt = 0

            for (q in -1..1 step 2) {
                val tmp = arrayOf(intArrayOf(objectY, objectX + q), intArrayOf(objectY + q, objectX))

                for (z in tmp) {
                    cnt++

                    if (z[0] < 1 || z[1] < 1 || z[0] >= map.size - 1 || z[1] >= map[0].size - 1)
                        continue

                    for (j in map[z[0]][z[1]].withIndex()) {
                        var tmpw = ""
                        if (j.index > 1 && j.value == "") {
                            val tile = map[z[0]][z[1]][1]

                            if (tile == "0" || tile == "A" || tile == "B" || tile == "C" || tile == "D" || tile == "E") {
                                tmpw = if (isDig) {
                                    when (cnt) {
                                       1 -> "y" 2 -> "w" 3 -> "x" 4 -> "v" else -> ""
                                   }
                               }
                                else{
                                    when (cnt) {
                                       1 -> "u" 2 -> "s" 3 -> "t" 4 -> "r" else -> ""
                                   }
                               }
                            }
                            if (tile == "1" || tile == "O" || tile == "P" || tile == "Q"){
                                tmpw = if (isDig) {
                                    when (cnt) {
                                        1 -> "q" 2 -> "o" 3 -> "p" 4 -> "e" else -> ""
                                    }
                                }
                                else{
                                    when (cnt) {
                                        1 -> "d" 2 -> "b" 3 -> "c" 4 -> "a" else -> ""
                                    }
                                }
                            }
                            if (tile == "2" || tile == "3" || tile == "4" || tile == "5"){
                                tmpw = if (isDig) {
                                    when (cnt) {
                                        1 -> if (tile == "2" || tile == "5")
                                            "q"
                                        else if (tile == "3" || tile == "4")
                                            "y"
                                        else
                                            ""
                                        2 -> if (tile == "2" || tile == "3")
                                            "o"
                                        else if (tile == "4" || tile == "5")
                                            "w"
                                        else
                                            ""
                                        3 -> if (tile == "3" || tile == "4")
                                            "p"
                                        else if (tile == "2" || tile == "5")
                                            "x"
                                        else
                                            ""
                                        4 -> if (tile == "4" || tile == "5")
                                            "e"
                                        else if (tile == "2" || tile == "3")
                                            "v"
                                        else
                                            ""
                                        else -> ""
                                    }
                                } else{
                                    when (cnt) {
                                        1 -> if (tile == "2" || tile == "5")
                                            "d"
                                        else if (tile == "3" || tile == "4")
                                            "u"
                                        else
                                            ""
                                        2 -> if (tile == "2" || tile == "3")
                                            "b"
                                        else if (tile == "4" || tile == "5")
                                            "s"
                                        else
                                            ""
                                        3 -> if (tile == "3" || tile == "4")
                                            "c"
                                        else if (tile == "2" || tile == "5")
                                            "t"
                                        else
                                            ""
                                        4 -> if (tile == "4" || tile == "5")
                                            "a"
                                        else if (tile == "2" || tile == "3")
                                            "r"
                                        else
                                            ""
                                        else -> ""
                                    }
                                }
                            }

                            if (map[z[0]][z[1]].contains(tmpw))
                                break
                            else
                                map[z[0]][z[1]][j.index] = tmpw
                            break
                        }
                    }
                }
            }
            return map
        }

        private fun addSandWithRock(map: Array<Array<Array<String>>>, quantity: Int): Array<Array<Array<String>>> {
            for (i in 1..quantity) {
                var result: String
                var y: Int
                var x: Int

                do {
                    x = Random.nextInt(1, mapWidth - 1)
                    y = Random.nextInt(1, mapHeight - 1)

                    result = map[y][x][1]
                } while (result != "0")

                if (Random.nextFloat() > 0.5f) {
                    map[y][x][1] = "D"
                    map[y][x][0] = "8"
                } else {
                    map[y][x][1] = "E"
                    map[y][x][0] = "5"
                }
            }
            return map
        }

        private fun addSandAndRockRandom(map: Array<Array<Array<String>>>): Array<Array<Array<String>>> {
            for (y in map.indices) {
                for (x in map[0].indices) {
                    if (map[y][x][1] == "0"){
                        map[y][x][1] = when(Random.nextInt(1, 4)){
                            1 -> "A"
                            2 -> "B"
                            else -> "C"
                        }
                    }
                    if (map[y][x][1] == "1"){
                        map[y][x][1] = when(Random.nextInt(1, 4)){
                            1 -> "O"
                            2 -> "P"
                            else -> "Q"
                        }
                    }
                }
            }
            return map
        }

        fun generateRectangleMap(map: Array<Array<Array<String>>>, offsetX: Float, offsetY: Float, scaleRatioX: Float, scaleRatioY: Float): Array<Array<Rectangle>>{
            val rectangleMap = Array(mapHeight) {Array(mapWidth) { Rectangle() }}

            var positionY = offsetY
            for (y in map.indices) {
                var positionX = offsetX

                for (x in map[0].indices) {
                    rectangleMap[y][x] = Rectangle(positionX, positionY, scaleRatioX, scaleRatioY)
                    positionX += scaleRatioX
                }
                positionY -= scaleRatioY
            }
            return rectangleMap
        }

    }
}