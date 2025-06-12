package com.example.securenotes

import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

/**
 * Base class for Hilt tests.
 *
 * This class sets up the Hilt test environment and provides common test functionality.
 * All Hilt tests should extend this class.
 */
abstract class BaseHiltTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Before
    open fun setUp() {
        hiltRule.inject()
    }
}
