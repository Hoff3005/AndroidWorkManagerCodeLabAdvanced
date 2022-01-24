package com.example.background.workers

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import org.hamcrest.CoreMatchers.`is`
import org.junit.Rule
import org.junit.Test

class CleanupWorkerTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var wmRule = WorkManagerTestRule()

    @Test
    fun testCleanUpWork(){
        val testUri = copyFileFromTestToTargetCtx(wmRule.testContext, wmRule.targetContext, "test_image.jpg")
        assertThat(uriFileExists(wmRule.targetContext, testUri.toString()), `is`(true))

        val request = OneTimeWorkRequestBuilder<CleanupWorker>().build()
        wmRule.workManager.enqueue(request).result.get()
        val workInfo = wmRule.workManager.getWorkInfoById(request.id).get()

        assertThat(uriFileExists(wmRule.targetContext, testUri.toString()), `is`(false))
        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
    }
}