package Bilibili;

import java.sql.Date;
import java.sql.SQLException;

public class Controler {
    public boolean checkDate() throws SQLException, ClassNotFoundException {
        DBforBili dbb=new DBforBili();
        Date nowTime=new Date(new java.util.Date().getTime());
        Date lastTime=Date.valueOf(dbb.getDateTime());
        dbb.close();
        return lastTime.toString().equals(nowTime.toString());
    }
    public String getStatus() throws SQLException, ClassNotFoundException {
        DBforBili dbb=new DBforBili();
        String s=dbb.getStatus();
        dbb.close();
        return s;
    }
}
