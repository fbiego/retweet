package com.fbiego.tweet.utils


import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.core.app.RemoteInput

class RemoteInputParcel : Parcelable {
    var label: String?
        private set
    var resultKey: String?
        private set
    private var choices: Array<String?>? = arrayOfNulls(0)
    var isAllowFreeFormInput: Boolean
        private set
    var extras: Bundle?
        private set

    constructor(input: RemoteInput) {
        label = input.label.toString()
        resultKey = input.resultKey
        charSequenceToStringArray(input.choices)
        isAllowFreeFormInput = input.allowFreeFormInput
        extras = input.extras
    }

    constructor(`in`: Parcel) {
        label = `in`.readString()
        resultKey = `in`.readString()
        choices = `in`.createStringArray()
        isAllowFreeFormInput = `in`.readByte().toInt() != 0
        extras = `in`.readParcelable(Bundle::class.java.classLoader)
    }

    private fun charSequenceToStringArray(charSequence: Array<CharSequence>?) {
        if (charSequence != null) {
            val size = charSequence.size
            choices = arrayOfNulls(charSequence.size)
            for (i in 0 until size) choices!![i] = charSequence[i].toString()
        }
    }

    fun getChoices(): Array<String?>? {
        return choices
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(label)
        dest.writeString(resultKey)
        dest.writeStringArray(choices)
        dest.writeByte((if (isAllowFreeFormInput) 1 else 0).toByte())
        dest.writeParcelable(extras, flags)
    }

    override fun describeContents(): Int {
        return 0
    }



    companion object CREATOR : Parcelable.Creator<RemoteInputParcel> {
        override fun createFromParcel(parcel: Parcel): RemoteInputParcel {
            return RemoteInputParcel(parcel)
        }

        override fun newArray(size: Int): Array<RemoteInputParcel?> {
            return arrayOfNulls(size)
        }
    }
}