package com.felype.minefield.client;

import java.awt.Dialog;
import java.sql.Date;

import com.felype.minefield.client.ButtonField.Type;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class Minefield implements EntryPoint, ButtonField.MinefieldListener {

	private static final String TITLE_STYLE = "title";
	private static final String DOUBT_CHARACTER = "?";
	private static final String CLEAR_CHARACTER = "";
	private static final String BOMB_CHARACTER = "B";

	private static final int COLUMNS = 40;
	private static final int LINES = 25;
	private static final int PLACES = COLUMNS * LINES;
	private int bombs;

	private static final String BUTTON_CONTEXT_STYLE = "button-context";
	private static final String BUTTON_INITIAL_STYLE = "button-initial";
	private static final String BUTTON_REVEALED_STYLE = "button-revealed";
	private static final String LABEL_STYLE = "label";
	private static final String CONTROLS_STYLE = "controls";
	private static final String HEADER_STYLE = "header";
	private static final String GRID_STYLE = "grid";
	

	private ButtonField selectedBox;

	private Label labelCounter;
	private Label labelTime;
	private Label labelLevel;

	private int selectedPossibleBombs;
	private int correctSelectedBombs;

	private Grid grid;

	private VerticalPanel mainLayout;

	private GameTimer timer;

	public static EventBus EVENT_BUS = GWT.create(SimpleEventBus.class);

	private PopupPanel popupPanel = new PopupPanel(true) {
		public void hide() {
			super.hide();

			if (selectedBox != null) {
				selectedBox.removeStyleName(BUTTON_CONTEXT_STYLE);
				selectedBox.addStyleName(BUTTON_INITIAL_STYLE);
			}
		};
	};

	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	private Command doubtCommand = new Command() {
		public void execute() {
			if (selectedBox.getText().equals(BOMB_CHARACTER)) {
				selectedPossibleBombs--;

				if (selectedBox.getType() == Type.BOMB) {
					correctSelectedBombs--;
				}
			}

			selectedBox.setText(DOUBT_CHARACTER);
			popupPanel.hide();

			refreshSelectedBombs();
			checkWin();
		}
	};

	private Command bombCommand = new Command() {
		public void execute() {
			if (!selectedBox.getText().equals(BOMB_CHARACTER)) {
				selectedPossibleBombs++;

				if (selectedBox.getType() == Type.BOMB) {
					correctSelectedBombs++;
				}
			}

			selectedBox.setText(BOMB_CHARACTER);
			popupPanel.hide();

			refreshSelectedBombs();
			checkWin();
		}
	};

	private Command clearCommand = new Command() {
		public void execute() {
			if (selectedBox.getText().equals(BOMB_CHARACTER)) {
				selectedPossibleBombs--;

				if (selectedBox.getType() == Type.BOMB) {
					correctSelectedBombs--;
				}
			}

			selectedBox.setText(CLEAR_CHARACTER);
			popupPanel.hide();

			refreshSelectedBombs();
			checkWin();
		}
	};

	public void onModuleLoad() {
		createContextMenu();

		labelCounter = new Label();
		labelCounter.setStyleName(LABEL_STYLE);

		labelTime = new Label();
		labelTime.setStyleName(LABEL_STYLE);

		Label labelTitle = new Label("Minefield");
		labelTitle.setStyleName(TITLE_STYLE);

		Button btnNewGame = new Button("New Game", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new GameLevelDialog().show();
			}
		});

		labelLevel = new Label();

		HorizontalPanel topBar = new HorizontalPanel();
		topBar.add(labelCounter);
		topBar.add(labelTime);
		topBar.setCellHorizontalAlignment(labelTime,
				HasHorizontalAlignment.ALIGN_RIGHT);
		topBar.addStyleName(HEADER_STYLE);

		HorizontalPanel controlsLayout = new HorizontalPanel();
		controlsLayout.add(labelLevel);
		controlsLayout.add(btnNewGame);
		controlsLayout.setStyleName(CONTROLS_STYLE);
		controlsLayout.setCellHorizontalAlignment(btnNewGame,
				HasHorizontalAlignment.ALIGN_RIGHT);

		mainLayout = new VerticalPanel();
		mainLayout.add(labelTitle);
		mainLayout.setCellHorizontalAlignment(labelTitle,
				HasHorizontalAlignment.ALIGN_CENTER);

		mainLayout.add(topBar);
		mainLayout.add(controlsLayout);
		

		RootPanel.get("main").add(mainLayout);

		EVENT_BUS.addHandler(NewGameEvent.TYPE, new NewGameEventHandler() {

			@Override
			public void onEvent(NewGameEvent event) {
				startGame(event.getGameLevel());
			}
		});

		GameLevelDialog dialog = new GameLevelDialog();
		dialog.center();
		dialog.show();
	}

	private void startGame(GameLevel gameLevel) {
		bombs = gameLevel.calculateBombs(PLACES);

		labelLevel.setText(gameLevel.getName());
		createGrid();
		refreshSelectedBombs();
		startTimer();
	}

	private void createGrid() {
		if (grid != null) {
			mainLayout.remove(grid);
		}

		grid = new Grid(LINES, COLUMNS);

		for (int i = 0; i < grid.getRowCount(); i++) {
			for (int j = 0; j < grid.getColumnCount(); j++) {
				final ButtonField btn = new ButtonField();
				btn.setPixelSize(30, 30);
				btn.setMineFieldListener(Minefield.this);
				btn.setStyleName(BUTTON_INITIAL_STYLE);

				grid.setCellSpacing(0);
				grid.setCellPadding(0);

				actionReveal(i, j, btn, grid);

				grid.setWidget(i, j, btn);
			}
		}

		plantBombs(grid);
		grid.addStyleName(GRID_STYLE);

		mainLayout.add(grid);
	}

	private void startTimer() {
		if (timer == null) {
			timer = new GameTimer();

			timer.reset();

			timer.schedule(1000);
		} else {
			timer.reset();
		}
	}

	protected void checkWin() {
		if (correctSelectedBombs == bombs) {
			Window.alert("Congratulations! You are a bomb detector!");
		}
	}

	private void refreshSelectedBombs() {
		int counter = bombs - selectedPossibleBombs;
		labelCounter.setText(String.valueOf(counter >= 0 ? counter : 0));
	}

	private void createContextMenu() {
		MenuBar popupMenuBar = new MenuBar(true);
		MenuItem doubtItem = new MenuItem("Doubt", true, doubtCommand);
		MenuItem bombItem = new MenuItem("Bomb", true, bombCommand);
		MenuItem clearItem = new MenuItem("Clear", true, clearCommand);

		popupMenuBar.addItem(doubtItem);
		popupMenuBar.addItem(bombItem);
		popupMenuBar.addItem(clearItem);

		popupMenuBar.setVisible(true);
		popupPanel.add(popupMenuBar);
	}

	private void actionReveal(final int i, final int j, final ButtonField btn,
			final Grid grid) {
		btn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				reveal(i, j, grid);
			}
		});
	}

	protected void reveal(int i, int j, Grid grid) {
		ButtonField buttonField = getButton(grid, i, j);

		if (buttonField != null && !buttonField.isRevealed()) {
			buttonField.setRevealed(true);
			buttonField.setEnabled(false);
			buttonField.addStyleName(BUTTON_REVEALED_STYLE);

			if (buttonField.getType() == ButtonField.Type.BOMB) {
				buttonField.setText(BOMB_CHARACTER);

				Window.addWindowClosingHandler(new ClosingHandler() {

					@Override
					public void onWindowClosing(ClosingEvent event) {
						// TODO Auto-generated method stub

					}
				});

				Window.alert("Loses");
			} else {
				int bombsAround = countBombsAround(i, j, grid);

				if (bombsAround > 0) {
					buttonField.setText(String.valueOf(bombsAround));
				}

				if (bombsAround == 0) {
					reveal(i + 1, j - 1, grid);
					reveal(i + 1, j, grid);
					reveal(i + 1, j + 1, grid);

					reveal(i - 1, j - 1, grid);
					reveal(i - 1, j, grid);
					reveal(i - 1, j + 1, grid);

					reveal(i, j - 1, grid);
					reveal(i, j + 1, grid);
				}
			}
		}
	}

	private ButtonField getButton(Grid grid, int i, int j) {
		ButtonField result = null;

		if (i >= 0 && i < LINES && j >= 0 && j < COLUMNS) {
			result = (ButtonField) grid.getWidget(i, j);
		}

		return result;
	}

	protected int countBombsAround(int line, int column, Grid grid) {
		int result = 0;

		ButtonField buttonField = getButton(grid, line - 1, column);

		if (buttonField != null
				&& buttonField.getType() == ButtonField.Type.BOMB) {
			result = result + 1;
		}

		buttonField = (ButtonField) getButton(grid, line + 1, column);

		if (buttonField != null
				&& buttonField.getType() == ButtonField.Type.BOMB) {
			result = result + 1;
		}

		buttonField = (ButtonField) getButton(grid, line, column - 1);

		if (buttonField != null
				&& buttonField.getType() == ButtonField.Type.BOMB) {
			result = result + 1;
		}

		buttonField = (ButtonField) getButton(grid, line, column + 1);

		if (buttonField != null
				&& buttonField.getType() == ButtonField.Type.BOMB) {
			result = result + 1;
		}

		buttonField = (ButtonField) getButton(grid, line - 1, column - 1);

		if (buttonField != null
				&& buttonField.getType() == ButtonField.Type.BOMB) {
			result = result + 1;
		}

		buttonField = (ButtonField) getButton(grid, line - 1, column + 1);

		if (buttonField != null
				&& buttonField.getType() == ButtonField.Type.BOMB) {
			result = result + 1;
		}

		buttonField = (ButtonField) getButton(grid, line + 1, column - 1);

		if (buttonField != null
				&& buttonField.getType() == ButtonField.Type.BOMB) {
			result = result + 1;
		}

		buttonField = (ButtonField) getButton(grid, line + 1, column + 1);

		if (buttonField != null
				&& buttonField.getType() == ButtonField.Type.BOMB) {
			result = result + 1;
		}

		return result;
	}

	private void plantBombs(Grid grid) {
		for (int i = 0; i < bombs; i++) {
			ButtonField buttonField = (ButtonField) grid.getWidget(
					(int) (LINES * Math.random()),
					(int) (COLUMNS * Math.random()));

			if (buttonField.getType() == ButtonField.Type.CLEAR) {
				buttonField.setType(ButtonField.Type.BOMB);
			} else {
				i--;
			}
		}
	}

	@Override
	public void onRightClick(Widget sender, Event event) {
		selectedBox = (ButtonField) sender;

		if (!selectedBox.isRevealed()) {
			int x = event.getClientX();
			int y = event.getClientY();
			popupPanel.setPopupPosition(x, y);
			popupPanel.show();

			selectedBox.setStyleName(BUTTON_CONTEXT_STYLE);
		}
	}

	class GameTimer extends Timer {

		private long startTime;

		private DateTimeFormat formatter = DateTimeFormat.getFormat("mm:ss");

		@Override
		public void run() {
			long elapsedTime = System.currentTimeMillis() - startTime;

			Date date = new Date(elapsedTime);

			labelTime.setText(formatter.format(date));

			schedule(1000);
		}

		public void reset() {
			startTime = System.currentTimeMillis();
		}
	}
}
