package com.sigon.piman.game

import com.badlogic.gdx.math.Rectangle

class Player(var name: SpriteName, var x: Float, var y: Float, var scaleRatioX: Float, var scaleRatioY: Float) {
    var health: Int = 20
    var money: Int = 0
    var digPower: Int = 3
    var step = 8
    var rectangle = Rectangle(x, y, scaleRatioX, scaleRatioY)

    var xCell = 0
    var yCell = 0

    lateinit var weaponMap: MutableMap<String, Int>


}