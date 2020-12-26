package dusty.world.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.geom.Vec2;
import arc.struct.IntSeq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.Tile;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.blocks.power.PowerNode;

import static mindustry.Vars.tilesize;

public class transistor extends PowerBlock {
    public int presstick = 1;
    public int timerid = 0;
    public int loopthresh = 150;
    public int gloops = 500;
    public Color color1 = Color.valueOf("ffaa5f");
    public Color color2 = Color.valueOf("84f491");
    public Color coloroff = Color.valueOf("6974c4");

    public transistor(String name) {
        super(name);
    }

    public class TransistorBuild extends Building {
        @Override
        public void drawConfigure() {
            int tx1 = 0;
            int ty1 = 0;
            if (tile.build.rotation() == 0) {
                tx1 = -1;
                ty1 = 1;
            } else if (tile.build.rotation() == 1) {
                tx1 = -1;
                ty1 = -1;
            } else if (tile.build.rotation() == 2) {
                tx1 = 1;
                ty1 = -1;
            } else if (tile.build.rotation() == 3) {
                tx1 = 1;
                ty1 = 1;
            }
            Tile in1 = Vars.world.tile(tile.x + tx1, tile.y + ty1);
            Draw.color(color1);
            Lines.square(in1.drawx(), in1.drawy(), 1 * tilesize / 2 + 1);
            super.drawConfigure();
        }
        public boolean checkState(Tile tile){
            int tx1=0; int ty1=0;
            if(tile.build.rotation()==0){
                tx1=-1; ty1=1;
            }
            else if(tile.build.rotation()==1){
                tx1=-1; ty1=-1;
            }
            else if(tile.build.rotation()==2){
                tx1=1; ty1=-1;
            }
            else if(tile.build.rotation()==3){
                tx1=1; ty1=1;
            }
            Tile in1=Vars.world.tile(tile.x+tx1,tile.y+ty1);
            PowerGraph graph = in1.build.power.graph;
            if(graph.getPowerProduced()-graph.getPowerNeeded()>0) return true;
            else return false;
        }
        @Override
        public void draw(){
            Draw.rect(Core.atlas.find("dusty-power-base"), tile.drawx(), tile.drawy());
            Draw.rect((((TransistorBuild) tile.build).offlink.size <= 0)?Core.atlas.find("dusty-trans-on"):Core.atlas.find("dusty-trans-off"), tile.drawx(), tile.drawy(),90*tile.build.rotation());
        }
        @Override
        public void updateTile() {
            boolean state = this.checkState(tile);
            if (state) {
                IntSeq links = ((TransistorBuild) tile.build).offlink;
                for (int i = 0; i < links.size; i++) {
                    Tile other = Vars.world.tile(links.get(i));
                    if (other.block() instanceof PowerNode) other.build.configure(tile.pos());
                }
                ((TransistorBuild) tile.build).offlink = new IntSeq();
            } else {
                //disconnect
                int[] links = tile.build.power.links.toArray();//links.get(i), links.size
                for (int i = 0; i < links.length; i++) {
                    //if(links.get(i)==tile.pos()) continue;
                    Tile other = Vars.world.tile(links[i]);
                    if (other.block() instanceof PowerNode) {
                        other.build.configure(tile.pos());
                        ((TransistorBuild) tile.build).toggleOffLink(links[i]);
                    }
                }
            }
            super.updateTile();
        }
        Vec2 t1 = new Vec2();
        Vec2 t2 = new Vec2();
        public void drawLaser(Tile target){
            int opacityPercentage = Core.settings.getInt("lasersopacity");
            if(opacityPercentage == 0) return;
            int opacity = opacityPercentage / 100;

            float x1 = tile.drawx();
            float y1 = tile.drawy();
            float x2 = target.drawx(); float y2 = target.drawy();

            float angle1 = Angles.angle(x1, y1, x2, y2);
            this.t1.trns(angle1, (float)(tile.block().size * Vars.tilesize / 2 - 1.5));
            this.t2.trns(angle1 + 180, (float)(target.block().size * Vars.tilesize / 2 - 1.5));

            x1 += this.t1.x;
            y1 += this.t1.y;
            x2 += this.t2.x;
            y2 += this.t2.y;

            Draw.color(Color.white, coloroff, (float)0.86);
            Draw.alpha(opacity);
            Drawf.laser(this.team, Core.atlas.find("laser"), Core.atlas.find("laser-end"), x1, y1, x2, y2, (float)0.25);
            Draw.color();
        }
        public void drawLayer(){
            if(Core.settings.getInt("lasersopacity") == 0) return;
            IntSeq links = ((TransistorBuild) tile.build).offlink;
            for(int i=0; i<links.size; i++){
                Tile link = Vars.world.tile(links.get(i));
                if(!(link.block() instanceof PowerNode)) continue;
                this.drawLaser(link);
            }
            Draw.reset();
        }
        public void toggleOffLink(int a){
            int i=this.offlink.indexOf(a);
            if(i<0) this.offlink.add(a);
            else this.offlink.removeIndex(i);
        }
        public IntSeq offlink = new IntSeq();
        @Override
        public void write(Writes stream){
            super.write(stream);
            stream.s(this.offlink.size);
            for(int i = 0; i<this.offlink.size; i++) {
                stream.i(this.offlink.get(i));
            }
        }
        @Override
        public void read(Reads stream, byte revision){
            super.read(stream,revision);
            this.offlink = new IntSeq();
            int amount=stream.s();
            for(int i=0;i<amount;i++){
                if(this.offlink.indexOf(stream.i())<0) this.offlink.add(stream.i());
            }
        }
    }
}