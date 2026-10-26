package com.sign3.appvaultintegration.config

import com.sign3.appvault.IgnoreAppVault

/**
 * Use case 4: opt a class out. It is inside fogPackages, but `@IgnoreAppVault` keeps its
 * literals in plaintext -- useful for non-sensitive strings (log tags, UI text) to skip the
 * decrypt cost.
 */
@IgnoreAppVault
object PublicStrings {

    fun appTitle() = "This string is NOT sensitive and stays plaintext."
}
