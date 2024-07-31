package tfar.nations3;

import net.minecraftforge.fml.common.Mod;

@Mod(Nations3.MOD_ID)
public class Nations3Forge {
    
    public Nations3Forge() {
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        Nations3.init();
        
    }
}