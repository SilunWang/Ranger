package mass.Ranger.Security;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

@SuppressWarnings("UnusedDeclaration")
public class AccessToken {
    private final static String TAG = AccessToken.class.getName();
    @SerializedName("AccessToken")
    private String token;
    @SerializedName("TimeoutSecond")
    private long timeoutSecond;
    @SerializedName("UserID")
    private String userId;
    @SerializedName("LoginName")
    private String loginName;
    @SerializedName("UserName")
    private String userName;
    @SerializedName("Roles")
    private String[] roles;
    @SerializedName("DeviceKey")
    private String encryptKey;
    @Expose
    private Long updateTimeMillis = null;

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setUpdateTimeMillis(long updateTimestamps) {
        if (this.updateTimeMillis == null) {
            this.updateTimeMillis = updateTimestamps;
        } else {
            throw new IllegalStateException("updateTimeMillis can be set only once");
        }
    }

    public boolean isTokenExpired() {
        return (System.currentTimeMillis() - this.updateTimeMillis) / 1000 > timeoutSecond;
    }

    public String getToken() {
        return token;
    }

    public long getTimeoutSecond() {
        return timeoutSecond;
    }

    public String getUserId() {
        return userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getUserName() {
        return userName;
    }

    public String[] getRoles() {
        return Arrays.copyOf(roles, roles.length);
    }
}
