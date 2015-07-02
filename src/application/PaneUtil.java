package application;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class PaneUtil {

	public static void setCircleColor(Pane pane, Color color) {
		for (Node node: pane.getChildren()) {       		
			if (node instanceof Rectangle) {         
				((Rectangle) node).setFill(color);
			}				
		}	
	}
	
	
	
	public static void setStyleChildreParent(Pane pane, String classStyle) {		
		for (Node node: pane.getChildren()) {        		
			if (node instanceof Rectangle) {        
				Rectangle rect = (Rectangle) node;
				rect.getStyleClass().clear();
				rect.getStyleClass().add(classStyle);  
			}				
		}		
	}

	public static Line addLine(Pane tower) {            
        Line line = new Line(tower.getPrefWidth()/2, tower.getPrefHeight()-5, tower.getPrefWidth()/2, 80);
        tower.getChildren().add(line);                        
        return line;
    }	
	
	public static void addCircle(Pane tower, Rectangle circle) { 
        tower.getChildren().add(circle);
        circle.setLayoutX((tower.getPrefWidth() - circle.getWidth()) / 2);
        circle.setLayoutY(tower.getPrefHeight() - (circle.getHeight()) * (tower.getChildren().size()-1));       
    }	
	
	public static void moveCircle(Pane sourceTower, Pane targetTower, Rectangle circle) {   	
    	sourceTower.getChildren().remove(circle);        
        PaneUtil.addCircle(targetTower, circle);                  
    }	
		
	public static boolean isLastCircle(Rectangle circle) {   
	Pane tower = (Pane) circle.getParent();                
        return tower.getChildren().get(tower.getChildren().size()-1 ) == circle;
    }

	public static boolean isLegalMove(Pane sourceTower, Pane targetTower) {       
        if (sourceTower == targetTower) return false;     
    	
    	if (sourceTower.getChildren().size() == 1) {        	
            return false;
        }
        if (targetTower.getChildren().size() == 1) {    
            return true;
        }
        Rectangle sourceCircle = (Rectangle) sourceTower.getChildren().get(sourceTower.getChildren().size() - 1);
        Rectangle targetCircle = (Rectangle) targetTower.getChildren().get(targetTower.getChildren().size() - 1);
        return targetCircle.getWidth() >= sourceCircle.getWidth();  
    }	
	
	
}
