package com.muzz.core.domain.policy

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration

object MessagePolicies {
    @RequiresApi(Build.VERSION_CODES.O)
    val SECTION_GAP: Duration = Duration.ofHours(1) // header if gap > 1h
    const val GROUP_GAP_SECS: Long = 20             // compact spacing if <20s & same sender
}
