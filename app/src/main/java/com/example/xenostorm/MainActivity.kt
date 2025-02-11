package com.example.xenostorm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text

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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.LineHeightStyle.Alignment.Companion

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlinx.coroutines.delay
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
        Circle()

        Box(modifier = Modifier.align(Alignment.BottomCenter).offset(y=-10.dp)){
            DragShooter()
        }

    }
}
@Composable
fun Circle(){
    var point by remember { mutableStateOf(0) }
    fun GetPoint(click: Int){
        point += click
    }
    // x and y will remember the balls position
    var x by remember { mutableStateOf(100f) }
    var y by remember {mutableStateOf(200f)}

//    val cornerRadius by animateDpAsState(targetValue = 50.dp, animationSpec = tween(1000))

    val smoothX by animateFloatAsState(targetValue = x, animationSpec = tween(500)) // animationSpec makes it more smoother
    val smoothY by animateFloatAsState(targetValue = y, animationSpec = tween(500))
    LaunchedEffect(Unit) {
        while(true){
            x = Random.nextInt(-250, 250).toFloat();
            y = Random.nextInt(20,800).toFloat();
            delay(1000);
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(40.dp), verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Score: "+point, color = Color.White, fontSize = 24.sp)
        Box(
            modifier = Modifier
                .offset(smoothX.dp, smoothY.dp)
                .clip(CircleShape).background(Color.hsv(310f, 1f, 1f))
                .size(100.dp)
                .clickable { GetPoint(1) }

            // offset always need to come first or the ball won't move
        )
    }
}
@Composable
fun DragShooter(){
    // Stores the Position of the Shooter and its changes dynamically
    var shooterX by remember { mutableStateOf(0f) }


    var containerWidth by remember { mutableStateOf(0) }
    var shooterWidth = 50.dp;

    Box( // Shooter Bar
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black)
            .onSizeChanged { containerWidth = it.width}, // stores the container width in pixels
        contentAlignment = Alignment.Center,

    ) {
        Box( // Shooter Box: Capable of Moving when Clicked and Dragged
            modifier = Modifier
                .offset(shooterX.dp)
                .pointerInput(Unit){ // Pointer Input will Detect the Pointer Movements
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        // turning to density pixels dp cause offset returns pixels and pointerInput needs to be in dp
                        val newX = shooterX + dragAmount.x / density
                        val  containerWidthInDP = containerWidth / density
                        shooterX = newX.coerceIn(
                            minimumValue = -(containerWidthInDP - shooterWidth.value) / 2,
                            maximumValue = (containerWidthInDP - shooterWidth.value) / 2 )
                        // Calculations Made to make sure the shooter doesn't move out of the Shooter's Container
                    }
                }
                .size(shooterWidth, 30.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.hsv(200f, 1f, 1f))
        )
    }

}
