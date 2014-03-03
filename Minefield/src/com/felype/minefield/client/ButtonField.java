package com.felype.minefield.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public class ButtonField extends Button {

	public interface MinefieldListener {
		void onRightClick(Widget sender, Event event);
	}

	private Type type;

	private boolean revealed;

	private MinefieldListener listener;

	public static enum Type {
		CLEAR, BOMB
	}

	public ButtonField() {
		type = Type.CLEAR;

		sinkEvents(Event.ONCONTEXTMENU);
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

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);

		event.preventDefault();

		switch (DOM.eventGetType(event)) {
		case Event.ONCONTEXTMENU:
			listener.onRightClick(this, event);
			break;
		}
	}

	public void setMineFieldListener(MinefieldListener listener) {
		this.listener = listener;
	}
}
