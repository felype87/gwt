package com.felype.minefield.client;

import com.felype.minefield.client.Minefield.MinefieldListener;

public class MinefieldPresenter implements MinefieldListener,
		MinefieldView.MinefieldViewListener {

	private MinefieldView view;

	private Minefield minefield;

	public MinefieldPresenter(MinefieldView view) {
		this.view = view;

		minefield = new Minefield();
		minefield.setMinefieldListener(this);
	}

	@Override
	public void fieldClick(int id) {
		minefield.reveal(id);
	}

	@Override
	public void startClick(GameLevel gameLevel) {
		minefield.calculateBombs(gameLevel);

		view.startGame(gameLevel);
		view.refreshCounter(minefield.getBombs()
				- minefield.getSelectedPossibleBombs());
	}

	@Override
	public void onPlaceReveiled(Place place) {
		view.reveal(place);
	}

	@Override
	public void onLose() {
		view.showLoses();
	}

	@Override
	public void bombClick(int id) {
		minefield.guessBomb(id);

		refresh();
	}

	@Override
	public void doubtClick(int id) {
		minefield.guessNotBomb(id);

		refresh();
	}

	private void refresh() {
		view.refreshCounter(minefield.getBombs()
				- minefield.getSelectedPossibleBombs());

		if (minefield.wins()) {
			view.showWins();
		}
	}

	public void clearClick(int id) {
		doubtClick(id);
	}
}
