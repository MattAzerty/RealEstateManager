package fr.melanoxy.realestatemanager

import android.content.ContentResolver
import android.content.Context
import android.content.pm.ProviderInfo
import android.net.Uri
import android.os.Build.VERSION_CODES.Q
import androidx.test.core.app.ApplicationProvider
import fr.melanoxy.realestatemanager.data.MyContentProvider
import fr.melanoxy.realestatemanager.data.MyContentProvider.Companion.AUTHORITY
import fr.melanoxy.realestatemanager.ui.utils.DATABASE_NAME
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Q])
class MyContentProviderTest {

    private var contentResolver : ContentResolver? = null
    //private var shadowContentResolver : ShadowContentResolver? = null
    private var myContentProviderProvider : MyContentProvider? = null

    @Before
    fun setUp() {
        contentResolver = ApplicationProvider.getApplicationContext<Context>().contentResolver
        val providerInfo = ProviderInfo()
        providerInfo.authority = AUTHORITY
        providerInfo.grantUriPermissions = true
        val controller = Robolectric.buildContentProvider(MyContentProvider::class.java).create(providerInfo)
        myContentProviderProvider = controller.get()

    }

    @Test
    fun onCreate() {
        val res  = myContentProviderProvider?.onCreate()
        Assertions.assertEquals(res, true)
    }

    @Test
    fun queryFail() {
        val uri = Uri.parse("content://$AUTHORITY/WRONG_DATABASE_NAME")
        val exception = assertThrows(IllegalArgumentException::class.java) {
            contentResolver?.query(uri,null,null,null,null)
        }
        assertEquals("Unknown URI: $uri", exception.message)
    }

   @Test
    fun querySuccess() {
        val uri = Uri.parse("content://$AUTHORITY/$DATABASE_NAME")
        val cursor = contentResolver?.query(uri,null,null,null,null)
        Assertions.assertNotNull(cursor)
        var name : String? = null
        if(cursor != null && cursor.moveToFirst()){
            name = cursor.getString(cursor.getColumnIndex("name"))
        }
        cursor?.close()
        Assertions.assertNull(name)
    }

}