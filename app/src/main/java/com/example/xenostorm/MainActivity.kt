package com.example.xenostorm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration


import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GUI()
        }
    }
}
@Preview
@Composable
fun GUI(){
    Box(modifier = Modifier.
    background(Color.hsv(270f, 0.8f, 0.1f))
        .fillMaxSize()){
        FallingAsteroids()
        Box(modifier = Modifier.align(Alignment.BottomCenter).offset(y=-15.dp)){
            DragShooter()
        }


    }
}
data class Asteroid(
    val id: Int, // will generate a unique id to each asteroid
    val x: Float, // will hold an asteroid's x position
    val y: Float,  // will hold an asteroid's y position
    val startedFromMiddle: Boolean
)
@Composable
fun FallingAsteroids(){
    var asteroids by remember { mutableStateOf(listOf<Asteroid>()) }
    // will hold all the asteroids on the screen with this list. New one's are added and the old ones are removed from the list
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp.value;
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp.value;
    val speed = 3f;
    val xspeed = 3f;
    val yspeed = 5f; // can change these to make the game harder or easier
    val asteroidSize = 50.dp;
    val idCounter = remember { AtomicInteger(0) }



    LaunchedEffect(Unit) { // Creating new Asteroids at Random positions, and Removing them Off screen
        while (true){
            val startFromMiddle = Random.nextBoolean();
            // Randomly assigns the Asteroids to start from the middle of the screen or from the Top
            val startY = if (startFromMiddle)
            {
                screenHeight/2 - asteroidSize.value}
            else { -asteroidSize.value }
            val newAsteroid = Asteroid(
                id = idCounter.incrementAndGet(),
                x = (Math.random() * (screenWidth - asteroidSize.value*2)).toFloat(),
                y= startY,
                startedFromMiddle = startFromMiddle
            )
            asteroids += newAsteroid; // Adding the new Asteroids to the List
            asteroids = asteroids.filter {
                asteroid -> asteroid.y < screenHeight + asteroidSize.value }
            delay(1000)
        }

    }
    LaunchedEffect(Unit){
        while (true){
            asteroids = asteroids.map { asteroid -> asteroid.copy(
                x= asteroid.x + xspeed,
                y= asteroid.y + yspeed
            )
            }
            delay(16)
        }
    }
    Box(modifier = Modifier.fillMaxSize()){
        asteroids.forEach{
            asteroid -> Box(
                modifier = Modifier.offset(asteroid.x.dp, asteroid.y.dp).size(asteroidSize).clip(
                    CircleShape).background(Color.Yellow)
            )
        }
    }

}

// The Shooter Can Be Dragged and Released to Shoot
@Composable
fun DragShooter(){
    // Stores the Position of the Shooter and its changes dynamically
    var shooterX by remember { mutableStateOf(0f) }
    // Stores the Last Position of the Shooter
    var lastshooterX by remember { mutableStateOf(0f) }

    var containerWidth by remember { mutableStateOf(0) }
    val shooterWidth = 50.dp;
    var centerX by remember { mutableStateOf(0f) }
    var pelletPositions by remember { mutableStateOf(listOf<Pair<Float, Float>>()) }
    // this list will remember each pellets x and y positions in pairs (0,700), (45,700)
    Box {
        Box(
            // Shooter Bar
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black)
                .onSizeChanged {
                    containerWidth = it.width
                }, // stores the container width in pixels
            contentAlignment = Alignment.Center,

            ) {
            Box( // Shooter Box: Capable of Moving when Clicked and Dragged
                modifier = Modifier
                    .offset(shooterX.dp)
                    .pointerInput(Unit) { // Pointer Input will Detect the Pointer Movements
                        detectDragGestures(onDrag = { change, dragAmount ->
                            change.consume()
                            // turning to density pixels dp cause offset returns pixels and pointerInput needs to be in dp
                            shooterX += dragAmount.x / density
                            // Constrain to container bounds
                            val maxOffset = (containerWidth / 2f) / density - shooterWidth.value / 2
                            shooterX = shooterX.coerceIn(-maxOffset, maxOffset)
                                                    },
                            onDragEnd = {
                                centerX = (containerWidth / 2f)/ density;
                                var pelletX = centerX + shooterX - (shooterWidth.value / 2)
                                pelletPositions = pelletPositions + Pair(pelletX, 800 / density)
                                // adding a new pallet to the pallet position list
                            }
                        )
                    }
                    .size(shooterWidth, 30.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.hsv(200f, 1f, 1f))
            )
        }
        pelletPositions.forEach{(x,y)-> Pellets(pelletX = x, startpelletY = y) }
    }

}
/*When the Shooter is released Pallets will Shoot
The shooter's x will be Pallets X Position Y will be Max Height of screen
 */
@Composable
fun Pellets(pelletX:Float, startpelletY:Float){
    var pelletY by remember { mutableStateOf(startpelletY) }
    val animatedY by animateFloatAsState(targetValue = pelletY, animationSpec = tween(500))
    LaunchedEffect(Unit){
        pelletY = -800f; // if you say of here it means only the shooter's 0f
    }

    Box(modifier = Modifier
        .offset(pelletX.dp, animatedY.dp)
        .clip(RectangleShape)
        .background(Color.Yellow)
        .width(11.dp).height(15.dp))

}













//fun Circle(){
//    // todo : Update the Circle Movements move more like Asteroids Attacking Earth.
//    // todo : Add a Line where the Asteroids would come Minus points
//    // todo : Update it so that when a Pallet hits Score!
//    var point by remember { mutableStateOf(0) }
//    fun GetPoint(click: Int){
//        point += click
//    }
//    // x and y will remember the balls position
//    var x by remember { mutableStateOf(100f) }
//    var y by remember {mutableStateOf(200f)}
//
////    val cornerRadius by animateDpAsState(targetValue = 50.dp, animationSpec = tween(1000))
//
//    val smoothX by animateFloatAsState(targetValue = x, animationSpec = tween(500)) // animationSpec makes it more smoother
//    val smoothY by animateFloatAsState(targetValue = y, animationSpec = tween(500))
//    LaunchedEffect(Unit) {
//        while(true){
//            x = Random.nextInt(-250, 250).toFloat();
//            y = Random.nextInt(20,800).toFloat();
//            delay(1000);
//        }
//    }
//    Column(
//        modifier = Modifier.fillMaxSize().padding(40.dp), verticalArrangement = Arrangement.Top
//    ) {
//        Text(text = "Score: "+point, color = Color.White, fontSize = 24.sp)
//        Box(
//            modifier = Modifier
//                .offset(smoothX.dp, smoothY.dp)
//                .clip(CircleShape).background(Color.hsv(310f, 1f, 1f))
//                .size(100.dp)
//                .clickable { GetPoint(1) }
//
//            // offset always need to come first or the ball won't move
//        )
//    }
//}