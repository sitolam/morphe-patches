/**
 * Copyright 2026 Hoo-dles
 * https://github.com/hoo-dles/morphe-patches
 */

package hoodles.morphe.patches.macrofactor.premium

import app.morphe.patcher.Fingerprint

object BuildCustomerInfoFingerprint : Fingerprint(
    name = "buildCustomerInfo",
    definingClass = "/CustomerInfoFactory;",
    // "subscriptions" alone is ambiguous as of RevenueCat SDK versions bundled with
    // MacroFactor >= 5.8.x: parseSubscriptionInfos(JSONObject, Date) also contains it.
    // "subscriber" + "non_subscriptions" are unique to buildCustomerInfo(JSONObject, ...).
    strings = listOf("subscriber", "non_subscriptions")
)