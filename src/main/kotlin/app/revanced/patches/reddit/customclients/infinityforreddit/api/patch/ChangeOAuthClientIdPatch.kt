package app.revanced.patches.reddit.customclients.infinityforreddit.api.patch

import app.revanced.patcher.annotation.*
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.reddit.customclients.AbstractChangeOAuthClientIdPatch
import app.revanced.patches.reddit.customclients.infinityforreddit.api.fingerprints.GetHTTPBasicAuthHeaderFingerprint
import app.revanced.patches.reddit.customclients.infinityforreddit.api.fingerprints.LoginActivityOnCreateFingerprint
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@Name("change-oauth-client-id")
@Description("Changes the OAuth client ID.")
@Compatibility([Package("ml.docilealligator.infinityforreddit")])
@Version("0.0.1")
class ChangeOAuthClientIdPatch : AbstractChangeOAuthClientIdPatch(
    "infinity://localhost",
    Options,
    listOf(GetHTTPBasicAuthHeaderFingerprint, LoginActivityOnCreateFingerprint)
) {
    override fun List<MethodFingerprintResult>.patch(context: BytecodeContext): PatchResult {
        forEach {
            val clientIdIndex = it.scanResult.stringsScanResult!!.matches.first().index
            it.mutableMethod.apply {
                val oAuthClientIdRegister = getInstruction<OneRegisterInstruction>(clientIdIndex).registerA

                replaceInstruction(
                    clientIdIndex,
                    "const-string v$oAuthClientIdRegister, \"$clientId\""
                )
            }
        }

        return PatchResultSuccess()
    }

    companion object Options : AbstractChangeOAuthClientIdPatch.Options.ChangeOAuthClientIdOptionsContainer()
}