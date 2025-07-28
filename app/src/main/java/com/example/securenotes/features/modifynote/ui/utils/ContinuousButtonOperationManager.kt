package com.example.securenotes.features.modifynote.ui.utils

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class ContinuousButtonOperationManager(
    val scope: CoroutineScope, val continuouslyAdditionCondition: LiveData<Boolean>
) {
    private var continuously: AtomicBoolean = AtomicBoolean(false)
    private var operationJob: Job? = null

    fun start(operation: () -> Unit) {
        operationJob = scope.launch(Dispatchers.IO) {
            continuously.set(true)
            withContext(Dispatchers.Main) {
                operation()
            }
            delay(750)

            while (continuously.get() && continuouslyAdditionCondition.value!!) {
                withContext(Dispatchers.Main) {
                    operation()
                }
                delay(100)
            }
        }
    }

    fun stop() {
        continuously.set(false)
        operationJob?.cancel()
    }

    companion object {
        private const val TAG = "ContinuousButtonOperationManager"
    }
}