package com.github.charleslzq.dicom.spring

import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.core.task.SimpleAsyncTaskExecutor

open class DicomParseConfigurer {

    open fun parseExecutor(): AsyncTaskExecutor {
        return SimpleAsyncTaskExecutor()
    }

    open fun fileWatchExecutor(): AsyncTaskExecutor {
        return SimpleAsyncTaskExecutor()
    }

    open fun dataListenerExecutor(): AsyncTaskExecutor {
        return SimpleAsyncTaskExecutor()
    }
}