package com.example.newe_pit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.ui.theme.*

/**
 * Komponen Reusable: Stepper Control dengan Input Manual Direct Typing & Tombol Penyesuaian Cepat Rapi.
 */
@Composable
fun EPITStepperControl(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    quickSteps: List<Int> = listOf(10, 50)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryNavy,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Tombol Kurang Cepat (-50, -10)
            quickSteps.reversed().forEach { step ->
                Button(
                    onClick = { onValueChange((value - step).coerceAtLeast(0)) },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = PrimaryNavy
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                ) {
                    Text(text = "-$step", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Input Manual Field Interaktif (Langsung diketik misal 312)
            OutlinedTextField(
                value = if (value == 0) "" else value.toString(),
                onValueChange = { input ->
                    val parsed = input.filter { it.isDigit() }.toIntOrNull() ?: 0
                    onValueChange(parsed)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = PrimaryNavy
                ),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ActionCyan,
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .weight(1.8f)
                    .height(48.dp)
            )

            // Tombol Tambah Cepat (+10, +50)
            quickSteps.forEach { step ->
                Button(
                    onClick = { onValueChange(value + step) },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = PrimaryNavy
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                ) {
                    Text(text = "+$step", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}