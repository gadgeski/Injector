package com.example.injector.ui.proxy

import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.injector.domain.ProcessedText
import com.example.injector.domain.TextProcessor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * クリップボード読み取り用プロキシActivity
 * 画面には表示されない（透明）が、フォアグラウンド判定を得るために存在する
 */
@AndroidEntryPoint // ★ Added: Hiltを使うために追加
class ClipboardProxyActivity : ComponentActivity() {

    // ★ Added: テキスト加工ロジックを注入
    @Inject
    lateinit var textProcessor: TextProcessor

    // 処理済みフラグ（フォーカスイベントが複数回走った場合の重複防止）
    private var isProcessed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // UIは描画しないので setContentView は呼ばない
    }

    // onCreate ではなく、ウィンドウフォーカスを得たタイミングで実行する
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus && !isProcessed) {
            isProcessed = true
            injectClipboardContent()

            // 仕事が終わったら即座に終了
            finish()
            disableTransitionAnimation()
        }
    }

    private fun disableTransitionAnimation() {
        // minSdk = 34 なので、分岐なしで新しいAPIを使用
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
    }

    private fun injectClipboardContent() {
        // Context.CLIPBOARD_SERVICE -> CLIPBOARD_SERVICE
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        // クリップボードが空、またはテキストでない場合
        if (!clipboard.hasPrimaryClip() || clipboard.primaryClipDescription?.hasMimeType("text/*") == false) {
            Toast.makeText(this, "Clipboard is empty or not text", Toast.LENGTH_SHORT).show()
            return
        }

        val item = clipboard.primaryClip?.getItemAt(0)
        val text = item?.text?.toString()

        if (text.isNullOrBlank()) {
            Toast.makeText(this, "No text to inject", Toast.LENGTH_SHORT).show()
            return
        }

        // ★ Added: TextProcessor を使ってデータを加工（タイトル生成など）
        val processedData = textProcessor.process(text)

        sendToBugMemo(processedData)
    }

    private fun sendToBugMemo(data: ProcessedText) {
        val bugMemoPackage = "com.example.bugmemo"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            // ★ Changed: 加工済みの本文をセット
            putExtra(Intent.EXTRA_TEXT, data.content)
            // ★ Added: 自動生成されたタイトルもセット（BugMemo側が対応していれば使われる）
            putExtra(Intent.EXTRA_SUBJECT, data.title)

            // BugMemoを明示的に指定して、Chooserを出さずに即転送
            setPackage(bugMemoPackage)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            startActivity(intent)
            // 成功フィードバック
            Toast.makeText(this, "💉 Injection Complete", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            // BugMemoが見つからない場合は、汎用シェアメニューを出すフォールバック
            try {
                val chooser = Intent.createChooser(intent, "Inject to...").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(chooser)
            } catch (_: Exception) {
                Toast.makeText(this, "Target not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}