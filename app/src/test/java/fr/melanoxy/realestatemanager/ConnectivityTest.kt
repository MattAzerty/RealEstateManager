package fr.melanoxy.realestatemanager

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import fr.melanoxy.realestatemanager.ui.utils.Utils.isInternetAvailable
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNetworkInfo

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class ConnectivityTest {
    @Test
    fun `isInternetAvailable should return false when no internet is available`() {
        // Given
        val context = ApplicationProvider.getApplicationContext<Context>()
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = ShadowNetworkInfo.newInstance(null, 0, 0, false, false)
        Shadows.shadowOf(connectivityManager).setActiveNetworkInfo(networkInfo)

        // When
        val result = isInternetAvailable(context)

        // Then
        Assertions.assertFalse(result)
    }
    @Test
    fun `isInternetAvailable should return false when internet is available`() {
        // Given
        val context = ApplicationProvider.getApplicationContext<Context>()
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = ShadowNetworkInfo.newInstance(null, 0, 0, true, true)
        Shadows.shadowOf(connectivityManager).setActiveNetworkInfo(networkInfo)

        // When
        val result = isInternetAvailable(context)

        // Then
        Assertions.assertTrue(result)
    }
}