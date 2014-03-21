package com.felype.minefield.client;

public interface MinefieldView {

	public interface MinefieldViewListener {
		void fieldClick(int id);

		void startClick(GameLevel gameLevel);

		void bombClick(int id);

		void doubtClick(int id);
	}

	void startGame(GameLevel gameLevel);

	void refreshCounter(int counter);

	void reveal(Place place);

	void showLoses();

	void showWins();

}
