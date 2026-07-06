package com.deditor.nativefx

import android.graphics.Bitmap

object NativeEffects {
    init {
        System.loadLibrary("deditor_native")
    }

    external fun grayscale(bitmap: Bitmap)
    external fun invert(bitmap: Bitmap)
    external fun warm(bitmap: Bitmap)
    external fun cool(bitmap: Bitmap)
    external fun vignette(bitmap: Bitmap)
}
