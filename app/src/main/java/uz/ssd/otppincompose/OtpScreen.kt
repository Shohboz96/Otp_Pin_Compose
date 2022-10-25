package uz.ssd.otppincompose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun PinInput(
    modifier: Modifier = Modifier,
    length: Int = 5,
    value: String = "",
    onValueChanged: (String) -> Unit,
    focusedBorderColor: Color = Color.Red,
    unFocusedBorderColor: Color = Color.Black,
    cursorColor: Color = Color.Black,
    otpWidth:Int = 40,
    otpHeight:Int = 40,
    textSize:Int = 18,
    hint:String = ""
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    BasicTextField(
        value = value,
        onValueChange = {
            if (it.length <= length) {
                if (it.all { c -> c in '0'..'9' }) {
                    onValueChanged(it)
                }
                if (it.length >= length) {
                    keyboard?.hide()
                }
            }
        },
        // Hide the text field
        modifier = Modifier
            .size(0.dp)
            .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(length) {
            OtpCell(
                modifier = modifier
                    .size(width = otpWidth.dp, height = otpHeight.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colors.surface)
                    .clickable(indication = rememberRipple(color = Color.White), interactionSource = MutableInteractionSource()) {
                        focusRequester.requestFocus()
                        keyboard?.show()
                    },
                value = value.getOrNull(it)?.toString() ?: hint,
                focusedBorderColor = focusedBorderColor,
                unFocusedBorderColor = unFocusedBorderColor,
                textSize = textSize,
                isCursorVisible = value.length == it,
                cursorColor = cursorColor
            )
            Spacer(modifier = Modifier.size(8.dp))
        }
    }


}

@Composable
fun OtpCell(
    modifier: Modifier,
    value: String,
    textSize:Int = 18,
    isCursorVisible: Boolean = true,
    focusedBorderColor: Color = Color.Red,
    unFocusedBorderColor: Color = Color.Black,
    cursorColor: Color = Color.Black,
) {
    val scope = rememberCoroutineScope()
    val (cursorSymbol, setCursorSymbol) = remember { mutableStateOf("") }

    LaunchedEffect(key1 = cursorSymbol, isCursorVisible) {
        if (isCursorVisible) {
            scope.launch {
                delay(350)
                setCursorSymbol(if (cursorSymbol.isEmpty()) "|" else "")
            }
        }
    }

    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isCursorVisible) focusedBorderColor else unFocusedBorderColor,
                shape = RoundedCornerShape(6.dp)
            )
    ) {
        Text(
            text = if (isCursorVisible) cursorSymbol else value,
            style = MaterialTheme.typography.body1,
            fontSize = textSize.sp,
            color = if(isCursorVisible) cursorColor else Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
