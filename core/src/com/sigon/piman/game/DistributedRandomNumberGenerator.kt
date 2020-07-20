package com.sigon.piman.game

import kotlin.random.Random

class DistributedRandomNumberGenerator {
    companion object {
        private var distributionMap: MutableMap<Int, Float> = mutableMapOf()
        private var distSum: Float = 0f

        private fun addNumber(value: Int, distribution: Float){
            if (distributionMap[value] != null) {
                distSum -= distributionMap.getValue(value)
            }
            distributionMap[value] = distribution
            distSum += distribution
        }

        fun getDistributedRandomNumber(): Int{
            addTreasuresPossibilities()

            val rand = Random.nextFloat()
            val ratio = 1.0f / distSum
            var tempDist = 0f

            for (i in distributionMap.keys){
                tempDist += distributionMap[i]!!
                if ((rand / ratio) <= tempDist){
                    return i
                }
            }
            return 0
        }

        private fun addTreasuresPossibilities(){
            this.addNumber(1, 0.25f)
            this.addNumber(2, 0.17f)
            this.addNumber(3, 0.15f)
            this.addNumber(4, 0.12f)
            this.addNumber(5, 0.09f)
            this.addNumber(6, 0.08f)
            this.addNumber(7, 0.07f)
            this.addNumber(8, 0.05f)
            this.addNumber(9, 0.02f)
        }
    }
}