package com.muzz.core.domain.usecase


import com.muzz.core.domain.model.Message
import com.muzz.core.domain.repo.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessages @Inject constructor(
    private val repo: MessageRepository
) {
    operator fun invoke(): Flow<List<Message>> = repo.observeAll()
}
