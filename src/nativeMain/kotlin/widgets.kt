// SPDX-License-Identifier: MIT OR Apache-2.0

package libui.ktx

import kotlinx.cinterop.*
import libui.*
import platform.posix.tm
import platform.posix.*

/** Wrapper class for [uiDateTimePicker] - a widget to edit date and time. */
open class DateTimePicker internal constructor(
    alloc: CPointer<uiDateTimePicker>?
) : Control<uiDateTimePicker>(alloc) {
    constructor() : this(uiNewDateTimePicker())

    internal var action: (DateTimePicker.() -> Unit)? = null
    internal open var defaultFormat = "%c"

    /** The current value as posix `struct tm` */
    fun getValue(value: CPointer<tm>) = uiDateTimePickerTime(ptr, value)

    /** Set current value from posix `struct tm` */
    fun setValue(value: CPointer<tm>) = uiDateTimePickerSetTime(ptr, value)

    /** The current value in Unix epoch */
    var value: time_t
        get() = memScoped {
            val tm = alloc<tm>()
            getValue(tm.ptr)
            mktime(tm.ptr)
        }
        set(value) = memScoped {
            val time = alloc<time_tVar>()
            time.value = value
            setValue(localtime(time.ptr)!!)
        }

    /** The current value as String. */
    fun textValue(format: String = defaultFormat): String = memScoped {
        val tm = alloc<tm>()
        val buf = allocArray<ByteVar>(64)
        uiDateTimePickerTime(ptr, tm.ptr)
        strftime(buf, 64, format, tm.ptr)
        return buf.toKString()
    }

    /** Function to be run when the user makes a change to the Picker.
     *  Only one function can be registered at a time. */
    fun action(block: DateTimePicker.() -> Unit) {
        action = block
        uiDateTimePickerOnChanged(ptr, staticCFunction { _, ref ->
            with(ref.to<DateTimePicker>()) {
                action?.invoke(this)
            }
        }, ref.asCPointer())
    }
}
