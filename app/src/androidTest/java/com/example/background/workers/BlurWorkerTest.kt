package com.example.background.workers

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import org.hamcrest.CoreMatchers.`is`
import org.junit.Rule
import org.junit.Test
import java.lang.Exception

class BlurWorkerTest {

    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    @get:Rule
    var wmRule = WorkManagerTestRule()

    @Test
    fun testFailsIfNoInput() {

        val request = OneTimeWorkRequestBuilder<BlurWorker>().build()
        wmRule.workManager.enqueue(request).result.get()
        val workInfo = wmRule.workManager.getWorkInfoById(request.id).get()

        assertThat(workInfo.state, `is`(WorkInfo.State.FAILED))
    }

    @Test
    @Throws(Exception::class)
    fun testAppliesBlur() {
        val inputDataUri = copyFileFromTestToTargetCtx(
            wmRule.testContext,
            wmRule.targetContext,
            "test_image.jpg"
        )
        val inputData = workDataOf(KEY_IMAGE_URI to inputDataUri.toString())

        val request = OneTimeWorkRequestBuilder<BlurWorker>()
            .setInputData(inputData)
            .build()

        wmRule.workManager.enqueue(request).result.get()

        val workInfo = wmRule.workManager.getWorkInfoById(request.id).get()
        val outputData = workInfo.outputData.getString(KEY_IMAGE_URI)

        assertThat(uriFileExists(wmRule.targetContext, outputData), `is`(true))
        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
    }

}