package com.example.calorieapp.presentation.components
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs


@Composable
fun WheelPicker(
    range: IntRange,
    initialValue: Int,
    itemHeight: Dp = 60.dp,
    onValueChange: (Int) -> Unit,
    itemsShowOnScreen: Int = 5,
    itemUnit : String? = ""
){
    var listState = rememberLazyListState(initialFirstVisibleItemIndex = initialValue - range.first)
    var snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeight.toPx() }

    val visibleItemsInfo by remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo } }
    val centerOffset by remember {
        derivedStateOf {
            listState.layoutInfo.viewportStartOffset + (listState.layoutInfo.viewportSize.height / 2)
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if(!listState.isScrollInProgress){

            var layoutInfo = listState.layoutInfo

            var centerItem = layoutInfo.visibleItemsInfo.minByOrNull {
                abs((it.offset + it.size/2) - centerOffset)
            }

            centerItem?.let {
                var newValue = range.first + it.index
                onValueChange(newValue)
            }
        }

    }

    Box(
        modifier = Modifier
            .height(itemHeight * itemsShowOnScreen)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        LazyColumn (
            state = listState,
            flingBehavior = snapBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = itemHeight * (itemsShowOnScreen/2).toInt()),
            modifier = Modifier.fillMaxWidth().height(itemHeight * itemsShowOnScreen)
        ){
            items (range.last - range.first + 1){index ->
                 val number = range.first + index

                var itemInfo = visibleItemsInfo.find { it.index == index  }
                var distance = itemInfo?.let{
                    abs((it.offset + it.size/2) - centerOffset).toFloat()
                } ?: Float.MAX_VALUE

                var scale = if(distance < itemHeightPx / 2) 1.2f else 1f
                var alpha = if(distance < itemHeightPx / 2) 1f else 0.3f
                var color = if(distance < itemHeightPx / 2)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.secondary

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(60.dp)
                    )
                    Text(
                        number.toString() + itemUnit,style= TextStyle(
                        fontSize = 18.sp,
                        color = color
                    ), modifier = Modifier.scale(scale).alpha(alpha))
                }
            }
        }
    }
}