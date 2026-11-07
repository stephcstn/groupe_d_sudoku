package fr.univpoitiers.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.univpoitiers.app.view.grid.CellView;
import fr.univpoitiers.app.view.grid.GridView;
import fr.univpoitiers.app.view.grid.NoteView;
import fr.univpoitiers.app.view.pad.PadView;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "DISPLAY", matches = ".+")
class SudokuInterfaceTest {

	@BeforeAll
	static void startJavaFx() {
		Platform.startup(() -> { });
	}

	@AfterAll
	static void stopJavaFx() {
		Platform.exit();
	}

	@Test
	void padNotesAndClearActOnSelectedCell() throws Exception {
		FutureTask<Void> test = new FutureTask<>(() -> {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univpoitiers/views/vue.fxml"));
			BorderPane root = loader.load();
			new Scene(root, 878, 689);
			root.applyCss();
			root.layout();

			BorderPane centerPane = (BorderPane) loader.getNamespace().get("centerPane");
			GridView grid = (GridView) centerPane.getCenter();
			CellView cell = (CellView) grid.getChildren().get(0);
			CellView peer = (CellView) grid.getChildren().get(1);
			BorderPane rightPane = (BorderPane) loader.getNamespace().get("rightPane");
			PadView pad = (PadView) rightPane.getCenter();
			Button two = numberButton(pad, "2");
			Button seven = numberButton(pad, "7");
			Button clear = (Button) loader.getNamespace().get("clearButton");
			Button reset = (Button) loader.getNamespace().get("resetButton");
			ToggleButton notes = (ToggleButton) pad.getChildren().stream()
					.filter(ToggleButton.class::isInstance).findFirst().orElseThrow();

			two.fire();
			clear.fire();
			reset.fire();
			assertEquals(0, cell.getCell().getValue());
			cell.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 1, 1, 1, 1,
					MouseButton.PRIMARY, 1, false, false, false, false, false, false,
					false, false, false, true, null));
			assertTrue(cell.getStyleClass().contains("selected"));
			assertTrue(peer.getStyleClass().contains("highlighted"));

			notes.fire();
			two.fire();
			seven.fire();
			assertTrue(cell.getCell().hasNote(2));
			assertTrue(cell.getCell().hasNote(7));
			GridPane noteGrid = (GridPane) cell.getChildren().get(0);
			NoteView noteTwo = (NoteView) noteGrid.getChildren().get(1);
			NoteView noteSeven = (NoteView) noteGrid.getChildren().get(6);
			assertTrue(((Label) noteTwo.getCenter()).isVisible());
			assertTrue(((Label) noteSeven.getCenter()).isVisible());
			root.layout();
			assertTrue(noteTwo.getBoundsInParent().getMinX() > noteSeven.getBoundsInParent().getMinX());
			assertTrue(noteSeven.getBoundsInParent().getMinY() > noteTwo.getBoundsInParent().getMinY());
			two.fire();
			assertFalse(cell.getCell().hasNote(2));
			assertFalse(((Label) noteTwo.getCenter()).isVisible());

			notes.fire();
			two.fire();
			assertEquals(2, cell.getCell().getValue());
			assertFalse(cell.getCell().hasNote(7));
			notes.fire();
			seven.fire();
			assertEquals(2, cell.getCell().getValue());
			assertFalse(cell.getCell().hasNote(7));
			clear.fire();
			assertEquals(0, cell.getCell().getValue());
			seven.fire();
			clear.fire();
			assertFalse(cell.getCell().hasNote(7));

			two.fire();
			seven.fire();
			peer.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 1, 1, 1, 1,
					MouseButton.PRIMARY, 1, false, false, false, false, false, false,
					false, false, false, true, null));
			seven.fire();
			notes.fire();
			two.fire();
			peer.getCell().lock();
			cell.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 1, 1, 1, 1,
					MouseButton.PRIMARY, 1, false, false, false, false, false, false,
					false, false, false, true, null));
			notes.fire();

			reset.fire();

			assertEquals(0, cell.getCell().getValue());
			assertEquals(0, peer.getCell().getValue());
			assertFalse(cell.getCell().hasNote(2));
			assertFalse(cell.getCell().hasNote(7));
			assertFalse(peer.getCell().hasNote(7));
			assertFalse(peer.getCell().isLocked());
			assertTrue(cell.getStyleClass().contains("selected"));
			assertTrue(notes.isSelected());
			seven.fire();
			assertTrue(cell.getCell().hasNote(7));
			return null;
		});
		Platform.runLater(test);
		test.get(30, TimeUnit.SECONDS);
	}

	private static Button numberButton(PadView pad, String value) {
		return pad.getChildren().stream().filter(Button.class::isInstance)
				.map(Button.class::cast).filter(button -> button.getText().equals(value))
				.findFirst().orElseThrow();
	}
}