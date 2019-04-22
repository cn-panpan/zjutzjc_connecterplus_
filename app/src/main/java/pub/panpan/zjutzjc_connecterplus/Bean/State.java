package pub.panpan.zjutzjc_connecterplus.Bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by panpan on 21/04/2019.
 */

public class State extends BmobObject {
    private String username;
    private Boolean flag;
    private Boolean PCstate;
    private Boolean MBstate;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Boolean getPCstate() {
        return PCstate;
    }

    public void setPCstate(Boolean PCstate) {
        this.PCstate = PCstate;
    }

    public Boolean getMBstate() {
        return MBstate;
    }

    public void setMBstate(Boolean MBstate) {
        this.MBstate = MBstate;
    }
}
