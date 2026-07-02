package com.tlincompose

import com.tlincompose.core.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher

class TestDispatcherProvider(
    dispatcher: CoroutineDispatcher,
) : DispatcherProvider {
    override val main: CoroutineDispatcher = dispatcher
    override val io: CoroutineDispatcher = dispatcher
    override val default: CoroutineDispatcher = dispatcher
}
