package org.alphatrade.snippet.java.xmlparse;

import org.alphatrade.snippet.java.xmlparse.DICHead;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author Knowles
 * Streaming SAX XML Parser
 */
public class MemberPanelSTAX extends JFrame {

    //StAX
    private final String path = "http://www.dreamincode.net/forums/xml.php?showuser=";
    URL url;
    XMLInputFactory factory;
    XMLStreamReader reader;
    //GUI
    private JPanel dataHolder;
    private JLabel name, joinDate, group, numPosts, pic;
    private JTextField memberInput;
    private JButton parseMember;
    //location
    private Dimension screenCoords;
    private final int APP_WIDTH = 200, APP_HEIGHT = 400;
    //Object
    private DICHead thePerson;

    public MemberPanelSTAX(){
        //Parser setup
        try{
            //save this for later
            factory = XMLInputFactory.newInstance();
            //single instance, no handler
            thePerson = new DICHead();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //GUI
        screenCoords = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(APP_WIDTH, APP_HEIGHT);
        setTitle("DIC XML");
        setLocation(screenCoords.width/2 - APP_WIDTH/2, screenCoords.height/2 - APP_HEIGHT/2);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new GridLayout(2,1));
        dataHolder = new JPanel();
        pic = new JLabel();
        name = new JLabel("Name: ");
        joinDate = new JLabel("Join Date: ");
        group  = new JLabel("Group: ");
        numPosts = new JLabel("Total Posts: ");
        memberInput = new JTextField("Enter user number...");
        parseMember = new JButton("Parse Details");
        parseMember.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                //specfifc error catching--user information
                try{
                    int userID = Integer.parseInt(memberInput.getText());
                    if (userID <= 0) throw new NumberFormatException();
                    url = new URL(path+userID);
                    reader = factory.createXMLStreamReader(url.openStream());
                    readStream();
                    fillOutDetails();
                }
                catch(NumberFormatException ex){
                    JOptionPane.showMessageDialog(null, "Please enter a valid number");
                }
                catch (XMLStreamException er){
                    JOptionPane.showMessageDialog(null, "Error Parsing. Please try again.");
                }
                catch (IOException err){
                    JOptionPane.showMessageDialog(null, "IO Issue. Please try again");
                }
            }
        }
        );

        dataHolder.setLayout(new GridLayout(6,1));
        dataHolder.add(name);
        dataHolder.add(joinDate);
        dataHolder.add(group);
        dataHolder.add(numPosts);
        dataHolder.add(memberInput);
        dataHolder.add(parseMember);

        add(pic);
        add(dataHolder);
        validate();
        setVisible(true);
    }

    public void fillOutDetails(){
        pic.setIcon(thePerson.getPicture());
        name.setText("Name: " + thePerson.getName());
        joinDate.setText("Join Date: " + thePerson.getJoinDate());
        group.setForeground(thePerson.getGroupColor());
        group.setText("Group: " + thePerson.getGroup());
        numPosts.setText("Total Posts: " + thePerson.getTotalPosts());
    }
    
    private void readStream(){
        try{
            int tagType;
            boolean notDone = true;
            String temp = "", tagName = "", color = "";
            //read through it all, we'll break prematurely
            //right after "join date" close tag
            while(reader.hasNext() && notDone){
                //next() returns the "type" of constant
                tagType = reader.next();
                //start, end, etc...
                switch(tagType){
                    case XMLStreamConstants.START_ELEMENT:
                        tagName = reader.getLocalName();
                        //the presence of a span tag is
                        //indicative of a color i.e. anything but "Members"
                        if(tagName.equals("span")){
                            //only one attribute "style"
                            color = reader.getAttributeValue(0);
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        temp = reader.getText();
                        //same deal with SAX, except no Handler reuqired
                        if(tagName.equals("name")){
                            thePerson.setName(temp);
                        }
                        else if(tagName.equals("photo")){
                            thePerson.setImage(temp);
                        }
                        //some members don't have a "color"
                        //thus no "span" tag
                        else if(tagName.equals("group")){
                            thePerson.setGroup(temp);
                            thePerson.setColor(Color.BLACK);
                        }
                        else if(tagName.equals("span")){
                            thePerson.setGroup(temp);
                            //color setting
                            //still a consistency issue
                            //see DOM blog post [part 2]
                            if(temp.equals("Moderators")){
                                thePerson.setColor(Color.BLUE);
                            }
                            else if (temp.equals("Admins")){
                                thePerson.setColor(Color.GREEN.darker());
                            }
                            else{
                                color = color.substring(6, 13); //grab the HTML color code
                                color = color.substring(1); //get rid of the '#'
                                thePerson.setColor(new Color(Integer.parseInt(color, 16)));
                            }
                        }
                        else if(tagName.equals("posts")){
                            thePerson.setNumPosts(temp);
                        }
                        else if(tagName.equals("joined")){
                            thePerson.setJoinDate(temp);
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        if(tagName.equals("joined")){
                            //debug
                            //System.out.println("end joined tag hit, quitting early!");
                            notDone = false;
                        }
                        //avoid some URL issues
                        temp = "";
                        tagName = "";
                        break;
                }
            }
            //clean up after ourselves
            reader.close();
        }
        catch(Exception e){
            System.out.println("Failure in the stream reading!");
            e.printStackTrace();
        }
    }
}

