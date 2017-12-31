import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Main extends Application {

	private int windowSize = 1200;

	private double movementFactor = 10.0;
	private double walkerSize = 10.0;
	private int MAX_WALKERS = 10;

	// private GraphicsContext gcTree, gcWalker;
	private GraphicsContext gcWalker;
	// private Canvas canvasTree, canvasWalker;
	private Canvas canvasWalker;
	private Random rand = new Random();

	private ArrayList<Walker> Walkers = new ArrayList<Walker>();
	private ArrayList<Walker> Tree = new ArrayList<Walker>();

	public static void main(String[] args){
		launch(args);
	}

	public void start(Stage window){

		canvasWalker = new Canvas(windowSize,windowSize);
		// canvasTree = new Canvas(windowSize,windowSize);
		gcWalker = canvasWalker.getGraphicsContext2D();
		gcWalker.setFill(Color.BLUE);
		// gcTree = canvasTree.getGraphicsContext2D();
		// gcTree.setFill(Color.RED);
		canvasWalker.toFront();

		// Walker centerWalker = new Walker(canvasTree.getWidth()/2.0,
				// canvasTree.getHeight()/2.0,walkerSize);

		// gcTree.fillOval(centerWalker.getCenterX(),centerWalker.getCenterY(),
				// centerWalker.getRadius(),centerWalker.getRadius());

		for(int i=0; i<MAX_WALKERS; i++){
			Walkers.add(new Walker(
						rand.nextInt((int)canvasWalker.getWidth()),
						rand.nextInt((int)canvasWalker.getHeight()),
						walkerSize)
					);
		}

		/*
		 * for(Walker w : Walkers){
		 *         gcWalker.fillOval(w.getCenterX(),w.getCenterY(),
		 *                         w.getRadius(),w.getRadius());
		 * }
		 */

		StackPane pane = new StackPane();
		// pane.getChildren().addAll(canvasTree,canvasWalker);
		pane.getChildren().addAll(canvasWalker);
		// BorderPane bpMain = new BorderPane();
		// bpMain.setStyle("-fx-background-color: black");
		// bpMain.setCenter(pane);

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

		/*
		 * for(Walker w: Walkers){
		 *         w.walk();
		 * }
		 */

		for(Walker w : Walkers){
			updateThreads.add(new Thread(w));
		}
		for(Thread t : updateThreads){
			t.start();
		}

		for(Thread t : updateThreads){
			try{
				t.join();
			}catch(InterruptedException ie){
				System.out.println("A update thread got interrupted.");
			}
		}
		System.out.println("ran update");
	}

	public void draw(){

		// gcWalker.clearRect(0,0,canvasWalker.getWidth(),
				// canvasWalker.getHeight());

		// for(Walker t : Tree){
			// gcTree.fillOval(t.getCenterX(),t.getCenterY(),
					// t.getRadius(),t.getRadius());
		// }

		for(Walker w : Walkers){
			gcWalker.fillOval(w.getCenterX(),w.getCenterY(),
					w.getRadius(),w.getRadius());
		}

		System.out.println("ran draw");
	}

	class Walker extends Circle implements Runnable{

		private	boolean frozen = false;

		public Walker(double x, double y, double r){
			super(x,y,r);
		}

		private void walk(){
			if(!this.frozen){
				this.setCenterX(rand.nextBoolean()?
						(this.getCenterX()+rand.nextDouble()*-1)*movementFactor:
						(this.getCenterX()+rand.nextDouble())*movementFactor);

				this.setCenterY(rand.nextBoolean()?
						(this.getCenterY()+rand.nextDouble()*-1)*movementFactor:
						(this.getCenterY()+rand.nextDouble())*movementFactor);

				System.out.println("ran walk");
			}
		}

		private void setFrozen(){
			this.frozen = true;
			Tree.add(Walkers.get(Walkers.indexOf(this)));
			Walkers.remove(this);
		}

		//TODO: finish this.
		private boolean collides(Walker otherWalker){
			return true;
		}

		public void run(){
			this.walk();
		}
	}
}
