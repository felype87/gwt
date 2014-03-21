package com.felype.minefield.client;

import java.util.ArrayList;
import java.util.HashSet;

import com.felype.minefield.client.Place.Type;

public class Minefield {

	public interface MinefieldListener {
		void onPlaceReveiled(Place place);

		void onLose();
	}

	public static final int COLUMNS = 40;
	public static final int ROWS = 25;
	public static final int PLACES = COLUMNS * ROWS;

	private ArrayList<Place> field;

	private HashSet<Place> selectedPossibleBombs;

	private HashSet<Place> correctSelectedBombs;

	private int bombs;

	private MinefieldListener listener;

	public Minefield() {
		field = new ArrayList<>();
		selectedPossibleBombs = new HashSet<>();
		correctSelectedBombs = new HashSet<>();

		for (int i = 0; i < Minefield.ROWS * Minefield.COLUMNS; i++) {
			field.add(new Place(field.size()));
		}
	}

	public void calculateBombs(GameLevel gameLevel) {
		bombs = gameLevel.calculateBombs(PLACES);

		plantBombs();
	}

	private void plantBombs() {
		for (int i = 0; i < bombs; i++) {
			Place place = field.get((int) (field.size() * Math.random()));

			if (place.getType() == Type.CLEAR) {
				place.setType(Type.BOMB);
			} else {
				i--;
			}
		}
	}

	public int getBombs() {
		return bombs;
	}

	public int getSelectedPossibleBombs() {
		return selectedPossibleBombs.size();
	}

	public void reveal(int id) {
		if (id >= 0 && id < field.size()) {
			Place place = field.get(id);

			if (!place.isRevealed()) {
				place.setRevealed(true);

				if (place.getType() == Type.BOMB) {
					listener.onLose();
				} else {
					countBombsAround(place);

					if (place.getBombsAround() == 0) {
						reveal(place.getId() + 1);
						reveal(place.getId() - 1);
						reveal(place.getId() - COLUMNS);
						reveal(place.getId() + COLUMNS);
						reveal(place.getId() - COLUMNS - 1);
						reveal(place.getId() - COLUMNS + 1);
						reveal(place.getId() + COLUMNS - 1);
						reveal(place.getId() + COLUMNS + 1);
					}
				}

				listener.onPlaceReveiled(place);
			}
		}
	}

	private void countBombsAround(Place place) {
		int result = 0;

		result = result + (isBomb(place.getId() + 1) ? 1 : 0);
		result = result + (isBomb(place.getId() - 1) ? 1 : 0);
		result = result + (isBomb(place.getId() - COLUMNS) ? 1 : 0);
		result = result + (isBomb(place.getId() + COLUMNS) ? 1 : 0);
		result = result + (isBomb(place.getId() - COLUMNS - 1) ? 1 : 0);
		result = result + (isBomb(place.getId() - COLUMNS + 1) ? 1 : 0);
		result = result + (isBomb(place.getId() + COLUMNS - 1) ? 1 : 0);
		result = result + (isBomb(place.getId() + COLUMNS + 1) ? 1 : 0);

		place.setBombsAround(result);
	}

	private boolean isBomb(int id) {
		boolean result = false;

		if (id >= 0 && id < field.size()) {
			result = field.get(id).getType() == Type.BOMB;
		}

		return result;
	}

	public void guessBomb(int id) {
		Place place = field.get(id);

		selectedPossibleBombs.add(place);

		if (place.getType() == Type.BOMB) {
			correctSelectedBombs.add(place);
		}
	}

	public boolean wins() {
		return selectedPossibleBombs.size() == bombs
				&& selectedPossibleBombs.containsAll(correctSelectedBombs)
				&& correctSelectedBombs.containsAll(selectedPossibleBombs);
	}

	public void guessNotBomb(int id) {
		Place place = field.get(id);

		selectedPossibleBombs.remove(place);
		correctSelectedBombs.remove(place);
	}

	public void setMinefieldListener(MinefieldListener listener) {
		this.listener = listener;
	}
}
