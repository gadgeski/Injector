package com.gadgeski.injector.service

import android.app.PendingIntent
import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.gadgeski.injector.ui.proxy.ClipboardProxyActivity

class InjectorTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        // タイルを常にアクティブ状態にする
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()

        // クリップボード処理用の透明Activityを起動するIntent
        val intent = Intent(this, ClipboardProxyActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // ★ Fix: Android 14 (API 34) 以降の推奨手順
        // Intent を直接渡すのではなく、PendingIntent でラップしてから渡します。
        // これにより Deprecated 警告が解消され、将来的な互換性も確保されます。
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 通知シェードを閉じて Activity を起動
        startActivityAndCollapse(pendingIntent)
    }
}