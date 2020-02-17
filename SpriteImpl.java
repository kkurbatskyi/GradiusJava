import java.awt.*;
import java.awt.geom.*;

public abstract class SpriteImpl implements Sprite {

	// drawing
	private Shape shape;
	private final Color border;
	private Color fill;

	// movement
	private float dx, dy;
	private final Rectangle2D bounds;
	private final boolean isBoundsEnforced;

	protected SpriteImpl(Shape shape, Rectangle2D bounds, boolean boundsEnforced, Color border, Color fill) {
		this.shape = shape;
		this.bounds = bounds;
		this.isBoundsEnforced = boundsEnforced;
		this.border = border;
		this.fill = fill;
	}
	protected SpriteImpl(Shape shape, Rectangle2D bounds, boolean boundsEnforced, Color fill) {
		this(shape, bounds, boundsEnforced, null, fill);
	}

	public Shape getShape() {
		return shape;
	}

	public void setVelocity(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public void move() {
		this.shape = AffineTransform.getTranslateInstance(this.dx, this.dy)
									.createTransformedShape(shape);
		if(!isInBounds() && this.isBoundsEnforced)
		{
			this.shape = AffineTransform.getTranslateInstance(-this.dx, -this.dy)
					.createTransformedShape(shape);
		}
	}

	public void setColor(Color color)
	{
		fill = color;
	}
	
	public boolean isOutOfBounds() {
		return !this.intersects(this.bounds) && this.shape.getBounds2D().getX() + this.shape.getBounds2D().getWidth() < this.bounds.getX();
	}
	public boolean isInBounds() {
		return isInBounds(bounds, shape);
	}
	private static boolean isInBounds(Rectangle2D bounds, Shape s) {
		return bounds.contains(s.getBounds2D().getX(), s.getBounds2D().getY()) && bounds.contains(s.getBounds2D().getX() + s.getBounds2D().getWidth(), s.getBounds2D().getY() + s.getBounds2D().getHeight());
	}

	public void draw(Graphics2D g2) {
		Graphics2D g3 = (Graphics2D)g2.create();
		g3.setPaint(this.fill);
		g3.fill(this.shape);
		g3.setPaint(border);
		g3.draw(this.shape);
		g3.dispose();
	}

	public boolean intersects(Sprite other) {
		return intersects(other.getShape());
	}
	private boolean intersects(Shape other) {
		return this.shape.intersects(other.getBounds2D()) && intersects(new Area(other), new Area(this.getShape()));
	}
	private static boolean intersects(Area a, Area b) {
		return a.intersects(b.getBounds2D());
	}
}
