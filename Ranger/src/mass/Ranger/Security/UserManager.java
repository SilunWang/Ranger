package mass.Ranger.Security;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class UserManager {
    private final static String                       TAG            = UserManager.class.getName();
    private final static UserManager                  MANAGER        = new UserManager();
    private final static ReentrantLock                lock           = new ReentrantLock();
    private final static Condition                    tokenCondition = lock.newCondition();
    private              AtomicReference<AccessToken> latestToken    = new AtomicReference<AccessToken>(null);

    private UserManager() {
    }

    public static AccessToken getTokenOrWait() {
        lock.lock();
        AccessToken token;
        try {
            while ((token = MANAGER.getToken()) == null) {
                try {
                    //Logger.d(TAG, "Token not found, wait for token to load");
                    tokenCondition.await();
                } catch (InterruptedException e) {
                    //Logger.d(TAG, "Wait for token been interrupted", e);
                }
            }
        } finally {
            lock.unlock();
        }
        return token;
    }

    /**
     * return the current <code>AccessToken</code>, is it's already timeout, return null instead
     *
     * @return the current <code>AccessToken</code>, is it's already timeout, return null instead
     */
    public AccessToken getToken() {
        lock.lock();
        try {
            AccessToken token = latestToken.get();
            if (token != null && token.isTokenExpired()) {
                setToken(null);
                return null;
            }
            return token;
        } finally {
            lock.unlock();
        }
    }

    public void setToken(AccessToken token) {
        lock.lock();
        try {
            latestToken.getAndSet(token);
            //Logger.d(TAG, "token been set, signal others");
            tokenCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    public static UserManager getInstance() {
        return MANAGER;
    }
}
