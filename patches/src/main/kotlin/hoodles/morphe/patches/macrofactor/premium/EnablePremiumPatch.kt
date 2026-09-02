/**
 * Copyright 2026 Hoo-dles
 * https://github.com/hoo-dles/morphe-patches
 */

package hoodles.morphe.patches.macrofactor.premium

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.all.misc.fix.changepackageinstaller.changePackageInstallerPatch
import hoodles.morphe.patches.macrofactor.shared.Constants
import hoodles.morphe.patches.shared.misc.extension.activityOnCreateExtensionHook
import hoodles.morphe.patches.shared.misc.extension.sharedExtensionPatch
import hoodles.morphe.util.requireRootMount

internal val extensionPatch = sharedExtensionPatch(
    "macrofactor",
    activityOnCreateExtensionHook("/FlutterFragmentActivity;")
)

val enablePremiumPatch = bytecodePatch(
    name = "Enable Premium (ROOT)",
    description = "Enables app features locked behind the subscription paywall. Requirements: root mount"
) {
    compatibleWith(*Constants.COMPATIBILITY)

    availability(requireRootMount)

    // Not depending on spoofSignaturePatch: getPackageCertificateHashBytes has no callers
    // in MacroFactor's own code in 5.8.5 — its only callers are Firebase internals
    // (Installations, App Check, Remote Config, Auth). Spoofing it does nothing for the
    // premium unlock (handled entirely by the CustomerInfo JSON patch below) but corrupts
    // the cert hash Firebase sends server-side, causing Installation/App Check registration
    // to be rejected. That leaves the app permanently waiting on Firebase calls that can
    // never succeed, which presents as the app freezing after a period of use.
    dependsOn(extensionPatch, changePackageInstallerPatch())

    execute {
        BuildCustomerInfoFingerprint.method.addInstructions(0, """
            invoke-static { p1 }, Lhoodles/morphe/extension/macrofactor/premium/EnablePremiumPatch;->updateCustomerInfo(Lorg/json/JSONObject;)V
        """.trimIndent())
    }
}