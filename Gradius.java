import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Gradius extends JFrame {

	private final static int WIDTH = 900;
	public final static int HEIGHT = 700;

	private final GradiusComp comp;

	public Gradius() {
		setResizable(false);
		comp = new GradiusComp();
		setContentPane(comp);
	}

	public static void main(String[] args) {
		Gradius frame = new Gradius();
		frame.setSize(WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.comp.start();
	}
}
