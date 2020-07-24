package com.sigon.piman.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.TimeUtils
import kotlin.math.abs


class Pitman: ApplicationAdapter() {
    private lateinit var batch: SpriteBatch
    private lateinit var textureAtlas: TextureAtlas

    private var spritesMap: MutableMap<String, Sprite> = mutableMapOf()
    private var weaponList: MutableList<Weapon> = mutableListOf()
    private var playersList: MutableList<Player> = mutableListOf()
    private var explosionList: MutableList<kotlin.Array<Int>> = mutableListOf()

    private lateinit var animation: Animation<TextureRegion>
    private lateinit var map: kotlin.Array<kotlin.Array<kotlin.Array<String>>>
    private lateinit var rectangleMap: kotlin.Array<kotlin.Array<Rectangle>>
    private var offsetX = 50f
    private var offsetY = 900f
    private var scaleRatioX = 30f
    private var scaleRatioY = 30f
    private var stateTime = 0f
   // private var playerAngle = 180f
    private var player =  Player(SpriteName.PLAYER_1, 80f, 870f, scaleRatioX, scaleRatioY)

    private lateinit var buttonSprite: Sprite
    private lateinit var buttonSwitchSprite: Sprite
    private lateinit var currentWeapon: WeaponType

    private var timeSeconds = 0f
    private var period = 1f

    private var startTime = 0L

    override fun create() {
        batch = SpriteBatch()
        textureAtlas = TextureAtlas("sprites.txt")
        addSprites()

        playersList.add(Player(SpriteName.PLAYER_1, offsetX + (scaleRatioX * 1), offsetY - (scaleRatioY * 1), scaleRatioX, scaleRatioY))
        playersList.add(Player(SpriteName.PLAYER_2, offsetX + scaleRatioX * (GameMap.mapWidth - 1), offsetY - (scaleRatioY * GameMap.mapHeight), scaleRatioX, scaleRatioY))
        
        animation = Animation<TextureRegion>(0.1f, textureAtlas.findRegions(playersList[0].name.fileName), Animation.PlayMode.LOOP)
        stateTime = 0f

        map = GameMap.generateMap()
        rectangleMap = GameMap.generateRectangleMap(map, offsetX, offsetY, scaleRatioX, scaleRatioY)

        startTime = TimeUtils.nanoTime()

        ////////////////////////////////
        buttonSwitchSprite = Sprite(Texture("button_switch.png"))
        buttonSwitchSprite.setSize(300f, 300f)
        buttonSwitchSprite.setPosition(offsetX + GameMap.mapWidth * scaleRatioX + 150, offsetY - 200)

        buttonSprite = Sprite(Texture("button_put.png"))
        buttonSprite.setSize(300f, 300f)
        buttonSprite.setPosition(offsetX + GameMap.mapWidth * scaleRatioX + 150, offsetY - 600)
        ///////////////////////////////////////

        currentWeapon = WeaponType.values()[1]
    }

    override fun render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stateTime += Gdx.graphics.deltaTime
        val currentFrame = animation.getKeyFrame(stateTime, true)

        batch.begin()
        timeSeconds += Gdx.graphics.rawDeltaTime

        drawMap(map)

        checkPlayerAction()
        checkPlacedBomb()

        move(player.moveAngle)
        batch.draw(currentFrame, player.x, player.y, scaleRatioX / 2, scaleRatioY / 2, scaleRatioX, scaleRatioY, 1f, 1f, player.moveAngle)

        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        textureAtlas.dispose()
    }

    private fun checkPlayerAction(){
        if (Gdx.input.isTouched){
            if (Gdx.input.x >= offsetX + GameMap.mapWidth * scaleRatioX + 150 && Gdx.input.x <= offsetX + GameMap.mapWidth * scaleRatioX + 450
                    && (Gdx.graphics.height - 1 - Gdx.input.y) <= offsetY + 100 && (Gdx.graphics.height - 1 - Gdx.input.y) >= offsetY - 200){

                val tmp = WeaponType.values().indexOf(currentWeapon)

                currentWeapon = if (tmp + 1 <= WeaponType.values().size - 1){
                    WeaponType.values()[tmp + 1]
                }else
                    WeaponType.values()[0]
            }

            else if (Gdx.input.x >= offsetX + GameMap.mapWidth * scaleRatioX + 150 && Gdx.input.x <= offsetX + GameMap.mapWidth * scaleRatioX + 450
                    && (Gdx.graphics.height - 1 - Gdx.input.y) <= offsetY - 300 && (Gdx.graphics.height - 1 - Gdx.input.y) >= offsetY - 600){

                for (player in playersList){
                    if (player.name == SpriteName.PLAYER_1)
                        placeBomb(currentWeapon, player)
                }
            }
/*
            if (Gdx.input.x >= offsetX + scaleRatioX * (GameMap.mapWidth - 1) && Gdx.input.x <= offsetX + scaleRatioX * GameMap.mapWidth){
                placeBomb(WeaponType.SMALL_SHELL)
            }
 */
            else {
                for (player in playersList){
                    if (player.name == SpriteName.PLAYER_1){
                        playersList[playersList.indexOf(player)].moveAngle = getMoveAngle()
                    }
                }

                //   player.moveAngle = getMoveAngle()
            }
        }
    }
    private fun checkPlacedBomb(){
        /*
        if ( TimeUtils.timeSinceNanos(startTime) >= 5000000000) {
          // println((TimeUtils.nanoTime() - startTime) / 1000000000)

           startTime = TimeUtils.nanoTime()
       }
         */

        if (weaponList.isNotEmpty()){
            val mutableIterator = weaponList.iterator()

            while (mutableIterator.hasNext()){
                val weapon = mutableIterator.next()
                var tmpOverlaps = false

                for (player in playersList){
                    if (Intersector.overlaps(player.rectangle, weapon.rectangle))
                        tmpOverlaps = true
                }

                if (map[weapon.cellY][weapon.cellX][0] != "-1" && !tmpOverlaps){
                    map[weapon.cellY][weapon.cellX][0] = "-1"
                }

                if (TimeUtils.timeSinceNanos(weapon.startTime) >= weapon.type.fuseDelay){
                    explosionList.addAll(weapon.blast(map))

                    mutableIterator.remove()
                }
            }
        }

        if (explosionList.isNotEmpty()){
            val mutableIterator = explosionList.iterator()
            var blast: kotlin.Array<Int>
            var y: Int
            var x: Int
            var cellHealth: Int
            var blastDamage: Int
            var blastRectangle: Rectangle

            while (mutableIterator.hasNext()){
                blast = mutableIterator.next()

                y = blast[0]
                x = blast[1]
                blastDamage = blast[2]
                cellHealth = map[y][x][0].toInt()
                blastRectangle =  Rectangle(x.toFloat(), y.toFloat(), scaleRatioX, scaleRatioY)

                for (player in playersList){
                    if (Intersector.overlaps(player.rectangle, blastRectangle)){
                        val index = playersList.indexOf(player)
                        playersList[index].health -= blastDamage

                        if (playersList[index].health <= 0){
                            map[y][x][0] = "0"
                            map[y][x][1] = "z"
                        }
                    }
                }

                if (cellHealth - blastDamage <= 0) {
                    map[y][x][0] = "0"
                    map[y][x][1] = "G"

                    for (i in 2 until map[y][x].size){
                        map[y][x][i] = ""
                    }
                    drawSprites(map[y][x][1], offsetX + x * scaleRatioX, offsetY - y * scaleRatioY)

                    GameMap.addMask(map, x, y, false)
                }
                else
                    map[y][x][0] = (cellHealth - blastDamage).toString()

                mutableIterator.remove()
            }
        }
    }

    private fun addSprites(){
        val regions: Array<TextureAtlas.AtlasRegion>? = textureAtlas.regions

        if (regions != null) {
            for (region in regions){
                val sprite = textureAtlas.createSprite(region.name)

                spritesMap[region.name] = sprite
            }
        }
    }

    private fun drawSprites(spriteLetter: String, x: Float, y: Float){
        val name = spriteProcessing(spriteLetter)
        val sprite = spritesMap[name.fileName]


        if (name == SpriteName.ROCK_DIGG_RIGHT || name == SpriteName.ROCK_BLAST_RIGHT || name == SpriteName.SAND_DIGG_RIGHT || name == SpriteName.SAND_BLAST_RIGHT){
            sprite?.setPosition(x + 20, y)
            sprite?.setSize(10f, 30f)
        }

        else if (name == SpriteName.ROCK_DIGG_UP || name == SpriteName.ROCK_BLAST_UP || name == SpriteName.SAND_DIGG_UP || name == SpriteName.SAND_BLAST_UP){
            sprite?.setPosition(x, y + 20)
            sprite?.setSize(30f, 10f)
        }

        else if (name == SpriteName.ROCK_DIGG_DOWN || name == SpriteName.ROCK_BLAST_DOWN || name == SpriteName.SAND_DIGG_DOWN || name == SpriteName.SAND_BLAST_DOWN){
            sprite?.setPosition(x, y)
            sprite?.setSize(30f, 10f)
        }

        else if (name == SpriteName.ROCK_DIGG_LEFT || name == SpriteName.ROCK_BLAST_LEFT || name == SpriteName.SAND_DIGG_LEFT || name == SpriteName.SAND_BLAST_LEFT){
            sprite?.setPosition(x, y)
            sprite?.setSize(10f, 30f)
        }

        else {
            sprite?.setPosition(x, y)
            sprite?.setSize(scaleRatioX, scaleRatioY)
        }
        sprite?.draw(batch)
    }

    private fun spriteProcessing(label: String): SpriteName{
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
            //   "L" -> SpriteName.ROCK_DAMAGED_2
            //   "M" -> SpriteName.ROCK_DAMAGED_2
            //   "N" -> SpriteName.ROCK_DAMAGED_2
            //   "W" -> SpriteName.ROCK_DAMAGED_2
            //   "X" -> SpriteName.ROCK_DAMAGED_2
            //   "Y" -> SpriteName.ROCK_DAMAGED_2
            //   "6" -> SpriteName.ROCK_DAMAGED_2
            //   "7" -> SpriteName.ROCK_DAMAGED_2
            //   "9" -> SpriteName.ROCK_DAMAGED_2
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

    private  fun move(angle: Float){
        player.rectangle.x = player.x
        player.rectangle.y = player.y

        val tmpX = if ((player.x - offsetX) % scaleRatioX < scaleRatioX / 2)
            (player.x - offsetX) % scaleRatioX
        else
            (scaleRatioX - (player.x - offsetX) % scaleRatioX) * -1

        val tmpY = if ((offsetY - player.y) % scaleRatioY < scaleRatioY / 2)
            (offsetY - player.y) % scaleRatioY
        else
            (scaleRatioY - (offsetY - player.y) % scaleRatioY) * -1

        when (angle) {
            0f  -> {
                if ((offsetY - (scaleRatioY * 1)) - (player.rectangle.y + player.step) < player.step)
                    player.rectangle.y = offsetY - (scaleRatioY * 1)
                else
                    player.rectangle.y += player.step

                player.rectangle.x = player.x - tmpX
            }
            180f -> {
                   if ((player.rectangle.y + player.step * -1) - (offsetY - (scaleRatioY * (GameMap.mapHeight - 2))) < player.step)
                       player.rectangle.y = offsetY - (scaleRatioY * (GameMap.mapHeight - 2))
                   else
                       player.rectangle.y += (player.step * -1)

                player.rectangle.x = player.x - tmpX
            }
            270f -> {
                if (offsetX + (scaleRatioX * (GameMap.mapWidth - 2)) - (player.rectangle.x + player.step) < player.step)
                    player.rectangle.x = offsetX + (scaleRatioX * (GameMap.mapWidth - 2))
                else
                    player.rectangle.x += player.step

                player.rectangle.y = player.y + tmpY
            }
            90f -> {
                if ((player.rectangle.x + player.step * -1) - (offsetX + (scaleRatioX * 1)) < player.step)
                    player.rectangle.x = offsetX + (scaleRatioX * 1)
                else
                    player.rectangle.x += (player.step * -1)

                player.rectangle.y = player.y + tmpY
            }
        }

        if (!isOverlapsNeedDig(player.rectangle)){
            player.x = player.rectangle.x
            player.y = player.rectangle.y
        }

        player.xCell = ((player.x - offsetX) / scaleRatioX).toInt()
        player.yCell = ((offsetY - player.y) / scaleRatioY).toInt()
    }

    private fun isOverlapsNeedDig(playerRectangle: Rectangle): Boolean{
        var needToDig = false

        for (y in rectangleMap.indices){
            for (x in rectangleMap[0].indices){
                val cellHealth = map[y][x][0]
                val cellType = map[y][x][1]

                if (Intersector.overlaps(playerRectangle, rectangleMap[y][x])){
                    if (cellHealth.toInt() < 0){
                        needToDig = true
                    }

                    if (cellHealth.toInt() > 0){
                        if (timeSeconds >= period){
                            if (cellHealth.toInt() - player.digPower > 0)
                                map[y][x][0] = (cellHealth.toInt() - player.digPower).toString()
                            else
                                map[y][x][0] = "0"

                            timeSeconds -= period
                        }
                        needToDig = true
                    }

                    if (cellHealth.toInt() == 0){
                        if ( cellType == "f" || cellType == "g" || cellType == "h" || cellType == "i" ||
                                cellType == "j" || cellType == "k" || cellType == "l" || cellType == "m" || cellType == "n"){
                            getCoins(cellType)
                        }
                        if (cellType != "Z" && cellType != "F" && cellType != "J" && cellType != "K"){
                            map[y][x][1] = "Z"
                            for (i in 2 until map[y][x].size){
                                map[y][x][i] = ""
                            }
                            map = GameMap.addMask(map, x, y, true)
                        }
                    }
                }
            }
        }
        return needToDig
    }

    private fun getMoveAngle(): Float{
       return if (abs(player.x -  Gdx.input.x) > abs(player.y - (Gdx.graphics.height - 1 - Gdx.input.y) )){
            if ((player.x -  Gdx.input.x) > 0) {
                90f
            }
            else{
                270f
            }
        }
        else {
            if ((player.y - (Gdx.graphics.height - 1 - Gdx.input.y)) > 0) {
                180f
            }
            else {
                0f
            }
        }
    }

    private fun getCoins(cellType: String){
       player.money += when (cellType){
            "f" -> 10
            "g" -> 15
            "h" -> 15
            "i" -> 25
            "j" -> 30
            "k" -> 35
            "l" -> 50
            "m" -> 60
            "n" -> 100
           else -> 0
        }
        println("Player coins: ${player.money}")
    }

    private fun drawMap(map: kotlin.Array<kotlin.Array<kotlin.Array<String>>>){
        buttonSwitchSprite.draw(batch)
        buttonSprite.draw(batch)

        var positionY = offsetY
        for(y in 0 until GameMap.mapHeight){
            var positionX = offsetX
            for (x in 0 until GameMap.mapWidth){
                if (map[y][x][1] == "G" || map[y][x][1] == "H" || map[y][x][1] == "I" && TimeUtils.timeSinceNanos(startTime) >= 500000000){
                    when(map[y][x][1]){
                        "G" -> {
                            map[y][x][1] = "H"
                        }
                        "H" -> {
                            map[y][x][1] = "I"
                        }
                        "I" -> {
                            map[y][x][1] = "Z"
                        }
                    }
                }
                if (map[y][x][0].toInt() > 1 && map[y][x][1] != "D" && map[y][x][1] != "E"){
                    when(map[y][x][0].toInt()){
                        in  6..10 -> if (map[y][x][1] != "R") map[y][x][1] = "R"
                        in  2..5 -> if (map[y][x][1] != "S") map[y][x][1] = "S"
                    }
                }

                for (i in map[y][x].withIndex()){
                    if (i.value == ""){
                        break
                    }
                    if (i.index > 0)
                        drawSprites(map[y][x][i.index], positionX, positionY)

                }
                positionX += scaleRatioX
            }
            positionY -= scaleRatioY
        }
        if (TimeUtils.timeSinceNanos(startTime) >= 500000000)
            startTime = TimeUtils.nanoTime()
    }

    private fun placeBomb(weaponType: WeaponType, currentPlayer: Player){
        val bombX = offsetX + currentPlayer.xCell * scaleRatioX
        val bombY = offsetY - currentPlayer.yCell * scaleRatioY

        if (map[currentPlayer.yCell][currentPlayer.xCell][1] == "Z"){
            map[currentPlayer.yCell][currentPlayer.xCell][1] = weaponType.letter

            weaponList.add(Weapon(weaponType, TimeUtils.nanoTime(), currentPlayer.xCell, currentPlayer.yCell, Rectangle(bombX, bombY, scaleRatioX, scaleRatioY)))
        }
    }
}