package minesweeper;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * @author Kevin GRILLET
 * @version 1.0
 *
 */
public class View implements IConfig, Observer {
	private Model model;
	private JFrame frame, fAPropos, fLoose, fRegles, fSettings, fWin;
	private JPanel panel, pLoose, pSettings, pWin;
	private JLabel lLoose, lWin;
	private JEditorPane epAPropos, epRegles;
	private JTextField tfH, tfM, tfW;
	private JButton bLoose, bSettings, bWin;
	private JMenuBar menuBar;
	private JMenu menuAide, menuPartie;
	private JMenuItem menuAPropos, menuNouvellePartie, menuOptions,
			menuQuitter, menuRegles;
	private AudioClip acBoom = Applet.newAudioClip(Minesweeper.class
			.getResource("minesweeper_sound.wav"));;
	private BufferedImage tiles;
	private BufferedImage[] img;
	private Image iMine, iTime;
	private Font font;

	/**
	 * Prepare tous les elements graphiques et affiche la fenetre de jeu.
	 * 
	 * @param model
	 *            Model
	 */
	View(final Model model) {
		this.model = model;

		// Préparation de l'image et de la police
		try {
			tiles = ImageIO.read(Minesweeper.class
					.getResource("minesweeper_tiles.jpg"));
			iMine = ImageIO.read(Minesweeper.class
					.getResource("minesweeper_icon_mine.png"));
			iTime = ImageIO.read(Minesweeper.class
					.getResource("minesweeper_icon_time.png"));
			font = Font.createFont(Font.TRUETYPE_FONT, Minesweeper.class
					.getResourceAsStream("minesweeper_font.ttf"));
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}
		img = new BufferedImage[12];
		for (int i = 0; i < 12; i++)
			img[i] = tiles.getSubimage(128 * (i % 4), 128 * (i / 4), 128, 128);
		font = font.deriveFont(0.7F * (float) SIZE);

		// Fenetre de jeu
		frame = new JFrame("MineSweeper");
		panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				for (int i = model.getWidth() - 1; i >= 0; i--)
					for (int j = model.getHeight() - 1; j >= 0; j--)
						if (model.getStatus()[i][j] == 0) {
							g.drawImage(img[0], MARGIN + i * SIZE, MARGIN + j
									* SIZE, SIZE, SIZE, null);
						} else if (model.getStatus()[i][j] == -1) {
							g.drawImage(img[1], MARGIN + i * SIZE, MARGIN + j
									* SIZE, SIZE, SIZE, null);
						} else
							g.drawImage(img[model.getValues()[i][j] + 3],
									MARGIN + i * SIZE, MARGIN + j * SIZE, SIZE,
									SIZE, null);

				// Images
				g.drawImage(iTime, MARGIN * 3,
						(int) (MARGIN * 3.25) + model.getHeight() * SIZE, SIZE,
						SIZE, null);
				g.drawImage(iMine, (model.getWidth() - 1) * SIZE - MARGIN / 2,
						(int) (MARGIN * 3.25) + model.getHeight() * SIZE, SIZE,
						SIZE, null);

				// Cadres
				g.setColor(Color.BLACK);
				g.drawRect(MARGIN * 2, MARGIN * 2 + model.getHeight() * SIZE,
						SIZE * 3, (int) (SIZE * 1.5));
				g.drawRect((model.getWidth() - 3) * SIZE,
						MARGIN * 2 + model.getHeight() * SIZE, SIZE * 3,
						(int) (SIZE * 1.5));

				// Texte
				g.setFont(font);
				g.setColor(Color.RED);
				g.drawString(Integer.toString(model.getT()), MARGIN * 8,
						(int) (MARGIN * 2.25) + (model.getHeight() + 1) * SIZE);
				g.drawString(Integer.toString(model.getRemainingMines()),
						(model.getWidth() - 3) * SIZE + MARGIN, MARGIN * 2
								+ (model.getHeight() + 1) * SIZE);
			}
		};
		frame.add(panel);

		// MenuBar et sous menu
		menuBar = new JMenuBar();
		menuPartie = new JMenu("Partie");
		menuNouvellePartie = new JMenuItem("Nouvelle Partie");
		menuOptions = new JMenuItem("Options");
		menuQuitter = new JMenuItem("Quitter");
		menuPartie.add(menuNouvellePartie);
		menuPartie.add(menuOptions);
		menuPartie.add(menuQuitter);
		menuBar.add(menuPartie);
		menuAide = new JMenu("?");
		menuRegles = new JMenuItem("Règles");
		menuAPropos = new JMenuItem("A Propos");
		menuAide.add(menuRegles);
		menuAide.add(menuAPropos);
		menuBar.add(menuAide);

		frame.setJMenuBar(menuBar);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(frame.getParent());
		frame.setResizable(false);

		// Fenetre Victoire
		fWin = new JFrame("Victoire");
		pWin = new JPanel();
		lWin = new JLabel("Gagné! en 999s.");
		bWin = new JButton("Rejouer");
		pWin.add(lWin);
		pWin.add(bWin);
		fWin.add(pWin);
		fWin.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		fWin.pack();
		fWin.setLocationRelativeTo(fWin.getParent());
		fWin.setResizable(false);

		// Fenetre defaite
		fLoose = new JFrame("Defaite");
		pLoose = new JPanel();
		lLoose = new JLabel("Perdu!");
		bLoose = new JButton("Rejouer");
		pLoose.add(lLoose);
		pLoose.add(bLoose);
		fLoose.add(pLoose);
		fLoose.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		fLoose.pack();
		fLoose.setLocationRelativeTo(fLoose.getParent());
		fLoose.setResizable(false);

		// Fenetre Options
		fSettings = new JFrame("Options");
		pSettings = new JPanel();
		tfW = new JTextField("Largeur (9-30)", 8);
		tfH = new JTextField("Hauteur (9-24)", 8);
		tfM = new JTextField("Mines (10-668)", 9);
		pSettings.add(tfW);
		pSettings.add(tfH);
		pSettings.add(tfM);
		bSettings = new JButton("Valider");
		pSettings.add(bSettings);
		fSettings.add(pSettings);
		fSettings.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		fSettings.pack();
		fSettings.setLocationRelativeTo(fSettings.getParent());
		fSettings.setResizable(false);

		// Fenetre regles
		fRegles = new JFrame("Regles");
		epRegles = new JEditorPane(
				"text/html",
				"Le principe est simple, comme son nom l'indique le but du jeu est de déminer le terrain, symbolisé par une grille.");
		epRegles.setEditable(false);
		fRegles.add(epRegles);
		fRegles.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		fRegles.pack();
		fRegles.setLocationRelativeTo(fRegles.getParent());
		fRegles.setResizable(false);

		// Fenetre a Propos
		fAPropos = new JFrame("APropos");
		epAPropos = new JEditorPane("text/html", "Réalisé par Kévin GRILLET");
		epAPropos.setEditable(false);
		fAPropos.add(epAPropos);
		fAPropos.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		fAPropos.pack();
		fAPropos.setLocationRelativeTo(fAPropos.getParent());
		fAPropos.setResizable(false);

		frame.setVisible(true);
	}

	/**
	 * Met a jour l'interface apres un changement de dimmension.
	 */
	public void settings() {
		fSettings.setVisible(false);
		panel.setPreferredSize(new Dimension(model.getWidth() * SIZE + 2
				* MARGIN, (model.getHeight() + 1) * SIZE + 5 * MARGIN));
		frame.pack();

	}

	@Override
	public void update(Observable o, Object arg) {
		if (model.getGameStatus() == 1) {
			lWin.setText("Gagné! en " + model.getT() + "s.");
			fWin.setVisible(true);
		}
		if (model.getGameStatus() == 2) {
			fLoose.setVisible(true);
			acBoom.play();
		}
		panel.repaint();
	}

	public JButton getbLoose() {
		return bLoose;
	}

	public JButton getbSettings() {
		return bSettings;
	}

	public JButton getbWin() {
		return bWin;
	}

	public JFrame getfAPropos() {
		return fAPropos;
	}

	public JFrame getfLoose() {
		return fLoose;
	}

	public JFrame getfRegles() {
		return fRegles;
	}

	public JFrame getfSettings() {
		return fSettings;
	}

	public JFrame getfWin() {
		return fWin;
	}

	public JMenuItem getMenuAPropos() {
		return menuAPropos;
	}

	public JMenuItem getMenuNouvellePartie() {
		return menuNouvellePartie;
	}

	public JMenuItem getMenuOptions() {
		return menuOptions;
	}

	public JMenuItem getMenuQuitter() {
		return menuQuitter;
	}

	public JMenuItem getMenuRegles() {
		return menuRegles;
	}

	public JPanel getPanel() {
		return panel;
	}

	public JTextField getTfH() {
		return tfH;
	}

	public JTextField getTfM() {
		return tfM;
	}

	public JTextField getTfW() {
		return tfW;
	}
}
