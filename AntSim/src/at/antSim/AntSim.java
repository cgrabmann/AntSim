package at.antSim;

import at.antSim.config.ConfigReader;
import at.antSim.graphics.graphicsUtils.DisplayManager;
import at.antSim.graphics.graphicsUtils.Loader;
import at.antSim.graphics.renderer.MasterRenderer;

/**
 * Created on 24.03.2015.
 * @author Clemens, Flo
 */
public class AntSim {
	
	Loader loader;
	MasterRenderer masterRenderer;
	
    public AntSim(){}

    public static void main(String[] args) {
        AntSim game = new AntSim();
        game.launch();
    }

    public void launch() {
        preInit();
        init();
        postInit();
    }

    private void preInit() {
    	
    	//read config
        ConfigReader myreader = new ConfigReader();
        myreader.readConfig();
    }

    private void init() {		
    	
    	//initialize display manager
    	DisplayManager.createDisplay();
		loader = new Loader();
		
		//initialize render engine
		masterRenderer = new MasterRenderer(loader);
    }

    private void postInit() {
    	
    	//launch main game
    	EngineTester.launch(loader, masterRenderer);
    }
    
//	ConfigWriter mywriter = new ConfigWriter();
//	try {
//		//mywriter.writeConfig();
//	} catch (NoSuchFieldException | SecurityException
//			| IllegalArgumentException | IllegalAccessException
//			| IOException e) {
//		e.printStackTrace();
//	}

}
