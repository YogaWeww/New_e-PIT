package com.example.newe_pit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.ui.theme.*

/**
 * Komponen Reusable: Stepper Control (+ / -) untuk Input Berat & Jumlah Tangkapan
 */
@Composable
fun EPITStepperControl(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    stepSizes: List<Int> = listOf(1, 10, 50)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryNavy
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            stepSizes.reversed().forEach { step ->
                StepperButton(text = "-$step") {
                    onValueChange((value - step).coerceAtLeast(0))
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(38.dp)
                    .background(CardSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryNavy,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            stepSizes.forEach { step ->
                StepperButton(text = "+$step") {
                    onValueChange(value + step)
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
private fun StepperButton(
    text: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .background(NeutralCanvas, RoundedCornerShape(8.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryNavy
        )
    }
}