package com.example.calculator.mvvm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.mvvm.model.Operation
import com.example.calculator.mvvm.model.SelectedBox
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat

class CalculatorViewModel : ViewModel() {

    var numberA by mutableStateOf("0")
        private set

    var numberB by mutableStateOf("0")
        private set

    var operation by mutableStateOf<Operation?>(null)
        private set

    var result by mutableStateOf("0")
        private set

    var selectedBox by mutableStateOf(SelectedBox.A)
        private set

    // For single-event notifications like toast side-effects
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    fun selectBox(box: SelectedBox) {
        selectedBox = box
    }

    fun selectOperation(op: Operation) {
        operation = op
    }

    fun addNumber(digit: String) {
        if (selectedBox == SelectedBox.A) {
            numberA = if (numberA == "0") digit else numberA + digit
        } else {
            numberB = if (numberB == "0") digit else numberB + digit
        }
    }

    fun deleteLast() {
        if (selectedBox == SelectedBox.A) {
            numberA = if (numberA.length <= 1) "0" else numberA.dropLast(1)
        } else {
            numberB = if (numberB.length <= 1) "0" else numberB.dropLast(1)
        }
    }

    fun clearCurrent() {
        numberA = "0"
        numberB = "0"
        result = "0"
        operation = null
        selectedBox = SelectedBox.A
    }

    fun calculate() {
        val valA = numberA.toDoubleOrNull() ?: 0.0
        val valB = numberB.toDoubleOrNull() ?: 0.0
        val currentOp = operation

        if (currentOp == null) {
            viewModelScope.launch {
                _toastEvent.emit("Please select a mathematical operation")
            }
            return
        }

        if (currentOp == Operation.DIV && valB == 0.0) {
            viewModelScope.launch {
                _toastEvent.emit("Division by zero is not allowed!")
            }
            result = "Error"
            return
        }

        val rawResult = when (currentOp) {
            Operation.ADD -> valA + valB
            Operation.SUB -> valA - valB
            Operation.MUL -> valA * valB
            Operation.DIV -> valA / valB
        }

        result = formatResult(rawResult)
    }

    // Format result to a maximum of 4 decimal places without trailing zeros
    private fun formatResult(value: Double): String {
        return if (value % 1 == 0.0) {
            value.toLong().toString()
        } else {
            val df = DecimalFormat("#.####").apply {
                roundingMode = RoundingMode.HALF_UP
            }
            df.format(value)
        }
    }
}