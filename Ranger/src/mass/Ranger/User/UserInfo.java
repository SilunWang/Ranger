package mass.Ranger.User;

import mass.Ranger.Web.WebHelper;

/**
 * Created by v-mengyl on 1/7/2015.
 */
public class UserInfo {

    private String userName;
    private String userId;
    private String sessionID;

    public UserInfo(String userName, String userId) {
       new UserInfo(userName, userId, null);
    }

    public UserInfo(String userName, String userId, String sessionID){
        if(sessionID != null){
            this.userName = userName;
            this.userId = userId;
            this.sessionID = sessionID;
        }else{
            WebHelper webHelper = new WebHelper();
            this.userName = userName;
            this.userId = userId;
            this.sessionID = webHelper.getSessionIdFromWeb();
        }

    }

    public String getUserId(){
        return this.userId;
    }

    public String getUserName(){
        return this.userName;
    }

    public String getSessionID(){
        return this.sessionID;
    }





}
