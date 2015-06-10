package minesweeper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * @author Kevin GRILLET
 * @version 1.1
 * 
 */
class Controller extends MouseAdapter implements ActionListener, IConfig {
	private Model model = new Model();
	private View vue = new View(model);
	private Timer tTime;

	/**
	 * Met en place l'ensemble des actionListener, MouseListener et initialise
	 * le jeu.
	 */
	public Controller() {
		vue.getPanel().addMouseListener(this);

		vue.getbLoose().addActionListener(this);
		vue.getbSettings().addActionListener(this);
		vue.getbWin().addActionListener(this);
		vue.getMenuAPropos().addActionListener(this);
		vue.getMenuNouvellePartie().addActionListener(this);
		vue.getMenuOptions().addActionListener(this);
		vue.getMenuQuitter().addActionListener(this);
		vue.getMenuRegles().addActionListener(this);

		model.addObserver(vue);

		model.settings(9, 9, 10);
		vue.settings();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == vue.getMenuNouvellePartie()) {
			model.newGame();
			if (tTime != null)
				tTime.cancel();
		}
		if (e.getSource() == vue.getMenuOptions())
			vue.getfSettings().setVisible(true);
		if (e.getSource() == vue.getMenuQuitter())
			System.exit(0);
		if (e.getSource() == vue.getMenuRegles())
			vue.getfRegles().setVisible(true);
		if (e.getSource() == vue.getMenuAPropos())
			vue.getfAPropos().setVisible(true);
		if (e.getSource() == vue.getbWin()) {
			vue.getfWin().setVisible(false);
			model.newGame();
			vue.getPanel().repaint();
		}
		if (e.getSource() == vue.getbLoose()) {
			vue.getfLoose().setVisible(false);
			model.newGame();
			vue.getPanel().repaint();
		}
		if (e.getSource() == vue.getbSettings())
			if (Integer.parseInt(vue.getTfW().getText()) >= 9
					&& Integer.parseInt(vue.getTfW().getText()) <= 30
					&& Integer.parseInt(vue.getTfH().getText()) >= 9
					&& Integer.parseInt(vue.getTfH().getText()) <= 24
					&& Integer.parseInt(vue.getTfM().getText()) >= 10
					&& Integer.parseInt(vue.getTfM().getText()) <= 668
					&& Integer.parseInt(vue.getTfM().getText()) < Integer
							.parseInt(vue.getTfW().getText())
							* Integer.parseInt(vue.getTfH().getText())) {

				if (tTime != null)
					tTime.cancel();
				model.settings(Integer.parseInt(vue.getTfW().getText()),
						Integer.parseInt(vue.getTfH().getText()),
						Integer.parseInt(vue.getTfM().getText()));
				vue.settings();
			}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		model.clicked(e.getX(), e.getY(), e.getButton());

		switch (model.getGameStatus()) {
		case -2:
		case 1:
		case 2:
			if (tTime != null)
				tTime.cancel();
			break;
		case -1:
			if (tTime != null)
				tTime.cancel();
			tTime = new Timer();
			model.resetTime();
			tTime.schedule(new TimerTask() {
				@Override
				public void run() {
					model.tick();
				}
			}, 0, 1000);
			break;
		}

		vue.getPanel().repaint();
		super.mouseClicked(e);
	}
}
