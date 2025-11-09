package com.muzz.core.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.muzz.core.data.local.AppDatabase
import com.muzz.core.data.local.MessageDao
import com.muzz.core.data.repo.MessageRepositoryImpl
import com.muzz.core.domain.model.DeliveryStatus
import com.muzz.core.domain.model.Message
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.time.Instant


@RunWith(AndroidJUnit4::class)
@Config(manifest=Config.NONE) // ✅ FIX: Added this annotation to remove the manifest warning
class MessageRepositoryImplTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: MessageDao
    private lateinit var repo: MessageRepositoryImpl

    @Before fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.messageDao()
        repo = MessageRepositoryImpl(dao)
    }

    @After fun tearDown() {
        db.close()
    }

    @Test fun insertAndObserve() = runBlocking {
        val id = repo.insert(
            Message(
                fromUserId = "me",
                text = "hi",
                sentAt = Instant.now(),
                deliveryStatus = DeliveryStatus.SENDING
            )
        )
        val list = repo.observeAll().first()
        Assert.assertEquals(1, list.size)
        assertEquals(id, list.first().id)
    }

    @Test fun updateStatus() = runBlocking {
        val id = repo.insert(Message(fromUserId = "me", text = "hi", sentAt = Instant.now(), deliveryStatus = DeliveryStatus.SENDING))
        repo.updateStatus(id, DeliveryStatus.DELIVERED)
        val list = repo.observeAll().first()
        assertEquals(DeliveryStatus.DELIVERED, list.first().deliveryStatus)
    }
}
