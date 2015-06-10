package minesweeper;

import java.util.Observable;

/**
 * 
 * @author Kevin GRILLET
 * @version 1.1
 *
 */
class Model extends Observable implements IConfig {
	private int gameStatus, width, height, mines, remainingMines, t = 0,
			values[][], status[][];

	/**
	 * Prepare la partie pour une taille et un nombre de mine passe en
	 * parametre.
	 * 
	 * @param width
	 *            Largeur
	 * @param height
	 *            Hauteur
	 * @param mines
	 *            Nombre de mines
	 */
	public void settings(int width, int height, int mines) {
		if (DEBUG > 0)
			System.out.println("Settings: " + width + ", " + height + ", "
					+ mines);
		this.width = width;
		this.height = height;
		this.mines = mines;
		newGame();
	}

	/**
	 * Remet la partie a 0 pour en preparer une nouvelle.
	 */
	public void newGame() {
		if (DEBUG > 0)
			System.out.println("NewGame");
		values = new int[width][height];
		status = new int[width][height];
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) {
				values[i][j] = 0;
				status[i][j] = 0;
			}
		remainingMines = mines;
		t = 0;
		gameStatus = -2;
		setChanged();
		notifyObservers();
	}

	/**
	 * En partant des coordonnees et du bouton, met a jour la partie (initialise
	 * la grille si 1er clic, revele une ou plusieurs cases, vérifie la fin de
	 * partie, ...)
	 * 
	 * @param tX
	 *            Position du curseur X
	 * @param tY
	 *            Position du curseur Y
	 * @param button
	 *            Bouton
	 */
	public void clicked(int tX, int tY, int button) {
		if (DEBUG > 0)
			System.out.println("Clicked: [" + tX + "," + tY + "]:" + button);
		int cX = (tX - MARGIN) / SIZE, cY = (tY - MARGIN) / SIZE;
		if (DEBUG > 0)
			System.out.println("Case: [" + cX + "," + cY + "]");
		if (cX >= 0 && cX < width && cY >= 0 && cY < height) {
			if (gameStatus == -2)
				initMines(cX, cY);
			if (gameStatus == 0)
				reveal(cX, cY, button);
		}
		chkEnd();

		if (DEBUG > 1)
			for (int i = 0; i < width; i++)
				for (int j = 0; j < height; j++)
					System.out.println("values[" + i + "][" + j + "]="
							+ values[i][j]);
		if (DEBUG > 1)
			for (int i = 0; i < width; i++)
				for (int j = 0; j < height; j++)
					System.out.println("status[" + i + "][" + j + "]="
							+ status[i][j]);

		setChanged();
		notifyObservers();
	}

	/**
	 * Révèle la case cliquée ou y pose un flag.
	 * 
	 * @param cX
	 *            Case en X
	 * @param cY
	 *            Case en Y
	 * @param button
	 *            Bouton
	 */
	private void reveal(int cX, int cY, int button) {
		if (button == 1)
			if (status[cX][cY] == 0) {
				status[cX][cY] = 1;
				if (values[cX][cY] == -1) {
					gameStatus = 2;
				} else if (values[cX][cY] == 0)
					multiple(cX, cY);
			}
		if (button == 3)
			if (status[cX][cY] == 0) {
				status[cX][cY] = -1;
				remainingMines--;
			} else if (status[cX][cY] == -1) {
				status[cX][cY] = 0;
				remainingMines++;
			}

	}

	/**
	 * Initialise les mines par rapport à la case ou l'on vient de cliquer et
	 * met a jour les valeurs des cases au tour jusqu'a avoir place toutes les
	 * mines.
	 * 
	 * @param cX
	 *            Case en X
	 * @param cY
	 *            Case en Y
	 */
	private void initMines(int cX, int cY) {
		if (DEBUG > 0)
			System.out.println("InitMines: [" + cX + "," + cY + "]");
		int nbMines = 0;
		while (nbMines < mines) {
			int tX = (int) (Math.random() * (width - 1) + 1), tY = (int) (Math
					.random() * (height - 1) + 1);
			if (tX != cX && tY != cY && values[tX][tY] != -1) {
				if (tX > cX + 1 || tX < cX - 1 || tY > cY + 1 || tY < cY - 1) {
					values[tX][tY] = -1;
					for (int i = tX - 1; i < tX + 2; i++)
						for (int j = tY - 1; j < tY + 2; j++)
							if (i >= 0 && i < width && j >= 0 && j < height
									&& values[i][j] != -1)
								values[i][j]++;
					nbMines++;
				}
			}
		}
		gameStatus = -1;
		reveal(cX, cY, 1);
	}

	/**
	 * Verifie si en partant de la case [cX,cY] il est possible de reveler de
	 * multiples cases (Si on clic sur une case value 0 cela veut dire qu'il n'y
	 * a pas de bombes a proximite et donc que l'on peut reveler plusieurs cases
	 * jusqu'a tomber sur des cases values > 0)
	 * 
	 * @param cX
	 *            Case en X
	 * @param cY
	 *            Case en Y
	 */
	private void multiple(int cX, int cY) {
		if (cX >= 0 && cX < width && cY >= 0 && cY < height)
			for (int i = cX - 1; i < cX + 2; i++)
				for (int j = cY - 1; j < cY + 2; j++)
					if (i >= 0 && i < width && j >= 0 && j < height
							&& status[i][j] == 0)
						if (values[i][j] == 0) {
							status[i][j] = 1;
							multiple(i, j);
						} else
							status[i][j] = 1;
	}

	/**
	 * Remet le temps à 0
	 */
	public void resetTime() {
		t = -1;
		gameStatus = 0;
	}

	/**
	 * Realise un tick d'horloge pour l'IHM
	 */
	public void tick() {
		t++;
		if (t > 999)
			t = 999;
		setChanged();
		notifyObservers();
		if (DEBUG > 0)
			System.out.println("Tick " + t);
	}

	/**
	 * Verifie si la partie est finie (toutes les mines trouvees)
	 */
	private void chkEnd() {
		int revealed = width * height - mines;
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				if (status[i][j] == 1)
					revealed--;
		if (revealed == 0) {
			gameStatus = 1;
		}
	}

	public int getGameStatus() {
		return gameStatus;
	}

	public int getHeight() {
		return height;
	}

	public int getRemainingMines() {
		return remainingMines;
	}

	public int[][] getStatus() {
		return status;
	}

	public int getT() {
		return t;
	}

	public int[][] getValues() {
		return values;
	}

	public int getWidth() {
		return width;
	}
}
