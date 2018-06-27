package lakerc.games;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class SnakeGame extends Application{
	public static void main(String[] args){
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//500x500 window
		Pane pane = new Pane();
		int sceneDimen = 500;
		Scene scene = new Scene(pane, sceneDimen, sceneDimen);

		//create a new Snake and a new Apple
		Snake snake = new Snake();
		Apple apple = new Apple();

		//lines to border the play space
		Line line1 = new Line(0, 0, sceneDimen, 0);
		Line line2 = new Line(0, 0, 0, sceneDimen);
		Line line3 = new Line(0, sceneDimen, sceneDimen, sceneDimen);
		Line line4 = new Line(sceneDimen, 0, sceneDimen, sceneDimen);


		//add the snake, apple, and lines to the pane
		pane.getChildren().addAll(snake.getSnake());
		pane.getChildren().addAll(line1, line2, line3, line4);
		pane.getChildren().add(apple.getApple());

		//background thread for controlling the snake's movement
		Thread snek = new Thread(() -> {
            boolean restart;
            while (true) {
                try {
                    //move the snake in its current direction, and check if it overlaps the apple. Then sleep for 100ms
                    updatePos(snake.getDir(), snake.getSnake(), sceneDimen);
                    checkAte(pane, snake.getSnake(), apple, scene);
                    restart = checkLose(snake.getSnake());
                    //if snake eats itself, restart the game
                    if (restart) {
                        Platform.runLater(() -> {
                            primaryStage.close();
                            try {
                                new SnakeGame().start(new Stage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Exception caught");
                    e.printStackTrace();
                }
            }
        });
		snek.start();

		//onKeyPressed, checks which arrow key was pressed and sets the snake's direction. Exits when Q is pressed
		scene.setOnKeyPressed(event -> {
				String key = event.getCode().toString();
				//System.out.println("Key pressed: " + key);
				switch(key){
					case "UP":
							//prevent snake from doing a 180 into itself before changing direction
							if (!snake.getDir().equals("down"))
								snake.setDir("up");
						break;
					case "LEFT":
							if (!snake.getDir().equals("right")) //same here
								snake.setDir("left");
						break;
					case "DOWN":
							if (!snake.getDir().equals("up")) //and here
							snake.setDir("down");
						break;
					case "RIGHT":
							if (!snake.getDir().equals("left")) //here too!
							snake.setDir("right");
						break;
					case "Q":
						System.exit(0);
				}
		});
		
		//Set the scene, add a title to the window, and show the window
		primaryStage.setScene(scene);
		primaryStage.setTitle("Snake");
		primaryStage.show();
	}

	//check if snake ate itself
	private boolean checkLose(ArrayList<Rectangle> snake) {
		for (int i = 1; i < snake.size(); i++){
			if (snake.get(0).getBoundsInParent().intersects(snake.get(i).getBoundsInParent())) {
				return true;
			}
		}
		return false;
	}

	//check if moved player overlaps or "eats" apple
	public void checkAte(Pane pane, ArrayList<Rectangle> snake, Apple apple, Scene scene){
		//if the first rectangle overlaps the apple, generate a new position for the apple and add a new rectangle to the player
		if (snake.get(0).getBoundsInParent().intersects(apple.getApple().getBoundsInParent())){
			//generate and set new coords for the apple
			int[] coords = genAppCoords();
			apple.getApple().setX(coords[0]);
			apple.getApple().setY(coords[1]);
			//add a new rectagle to the end of the player, and set its color
			snake.add(new Rectangle(snake.get(snake.size() - 1).getX(), snake.get(snake.size() - 1).getY(),19, 19));
			snake.get(snake.size() - 1).setFill(Color.rgb(0, 255, 50));

			//JavaFX doesn't like it when you modify the UI outside of its thread so this needs to be here
			Platform.runLater(() -> {
                //add the new rectangle to the pane
                pane.getChildren().add(snake.get(snake.size() - 1));
            });
		}
	}

	//Generate new x and y positions for the apple
	public int[] genAppCoords(){
		int[] coords = new int[2];
		//generate random coords between 20 and current width - 20 in chunks of 20
		coords[0] = ((1 + (int)(Math.random() * 24)) * 20);
		coords[1] = ((1 + (int)(Math.random() * 24)) * 20);
		return coords;
	}

	//update the player's position
	public void updatePos(String dir, ArrayList<Rectangle> snake, int sceneDimen){
			//vars for keeping the coords of previous chunk
			int pX = 0;
			int pY = 0;
			for (int i = 0; i < snake.size(); i++){
				//get the current chunk's coords
				int posY = (int)snake.get(i).getY();
				int posX = (int)snake.get(i).getX();
				//System.out.println("player chunk " + i + " at location " + posX + ", " + posY);
				//for the first chunk
				if (i == 0){
					//set the prev chunk coords to first chunk's coords
					pX = posX;
					pY = posY;

					//wrap snake if it goes beyond play area
					wrapSnake(i, dir, snake, posX, posY, sceneDimen);
					//System.out.println("player chunk " + i + " moved to location " + posX + ", " + (posY - 20));
				}
				else{
					//for each successive chunk, set its coords to the previous chunk's coords
					snake.get(i).setX(pX);
					snake.get(i).setY(pY);
					//System.out.println("player chunk " + i + " moved to location " + pX + ", " + pY);
					//set the prev chunk coords to the current chunk's coords before it was moved
					pX = posX;
					pY = posY;
				}	
				
			}
	}

	public void wrapSnake(int i, String dir, ArrayList<Rectangle> snake, int posX, int posY, int sceneDimen){
		switch(dir) {
			//wrap snake to bottom if he goes past top
			case "up":
				if (posY - 20 < 0) {
					posY = sceneDimen - 20;
					snake.get(i).setY(posY);
				}
				else
					snake.get(i).setY(posY - 20);
				break;

			//wrap snake to top if he goes past bottom
			case "down":
				if (posY + 20 > sceneDimen - 20) {
					snake.get(i).setY(0);
				}
				else
					snake.get(i).setY(posY + 20);
				break;

			//wrap snake to right side if he goes past left side
			case "left":
				if (posX - 20 < 0) {
					posX = sceneDimen - 20;
					snake.get(i).setX(posX);
				}
				else
					snake.get(i).setX(posX - 20);
				break;

			//wrap snake to left side if he goes past right side
			case "right":
				if (posX + 20 > sceneDimen - 20) {
					snake.get(i).setX(0);
				}
				else
					snake.get(i).setX(posX + 20);
				break;
		}
	}
}