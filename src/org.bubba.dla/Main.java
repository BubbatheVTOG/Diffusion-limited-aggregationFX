import java.util.ArrayList;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Main extends Application {

	private GraphicsContext gc;
	private Canvas canvas;
	private AnimationTimer at;
	private MyRandom rand = new MyRandom();
	private int MAX_WALKERS = 10;

	private ArrayList<Walker> Walkers = new ArrayList<Walker>();

	public static void main(String[] args){
		launch(args);
	}

	public void start(Stage window){

		Canvas canvas = new Canvas(window.getWidth(),window.getHeight());
		gc = canvas.getGraphicsContext2D();

		gc.setFill(Color.RED);
		for(int i=0; i<MAX_WALKERS; i++){
			Walkers.add(new Walker(
						rand.nonNegativeNextInt((int)canvas.getWidth()),
						rand.nonNegativeNextInt((int)canvas.getHeight()),
						2)
					);
		}
		for(Walker w : Walkers){
			gc.fillOval(w.getCenterX(),w.getCenterY(),
					w.getCenterX()*w.getRadius(),
					w.getCenterY()*w.getRadius());
		}

		BorderPane bpMain = new BorderPane();
		bpMain.setStyle("-fx-background-color: black");
		bpMain.getChildren().add(canvas);

		Scene scene = new Scene(bpMain,500,500);

		window.setMinWidth(500);
		window.setMinHeight(500);
		window.setScene(scene);
		window.show();
	}

	//update
	//clear
	//draw

	class Walker extends Circle {

		private	boolean frozen;

		public Walker(double x, double y, double r){
			super.setCenterX(x);
			super.setCenterY(y);
			super.setRadius(r);
		}

		public void update(double x, double y, double r){
			if(this.frozen){
				super.setCenterX(x);
				super.setCenterY(y);
				super.setRadius(r);
			}
		}

		public void setFrozen(){
			this.frozen = false;
		}
	}

	class MyRandom extends Random{

		Random aRandom = new Random();

		public int nonNegativeNextInt(int bound){
			bound = (bound < 0)? bound * -1: bound;
			int val = aRandom.nextInt(bound);
			return (val < 0)? val * -1: val;
		}
	}

}
