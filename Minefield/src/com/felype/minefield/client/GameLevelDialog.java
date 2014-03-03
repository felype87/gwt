package com.felype.minefield.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class GameLevelDialog extends DialogBox {

	private static GameLevelDialogUiBinder uiBinder = GWT
			.create(GameLevelDialogUiBinder.class);

	interface GameLevelDialogUiBinder extends UiBinder<Widget, GameLevelDialog> {
	}

	@UiField
	Button buttonEasy;

	@UiField
	Button buttonMedium;

	@UiField
	Button buttonHard;

	public GameLevelDialog() {
		setWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("buttonEasy")
	void onClickEasy(ClickEvent e) {
		this.hide();

		Minefield.EVENT_BUS.fireEvent(new NewGameEvent(GameLevel.EASY));
	}

	@UiHandler("buttonMedium")
	void onClickMedium(ClickEvent e) {
		this.hide();

		Minefield.EVENT_BUS.fireEvent(new NewGameEvent(GameLevel.MEDIUM));
	}

	@UiHandler("buttonHard")
	void onClickHard(ClickEvent e) {
		this.hide();

		Minefield.EVENT_BUS.fireEvent(new NewGameEvent(GameLevel.HARD));
	}

}
