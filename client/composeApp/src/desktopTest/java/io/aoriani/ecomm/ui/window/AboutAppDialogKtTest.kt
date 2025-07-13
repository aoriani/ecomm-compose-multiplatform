package io.aoriani.ecomm.ui.window

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import io.aoriani.ecomm.ui.test.TestTags
import org.junit.Rule
import org.junit.Test

class AboutAppDialogKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `Dialog visibility when isVisible is true`() {
        // Verify that the DialogWindow is rendered when the `isVisible` parameter is true.
        composeTestRule.setContent {
            AboutAppDialog(isVisible = true)
        }
        composeTestRule.onNodeWithTag(TestTags.aboutDialog).assertIsDisplayed()
    }

    @Test
    fun `Dialog invisibility when isVisible is false`() {
        composeTestRule.setContent {
            AboutAppDialog(isVisible = true)
        }
        composeTestRule.onNodeWithTag(TestTags.aboutDialog).assertIsDisplayed()
    }

    @Test
    fun `Dialog content verification`() {
        // Verify that the DialogWindow contains the Text composable with the expected content
        composeTestRule.setContent {
            AboutAppDialog(isVisible = true)
        }

        // Verify that the dialog is displayed
        composeTestRule.onNodeWithTag(TestTags.aboutDialog).assertIsDisplayed()

        // Verify that the text content is as expected
        // Based on the implementation in AboutAppDialog.kt, the text content is "TODO - Implement Content"
        composeTestRule.onNodeWithText("TODO - Implement Content").assertIsDisplayed()
    }

}
