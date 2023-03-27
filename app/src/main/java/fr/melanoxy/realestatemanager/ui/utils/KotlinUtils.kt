package fr.melanoxy.realestatemanager.ui.utils

@Suppress("unused") // Receiver reference forces when into expression form.
// See https://github.com/cashapp/exhaustive#library-trailing-property
inline val Any?.exhaustive
    get() = Unit

inline fun <reified T : Any> allFieldsNonNull(obj: T): Boolean {
    return T::class.java.declaredFields.all { field ->
        field.isAccessible = true
        field.get(obj) != null
    }
}