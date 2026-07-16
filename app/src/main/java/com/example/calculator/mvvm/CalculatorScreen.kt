package com.example.calculator.mvvm

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.mvvm.model.Operation
import com.example.calculator.mvvm.model.SelectedBox
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CalculatorScreen(vm: CalculatorViewModel) {
    val context = LocalContext.current

    // Listening to single-event side effects (like Division by Zero error)
    LaunchedEffect(key1 = true) {
        vm.toastEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val buttons = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "C", "0", "⌫"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Result display box
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color(0xFFEDEDED))
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Result",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )

                    Text(
                        text = vm.result,
                        fontSize = 60.sp,
                        color = Color(0xFF243EAD),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Input Selector (Number A & Number B)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Box Number A
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFFF1F1F1))
                        .border(
                            width = if (vm.selectedBox == SelectedBox.A) 3.dp else 0.dp,
                            color = if (vm.selectedBox == SelectedBox.A) Color(0xFF9FA8DA) else Color.Transparent,
                            shape = RoundedCornerShape(32.dp)
                        )
                        .clickable { vm.selectBox(SelectedBox.A) }
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Number A",
                            fontSize = 18.sp,
                            color = Color.DarkGray
                        )

                        Text(
                            text = vm.numberA,
                            fontSize = 34.sp,
                            color = Color.Black
                        )
                    }
                }

                // Box Number B
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFFF1F1F1))
                        .border(
                            width = if (vm.selectedBox == SelectedBox.B) 3.dp else 0.dp,
                            color = if (vm.selectedBox == SelectedBox.B) Color(0xFF9FA8DA) else Color.Transparent,
                            shape = RoundedCornerShape(32.dp)
                        )
                        .clickable { vm.selectBox(SelectedBox.B) }
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Number B",
                            fontSize = 18.sp,
                            color = Color.DarkGray
                        )

                        Text(
                            text = vm.numberB,
                            fontSize = 34.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // Math operations row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Operation.values().forEach { op ->
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(if (vm.operation == op) Color(0xFF8DE7D5) else Color(0xFFD8F5EE))
                            .clickable { vm.selectOperation(op) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = op.symbol, fontSize = 28.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Numeric keyboard
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                buttons.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        row.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(90.dp)
                                    .clip(RoundedCornerShape(30.dp))
                                    .background(Color(0xFFF5F5F5))
                                    .border(
                                        BorderStroke(1.dp, Color(0xFFD4D4D4)),
                                        RoundedCornerShape(30.dp)
                                    )
                                    .clickable {
                                        when (item) {
                                            "C" -> vm.clearCurrent()
                                            "⌫" -> vm.deleteLast()
                                            else -> vm.addNumber(item)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item,
                                    fontSize = 22.sp,
                                    color = if (item == "C") Color.Red else Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Calculate button
        item {
            Button(
                onClick = { vm.calculate() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                shape = RoundedCornerShape(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E3FA8)
                )
            ) {
                Text(
                    text = "Calculate",
                    fontSize = 22.sp,
                    color = Color.White
                )
            }
        }
    }
}