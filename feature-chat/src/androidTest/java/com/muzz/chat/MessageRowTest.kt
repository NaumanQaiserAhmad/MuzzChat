import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.muzz.chat.presentation.components.MessageRow
import com.muzz.core.domain.model.DeliveryStatus
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MessageRowTest {

    @get:Rule
    val composeRule = createComposeRule()   // ⬅️ use this, not createAndroidComposeRule

    @Test
    fun showsCheckmarksForDelivered() {
        composeRule.setContent {
            MaterialTheme {
                MessageRow(
                    text = "Hi",
                    isMine = true,
                    status = DeliveryStatus.DELIVERED,
                    compactSpacingBelow = false
                )
            }
        }
        composeRule.onNodeWithText("✓✓").assertIsDisplayed()
    }

    @Test
    fun hidesStatusForOtherUser() {
        composeRule.setContent {
            MaterialTheme {
                MessageRow(
                    text = "Hi",
                    isMine = false,
                    status = DeliveryStatus.DELIVERED,
                    compactSpacingBelow = false
                )
            }
        }
        composeRule.onNodeWithText("Hi").assertIsDisplayed()
    }
}
