package Bilibili;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class Test {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        DBforBili dbb=new DBforBili();
        List<Extracted> t=dbb.read(new Date(new java.util.Date().getTime()));
        for(int i=0;i<t.size();i++){
            System.out.println(t.get(i).tags);
        }
    }
}
