package com.felype.minefield.client;

import com.google.gwt.event.shared.GwtEvent;

public class NewGameEvent extends GwtEvent<NewGameEventHandler> {

	public static Type<NewGameEventHandler> TYPE = new Type<NewGameEventHandler>();

	private GameLevel gameLevel;

	public NewGameEvent(GameLevel gameLevel) {
		this.gameLevel = gameLevel;
	}

	public GameLevel getGameLevel() {
		return gameLevel;
	}

	@Override
	protected void dispatch(NewGameEventHandler handler) {
		handler.onEvent(this);
	}

	@Override
	public Type<NewGameEventHandler> getAssociatedType() {
		return TYPE;
	}
}
