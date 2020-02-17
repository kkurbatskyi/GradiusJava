import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.JComponent;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class GradiusComp extends JComponent {

	private final static int GAME_TICK = 1000 / 60;
	private final static int ASTEROID_MAKE_TICK = 1000/4;
	private final static int SHIELDING_TIME = 3000;

	private final static int SHIP_INIT_X = 10;
	private final static int SHIP_INIT_Y = Gradius.HEIGHT/3;
	private final static int SHIP_VEL_BASE = 2;
	private final static int SHIP_VEL_FAST = 4;
	
	private Ship ship;
	private Collection<Asteroid> roids;
	private List<Timer> gameTick;
	
	private static int gameTime = 0;
	private static boolean gameOver = false;
	private static int shieldTimeOut = 0;
	
	public GradiusComp() {
		roids = new HashSet<Asteroid>();
		gameTick = new ArrayList<Timer>();
		//Don't fix if it ain't broken. Lambda looks wrong, but hey! It works!
		gameTick.add(new Timer(GAME_TICK, (a) -> {this.update();}));
		gameTick.add(new Timer(ASTEROID_MAKE_TICK, (a) -> {this.makeAsteroid();}));
		addKeyListener(new ShipKeyListener());
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		paintComponent(g2);
	}
	private void paintComponent(Graphics2D g2) {
		g2.setBackground(Color.black);
		g2.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		Stream<Asteroid> asters = roids.stream().sequential();
		asters.forEach(a -> a.draw(g2));
		ship.draw(g2);
		g2.setPaint(Color.black);
		g2.fill(new Rectangle(0, 0, 75, 25));
		drawTime(g2);
		drawScore(g2);
		drawShield(g2);
		if(gameOver)
		{
			g2.setPaint(Color.RED);
			g2.setFont(g2.getFont().deriveFont(100f));
			g2.drawString("Game Over", this.getWidth() / 4, this.getHeight() / 2);
			g2.setFont(g2.getFont().deriveFont(50f));
			g2.drawString("Press \"R\" to restart", this.getWidth() / 3, 1.75f * this.getHeight() / 3);
		}
	}
	
	private void drawTime(Graphics2D g2) {
		Graphics2D g3 = (Graphics2D) g2.create();
		String time = "" + (gameTime * 1000 / 60)/1000 + ":" + (gameTime * 1000 / 60)%1000;
		g3.setPaint(Color.white);
		g3.drawString("Time: " + time, 2, 10);
		g3.dispose();
	}
	
	private void drawScore(Graphics2D g2) {
		Graphics2D g3 = (Graphics2D) g2.create();
		String score = "" + (gameTime * 60 / 1000);
		g3.setPaint(Color.white);
		g3.drawString("Score: " + score, 2, 22);
		g3.dispose();
	}
	
	private void drawShield(Graphics2D g2) {
		Graphics2D g3 = (Graphics2D)g2.create();
		String str;
		float x = 13 * this.getWidth() / 15;
		float y = this.getHeight() / 25 + 1.4f * this.getWidth() / 25;
		float textx = 22 * this.getWidth() / 25;; 
		Ellipse2D shape = new Ellipse2D.Float(x, this.getHeight() / 25 + 3, this.getWidth() /15, this.getWidth() /15);
		g3.setPaint(Color.black);
		g3.fill(shape);
		if(shieldTimeOut > 0) {
			str = "" + (shieldTimeOut * 1000 / 60) / 1000;
			g3.setPaint(new Color(220, 20, 60));
		}
		else {
			str = "X";
			g3.setPaint(new Color(0, 0, 205));
		}
		g3.setStroke(new BasicStroke(3));
		g3.draw(shape);
		g3.setFont(getFont().deriveFont(25f));
		g3.drawString("Shield", x - this.getWidth() / 100, this.getHeight() / 26);
		g3.setFont(getFont().deriveFont(50f));
		g3.drawString(str, textx, y);
		g3.dispose();
	}
	
	public void makeAsteroid()
	{
		roids.add(AsteroidFactory.makeAsteroid());
	}

	public void start() {
		AsteroidFactory.setStartBounds(new Rectangle(this.getWidth(), 0, this.getWidth(), this.getHeight()));
		AsteroidFactory.setMoveBounds(this.getBounds());
		ship = new ShipImpl(SHIP_INIT_X, SHIP_INIT_Y, 
							new Rectangle2D.Float(0, 0, 
												  (int)this.getSize().getWidth(), 
												  (int)this.getSize().getHeight()));
		gameTick.stream().forEach(t -> t.start());
	}
	
	public void restart(){
		gameTime = 0;
		gameOver = false;
		shieldTimeOut = 0;
		roids.clear();
		AsteroidFactory.reset();
		gameTick.stream().forEach(t -> t.stop());
		start();
	}
	
	private void update()
	{
		requestFocusInWindow();
		gameTime++;
		//Asteroids are getting faster every 5 seconds
		if(gameTime * 1000 / 60 % 5000 == 0)AsteroidFactory.makeHarder();
		if(shieldTimeOut > 0)shieldTimeOut--;
		roids.parallelStream().forEach(a -> a.move());
		ship.move();
		roids.removeIf(a -> a.isOutOfBounds());
		if(roids.parallelStream().anyMatch(a -> a.intersects(ship)) && !ship.isShielded())
		{
			gameTick.stream().forEach(t -> t.stop());
			gameOver = true;
		}
		repaint();
	}

	private class ShipKeyListener extends KeyAdapter {
		
		private boolean up;
		private boolean down;
		private boolean left;
		private boolean right;
		
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_R)
			{
				restart();
				return;
			}
			
			if(e.getKeyCode() == KeyEvent.VK_X && shieldTimeOut == 0 )
			{
				ship.turnShield();
				shieldTimeOut = 600; // 10 seconds
				Timer shielded = new Timer(SHIELDING_TIME, (a) -> {ship.turnShield();});;
				shielded.setRepeats(false); 
				shielded.start();
				return;
			}
			
			setVelocity(e);
		}
		@Override
		public void keyReleased(KeyEvent e) {
			setVelocity(e);
		}
		
		private void setDirection(KeyEvent e){
			boolean state = (e.getID() == KeyEvent.KEY_PRESSED) ? true : false;
			switch(e.getKeyCode()){
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_KP_UP:
				up = state;
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_KP_LEFT:
				left = state;
				break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_KP_RIGHT:
				right = state;
				break;
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_KP_DOWN:
				down = state;
			}
		}
		
		private void setVelocity(KeyEvent e){
			setDirection(e);
			
			int dp = e.isShiftDown() ? SHIP_VEL_FAST : SHIP_VEL_BASE;
			int dy = 0,
				dx = 0;
			
			if(up && !down) {
				dy = -dp;
			} else if(down && !up) {
				dy = dp;
			}
			if(left && !right) {
				dx = -dp;
			} else if(right && !left) {
				dx = dp;
			}
			ship.setVelocity(dx, dy);
		}
	}
}
