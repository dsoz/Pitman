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
    private var animationList: MutableList<Animation<TextureRegion>> = mutableListOf()
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
   // private var player =  Player(SpriteName.PLAYER_1, 80f, 870f, scaleRatioX, scaleRatioY)
    private var gameOver = false

    private lateinit var buttonSprite: Sprite
    private lateinit var buttonSwitchSprite: Sprite
    private lateinit var currentWeapon: WeaponType

    private var timeSeconds = 0f
    private var period = 1f

    private var startTime = 0L

    override fun create() {
        batch = SpriteBatch()
        textureAtlas = TextureAtlas("sprites_2.txt")
        addSprites()

        playersList.add(Player(SpriteName.PLAYER_USER, offsetX + (scaleRatioX * 1), offsetY - (scaleRatioY * 1), scaleRatioX, scaleRatioY))
        playersList.add(Player(SpriteName.PLAYER_2, offsetX + scaleRatioX * (GameMap.mapWidth - 2), offsetY - (scaleRatioY * (GameMap.mapHeight - 2)), scaleRatioX, scaleRatioY))

        for (player in playersList){
            animationList.add(Animation<TextureRegion>(0.1f, textureAtlas.findRegions(player.name.fileName), Animation.PlayMode.LOOP))
        }
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

        if (!gameOver) {
            for (player in playersList) {
                move(player) //////+
                batch.draw(currentFrame, player.x, player.y, scaleRatioX / 2, scaleRatioY / 2, scaleRatioX, scaleRatioY, 1f, 1f, player.moveAngle) ///////+
            }
        }

        // move(player.moveAngle)
        // batch.draw(currentFrame, player.x, player.y, scaleRatioX / 2, scaleRatioY / 2, scaleRatioX, scaleRatioY, 1f, 1f, player.moveAngle)
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

                currentWeapon = WeaponType.SHELL_LARGE
                /*
                currentWeapon = if (tmp + 1 <= WeaponType.values().size - 1){
                    WeaponType.values()[tmp + 1]
                }else
                    WeaponType.values()[0]

                 */
            }

            else if (Gdx.input.x >= offsetX + GameMap.mapWidth * scaleRatioX + 150 && Gdx.input.x <= offsetX + GameMap.mapWidth * scaleRatioX + 450
                    && (Gdx.graphics.height - 1 - Gdx.input.y) <= offsetY - 300 && (Gdx.graphics.height - 1 - Gdx.input.y) >= offsetY - 600){

                for (player in playersList){
                    if (player.name == SpriteName.PLAYER_USER)
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
                    if (player.name == SpriteName.PLAYER_USER){
                        playersList[playersList.indexOf(player)].moveAngle = getMoveAngle(player)
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
                    val blastList = weapon.blast(map)
                    explosionList.addAll(blastList)


                    for (blast in blastList){
                        if (WeaponType.weaponLetters.contains(map[blast[0]][blast[1]][1])){
                            for (w in weaponList){
                                if (w.cellY == blast[0] && w.cellX == blast[1]){
                                    explosionList.addAll(w.blast(map))
                                    w.detonate = true
                                }
                            }
                        }
                    }


                    mutableIterator.remove()
                }
                if (weapon.detonate)
                    mutableIterator.remove()
            }
        }

        if (explosionList.isNotEmpty()){
            val mutableExplosionListIterator = explosionList.iterator()
            var blast: kotlin.Array<Int>
            var yCell: Int
            var xCell: Int
            var cellHealth: Int
            var cellType: String
            var blastDamage: Int
            var blastRectangle: Rectangle

            while (mutableExplosionListIterator.hasNext()){
                blast = mutableExplosionListIterator.next()
                yCell = blast[0]
                xCell = blast[1]
                blastDamage = blast[2]
                cellHealth = map[yCell][xCell][0].toInt()
                cellType = map[yCell][xCell][1]
                blastRectangle =  Rectangle((offsetX + (blast[1] * scaleRatioX)), (offsetY - (blast[0] * scaleRatioY)), scaleRatioX, scaleRatioY)

                if (cellHealth - blastDamage <= 0) {
                    map[yCell][xCell][0] = "0"
                    map[yCell][xCell][1] = SpriteName.BLAST.letter

                    for (i in 2 until map[yCell][xCell].size){
                        map[yCell][xCell][i] = ""
                    }
                    drawSprites(map[yCell][xCell][1], offsetX + xCell * scaleRatioX, offsetY - yCell * scaleRatioY)

                    GameMap.addMask(map, xCell, yCell, false)
                }
                else {
                    map[yCell][xCell][0] = (cellHealth - blastDamage).toString()
                }

                val playersListIterator = playersList.iterator()
                var currentPlayer: Player
                while (playersListIterator.hasNext()){
                    currentPlayer = playersListIterator.next()

                    if (Intersector.overlaps(currentPlayer.rectangle, blastRectangle)){
                        val index = playersList.indexOf(currentPlayer)
                        playersList[index].health -= blastDamage

                        if (playersList[index].health <= 0){
                            // DEATH
                            map[yCell][xCell][0] = "0"
                            map[yCell][xCell][1] = SpriteName.DEATH.letter

                            gameOver = true
                        }
                    }
                }
                mutableExplosionListIterator.remove()
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

        /*
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

         */

        sprite?.setPosition(x, y)
        sprite?.setSize(scaleRatioX, scaleRatioY)
        sprite?.draw(batch)
    }

    private fun spriteProcessing(label: String): SpriteName{
        for (sprite in SpriteName.values()){
            if (sprite.letter == label)
                return sprite
        }
        return SpriteName.BACKGROUND
        /*
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
         */
    }

    private  fun move(player: Player){
        player.rectangle.x = player.x
        player.rectangle.y = player.y

        val tmpX =
                if ((player.x - offsetX) % scaleRatioX < scaleRatioX / 2)
                    (player.x - offsetX) % scaleRatioX
                else
                    (scaleRatioX - (player.x - offsetX) % scaleRatioX) * -1

        val tmpY =
                if ((offsetY - player.y) % scaleRatioY < scaleRatioY / 2)
                    (offsetY - player.y) % scaleRatioY
                else
                    (scaleRatioY - (offsetY - player.y) % scaleRatioY) * -1


        when (player.moveAngle) {
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

        if (!isOverlapsNeedDig(player)){
            player.x = player.rectangle.x
            player.y = player.rectangle.y
        }


        player.xCell =
                if ((player.x - offsetX) % scaleRatioX < scaleRatioX / 2)
                  ((player.x - offsetX) / scaleRatioX).toInt()
                else
                    (((player.x - offsetX) / scaleRatioX).toInt()) + 1

        player.yCell =
                if ((offsetY - player.y) % scaleRatioY < scaleRatioY / 2)
                    ((offsetY - player.y) / scaleRatioY).toInt()
                else
                    (((offsetY - player.y) / scaleRatioY).toInt()) + 1

      //  player.xCell = ((player.x - offsetX) / scaleRatioX).toInt()
      //  player.yCell = ((offsetY - player.y) / scaleRatioY).toInt()
    }

    private fun isOverlapsNeedDig(player: Player): Boolean{
        var needToDig = false

        for (y in rectangleMap.indices){
            for (x in rectangleMap[0].indices){
                val cellHealth = map[y][x][0]
                val cellType = map[y][x][1]

                if (Intersector.overlaps(player.rectangle, rectangleMap[y][x])){
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
                            getCoins(cellType, player)
                        }
                        if (cellType != "Z" && !WeaponType.weaponLetters.contains(cellType)){
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

    private fun getMoveAngle(player: Player): Float{
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

    private fun getCoins(cellType: String, player: Player){
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
        println("Player ${player.name.fileName} coins: ${player.money}")
    }

    private fun drawMap(map: kotlin.Array<kotlin.Array<kotlin.Array<String>>>){
        buttonSwitchSprite.draw(batch)
        buttonSprite.draw(batch)

        var positionY = offsetY
        for(y in 0 until GameMap.mapHeight){
            var positionX = offsetX
            for (x in 0 until GameMap.mapWidth){
                if ((map[y][x][1] == SpriteName.BLAST.letter || map[y][x][1] == SpriteName.FOG_1.letter || map[y][x][1] == SpriteName.FOG_2.letter)
                        && TimeUtils.timeSinceNanos(startTime) >= 500000000){
                    when(map[y][x][1]){
                        SpriteName.BLAST.letter -> {
                            map[y][x][1] = SpriteName.FOG_1.letter
                        }
                        SpriteName.FOG_1.letter -> {
                            map[y][x][1] = SpriteName.FOG_2.letter
                        }
                        SpriteName.FOG_2.letter -> {
                            map[y][x][1] = SpriteName.BACKGROUND.letter
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

            weaponList.add(Weapon(currentPlayer, weaponType, TimeUtils.nanoTime(), currentPlayer.xCell, currentPlayer.yCell, Rectangle(bombX, bombY, scaleRatioX, scaleRatioY)))
        }
    }
}