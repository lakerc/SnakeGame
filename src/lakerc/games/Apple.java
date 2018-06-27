package lakerc.games;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Apple {
    private Rectangle apple;

    public Apple(){
        //always start apple at 200,280
        apple = new Rectangle(200, 280, 20, 20);
        apple.setFill(Color.RED);
    }

    public Rectangle getApple(){
        return apple;
    }
}