package com.southampton.comp6239.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

class ImageUtil {

    companion object{

        fun imageToBase64(bitmap :Bitmap) :String{
            val output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,output)
            val toByteArray = output.toByteArray()
            val encodedString: String = Base64.encodeToString(toByteArray, Base64.DEFAULT)
            return encodedString
        }

        fun base64ToImage(bitmap64 : String) : Bitmap{
            val bytes = Base64.decode(bitmap64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            return bitmap
        }
    }

}