package com.felype.minefield.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public class ButtonField extends Button {

	private int id;

	public interface OnBrowserEventListener {
		void onRightClick(Widget sender, Event event);
	}

	private OnBrowserEventListener listener;
	private boolean revealed;

	public ButtonField() {
		sinkEvents(Event.ONCONTEXTMENU);
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

	public void setOnBrowserEventListener(OnBrowserEventListener listener) {
		this.listener = listener;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setRevealed(boolean revealed) {
		this.revealed = revealed;
	}

	public boolean isRevealed() {
		return revealed;
	}
}
