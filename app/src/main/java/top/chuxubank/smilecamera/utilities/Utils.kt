package top.chuxubank.smilecamera.utilities

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission

object Utils {
    internal fun requestRuntimePermissions(activity: Activity) {

        val allNeededPermissions = getRequiredPermissions(activity).filter {
            checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        if (allNeededPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity, allNeededPermissions.toTypedArray(), /* requestCode= */ 0
            )
        }
    }

    internal fun allPermissionsGranted(context: Context) = getRequiredPermissions(context).all {
        checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getRequiredPermissions(context: Context): Array<String> {
        return try {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_PERMISSIONS
            )
            val ps = info.requestedPermissions
            if (ps != null && ps.isNotEmpty()) ps else arrayOf()
        } catch (e: Exception) {
            arrayOf()
        }
    }
}