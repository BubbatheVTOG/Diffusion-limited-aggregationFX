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
import javafx.scene.paint.Paint;

public class Main extends Application {

	private int windowSize = 1200;

	private double movementFactor = 3.0;
	private double walkerSize = 10.0;
	private int MAX_WALKERS = 1000;

	private Canvas canvasWalker;
	private GraphicsContext gcWalker;
	private Random rand = new Random();

	private ArrayList<Walker> walkers = new ArrayList<Walker>();

	public static void main(String[] args){
		launch(args);
	}

	public void start(Stage window){

		canvasWalker = new Canvas(windowSize,windowSize);
		gcWalker = canvasWalker.getGraphicsContext2D();
		canvasWalker.toFront();

		for(int i=0; i<MAX_WALKERS; i++){
			walkers.add(new Walker((double)rand.nextInt((int)canvasWalker.getWidth()),
						(double)rand.nextInt((int)canvasWalker.getHeight()),
						walkerSize));
		}

		Pane pane = new Pane();
		pane.getChildren().addAll(canvasWalker);

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
		Vector<Thread> updateThreads = new Vector<Thread>();
		for(Walker w: walkers){
			updateThreads.add(new Thread(w));
		}
		for(Thread t: updateThreads){
			t.start();
		}
		for(Thread t: updateThreads){
			try{
				t.join();
			}catch(InterruptedException ie){
				System.out.println("An update thread was interrupted.");
			}
		}
		// System.out.println(""+walkers.get(0).getCenterX());
	}

	public void draw(){
		gcWalker.clearRect(0,0,canvasWalker.getWidth(),canvasWalker.getHeight());
		for(Walker w: walkers){
			gcWalker.fillOval(w.getCenterX(), w.getCenterY(),
					w.getRadius(),w.getRadius());
		}
	}

	class Walker extends Circle implements Runnable{

		private boolean frozen = false;

		public Walker(double x, double y, double r){
			super(x,y,r);
			super.setFill(Paint.valueOf("red"));
		}

		public boolean isFrozen(){
			return frozen;
		}

		public void setFrozen(){
			frozen=true;
		}

		public void run(){
			this.update();
		}

		//TODO change this to private.
		public void update(){
			if(!this.frozen){
				super.setCenterX(rand.nextBoolean()?
						((rand.nextDouble()*movementFactor)*-1)+super.getCenterX():
						(rand.nextDouble()*movementFactor)+super.getCenterX()
						);

				super.setCenterY(rand.nextBoolean()?
						((rand.nextDouble()*movementFactor)*-1)+super.getCenterY():
						(rand.nextDouble()*movementFactor)+super.getCenterY()
						);
			}

		}

	}
}
