package com.avinash.autocurrencyconverter.Services

import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.graphics.drawable.IconCompat
import com.avinash.autocurrencyconverter.DummyActivity
import com.avinash.autocurrencyconverter.R

class TileService: TileService() {
    companion object{
        @JvmStatic
        val TILE_BROADCAST="broadcast_tile"
    }

    override fun onTileAdded() {
        // Called when the tile is added to the Quick Settings
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.label = "Convert Currency"
        qsTile.icon = Icon.createWithResource(applicationContext,R.drawable.convert_icon)
        qsTile.updateTile()
    }

    override fun onStartListening() {
        // Called when the tile becomes visible
        qsTile.label = "Convert Currency"
        qsTile.icon = Icon.createWithResource(applicationContext,R.drawable.convert_icon)

        qsTile.updateTile()

    }

    override fun onStopListening() {
        // Called when tile is no longer visible
    }

    override fun onTileRemoved() {
        // Called when tile is removed from Quick Settings
    }
    override fun onClick() {
        super.onClick()
        sendBroadcast(Intent(TILE_BROADCAST))

        startActivity(Intent(applicationContext, DummyActivity::class.java))
    }
}