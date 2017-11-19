package com.github.charleslzq.dicom.store

fun <F> defaultComparator(f1: F?, f2: F): Boolean {
    return f1 == f2
}

class FieldChecker<in T, F>(
        private val getter: (T) -> F?,
        private val comparator: (F?, F) -> Boolean = ::defaultComparator
) {
    fun needUpdate(old: T, new: T): Boolean {
        val oldFieldValue = getter.invoke(old)
        val newFieldValue = getter.invoke(new)
        return when (newFieldValue) {
            null -> false
            else -> !comparator.invoke(oldFieldValue, newFieldValue)
        }
    }
}