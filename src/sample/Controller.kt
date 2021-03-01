package sample

import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import java.util.concurrent.TimeUnit
import kotlin.math.floor

class Controller {
    @FXML
    lateinit var canvasDisplay: Canvas

    private lateinit var gc : GraphicsContext

    private val f = Game(40, null, null, null, null)
    private var player: Boolean = false


    private fun renderCells() {
        for (y in 0 until 40) {
            for(x in 0 until 40) {
                if (f.getVal(x, y)) {
                    gc.fillRect((x * 20).toDouble(), (y * 20).toDouble(), 20.0, 20.0)
                }
            }
        }
    }

    private fun renderLines() {
        //clear canvas
        gc.clearRect(0.0, 0.0, canvasDisplay.width, canvasDisplay.height)

        // draw grid lines
        for (x in 0 until 40) {
            gc.strokeLine((x * 20).toDouble(), 0.0, (x * 20).toDouble(), 800.0)
        }
        for (y in 0 until 40) {
            gc.strokeLine(0.0, (y * 20).toDouble(), 800.0, (y * 20).toDouble())
        }
    }

    @FXML
    fun initialize() {
        gc = canvasDisplay.graphicsContext2D

        // line
        gc.stroke = Color.BLACK
        gc.lineWidth = 1.0

        // fill cells
        gc.fill = Color.BLUE

        renderLines()

        for(i in 10 until 20) {
            f.setVal(i, 15, true)
        }

        renderCells()

        with(canvasDisplay) {
            addEventHandler(
                    MouseEvent.MOUSE_PRESSED
            ) { event ->
                if (event != null) {
                    val x = floor(event.x / 20).toInt()
                    val y = floor(event.y / 20).toInt()
                    f.setVal(x, y, !f.getVal(x, y))

                    renderLines()
                    renderCells()
                }
            }
        }

    }

    @FXML
    fun onNextGenButtonClick() {
        renderLines()

        //calculate next generation
        f.nextGeneration()

        //draw cells
        renderCells()

    }


    @FXML
    fun onStartClick() {
        println("start")
        if(!player) {
            /*
            println("notplayer")
            Thread(Runnable {
                println("running thread")
                while (player) {
                    renderLines()
                    //calculate next generation
                    f.nextGeneration()
                    //draw cells
                    renderCells()
                    Thread.sleep(1000)
                }
            }).start()
            */
            while (player) {
                renderLines()
                //calculate next generation
                f.nextGeneration()
                //draw cells
                renderCells()
                TimeUnit.SECONDS.sleep(1);
            }
        } else {
            player = true
        }

    }

    @FXML
    fun onStopClick() {
        player = false
    }

    @FXML
    fun onResetClick() {
        renderLines()

        //set game array all to false
        for(a in 0 until f.getWidth()) {
            for (b in 0 until f.getHeight()) {
                f.setVal(a,b,false)
            }
        }
    }

}

class Game(size: Int, top: Game?, right: Game?, left: Game?, bot: Game?) {
    //size of the game
    private val size = size
    //in case there are games beside the current one to be accounted for
    private val width = size + if (left != null) 1 else 0 + if (right != null) 1 else 0
    private val height = size + if (top != null) 1 else 0 + if (bot != null) 1 else 0

    private var arr = Array(width) { BooleanArray(height) }


    fun nextGeneration() {
        //reserve memory for new generation to not affect the old
        val newArr = Array(width) { BooleanArray(height) }
        var num:Int
        for (y in 0 until height) {
            for(x in 0 until width) {
                // number to represent neighbors
                num = 0
                // top edge case
                if(y == 0) {
                    if (arr[x][y + 1]) num++
                    if(x==0) { //left edge case
                        if (arr[x + 1][y]) num++
                        if (arr[x + 1][y + 1]) num++
                    } else if(x == width-1) { //right edge case
                        if (arr[x - 1][y]) num++
                        if (arr[x - 1][y + 1]) num++
                    } else {
                        if (arr[x + 1][y]) num++
                        if (arr[x + 1][y + 1]) num++
                        if (arr[x - 1][y]) num++
                        if (arr[x - 1][y + 1]) num++
                    }
                } else if(y == height -1) { //bottom edge case
                    if (arr[x][y - 1]) num++
                    if(x==0) { //left edge case
                        if (arr[x + 1][y]) num++
                        if (arr[x + 1][y - 1]) num++
                    } else if(x == width-1) { //right edge case
                        if (arr[x - 1][y]) num++
                        if (arr[x - 1][y - 1]) num++
                    } else {
                        if (arr[x + 1][y]) num++
                        if (arr[x + 1][y - 1]) num++
                        if (arr[x - 1][y]) num++
                        if (arr[x - 1][y - 1]) num++
                    }
                } else {
                    if (arr[x][y - 1]) num++
                    if (arr[x][y + 1]) num++
                    if(x == 0) {//left edge case
                        if (arr[x + 1][y - 1]) num++
                        if (arr[x + 1][y]) num++
                        if (arr[x + 1][y + 1]) num++
                    } else if (x == width-1) { //right edge case
                        if (arr[x - 1][y - 1]) num++
                        if (arr[x - 1][y]) num++
                        if (arr[x - 1][y + 1]) num++
                    } else {
                        if (arr[x + 1][y - 1]) num++
                        if (arr[x + 1][y]) num++
                        if (arr[x + 1][y + 1]) num++
                        if (arr[x - 1][y - 1]) num++
                        if (arr[x - 1][y]) num++
                        if (arr[x - 1][y + 1]) num++
                    }
                }

                // Game of life logic for new generation
                when {
                    num < 2 -> {
                        newArr[x][y] = false
                    }
                    num == 2 -> {
                        newArr[x][y] = arr[x][y]
                    }
                    num == 3 -> {
                        newArr[x][y] = true
                    }
                    else -> {
                        newArr[x][y] = false
                    }
                }
            }
        }
        // assign new generation as the current one
        arr = newArr
    }

    fun setVal(x: Int, y: Int, v: Boolean) {
        arr[x][y] = v
    }
    fun getVal(x: Int, y: Int): Boolean {
        return arr[x][y]
    }
    fun getWidth() : Int {
        return this.width
    }
    fun getHeight(): Int {
        return this.height
    }

/*
    fun getColumn(top: Boolean): BooleanArray {
        return arr[if (top) 0 else (height - 1)]
    }

    fun getRow(top: Boolean): BooleanArray {
        val a = BooleanArray()
        return arr[if(top) 0 else (height -1)]
    }
*/

    override fun toString(): String {
        var out: String = ""
        for (y in 0 until height) {
            for(x in 0 until width) {
                out += if (arr[x][y]) "█" else " "
            }
            out += "\n"
        }
        return out
    }

}
