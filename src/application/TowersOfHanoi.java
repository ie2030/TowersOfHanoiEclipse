package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class TowersOfHanoi extends Application {
    
	private final String textCountStep = "Moves Taken: ";	
    public int countStep = 0;
    public StringProperty sp = new SimpleStringProperty(textCountStep + countStep);
    
    private final Color primaryCircleColor = Color.RED; 
    private final Color lightingCircleColor = Color.YELLOW; 
    private final Color movingCircleColor = Color.LIGHTGRAY;
    
    private double x, x0 = 0;          
    private double y, y0 = 0;    
    private double mousex = 0;       
    private double mousey = 0;
    
    private Parent root;
    
    private boolean flagClick = false;    
    
    private Pane selectedTower = null;          
    private Pane destinationTower = null;             
    
    private ObservableList<Line> lines = FXCollections.observableArrayList();        
    
    private ObservableList<Rectangle> left = FXCollections.observableArrayList();    
    private ObservableList<Rectangle> center = FXCollections.observableArrayList();
    private ObservableList<Rectangle> right = FXCollections.observableArrayList();
    
    
    private ObservableList<ObservableList<Rectangle>> leftArray = FXCollections.observableArrayList(); //massiv soderzit vse massivi, eto dlia zapominanija vsex sostojanii
    private ObservableList<ObservableList<Rectangle>> rightArray = FXCollections.observableArrayList();
    private ObservableList<ObservableList<Rectangle>> centerArray = FXCollections.observableArrayList();
    
    @FXML
    private Label countStepLabel;
    
    @FXML
    private StackPane backColor;
    
    @FXML
    private ComboBox countBricks;
    
    @FXML
    private GridPane towersPane;    
    
    @FXML
    private Pane leftTower, centerTower, rightTower;
    
    @FXML
    private Button restartButton;
    
    @FXML
    private Button undoMoveButton, tower1Button, tower2Button, tower3Button;
    
    private DialogWindow dialog;
    private DialogWindow illegalMoveDialog;
    private DialogWindow endDialog;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        root = FXMLLoader.load(getClass().getResource("form.fxml"));        
        Scene scene = new Scene(root, 600, 400);    
        String css = getClass().getResource("application.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        primaryStage.setTitle("TowersOfHanoi");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        countStepLabel = (Label) root.lookup("#countStep");
        countStepLabel.textProperty().bind(sp);
        
        towersPane = (GridPane) root.lookup("#towersPane");
        leftTower = (Pane) root.lookup("#leftTower");
        	leftTower.setOnMousePressed(clickTower);
        centerTower = (Pane) root.lookup("#centerTower");
        	centerTower.setOnMousePressed(clickTower);
        rightTower = (Pane) root.lookup("#rightTower");
        	rightTower.setOnMousePressed(clickTower);
        
        restartButton = (Button) root.lookup("#restartButton");
        restartButton.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedRestartButton);
        restartButton.getStyleClass().add("button");

        
        tower1Button = (Button) root.lookup("#tower1Button");
        	tower1Button.addEventHandler(MouseEvent.MOUSE_PRESSED, clickButtonTower);
        tower2Button = (Button) root.lookup("#tower2Button");
        	tower2Button.addEventHandler(MouseEvent.MOUSE_PRESSED, clickButtonTower);
        tower3Button = (Button) root.lookup("#tower3Button");
        	tower3Button.addEventHandler(MouseEvent.MOUSE_PRESSED, clickButtonTower);
        
        countBricks = (ComboBox) root.lookup("#countBricks");
        countBricks.getSelectionModel().selectLast();
        countBricks.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            restart();
        });
   
        undoMoveButton = (Button) root.lookup("#undoMoveButton");
            undoMoveButton.setDisable(true);
            undoMoveButton.addEventHandler(MouseEvent.MOUSE_PRESSED, undoMove);
        backColor = (StackPane) root.lookup("#backColor");
        backColor.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(4), new Insets(1))));
                
        leftTower.prefWidthProperty().bind(towersPane.widthProperty().divide(3));
        leftTower.prefHeightProperty().bind(towersPane.heightProperty());
        centerTower.prefWidthProperty().bind(towersPane.widthProperty().divide(3));
        centerTower.prefHeightProperty().bind(towersPane.heightProperty());
        rightTower.prefWidthProperty().bind(towersPane.widthProperty().divide(3));
        rightTower.prefHeightProperty().bind(towersPane.heightProperty());
        
        illegalMoveDialog = new DialogWindow(new String[] {"Ok"}, "Error", "Try again !", 350, 100);
        endDialog = new DialogWindow(new String[] {"Yes", "Exit"}, "Information", "Game over ! Again ?", 350, 100);
        initUI(Integer.parseInt(countBricks.getSelectionModel().getSelectedItem().toString()));      
    }
    
    private void initUI (int countCircles) {
    	lines.add(PaneUtil.addLine(leftTower));          
        fillCircles(leftTower, countCircles);       
        lines.add(PaneUtil.addLine(centerTower));
        fillCircles(centerTower, 0);
        lines.add(PaneUtil.addLine(rightTower));
        fillCircles(rightTower, 0);
    }
    
    private void fillCircles (Pane tower, int countCircles) {      
        Rectangle circle;
        for (int i = 0; i < countCircles; i++) {           
            circle = new Rectangle(150 - i * 15, 25); //razmer
            circle.getStyleClass().add("circle");            
            circle.setOnMousePressed(pressed);                     
            circle.setOnMouseReleased(released);
            circle.setOnMouseDragged(dragged);
            circle.setOnMouseEntered(entered);
            circle.setOnMouseExited(exited);   
            PaneUtil.addCircle(tower, circle);
        }
    }
    
    private void changeCount(int i) {
    	countStep = countStep + i;                            
    	sp.set(textCountStep + countStep);                    
    	if (countStep > 0) undoMoveButton.setDisable(false);  
    	else undoMoveButton.setDisable(true);                
    }
    
    private void restart() {
        leftTower.getChildren().clear();  
        centerTower.getChildren().clear();
        rightTower.getChildren().clear();
        
        leftArray.clear();
        centerArray.clear();
        rightArray.clear();
        
        lines.clear();
        
        initUI(Integer.parseInt(countBricks.getSelectionModel().getSelectedItem().toString()));
        changeCount( -countStep );        
    }
    
    private void saveState() {
        
        left = FXCollections.observableArrayList();
        center = FXCollections.observableArrayList();
        right = FXCollections.observableArrayList();
        
        for (int i=1; i<leftTower.getChildren().size(); i++ ) {
            left.add((Rectangle)leftTower.getChildren().get(i));
        }        
        leftArray.add(left);
        
        for (int i=1; i<centerTower.getChildren().size(); i++ ) {
            center.add((Rectangle)centerTower.getChildren().get(i));
        }
        centerArray.add(center);
        
        for (int i=1; i<rightTower.getChildren().size(); i++ ) {
            right.add((Rectangle)rightTower.getChildren().get(i));
        }
        rightArray.add(right);
        
    }
    
   
    private int endGame(Pane tower) {
    	      
    	boolean value = Integer.parseInt(countBricks.getSelectionModel().getSelectedItem().toString()) == tower.getChildren().size()-1;    	
    	
    	
    	if (value && tower != leftTower) { //
    		endDialog.showAndWait();
    		if (endDialog.getOption() == 1) {
    			return 1;
    		} else {
    			return 2;
    		}
    	}
    	return 0;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    //--------------------------------------------------------------------------
    
    EventHandler<MouseEvent> pressedRestartButton = (MouseEvent event) -> {       
    	int option = 0;
    	dialog = new DialogWindow(new String[] {"Yes", "No", "I`m not sure:)"}, "Question", "Are you sure to restart the game?", 350, 100);
    	dialog.showAndWait();
    	option = dialog.getOption();

    	if (option == 1) {
    		restart();
    	}
  	
    };
    //--------------------------------------------------------------------------
    EventHandler<MouseEvent> pressed = (MouseEvent event) -> {
        Rectangle circle = ((Rectangle) event.getSource()); 
        Pane sourceTower = (Pane) circle.getParent();
    
        if (selectedTower != null ) { 
        	PaneUtil.setCircleColor(selectedTower, primaryCircleColor);
        	if (PaneUtil.isLegalMove(selectedTower, sourceTower)) {	
        		PaneUtil.setCircleColor(sourceTower, primaryCircleColor);
        		saveState();              
        		PaneUtil.moveCircle(selectedTower, sourceTower, (Rectangle)selectedTower.getChildren().get(selectedTower.getChildren().size()-1));
        		changeCount(1);        
        		
        		switch (endGame(sourceTower)) {
        			case 1: restart(); return; 
        			case 2: System.exit(0);
        			default: break;
        		}       		
        		selectedTower = null;   
        		return;                 
        	} else {
       			selectedTower = null;     
        		illegalMoveDialog.showAndWait();  
        		return;                 
        	}
        }       
        //-------------------if you click on tower--------        
        flagClick = true;                                            
        selectedTower = sourceTower;                                 
        PaneUtil.setCircleColor(sourceTower, lightingCircleColor);   
        //-------------------------------------------------
        mousex = event.getSceneX();                   
        mousey = event.getSceneY();
        //get the x and y position measure from Left-Top
        x = circle.getLayoutX();                      
        y = circle.getLayoutY();
        x0 = x;                                      
        y0 = y;
                   
        if (PaneUtil.isLastCircle(circle)) circle.getScene().setCursor(Cursor.MOVE);     
    };
    
    //--------------------events-----------------------------------------------
    EventHandler<MouseEvent> released = (MouseEvent event) -> {
        Rectangle circle = ((Rectangle) event.getSource());     
        Pane sourceTower = (Pane) circle.getParent();              	
        if (destinationTower != null) {                           
        	if (PaneUtil.isLegalMove(sourceTower, destinationTower)) {                       
        		saveState();
        		PaneUtil.moveCircle(sourceTower, destinationTower, circle);       
        		changeCount(1);                      
        		
        		switch (endGame(destinationTower)) {
	        		case 1: restart(); return; 
	        		case 2: System.exit(0);
	        		default: break;
        		}
        		
        		selectedTower = null;             
        	} else {                               
        		illegalMoveDialog.showAndWait();
        		circle.setLayoutX(x0);             
            	circle.setLayoutY(y0);
        	}          	             
        	PaneUtil.setCircleColor(sourceTower, primaryCircleColor);
        	destinationTower = null;         
        } else {             	        	
        	if (!flagClick) {   	
        		circle.setLayoutX(x0);     
        		circle.setLayoutY(y0);
        		PaneUtil.setCircleColor(sourceTower, primaryCircleColor);
        	}        	        	
        }       
        circle.getScene().setCursor(Cursor.HAND);
    };
    //--------------------------------------------------------------------------
    EventHandler<MouseEvent> dragged = (MouseEvent event) -> {
    	Rectangle circle = ((Rectangle) event.getSource());             
    	Pane sourceTower = (Pane) circle.getParent();                   
    	
    	if (PaneUtil.isLastCircle(circle)) {  
    		circle.setFill(movingCircleColor);
    		selectedTower = null;      
    		for (Line l : lines) {
    			Pane targetTower = (Pane) l.getParent();
    			if (Shape.intersect(circle, l).getBoundsInParent().isEmpty() == false && targetTower!=sourceTower) {
    				destinationTower = targetTower;       
    				break;                          
    			} else {
    				destinationTower = null;           
    			}
    		}
    		
    		flagClick = false;  
            
            x += event.getSceneX() - mousex;
            y += event.getSceneY() - mousey;
            
            circle.setLayoutX(x);
            circle.setLayoutY(y);
            
            mousex = event.getSceneX();
            mousey = event.getSceneY();
        }
    };
    //--------------------------------------------------------------------------
    EventHandler<MouseEvent> entered = (MouseEvent event) -> {
        Rectangle circle = ((Rectangle) event.getSource());
        if (!event.isPrimaryButtonDown()) {
        	if (selectedTower == null) {  
        		PaneUtil.setCircleColor((Pane)circle.getParent(), Color.PINK);
        	}
            circle.getScene().setCursor(Cursor.HAND);
        }
    };
    //--------------------------------------------------------------------------
    EventHandler<MouseEvent> exited = (MouseEvent event) -> {
        Rectangle circle = ((Rectangle) event.getSource());
        if (!event.isPrimaryButtonDown()) {
            circle.getScene().setCursor(Cursor.DEFAULT);
        }
        if (selectedTower == null) {
        	PaneUtil.setCircleColor((Pane)circle.getParent(), primaryCircleColor);
        }	
    };
    //--------------------------------------------------------------------------
    EventHandler<MouseEvent> undoMove = (MouseEvent event) -> {
        changeCount(-1);             
        
        leftTower.getChildren().clear();
        centerTower.getChildren().clear();
        rightTower.getChildren().clear();
        lines.clear();
        
        lines.add(PaneUtil.addLine(leftTower));        
        lines.add(PaneUtil.addLine(centerTower));
        lines.add(PaneUtil.addLine(rightTower));
    
        left = leftArray.get(countStep);
        left.stream().forEach((r) -> {
            PaneUtil.addCircle(leftTower, r);
        });
        leftArray.remove(countStep);

        center = centerArray.get(countStep);
        center.stream().forEach((r) -> {
        	PaneUtil.addCircle(centerTower, r);
        });
        centerArray.remove(countStep);

        right = rightArray.get(countStep);
        right.stream().forEach((r) -> {
        	PaneUtil.addCircle(rightTower, r);
        });
        rightArray.remove(countStep);
    };
    //--------------------------------------------------------------------------
    EventHandler<MouseEvent> clickButtonTower = (MouseEvent event) -> {
    	Button button = (Button) event.getSource();
    	Pane targetTower = null;

		if (button == tower1Button) targetTower = leftTower;    
		if (button == tower2Button) targetTower = centerTower;
		if (button == tower3Button) targetTower = rightTower;
		
    	if (selectedTower!=null) {                   
    		PaneUtil.setCircleColor(selectedTower, primaryCircleColor);  
    		
    		if (PaneUtil.isLegalMove(selectedTower, targetTower)) {     
    			saveState();                                            
    			PaneUtil.moveCircle(selectedTower, targetTower, (Rectangle)selectedTower.getChildren().get(selectedTower.getChildren().size()-1)); // move
    			changeCount(1);                                          
    			switch (endGame(targetTower)) {
	        		case 1: restart(); return; 
	        		case 2: System.exit(0);
	        		default: break; 
        		}
    		} else {
    			illegalMoveDialog.showAndWait();
    		}
    		selectedTower = null;       		
    	} else {
    		
    		selectedTower = targetTower;
    		PaneUtil.setCircleColor(targetTower, lightingCircleColor);   
    	}
    }; 
    //--------------------------------------------------------------------------
    EventHandler<MouseEvent> clickTower = (MouseEvent event) -> {
    	Pane targetTower = (Pane) event.getSource();       
    	if (selectedTower!=null && selectedTower != targetTower) {                   
    		PaneUtil.setCircleColor(selectedTower, primaryCircleColor);  
    		System.out.println(selectedTower + ": selelected, " + targetTower + ": target");
    		if (PaneUtil.isLegalMove(selectedTower, targetTower)) {     
    			saveState();                                           
    			PaneUtil.moveCircle(selectedTower, targetTower, (Rectangle)selectedTower.getChildren().get(selectedTower.getChildren().size()-1)); 
    			changeCount(1);                                         
    			switch (endGame(targetTower)) {
	        		case 1: restart(); return; 
	        		case 2: System.exit(0);
	        		default: break;
        		}
    			selectedTower = null;      
    		} else {
    			illegalMoveDialog.showAndWait();
    		}
    		     		   		
    	} 
    	
    };
    
}
