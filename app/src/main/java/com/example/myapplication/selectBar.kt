package com.example.myapplication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.SmallHeadingStyle
import com.example.myapplication.ui.theme.White
import com.example.myapplication.ui.theme.Yellow


@Composable
fun JetLaggedHeaderTabs(
    onTabSelected: (DayTab) -> Unit,
    selectedTab: DayTab,
    modifier: Modifier = Modifier,
) {
    ScrollableTabRow(
        modifier = modifier,
        edgePadding = 12.dp,
        selectedTabIndex = selectedTab.ordinal,
        indicator = { tabPositions: List<TabPosition> ->
            TabRowDefaults.Indicator(
                Modifier
                    .tabIndicatorOffset(
                        tabPositions[selectedTab.ordinal]
                    )
                    .height(2.dp),
                color = Color.Gray
            )
        },
    ) {
        DayTab.values().forEachIndexed { index, dayTab ->
            val selected = index == selectedTab.ordinal
            DDLTabText(
                dayTab = dayTab,
                selected = selected,
                onTabSelected = onTabSelected,
                index = index
            )
        }
    }
}

@Composable
fun JetLaggedHeaderTabs(
    onTabSelected: (WeekTab) -> Unit,
    selectedTab: WeekTab,
    modifier: Modifier = Modifier,
) {
    ScrollableTabRow(
        modifier = modifier,
        edgePadding = 12.dp,
        selectedTabIndex = selectedTab.ordinal,
        indicator = { tabPositions: List<TabPosition> ->
            TabRowDefaults.Indicator(
                Modifier
                    .tabIndicatorOffset(
                        tabPositions[selectedTab.ordinal]
                    )
                    .height(2.dp),
                color = Color.Gray
            )
        },
        divider = {}
    ) {
        WeekTab.values().forEachIndexed { index, weekTab ->
            val selected = index == selectedTab.ordinal
            DDLTabText(
                weekTab = weekTab,
                selected = selected,
                onTabSelected = onTabSelected,
                index = index
            )
        }
    }
}

private val textModifier = Modifier
    .padding(vertical = 6.dp, horizontal = 4.dp)

@Composable
private fun DDLTabText(
    dayTab: DayTab,
    selected: Boolean,
    index: Int,
    onTabSelected: (DayTab) -> Unit,
) {
    Tab(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(16.dp)),
        selected = selected,
        unselectedContentColor = Color.LightGray,
        selectedContentColor = Color.Black,
        onClick = {
            onTabSelected(DayTab.values()[index])
        }
    ) {
        Text(
            modifier = textModifier,
            text = stringResource(id = dayTab.title),
            style = SmallHeadingStyle
        )
    }
}

@Composable
private fun DDLTabText(
    weekTab: WeekTab,
    selected: Boolean,
    index: Int,
    onTabSelected: (WeekTab) -> Unit,
) {
    Tab(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(16.dp)),
        selected = selected,
        unselectedContentColor = Color.LightGray,
        selectedContentColor = Color.Black,
        onClick = {
            onTabSelected(WeekTab.values()[index])
        }
    ) {
        Text(
            modifier = textModifier,
            text = weekTab.name.toString(),
            style = SmallHeadingStyle
        )
    }
}