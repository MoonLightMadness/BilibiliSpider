package Bilibili;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MainTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException, ParseException {

        Controler controler=new Controler();
        if(!controler.checkDate() && controler.getStatus().equals(STATECODE.DONE.getCode())){
            System.out.println("Start to get Routine");
            getRoutine();
            System.out.println("Success");
        }
        if(STATECODE.UNDONE.getCode().equals(controler.getStatus())){
            System.out.println("Start to get Tags");
            getTags();
            System.out.println("Success");
        }
        if(controler.checkDate() && STATECODE.DONE.getCode().equals(controler.getStatus())){
            System.out.println("Today's work has been done");
        }


    }
    public static void getRoutine() throws SQLException, ClassNotFoundException, IOException {
        Controler controler=new Controler();
        DBforBili dbb=new DBforBili();
        if(!controler.checkDate() ) {
            BiliSite bili=new BiliSite();
            bili.setUrl(new URL("https://www.bilibili.com/ranking"));
            Spider spider=new Spider(bili);
            Extractor extractor=new Extractor(spider.downLoad());
            String[] title=extractor.getTitle().split("\n");
            String[] author=extractor.getAuthor().split("\n");
            String[] point=extractor.getPoint().split("\n");
            String[] url=extractor.getUrl().split("\n");
            for(int i=0;i<title.length;i++){
                dbb.writeWithoutTags(url[i],author[i],point[i],title[i],new Date(new java.util.Date().getTime()).toString());
            }
            dbb.turnToUndone();
            dbb.updateDateTime(new Date(new java.util.Date().getTime()).toString());

        }
        dbb.close();
    }
    public static void getTags() throws SQLException, ClassNotFoundException, ParseException, IOException, InterruptedException {
        BiliSite bili=new BiliSite();
        Spider spider;
        Extractor extractor=new Extractor("c");
        DBforBili dbb=new DBforBili();
        String nowDay=new Date(new java.util.Date().getTime()).toString();
        List<Extracted> afterDay=dbb.read(Tool.ChangeDate(new Date(new java.util.Date().getTime()),-1));
        if(afterDay.size()==0){
            for(int i=2;i<8;i++){
                afterDay=dbb.read(Tool.ChangeDate(new Date(new java.util.Date().getTime()),-i));
                if(afterDay.size()!=0){
                    break;
                }
                if(i==7){
                    System.out.println("No more data");
                    System.exit(0);
                }
            }
        }
        List<Extracted> thisDay=dbb.read(Tool.ChangeDate(new Date(new java.util.Date().getTime()),0));
        List<Extracted> newVideo=new ArrayList<>();
        boolean isRepeat=false;
        for (Extracted value : thisDay) {
//            if(!"".equals(value.tags)){
//                continue;
//            }
            for (Extracted extracted : afterDay) {
                if (value.title.equals(extracted.title)) {
                    System.out.println("Found "+value.title);
                    dbb.writeTags(value.title, extracted.tags, nowDay);
                    isRepeat=true;
                    break;
                }
            }
            if(!isRepeat) {
                newVideo.add(value);
            }
            isRepeat=false;
        }
        int count=1;
        for(int i=0;i<newVideo.size();i++){
            bili.setUrl(new URL(newVideo.get(i).url));
            spider=new Spider(bili);

            dbb.writeTags(newVideo.get(i).title,extractor.getTags(spider.downLoad()),nowDay);
            System.out.print("\rProcessing:"+String.valueOf(count)+"/"+String.valueOf(newVideo.size())+" "+newVideo.get(i).title);
            count++;
            Thread.sleep(5000);
        }
        dbb.setToDone();
        
        dbb.close();
        System.out.println("Done");
    }
}
