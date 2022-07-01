package mygame;



import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class KillFeedControl extends AbstractControl {
    float timer = 20;
    private SimpleApplication app;
    
    KillFeedControl() {
        
        
              // light color

    }

    @Override
    protected void controlUpdate(float tpf) {
       if(timer > 0) {
           spatial.move(0, 10*tpf*4, 0);
           timer = timer - 1*tpf;
       }
       if(timer <= 0) {
           spatial.getParent().detachChild(spatial);
       }

    }


public void initialize(AppStateManager stateManager, Application app) {



this.app = (SimpleApplication) app;

}
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

    
}