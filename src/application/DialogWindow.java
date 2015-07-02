package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DialogWindow extends Stage {

	private String[] stageButtons;
	private String stageTitle;
	private String contentMessage;
	private int stageWidth;
	private int stageHeight;
	
	private int option = 0;
	
	public DialogWindow(String[] buttons, String title, String message, int width, int heigth) {		
		super();		
		this.stageButtons = buttons;
		this.stageTitle = title;
		this.contentMessage = message;
		this.stageWidth = width;
		this.stageHeight = heigth;
		
		String styleSheet = DialogWindow.class.getResource("dialog.css").toExternalForm();		
		BorderPane layout = new BorderPane();
		
		VBox contentBox = new VBox();
		TextArea textArea = new TextArea(contentMessage);
		textArea.setWrapText(true);
		textArea.setEditable(false);
		textArea.setId("textArea");
		textArea.getStylesheets().add(styleSheet);
		
		contentBox.getChildren().add(textArea);
		contentBox.setAlignment(Pos.CENTER);
		
		HBox buttonsBox = new HBox(20);
		
		for (int i = 0; i < stageButtons.length; i++) {
			int b = i + 1;
			Button button = new Button(" " + stageButtons[i] + " ");
			button.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent ae) {
					option = b;
					close();
				}
			});
			buttonsBox.getChildren().add(button);
		}
		
		buttonsBox.setAlignment(Pos.CENTER);
		
		layout.setCenter(contentBox);
		layout.setBottom(buttonsBox);
		BorderPane.setMargin(contentBox, new Insets(20, 10, 10, 10));
		BorderPane.setMargin(buttonsBox, new Insets(10, 10, 20, 10));
		
		Scene scene = new Scene(layout, stageWidth, stageHeight);
		
		this.setScene(scene);
		//this.initModality(Modality.WINDOW_MODAL);
		this.initModality(Modality.APPLICATION_MODAL);
		this.setTitle(stageTitle);
		this.centerOnScreen();
		this.setResizable(false);
	}

	public int getOption() {
		return option;
	}

}
