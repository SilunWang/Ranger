package mass.Ranger.Device;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.google.gson.annotations.SerializedName;
import mass.Ranger.Util.Utils;

import java.text.MessageFormat;
import java.util.UUID;

@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
public class DeviceProfile {
    private static DeviceProfile instance = new DeviceProfile();
    @SerializedName("DeviceUniqueId")
    private String deviceUniqueId;
    @SerializedName("ExtendedDeviceInfo")
    private String extendedDeviceInfo;

    private DeviceProfile() {
        this.deviceUniqueId = Build.FINGERPRINT;
        this.extendedDeviceInfo = MessageFormat.format("{0} {1}", Utils.capitalize(Build.MANUFACTURER), Utils.capitalize(Build.DISPLAY));
    }

    public static DeviceProfile getInstance() {
        return instance;
    }

    public static String getPhoneDependencyId(Context context) {
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        String serial = telephonyManager.getSimSerialNumber();
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        UUID deviceUUID = new UUID(
                androidId.hashCode(),
                ((long) (deviceId == null ? 0 : deviceId.hashCode()) << 32) | (serial == null ? 0 : serial.hashCode()));
        return deviceUUID.toString();
    }
}
