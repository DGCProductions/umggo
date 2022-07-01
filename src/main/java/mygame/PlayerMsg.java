package mygame;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class PlayerMsg extends AbstractMessage {

    private Vector3f hello; 
    private Vector3f dir;// custom message data
    private int index;
    private String name;
    ColorRGBA col;
    public PlayerMsg() {} 
    public PlayerMsg(int i, Vector3f s,Vector3f d, String n) {
        hello = s;
        index = i;
        dir = d;
        name = n;
        col = ColorRGBA.randomColor();
    } // custom constructor
    
    public Vector3f getPos() {
        return hello;
    }
    public Vector3f getDir() {
        return dir;
    }
    public int getIndex() {
        return index;
    }
    public ColorRGBA getCol() {
        return col;
    }
    public String getName() {
        return name;
    }
}
