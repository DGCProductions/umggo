package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.util.SkyFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication implements ClientStateListener, ActionListener {
    Client myClient;
    Node dumbassfuckingpub;
    boolean connected = false;
    Vector3f lol;
    ColorRGBA thecol;
    static Main app;
    Node allboxes;
    Node worldandboxxes;
    Node model;
    String name = "Player";
    AudioNode pew;
    AudioNode owie;
    AudioNode chatf;
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    private Spatial sceneModel;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;
    public static void main(String[] args) throws BackingStoreException, IOException {
         app = new Main();


        BufferedImage[] icons = new BufferedImage[]{

                ImageIO.read(Main.class.getResource("/Interface/256x.png")),
                ImageIO.read(Main.class.getResource("/Interface/128x.png")),
                ImageIO.read(Main.class.getResource("/Interface/32x.png")),
                ImageIO.read(Main.class.getResource("/Interface/16x.png"))
        };

         AppSettings newSettings = new AppSettings(true);
      newSettings.setVSync(true);
      newSettings.setResolution(1000, 700);
      newSettings.setIcons(icons);
      String title = "ultra megagamer gungame online";
      newSettings.load(title);
      newSettings.setTitle(title);
app.setSettings(newSettings);
        app.setPauseOnLostFocus(false);
        
        app.start(JmeContext.Type.Display);
        
     

    }
    
    public void killFeed(String killer) {
       
        BitmapText killFeed = new BitmapText(guiFont, false);
        killFeed.setText(killer);
        killFeed.setSize(app.getContext().getSettings().getWidth() / 50.8f);
        killFeed.setColor(ColorRGBA.Cyan);
        killFeed.setLocalTranslation(app.getContext().getSettings().getWidth() - killFeed.getLineWidth(), killFeed.getLineHeight(), 0);
        app.getGuiNode().attachChild(killFeed);

        killFeed.addControl(new KillFeedControl());
    }
    
    private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(.7f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
    }
    @Override
    public void simpleInitApp() {
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        worldandboxxes = new Node("worldandboxes");
        rootNode.attachChild(worldandboxxes);
        allboxes = new Node("allboxes");
        worldandboxxes.attachChild(allboxes);
       
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        flyCam.setMoveSpeed(100);
       
      

        // We load the scene from the zip file and adjust its size.
         dumbassfuckingpub = (Node) assetManager.loadModel("Models/newScene.j3o");
        sceneModel = dumbassfuckingpub.getChild("cols");
        sceneModel.setLocalScale(2f);
        pew = new AudioNode(assetManager, "Sounds/speech1.wav", AudioData.DataType.Buffer);
        pew.setLooping(false);  // activate continuous playing
        pew.setPositional(false);
        pew.setVolume(4);
        rootNode.attachChild(pew);
        owie = new AudioNode(assetManager, "Sounds/owie.wav", AudioData.DataType.Buffer);
        owie.setLooping(false);  // activate continuous playing
        owie.setPositional(false);
        owie.setVolume(4);
        rootNode.attachChild(owie);
        chatf = new AudioNode(assetManager, "Sounds/NewBeep.wav", AudioData.DataType.Buffer);
        chatf.setLooping(false);  // activate continuous playing
        chatf.setPositional(false);
        chatf.setVolume(1);
        rootNode.attachChild(chatf);
        // We set up collision detection for the scene by creating a
        // compound collision shape and a static RigidBodyControl with mass zero.
        CollisionShape sceneShape
                = CollisionShapeFactory.createMeshShape(sceneModel);
        landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);
        
        setUpKeys();
        setUpLight();
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
          SSAOFilter ssao = new SSAOFilter();
        fpp.addFilter(ssao);
        viewPort.addProcessor(fpp);
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        model = (Node) assetManager.loadModel("Models/gunplayer.j3o");
        //model.setLocalScale(0.5f);
        model.setLocalTranslation(0, 10, 0);
        rootNode.attachChild(model);
        // We attach the scene and the player to the rootnode and the physics space,
        // to make them appear in the game world.
        worldandboxxes.attachChild(sceneModel);
        bulletAppState.getPhysicsSpace().add(landscape);
        bulletAppState.getPhysicsSpace().add(player);
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/BrightSky.dds",
                SkyFactory.EnvMapType.CubeMap));
       
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(10, 10, 0));
        
        
        String ip = JOptionPane.showInputDialog(null,
                "IP?",
                "IP?",
                JOptionPane.INFORMATION_MESSAGE);
        String port = JOptionPane.showInputDialog(null,
                "Port?",
                "Port?",
                JOptionPane.INFORMATION_MESSAGE);
	
        name = JOptionPane.showInputDialog(null,
                "Name?",
                "Name?",
                JOptionPane.INFORMATION_MESSAGE);
        
        
         myClient = null;
        
        try {
            //192.168.1.160
            //6143
            myClient = Network.connectToServer(ip, Integer.parseInt(port));
           
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            
            
        }
        
        initCrossHairs();
        killFeed(name + " has connected");
         myClient.addMessageListener(new MessageListener<Client>() {
            @Override
            public void messageReceived(Client source, Message message) {
               // if(connected) {
                if (message instanceof HelloMessage) {
                    // do something with the message
                    HelloMessage helloMessage = (HelloMessage) message;
                    if(helloMessage.getType() == 1) {
                       for (int i = 0; i < allboxes.getQuantity(); i++) {
                            Node bruh = (Node) allboxes.getChild(i);
                            if (bruh.getChild(0).getName().equals(helloMessage.getSomething())) {
                                killFeed(bruh.getUserData("name") + " has disconnected");
                                allboxes.detachChild(allboxes.getChild(i));
                            }
                        }
                    } else if(helloMessage.getType() == 2) {
                        Vector3f coords = player.getPhysicsLocation();
                        Message ping = new PlayerMsg(-1, coords, cam.getDirection(),name);
                        ((PlayerMsg) ping).col = thecol;
                        ping.setReliable(false);
                        myClient.send(ping);
                    } else if (helloMessage.getType() == 3) {
                        Node lol = (Node) dumbassfuckingpub.getChild("spawns");
                        int bruh = lol.getQuantity();
                        Random random = new Random();

                        player.setPhysicsLocation(lol.getChild(random.nextInt(bruh)).getWorldTranslation().addLocal(0,3,0).mult(2));
                        app.enqueue(new Callable<Spatial>() {
                            public Spatial call() throws Exception {
                                killFeed(name + " death");
                                owie.playInstance();
                                return null;
                            }
                        });
                        
                    } 
                    if(helloMessage.getType() == 4) {
                        
                        for (int i = 0; i < allboxes.getQuantity(); i++) {
                            Node bruh = (Node) allboxes.getChild(i);
                            if (bruh.getChild(0).getName().equals(helloMessage.getSomething())) {
                                final int pp = i;
                                app.enqueue(new Callable<Spatial>() {
                                    public Spatial call() throws Exception {
                                        killFeed(allboxes.getChild(pp).getUserData("name") + " death");
                                        return null;
                                    }
                                });
                            }
                        }
                    }
                    if(helloMessage.getType()==5) {
                         Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                                 mat.setColor("Color", helloMessage.getMyCol());
                                 thecol = helloMessage.getMyCol();
                                 model.setMaterial(mat);
                    }
                    if (helloMessage.getType() == 6) {
                        for (int i = 0; i < allboxes.getQuantity(); i++) {
                            Node bruh = (Node) allboxes.getChild(i);
                            if (bruh.getChild(0).getName().equals(helloMessage.getSomething())) {
                                final AudioNode ww = new AudioNode(assetManager, "Sounds/speech1.wav", AudioData.DataType.Buffer);
                                ww.setLooping(false);  // activate continuous playing
                                ww.setPositional(true);
                                ww.setVolume(4);
                                ww.setLocalTranslation(allboxes.getChild(i).getLocalTranslation());
                                app.enqueue(new Callable<Spatial>() {
                                    public Spatial call() throws Exception {
                                        rootNode.attachChild(ww);
                                        ww.playInstance();
                                        return null;
                                    }
                                });
                            }
                        }
                    }
                    if (helloMessage.getType() == 7) {
                        for (int i = 0; i < allboxes.getQuantity(); i++) {
                            Node bruh = (Node) allboxes.getChild(i);
                            if (bruh.getChild(0).getName().equals(helloMessage.getSomething())) {
                                final AudioNode ww = new AudioNode(assetManager, "Sounds/owie.wav", AudioData.DataType.Buffer);
                                ww.setLooping(false);  // activate continuous playing
                                ww.setPositional(true);
                                ww.setVolume(4);
                                ww.setLocalTranslation(allboxes.getChild(i).getLocalTranslation());
                                
                                app.enqueue(new Callable<Spatial>() {
                                    public Spatial call() throws Exception {
                                        rootNode.attachChild(ww);
                                ww.playInstance();
                                        return null;
                                    }
                                });
                               
                            }
                        }
                    }
                    if (helloMessage.getType() == 8) {
                        final String aa = helloMessage.getSomething();
                        app.enqueue(new Callable<Spatial>() {
                            public Spatial call() throws Exception {
                                killFeed(aa);
                                chatf.playInstance();
                                return null;
                            }
                        });
                    }
                    System.out.println("Client #" + source.getId() + " received: '" + helloMessage.getSomething() + "'");
                }
            }
             
         }, HelloMessage.class);
         
         myClient.addMessageListener(new MessageListener<Client>() {
            
             @Override
             public void messageReceived(Client source, final Message message) {
                // if(connected) {
                 if (message instanceof PlayerMsg) {
                     // do something with the message
                     app.enqueue(new Callable<Node>() {
                         @Override
                         public Node call() throws Exception {
                             
                             PlayerMsg playa = (PlayerMsg) message;
                             boolean exists = false;

                             for (int i = 0; i < allboxes.getQuantity(); i++) {
                                 Node bruh = (Node) allboxes.getChild(i);
                                 if (bruh.getChild(0).getName().equals(Integer.toString(playa.getIndex()))) {
                                  allboxes.getChild(i).setLocalTranslation(playa.getPos().addLocal(0, -2.5f, 0));
                                    
                                     allboxes.getChild(i).lookAt(new Vector3f(playa.getDir().x, 0, playa.getDir().z).addLocal(allboxes.getChild(i).getLocalTranslation()), Vector3f.UNIT_Y);
                                     exists = true;
                                     if(allboxes.getChild(i).getUserData("name") == null) {
                                         allboxes.getChild(i).setUserData("name", playa.getName());
                                         killFeed(playa.getName() + " has connected");
                                     }
                                     
                                     BitmapText aaa = (BitmapText) bruh.getChild(2);
                                     aaa.setText(playa.getName());
                                     aaa.setBox(new Rectangle(-playa.getName().length() * .2f, 0, 12, 6));
                                 }

                             }

                             if (!exists) {
                                Node gamer = (Node) assetManager.loadModel("Models/enemy.j3o");
                                
                                gamer.getChild("a").setName(Integer.toString(playa.getIndex()));
                                 
        BillboardControl control=new BillboardControl();
        
                                 Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                                 mat.setColor("Color", playa.getCol());
                                 gamer.setMaterial(mat);
                                 gamer.setUserData("name", playa.getName());
                                 gamer.setLocalTranslation(playa.getPos());
                                 allboxes.attachChild(gamer);
                                 BitmapText txt = new BitmapText(guiFont, false);
                                 
                                 txt.setQueueBucket(Bucket.Transparent);
                                 txt.setSize(01f);
                                 txt.setLocalTranslation(0, 4f, 0f);
                                txt.setColor(playa.getCol());
                                 
                                 txt.addControl(control);
                                 gamer.attachChild(txt);
                                 
                             }
                             return null;
                         }
                         });
                     
 
                 }
             
             }
        }, PlayerMsg.class);
        myClient.addClientStateListener(this);

            
        myClient.start();
        
        
        
       
        System.out.println(myClient.isStarted());
          System.out.println(myClient.isConnected());    
      
    }
    @Override
    public void clientConnected(Client arg0) {

        connected = true;
        System.out.println("deemedconnected");
    }
    
    
    protected void initCrossHairs() {
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - ch.getLineWidth() / 2,
                settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }
    @Override
    public void simpleUpdate(float tpf) {
       

        if(connected) {
            if (cam.getUp().y < 0) {
                cam.lookAtDirection(new Vector3f(0, cam.getDirection().y, 0), new Vector3f(cam.getUp().x, 0, cam.getUp().z));
            }
            camDir.set(cam.getDirection()).multLocal(0.4f, 0.0f, 0.4f);

            camLeft.set(cam.getLeft()).multLocal(0.4f);
            camDir.y = 0;
            camLeft.y = 0;
            walkDirection.set(0, 0, 0);
            if (cam.getUp().y > .01) {
            if (left) {
                walkDirection.addLocal(camLeft);
            }
            if (right) {
                walkDirection.addLocal(camLeft.negate());
            }
            if (up) {
                walkDirection.addLocal(camDir);
            }
            if (down) {
                walkDirection.addLocal(camDir.negate());
            }
            }
           
            player.setWalkDirection(walkDirection);
            
            cam.setLocation(player.getPhysicsLocation());
           
            
                Vector3f coords = player.getPhysicsLocation();
                Message message = new PlayerMsg(-1, coords, cam.getDirection(),name); 
                message.setReliable(false);
                myClient.send(message);
            
            Vector3f vectorDifference = new Vector3f(cam.getLocation().subtract(model.getWorldTranslation()));
            model.setLocalTranslation(vectorDifference.addLocal(model.getLocalTranslation()));

            Quaternion worldDiff = new Quaternion(cam.getRotation().mult(model.getWorldRotation().inverse()));
            model.setLocalRotation(worldDiff.multLocal(model.getLocalRotation()));
            //Move gun to the bottom right of the screen
            model.move(cam.getDirection().mult(.10f));
            model.move(cam.getUp().mult(0));
            model.move(cam.getLeft().mult(0));
            model.rotate(0.0f, 0, 0);
            if(player.getPhysicsLocation().y < -10) {
                Node lol = (Node) dumbassfuckingpub.getChild("spawns");
                int bruh = lol.getQuantity();
                Random random = new Random();

                player.setPhysicsLocation(lol.getChild(random.nextInt(bruh)).getWorldTranslation().addLocal(0, 3, 0).mult(2));
            }
        }
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    public void onAction(String binding, boolean isPressed, float tpf) {
        if(connected) {
        if (binding.equals("Left")) {
            left = isPressed;
        } else if (binding.equals("Right")) {
            right = isPressed;
        } else if (binding.equals("Up")) {
            up = isPressed;
        } else if (binding.equals("Down")) {
            down = isPressed;
        } else if (binding.equals("Jump")) {
            if (isPressed) {
                if (player.onGround()) {
                    player.setJumpSpeed(15);
                    player.jump();
                    
                }
            }
        } else if (binding.equals("chat") && !isPressed) {
            String chat = JOptionPane.showInputDialog(null,
                    "Chat?",
                    "Chat?",
                    JOptionPane.INFORMATION_MESSAGE);
            Message cchatt = new HelloMessage(name + ": " + chat, 8, null);
            
            myClient.send(cchatt);
        }
        if (binding.equals("Shoot") && !isPressed) {
            
            CollisionResults results = new CollisionResults();
            pew.playInstance();
            Message pewww = new HelloMessage("pew", 6, null);
            myClient.send(pewww);

            Ray ray = new Ray(cam.getLocation(), cam.getDirection());
            
            worldandboxxes.collideWith(ray, results);
            
            
           
            if (results.size() > 0) {
                
                CollisionResult closest = results.getClosestCollision();
               
                for (int i = 0; i < allboxes.getQuantity(); i++) {
                    Node bruh = (Node) allboxes.getChild(i);
                    if(closest.getGeometry().equals(bruh.getChild(0))) {
                        
                        Message message = new HelloMessage(bruh.getChild(0).getName(), 3, null);
                        myClient.send(message);
                        System.out.println("HIT!");
                        owie.playInstance();
                       // killFeed("You shot " + allboxes.getChild(i).getUserData("name"));
                    }
                }
            } 
        }
        }
    }
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Left");
        inputManager.addMapping("chat", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(this, "chat");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
        inputManager.addMapping("Shoot",
                 // trigger 1: spacebar
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
        inputManager.addListener(this, "Shoot");
    }


    @Override
  public void destroy() {
    
      myClient.close();
      super.destroy();
  }

    

    @Override
    public void clientDisconnected(Client arg0, DisconnectInfo arg1) {
        
    }
}
