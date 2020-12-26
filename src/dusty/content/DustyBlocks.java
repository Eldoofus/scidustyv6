package dusty.content;

import dusty.world.blocks.transistor;
import mindustry.content.Items;
import mindustry.ctype.ContentList;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

import static mindustry.type.ItemStack.with;

public class DustyBlocks implements ContentList {
    public static Block transistor;
    public void load(){
        transistor = new transistor("transistor"){{
            size = 1;
            configurable = true;
            requirements(Category.power, BuildVisibility.shown, with(Items.copper, 1));
            hasPower = true;
            consumesPower = false;
            outputsPower = false;
            configurable = true;
            insulated = true;
            rotate = true;
            sync = true;
        }};
    }
}