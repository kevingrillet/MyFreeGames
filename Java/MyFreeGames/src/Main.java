import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Kevin GRILLET
 * @version 1.0
 */
public class Main {
	static JFrame frame;
	static JPanel panel;
	static GridLayout gridLayout;
	static JButton bMinesweeper, bTetris, bSpaceinvaders, bPacMan;

	/**
	 * @param args
	 *            args
	 */
	public static void main(String[] args) {
		frame = new JFrame("MyFreeGames");
		panel = new JPanel();
		gridLayout = new GridLayout(1, 1);
		gridLayout.setVgap(5);
		bMinesweeper = new JButton("Minesweeper");
		bMinesweeper.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				minesweeper.Minesweeper.main(null);
				frame.setVisible(false);
			}
		});
		panel.setLayout(gridLayout);
		panel.setBackground(Color.BLACK);
		panel.add(bMinesweeper);

		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(250, 50));
		frame.setResizable(false);
		frame.setLocationRelativeTo(frame.getParent());
		frame.setVisible(true);
	}

}
