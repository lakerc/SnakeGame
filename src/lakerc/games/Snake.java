package lakerc.games;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Snake {
    private String dir;
    private ArrayList<Rectangle> snake;

    public Snake(){
        dir = "left";
        snake = new ArrayList<>();
        //always start snake with a size of 3 at 240, 240
        for (int i = 0; i < 3; i++){
            if (i == 0) {
                //snake is 19x19 to allow a pixel space between the chunks as well as to make it move evenly in 20 pixel chunks
                snake.add(new Rectangle(240, 240, 19, 19));
                snake.get(i).setFill(Color.rgb(0, 220, 0));
            }
            else {
                snake.add(new Rectangle(240 + (20 * i), 240, 19, 19));
                snake.get(i).setFill(Color.rgb(0, 255, 0));
            }
        }
    }

    public void setDir(String dir){
        this.dir = dir;
    }
    public String getDir(){
        return dir;
    }

    public ArrayList<Rectangle> getSnake(){
        return snake;
    }
}