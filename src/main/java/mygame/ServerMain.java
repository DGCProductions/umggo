package mygame;


import com.jme3.app.SimpleApplication;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.ConnectionListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializable;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class ServerMain extends SimpleApplication implements ConnectionListener{
    Server myServer;
    public static void main(String[] args) throws BackingStoreException {



      ServerMain app = new ServerMain();
       AppSettings newSettings = new AppSettings(true);

        String title = "umgggO SERVER";
        newSettings.load(title);
        newSettings.setTitle(title);
      newSettings.setVSync(true);
app.setSettings(newSettings);
      app.start(JmeContext.Type.Headless); // headless type for servers!
    }
    @Override
    public void simpleInitApp() {
String port = JOptionPane.showInputDialog(null,
                "Port?",
                "Port?",
                JOptionPane.INFORMATION_MESSAGE);
       myServer = null;
        try {
            myServer = Network.createServer(Integer.parseInt(port));
        } catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        Serializer.registerClass(HelloMessage.class);
        
        Serializer.registerClass(PlayerMsg.class);


        myServer.addConnectionListener(this);


        myServer.addMessageListener(new MessageListener<HostedConnection>() {
           @Override
           public void messageReceived(HostedConnection source, Message message) {
               if (message instanceof HelloMessage) {
                   // do something with the message
                   
                   if(((HelloMessage) message).getType() == 3) {
                       Message msg = new HelloMessage("dead",3, null);
                       message.setReliable(true);
                       int id = Integer.parseInt(((HelloMessage) message).getSomething());
                       myServer.broadcast(Filters.in(myServer.getConnection(id)), msg);
                       System.out.println("gothere");
                       
                       Message death = new HelloMessage(Integer.toString(id), 4, null);
                       myServer.broadcast(death);
                   }
                   if(((HelloMessage) message).getType() == 6) {
                      
                       
                       Message peww = new HelloMessage(Integer.toString(source.getId()), 6, null);
                       myServer.broadcast(Filters.notEqualTo(source), peww);
                   }
                   if (((HelloMessage) message).getType() == 7) {

                       Message peww = new HelloMessage(Integer.toString(source.getId()), 7, null);
                       myServer.broadcast(Filters.notEqualTo(source), peww);
                   }
                   if (((HelloMessage) message).getType() == 8) {

                       Message cc = new HelloMessage(((HelloMessage) message).getSomething(),8, null);
                       System.out.println(((HelloMessage) message).getSomething());
                       myServer.broadcast(cc);
                   }
                   HelloMessage helloMessage = (HelloMessage) message;
                   System.out.println("Server received '" + helloMessage.getSomething() + "' from client #" + source.getId());
               }
           }
            
        }, HelloMessage.class);
    

        myServer.addMessageListener(new MessageListener<HostedConnection>() {
           @Override
           public void messageReceived(HostedConnection source, Message message) {
               if (message instanceof PlayerMsg) {
                  
                   PlayerMsg playa = (PlayerMsg) message;
                   Message msg = new PlayerMsg(source.getId(), playa.getPos(), playa.getDir(), playa.getName());
                   ((PlayerMsg) msg).col = playa.col;
                   message.setReliable(false);
                   myServer.broadcast(Filters.notEqualTo(source), msg);
                 
                   //System.out.println(helloMessage.getSomething() + ", " + source.getId());
               }
           }
       }, PlayerMsg.class);
        
        myServer.start();
      
        
      


      

    }
    
    
    
    @Override
    public void simpleUpdate(float tpf) {
        //System.out.println(myServer.getConnections());
        //System.out.println(myServer.getConnections().size());
        
       
    }
    
    @Override
    public void destroy() {
      
      myServer.close();
        super.destroy();
    }

    @Override
    public void connectionAdded(Server arg0, HostedConnection arg1) {
        Message message = new HelloMessage("Welcome! There are currently " + myServer.getConnections().size() + " connected.", 0, null);
        myServer.broadcast(Filters.in(arg1), message);
        
        Message message2 = new PlayerMsg(arg1.getId(),new Vector3f(0,0,0), new Vector3f(0,0,0),null);
        myServer.broadcast(Filters.notEqualTo(arg1), message2);
        
        Message setyocol = new HelloMessage("", 5, ((PlayerMsg) message2).getCol());
        myServer.broadcast(Filters.in(arg1), setyocol);
        
        Message nc = new HelloMessage(Integer.toString(arg1.getId()), 2,null);
        myServer.broadcast(nc);
    }

    @Override
    public void connectionRemoved(Server arg0, HostedConnection arg1) {
        Message message2 = new HelloMessage("Client #" + arg1.getId() + " has left! There are currently " + myServer.getConnections().size() + " connected.", 0, null);
        myServer.broadcast(message2);
        
        Message dc = new HelloMessage(Integer.toString(arg1.getId()),1, null);
        myServer.broadcast(dc);
    }
}


