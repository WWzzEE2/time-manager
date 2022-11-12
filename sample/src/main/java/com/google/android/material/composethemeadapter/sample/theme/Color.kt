package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

//zyc Colorful import
val Lilac = Color(0xFFCCB6DC)
val Yellow = Color(0xFFFFCB66)
val YellowVariant = Color(0xFFFFDE9F)
val Coral = Color(0xFFF3A397)
val White = Color(0xFFFFFFFF)
val MintGreen = Color(0xFFACD6B8)
val DarkGray = Color(0xFF2B2B2D)

val DarkCoral = Color(0xFFF7A374)
val DarkYellow = Color(0xFFFFCE6F)
val Yellow_Awake = Color(0xFFffeac1)
val Yellow_Rem = Color(0xFFffdd9a)
val Yellow_Light = Color(0xFFffcb66)
val Yellow_Deep = Color(0xFFff973c)

val Red_T = Color(0xC8FF7777)

class courseBlockColor {
    companion object Obj {
        private val colors: List<List<Color>> = mutableListOf(
            mutableListOf(
                Color(0xFF77B4BF),
                Color(0xFF97CED7),
                Color(0xFF46909D),
                Color(0xFFB1D8DF),
            ),
            mutableListOf(
                Color(0xFF288BD8),
                Color(0xFF56B8FF),
                Color(0xFF7BC5FD),
                Color(0xFFADD9FA),
            )
        )

        fun getColor(select: Int = 0, style: Int = 0): Color {
            if (select >= 0) {
                return colors[style][select.mod(colors[style].size)];
            }
            return colors[style].random();
        }
    }
}

class ddlBlockColor {
    companion object Obj {
        private val colors: List<Color> =
            mutableListOf(
                Color(0xFFB1D8DF),
            )

        fun getColor(select: Int = 0): Color {
            if (select >= 0) {
                return colors[select.mod(colors.size)];
            }
            return colors.random();
        }
    }
}

