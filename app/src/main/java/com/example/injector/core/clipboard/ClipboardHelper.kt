package com.example.injector.core.clipboard

import android.content.ClipboardManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClipboardHelper @Inject constructor(
    // ★ Fix: "private val" を削除し、単なる引数にしました。
    // これによりプロパティ生成が抑制され、アノテーションのターゲットが明確になるため警告が消えます。
    // また、context は init 処理以外で使われていないため、保持する必要がありません。
    @ApplicationContext context: Context
) {
    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    fun getClipboardText(): String? {
        if (!clipboardManager.hasPrimaryClip()) return null
        val item = clipboardManager.primaryClip?.getItemAt(0) ?: return null
        return item.text?.toString()
    }
}