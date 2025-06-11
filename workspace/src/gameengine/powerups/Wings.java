package gameengine.powerups;
import java.awt.image.*;
import java.awt.Graphics;

import gameengine.PhysicsObject;
import gameengine.hitbox.RectHitbox;
import gamelogic.GameResources;
import gamelogic.level.Level;

public class Wings extends PhysicsObject{
    
	private BufferedImage image;
    public Wings(float x, float y, int width, int height, Level level) {
        super(x, y, width, height, level);
        this.hitbox = new RectHitbox(this, 10, 10, width - 10, height - 10);
        this.image = GameResources.wing;
    }

    @Override
	public void draw(Graphics g) {
		g.drawImage(image.getScaledInstance(100, 100, 0), (int)position.x, (int)position.y, width+30, height+30, null);
		
		hitbox.draw(g);
	}


}
