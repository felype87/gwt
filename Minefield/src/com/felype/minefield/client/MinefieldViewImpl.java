package com.felype.minefield.client;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.felype.minefield.client.Place.Type;
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

public class MinefieldViewImpl implements MinefieldView, EntryPoint,
		ButtonField.OnBrowserEventListener {

	private static final String BUTTON_STYLE = "button";
	private static final String TITLE_STYLE = "title";
	private static final String DOUBT_CHARACTER = "?";
	private static final String CLEAR_CHARACTER = "";
	private static final String BOMB_CHARACTER = "B";

	private static final String BUTTON_CONTEXT_STYLE = "button-context";
	private static final String BUTTON_INITIAL_STYLE = "button-initial";
	private static final String BUTTON_REVEALED_STYLE = "button-revealed";
	private static final String LABEL_STYLE = "label";
	private static final String CONTROLS_STYLE = "controls";
	private static final String HEADER_STYLE = "header";
	private static final String GRID_STYLE = "grid";

	private ButtonField selectedBox;
	private Button btnNewGame;

	private Label labelCounter;
	private Label labelTime;
	private Label labelLevel;
	private Label labelTitle;

	private Grid grid;

	private VerticalPanel mainLayout;

	private GameTimer timer;

	private MinefieldPresenter presenter;

	private List<ButtonField> listButtons;

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

	private Command doubtCommand = new Command() {
		public void execute() {
			presenter.doubtClick(selectedBox.getId());

			selectedBox.setText(DOUBT_CHARACTER);
			popupPanel.hide();
		}
	};

	private Command bombCommand = new Command() {
		public void execute() {
			presenter.bombClick(selectedBox.getId());

			selectedBox.setText(BOMB_CHARACTER);
			popupPanel.hide();
		}
	};

	private Command clearCommand = new Command() {
		public void execute() {
			presenter.clearClick(selectedBox.getId());

			selectedBox.setText(CLEAR_CHARACTER);
			popupPanel.hide();
		}
	};

	public void onModuleLoad() {
		listButtons = new ArrayList<>();

		presenter = new MinefieldPresenter(this);

		createContextMenu();

		labelCounter = new Label();
		labelCounter.setStyleName(LABEL_STYLE);

		labelTime = new Label();
		labelTime.setStyleName(LABEL_STYLE);

		labelTitle = new Label("Minefield");
		labelTitle.setStyleName(TITLE_STYLE);
		labelTitle.setVisible(false);

		btnNewGame = new Button("New Game", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				openNewGameDialog();
			}
		});
		btnNewGame.setVisible(false);

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
				presenter.startClick(event.getGameLevel());

				btnNewGame.setVisible(true);
				labelTitle.setVisible(true);
			}
		});

		openNewGameDialog();
	}

	private void openNewGameDialog() {
		GameLevelDialog dialog = new GameLevelDialog();
		dialog.center();
		dialog.show();
	}

	@Override
	public void startGame(GameLevel gameLevel) {
		labelLevel.setText(gameLevel.getName());
		createGrid();
		startTimer();
	}

	private void createGrid() {
		if (grid != null) {
			mainLayout.remove(grid);
		}

		grid = new Grid(Minefield.ROWS, Minefield.COLUMNS);
		listButtons.clear();

		for (int i = 0; i < Minefield.ROWS; i++) {
			for (int j = 0; j < Minefield.COLUMNS; j++) {
				final ButtonField btn = new ButtonField();
				btn.setPixelSize(30, 30);
				btn.setOnBrowserEventListener(this);
				btn.setStyleName(BUTTON_INITIAL_STYLE);
				btn.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						presenter.fieldClick(btn.getId());
					}
				});

				grid.setCellSpacing(0);
				grid.setCellPadding(0);
				grid.setWidget(i, j, btn);

				btn.setId(listButtons.size());
				listButtons.add(btn);
			}
		}

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

	@Override
	public void refreshCounter(int counter) {
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

	@Override
	public void onRightClick(Widget sender, Event event) {
		if (selectedBox != null && !selectedBox.isRevealed()) {
			selectedBox.setStyleName(BUTTON_INITIAL_STYLE);
		}

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

	@Override
	public void reveal(Place place) {
		ButtonField buttonField = listButtons.get(place.getId());

		buttonField.addStyleName(BUTTON_REVEALED_STYLE);
		buttonField.setRevealed(true);

		if (place.getType() == Type.BOMB) {
			buttonField.setText(BOMB_CHARACTER);
		} else {
			int bombsAround = place.getBombsAround();

			if (bombsAround > 0) {
				String bombsText = String.valueOf(bombsAround);
				buttonField.setText(bombsText);

				if (bombsAround > 4) {
					buttonField.addStyleName(BUTTON_STYLE + 4);
				} else {
					buttonField.addStyleName(BUTTON_STYLE + bombsAround);
				}
			}
		}
	}

	@Override
	public void showLoses() {
		Window.alert("Loses");
	}

	@Override
	public void showWins() {
		Window.alert("Congratulations!");
	}
}
