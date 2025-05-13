package com.ikseong.ucompass.ui.util.viewutil

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object ToastUtil {
    // 토스트 메시지 표시 함수
    fun showToast(context: Context, message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}