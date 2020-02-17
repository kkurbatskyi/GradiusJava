import java.awt.Rectangle;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.Random;

public class AsteroidFactory {

	private final static int ASTEROID_SIZE_MIN = 10;
	private final static int ASTEROID_SIZE_MAX = 40;
	private final static int ASTEROID_VEL_MIN = 1;
	private final static int ASTEROID_VEL_MAX = 4;

	private static Rectangle startBounds;
	private static Rectangle moveBounds;
	
	private static int difficulty = 0;
	
	private AsteroidFactory() {}
	
	public static void makeHarder()
	{
		difficulty++;
	}
	
	public static void reset()
	{
		difficulty = 0;
	}
	
	public static void setStartBounds(Rectangle r) {
		startBounds = r;
	}
	public static void setMoveBounds(Rectangle r) {
		moveBounds = new Rectangle(r.x, r.y, startBounds.width + 1, startBounds.height);
	}

	public static Asteroid makeAsteroid() {
		return new AsteroidImpl(random(startBounds.x, startBounds.x + startBounds.width),
								random(startBounds.y, startBounds.y + startBounds.height), 
								random(ASTEROID_SIZE_MIN, ASTEROID_SIZE_MAX), 
								random(ASTEROID_SIZE_MIN, ASTEROID_SIZE_MAX), 
								random(ASTEROID_VEL_MIN, ASTEROID_VEL_MAX) + difficulty);
	}

	private static int random(int min, int max) {
		if(max-min == 0) { return min; }
		Random rand = java.util.concurrent.ThreadLocalRandom.current();
		return min + rand.nextInt(max + 1);
	}

	private static class AsteroidImpl extends SpriteImpl implements Asteroid {
		private final static Color COLOR = new Color(193, 154, 107);//Color.DARK_GRAY;

		public AsteroidImpl(int x, int y, int w, int h, float v) {
			super(new Ellipse2D.Float(x, y, w, h), moveBounds, false, COLOR);
			this.setVelocity(-v, 0);
		}

	}
}
