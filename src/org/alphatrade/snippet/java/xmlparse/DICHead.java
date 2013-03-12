package org.alphatrade.snippet.java.xmlparse;


import java.awt.Color;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author Knowles
 * Encapsulates information retrieved from Dream.In.Code profiles
 */
public class DICHead{
    private String name;
    private String joinDate;
    private String group;
    private String numPosts;
    private ImageIcon pic;
    private Color groupColor;

    public DICHead(){
        name = joinDate = group = numPosts = "";
        groupColor = Color.BLACK;
    }

    //mutators (for parser)
    public void setName(String name)            { this.name = name;}
    public void setGroup(String group)          { this.group = group;}
    public void setJoinDate(String joinDate)    { this.joinDate = joinDate;}
    public void setNumPosts(String numPosts)    { this.numPosts = numPosts;}
    public void setColor(Color groupColor)      {this.groupColor = groupColor;}

    //accessors
    public String getName()                     {return name;}
    public String getJoinDate()                 {return joinDate;}
    public String getGroup()                    {return group;}
    public String getTotalPosts()               {return numPosts;}
    public ImageIcon getPicture()               {return pic;}
    public Color getGroupColor()                {return groupColor;}

    public void setImage(String img){
        try{
            URL url = new URL(img);
            pic = new ImageIcon(url);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //for debug purposes
    public void display(){
        System.out.println("Name: " + name);
        System.out.println("Group: " + group);
        System.out.println("Join Date: " + joinDate);
        System.out.println("Total Posts: " + numPosts);
    }
}

