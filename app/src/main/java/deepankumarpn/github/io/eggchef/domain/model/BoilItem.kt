package deepankumarpn.github.io.eggchef.domain.model

import android.content.Context
import deepankumarpn.github.io.eggchef.R

data class BoilItem(
    val title: String,
    val seconds: Int,
    val description: String,
    val isCustom: Boolean = false
) {
    companion object {
        fun defaultBoilItems(context: Context): List<BoilItem> = listOf(
            BoilItem(
                title = context.getString(R.string.boil_title_dippy),
                seconds = 180,
                description = context.getString(R.string.boil_desc_dippy)
            ),
            BoilItem(
                title = context.getString(R.string.boil_title_runny),
                seconds = 360,
                description = context.getString(R.string.boil_desc_runny)
            ),
            BoilItem(
                title = context.getString(R.string.boil_title_soft),
                seconds = 480,
                description = context.getString(R.string.boil_desc_soft)
            ),
            BoilItem(
                title = context.getString(R.string.boil_title_hard),
                seconds = 600,
                description = context.getString(R.string.boil_desc_hard)
            ),
            BoilItem(
                title = context.getString(R.string.boil_title_overcooked),
                seconds = 720,
                description = context.getString(R.string.boil_desc_overcooked)
            )
        )
    }
}
