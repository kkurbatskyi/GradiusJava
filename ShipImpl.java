import java.awt.Color;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

public class ShipImpl  extends SpriteImpl implements Ship {

	private final static Color FILL = Color.GREEN;
	private final static Color SHIELD = new Color(0, 191, 255);
	private final static Color BORDER = Color.BLACK;

	private final static int HEIGHT = 20;
	private final static int WIDTH = HEIGHT;
	
	private static boolean shieldUp = false;

	public boolean isShielded()
	{
		return shieldUp;
	}
	
	public void turnShield()
	{
		shieldUp = !shieldUp;
		if(shieldUp)
		{
			super.setColor(SHIELD);
		}
		else 
		{
			super.setColor(FILL);
		}
	}
	
	public ShipImpl(int x, int y, Rectangle2D moveBounds) {
		super(new Polygon(new int[]{x, x + WIDTH, x}, new int[]{y, y + HEIGHT / 2, y + HEIGHT}, 3), 
				moveBounds, true, BORDER, FILL);
	}

}
