package dusty;

import dusty.content.DustyBlocks;
import mindustry.Vars;
import mindustry.ctype.ContentList;
import mindustry.mod.Mod;

public class Scidustry extends Mod {
    private final ContentList[] dustyContent = {
            new DustyBlocks()
    };
    public void init(){
        Vars.enableConsole = true;
    }
    public void loadContent(){
        for(ContentList list : dustyContent){
            list.load();
        }
    }
}