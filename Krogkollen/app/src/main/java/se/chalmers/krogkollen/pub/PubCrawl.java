package se.chalmers.krogkollen.pub;

import java.util.Date;

/**
 * Created by Jonathan Nilsfors on 2014-08-27.
 */
public class PubCrawl {
    private String name;
    private Date date;
    private String description;

    public PubCrawl(String name, Date date, String description){
        this.name = name;
        this.date = date;
        this.description = description;
    }
    public String getName(){
        return this.name;
    }

    public Date getDate(){
        return this.date;
    }

    public String getDescription(){
        return this.description;
    }
}
