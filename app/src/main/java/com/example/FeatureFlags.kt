package com.example

/**
 * Release switches. Premium is OFF until real Google Play Billing is integrated:
 * showing a price and unlocking without payment breaks Play's Payments policy.
 * The admin console is a developer tool and only appears in debug builds.
 */
object FeatureFlags {
    const val PREMIUM_ENABLED = false
    val ADMIN_CONSOLE_ENABLED: Boolean get() = BuildConfig.DEBUG
}
