package com.felype.minefield.client;

public enum GameLevel {
	EASY(12, "Easy"), MEDIUM(8, "Medium"), HARD(4, "Hard");

	private int fraction;
	private String levelName;

	private GameLevel(int fraction, String levelName) {
		this.fraction = fraction;
		this.levelName = levelName;
	}

	public int calculateBombs(int totalPositions) {
		return totalPositions / fraction;
	}

	public String getName() {
		return levelName;
	}
}
