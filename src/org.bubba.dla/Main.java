import java.util.ArrayList;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Main extends Application {

	private int windowSize = 1200;

	private double movementFactor = 10.0;
	private double walkerSize = 10.0;
	private int MAX_WALKERS = 100;

	private GraphicsContext gcTree, gcWalker;
	private Canvas canvasTree, canvasWalker;
	private Random rand = new Random();

	private ArrayList<Walker> Walkers = new ArrayList<Walker>();
	private ArrayList<Walker> Tree = new ArrayList<Walker>();

	public static void main(String[] args){
		launch(args);
	}

	public void start(Stage window){

		canvasWalker = new Canvas(windowSize,windowSize);
		canvasTree = new Canvas(windowSize,windowSize);
		gcWalker = canvasWalker.getGraphicsContext2D();
		gcWalker.setFill(Color.BLUE);
		gcTree = canvasTree.getGraphicsContext2D();
		gcTree.setFill(Color.RED);

		Walker centerWalker = new Walker(canvasTree.getWidth()/2.0,
				canvasTree.getHeight()/2.0,walkerSize);

		gcTree.fillOval(centerWalker.getCenterX(),centerWalker.getCenterY(),
				centerWalker.getRadius(),centerWalker.getRadius());

		for(int i=0; i<MAX_WALKERS; i++){
			Walkers.add(new Walker(
						rand.nextInt((int)canvasWalker.getWidth()),
						rand.nextInt((int)canvasWalker.getHeight()),
						walkerSize)
					);
		}
		for(Walker w : Walkers){
			gcWalker.fillOval(w.getCenterX(),w.getCenterY(),
					w.getRadius(),w.getRadius());
		}

		Pane pane = new Pane();
		pane.getChildren().addAll(canvasTree,canvasWalker);
		BorderPane bpMain = new BorderPane();
		bpMain.setStyle("-fx-background-color: black");
		bpMain.setCenter(pane);

		Scene scene = new Scene(bpMain,windowSize,windowSize);

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
		for(Walker w : Walkers){
			w.update();
			/*
			 * for(Walker t : Tree){
			 *         if(w.collides(t)){
			 *                 t.setFrozen();
			 *         }
			 * }
			 */
		}
		System.out.println("ran update");
	}

	public void draw(){
		gcWalker.clearRect(0,0,canvasWalker.getWidth(),
				canvasWalker.getHeight());
		for(Walker w : Walkers){
			gcWalker.fillOval(w.getCenterX(),w.getCenterY(),
					w.getRadius(),w.getRadius());
		}
		for(Walker t : Tree){
			gcTree.fillOval(t.getCenterX(),t.getCenterY(),
					t.getRadius(),t.getRadius());
		}
		System.out.println("ran draw");
	}

	class Walker extends Circle {

		private	boolean frozen;

		public Walker(double x, double y, double r){
			super(x,y,r);
		}

		public void update(){
			if(this.frozen){
				super.setCenterX((super.getCenterX()+rand.nextDouble())*
						movementFactor);
				super.setCenterY((super.getCenterY()+rand.nextDouble())*
						movementFactor);
				//TODO: add size scaling.
			}
		}

		public void setFrozen(){
			this.frozen = false;
			Tree.add(Walkers.get(Walkers.indexOf(this)));
			Walkers.remove(this);
		}

		//TODO: finish this.
		public boolean collides(Walker w){
			return true;
		}
	}
}
