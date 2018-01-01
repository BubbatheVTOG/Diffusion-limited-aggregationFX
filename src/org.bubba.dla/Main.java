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

//TODO:
//fix error
//change colors
//change size
//infiniteWalkers - done
//boundry checking
//mousedrag listener
//title
//menu -> new && stop

public class Main extends Application {

	private int windowSize = 800;

	private double movementFactor = 3.0;
	private double walkerSize = 10.0;
	private int concurrentWalkers = 800;
	private boolean infiniteWalkers = true;

	private Color walkerColor = Color.AQUAMARINE;
	private Color treeColor = Color.FIREBRICK;

	private Canvas canvas;
	private Random rand = new Random();

	private Vector<Walker> walkers = new Vector<Walker>();
	private Vector<Walker> tree = new Vector<Walker>();

	public static void main(String[] args){
		launch(args);
	}

	public void start(Stage window){

		canvas = new Canvas(windowSize,windowSize);
		GraphicsContext gcWalker = canvas.getGraphicsContext2D();
		canvas.toFront();

		for(int i=0; i<concurrentWalkers; i++){
			walkers.add(new Walker((double)rand.nextInt((int)canvas.getWidth()),
						(double)rand.nextInt((int)canvas.getHeight()),
						walkerSize));
		}

		Walker startNode = new Walker(canvas.getWidth()/2.0,canvas.getHeight()/2.0,walkerSize);
		startNode.setFrozen();
		tree.add(startNode);

		Pane pane = new Pane();
		pane.setStyle("-fx-background-color: black");
		pane.getChildren().addAll(canvas);

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
				Main.this.draw(gcWalker);
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
		if(infiniteWalkers){
			for(int i=walkers.size(); i<concurrentWalkers; i++){
				walkers.add(new Walker((double)rand.nextInt((int)canvas.getWidth()),
							(double)rand.nextInt((int)canvas.getHeight()),
							walkerSize));
			}
		}
	}

	public void draw(GraphicsContext gc){
		gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
		gc.setFill(walkerColor);
		for(Walker w: walkers){
			gc.fillOval(w.getCenterX(), w.getCenterY(),
					w.getRadius(),w.getRadius());
		}

		gc.setFill(treeColor);
		for(Walker t: tree){
			gc.fillOval(t.getCenterX(), t.getCenterY(),
					t.getRadius(),t.getRadius());
		}
	}

	public void shiftTreeColor(Walker walker){
		double currentGreen = treeColor.getGreen();
		double currentRed = treeColor.getRed();
		double currentBlue = treeColor.getBlue();

		double minX = Math.min(Math.min(0,walker.getCenterX()),
				Math.min(canvas.getWidth(),walker.getCenterX()));
		double minY = Math.min(Math.min(0,walker.getCenterY()),
				Math.min(canvas.getWidth(),walker.getCenterY()));

		double min = Math.min(minX,minY);
	}

	class Walker extends Circle implements Runnable{

		private boolean frozen = false;

		public Walker(double x, double y, double r){
			super(x,y,r);
			super.setFill(Paint.valueOf("blue"));
		}

		public void setFrozen(){
			frozen=true;
		}

		private boolean isFrozen(){
			return frozen;
		}

		private void setFrozenAndChangeState(){
			frozen=true;
			if(this.frozen){
				tree.add(walkers.remove(walkers.indexOf(this)));
			}
		}

		private boolean collides(Walker otherWalker){
			boolean collides = false;
			double distance = Math.sqrt(
					(this.getCenterX()-otherWalker.getCenterX())*(this.getCenterX()-otherWalker.getCenterX())+
					(this.getCenterY()-otherWalker.getCenterY())*(this.getCenterY()-otherWalker.getCenterY())
					);

			if((distance < (this.getRadius()+otherWalker.getRadius())/2.0) && otherWalker.isFrozen()){
				collides=true;
			}
			return collides;
		}

		public void run(){
			this.update();
		}

		private void update(){
			if(!this.frozen){
				this.setCenterX(rand.nextBoolean()?
						((rand.nextDouble()*movementFactor)*-1)+this.getCenterX():
						(rand.nextDouble()*movementFactor)+this.getCenterX()
						);

				super.setCenterY(rand.nextBoolean()?
						((rand.nextDouble()*movementFactor)*-1)+this.getCenterY():
						(rand.nextDouble()*movementFactor)+this.getCenterY()
						);
			}

			for(Walker t: tree){
				if(this.collides(t)){
					this.setFrozenAndChangeState();
					Main.this.shiftTreeColor(t);
				}
			}
		}
	}
}
