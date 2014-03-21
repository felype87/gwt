package com.felype.minefield.client;

public class Place {

	private Type type;

	private boolean revealed;

	private int id;

	private int bombsAround;

	public static enum Type {
		CLEAR, BOMB
	}

	public Place(int id) {
		this.id = id;
		type = Type.CLEAR;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public boolean isRevealed() {
		return revealed;
	}

	public void setRevealed(boolean revealed) {
		this.revealed = revealed;
	}

	public int getId() {
		return id;
	}

	public int getBombsAround() {
		return bombsAround;
	}

	public void setBombsAround(int bombsAround) {
		this.bombsAround = bombsAround;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Place other = (Place) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
}
