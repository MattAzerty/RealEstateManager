package fr.melanoxy.realestatemanager.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import fr.melanoxy.realestatemanager.data.dao.RealEstateDao
import fr.melanoxy.realestatemanager.domain.realEstate.RealEstateEntity
import fr.melanoxy.realestatemanager.ui.utils.DATABASE_NAME
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

//https://developer.android.com/guide/topics/providers/content-provider-creating
//@AndroidEntryPoint
class MyContentProvider : ContentProvider() {

    @EntryPoint//https://developer.android.com/training/dependency-injection/hilt-android
    @InstallIn(SingletonComponent::class)
    interface ContentProviderEntryPoint {
        fun realEstateDao(): RealEstateDao
    }

    companion object {
        // defining authority so that other application can access it
        const val AUTHORITY = "fr.melanoxy.realestatemanager.MyContentProvider"
        const val MY_TABLE_CODE = 1
        // parsing the content URI (maps URIs to their corresponding data)
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, DATABASE_NAME, MY_TABLE_CODE)
        }
    }


    lateinit var realEstateDao: RealEstateDao

    override fun onCreate(): Boolean {
        val appContext = context?.applicationContext ?: throw IllegalStateException()
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext, ContentProviderEntryPoint::class.java
        )
        realEstateDao = entryPoint.realEstateDao()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor {
        when (uriMatcher.match(uri)) {//Query selection  MY_TABLE_CODE = allEntries
            MY_TABLE_CODE -> {
                val data =  runBlocking {
                    val result = mutableListOf<RealEstateEntity>()
                    realEstateDao.getAll().collect { list ->
                        result.addAll(list)
                    }
                    result
                }
                return MatrixCursor(arrayOf("_id", "name", "coordinate","price")).apply {
                    data.forEach { row ->
                        newRow().apply {
                            add(row.id.toString())
                            add(row.propertyType)
                            add(row.coordinates)
                            add(row.price.toString())
                        }
                    }
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    // Overridden methods not used in our case
    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw UnsupportedOperationException("Not yet implemented")
    }

}