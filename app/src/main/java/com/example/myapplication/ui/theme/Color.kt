package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Red_T = Color(0x9EFF4D4D)

class courseBlockColor {
    companion object Obj {
        private val colors: List<Color> =
            mutableListOf(
                Color(0xFF77B4BF),
                Color(0xFF97CED7),
                Color(0xFF46909D),
                Color(0xFFB1D8DF),
            )

        fun getColor(select:Int = 0): Color {
            if(select>=0)
            {
                return colors[select.mod(colors.size)];
            }
            return colors.random();
        }
    }

}