import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Main extends Application {

	private int windowSize = 1200;

	private double movementFactor = 10.0;
	private double walkerSize = 10.0;
	private int MAX_WALKERS = 10;

	private Canvas canvasWalker;
	private GraphicsContext gcWalker;

	public static void main(String[] args){
		launch(args);
	}

	public void start(Stage window){

		canvasWalker = new Canvas(windowSize,windowSize);
		gcWalker = canvasWalker.getGraphicsContext2D();
		gcWalker.setFill(Color.BLUE);
		canvasWalker.toFront();

		Pane pane = new Pane();
		pane.getChildren().addAll(pane);

		Scene scene = new Scene(pane,windowSize,windowSize);

		window.setMinWidth(windowSize);
		window.setMaxWidth(windowSize);
		window.setMinHeight(windowSize);
		window.setMaxHeight(windowSize);
		window.setScene(scene);
		window.show();

		new AnimationTimer(){
			@Override
			public void handle(long now){
				Main.this.update();
				Main.this.draw();
			}
		}.start();
	}

	public void update(){
	}

	public void draw(){
	}

	class Walker extends Circle implements Runnable{

		public void run(){

		}

	}
}
