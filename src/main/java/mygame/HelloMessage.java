package mygame;

import com.jme3.math.ColorRGBA;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class HelloMessage extends AbstractMessage {

    private String hello;       // custom message data
    private int type;
    private ColorRGBA col;
    
  
    public HelloMessage() {} 
    public HelloMessage(String s, int i, ColorRGBA mycol) {
        hello = s;
        type = i;
        col = mycol;
    } // custom constructor
    
    public String getSomething() {
        return hello;
    }
    public ColorRGBA getMyCol() {
        return col;
    }
    public int getType() {
        return type;
        // 0-normal
        // 1-disconnected
        // 2-reupdate
        // 3-hitdet
        // 4-publicdeath
        //5-setcol
        //6-pew
        //7-owie
        //8-chat
    }
}
