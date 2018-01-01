import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Paint;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;


//TODO:
//comments -- done
//fix error -- done
//change colors
//change size
//infiniteWalkers -- done
//boundry checking -- done
//mousedrag listener -- done
//title -- done
//menu -> new,stop,exit -- WIP
//stickiness factor
//implement a skip frames slider
//change walker behavior to only check for tree particles within the movementFactor radius?
//make this use JOGL, but that is beyond me atm.

public class Main extends Application {

	//Set our window size.
	private int windowSize = 800;

	//change the properties of our walkers.
	//movementFactor: Movement is -1,1 inclusively, this scales it.
	//walkerSize: How large to make the walkers.
	//concurrentWalkers: How many walkers should there be?
	//infiniteWalkers: Add a new walker when one attaches itself to to the tree.
	private double movementFactor = 5.0;
	private double walkerSize = 10.0;
	private int concurrentWalkers = 800;
	private boolean infiniteWalkers = true;

	//Change these to change display settings.
	//Some of these settings will result in procedure speedups.
	//default settings: 1,false,1
	//try: 100,true,50000
	private int skipFramesRatio = 1;
	private boolean displayIterrations = false;
	private int dontUpdateUntil = 1;

	//change the colors of the walkers.
	private Color walkerColor = Color.AQUAMARINE;
	private Color treeColor = Color.FIREBRICK;

	//global scoped stuff we need.
	private Canvas canvas;
	private Random rand = new Random();
	private AnimationTimer at;
	private Vector<Walker> walkers = new Vector<Walker>();
	private Vector<Walker> deadWalkers = new Vector<Walker>();
	private Vector<Walker> tree = new Vector<Walker>();
	double itterations = 0.0;

	//its main() yo.
	public static void main(String[] args){
		launch(args);
	}

	//start our application.
	public void start(Stage window){

		//this is our canvas.
		canvas = new Canvas(windowSize,windowSize);
		//this is our GraphicsContext.
		GraphicsContext gcWalker = canvas.getGraphicsContext2D();
		//not really needed, but better safe than sorry.
		canvas.toFront();

		//create our walkers.
		for(int i=0; i<concurrentWalkers; i++){
			walkers.add(new Walker((double)rand.nextInt((int)canvas.getWidth()),
						(double)rand.nextInt((int)canvas.getHeight()),
						walkerSize));
		}

		//our starting point.
		Walker startNode = new Walker(canvas.getWidth()/2.0,canvas.getHeight()/2.0,walkerSize);
		startNode.setFrozen();
		tree.add(startNode);

		//The pane that we are drawing on.
		Pane pane = new Pane();
		pane.setStyle("-fx-background-color: black");
		pane.getChildren().addAll(canvas);

		//Menu stuff.
		MenuBar menuBar = new MenuBar();
		Menu menuMenu = new Menu("Menu");
		MenuItem menuPause = new MenuItem("Pause");
		MenuItem menuStart = new MenuItem("Start");
		menuStart.setDisable(true);
		SeparatorMenuItem smiMenu = new SeparatorMenuItem();
		MenuItem miExit = new MenuItem("Exit");

		menuMenu.getItems().addAll(menuPause,menuStart,smiMenu,miExit);
		menuBar.getMenus().addAll(menuMenu);

		Scene scene = new Scene(pane,windowSize,windowSize);
		//TODO: This is wicked broken, don't know if its just a linux thing.
		((Pane)scene.getRoot()).getChildren().addAll(menuBar);

		//Window size contants because of i3wm.
		window.setMinWidth(windowSize);
		window.setMaxWidth(windowSize);
		window.setMinHeight(windowSize);
		window.setMaxHeight(windowSize);
		window.setTitle("Diffusion Limited Aggregation - By Bubba");
		window.setScene(scene);
		window.show();

		//the AnimationTimer we are using to control everything.
		at = new AnimationTimer(){
			@Override
			public void handle(long now){
				for(int i=0; i<=skipFramesRatio ;i++){
					Main.this.update();
					if(displayIterrations){
						System.out.println("Itterations: "+itterations);
					}
				}
				if(itterations > dontUpdateUntil){
					Main.this.draw(gcWalker);
				}
			}
		};
		at.start();

		//Click the canvas to add new walkers.
		canvas.setOnMouseDragged(e -> {
			walkers.add(new Walker(e.getX(),e.getY(),walkerSize));
		});

		menuPause.setOnAction(e -> {
			at.stop();
			menuStart.setDisable(false);
			menuPause.setDisable(true);
		});

		menuStart.setOnAction(e -> {
			at.start();
			menuStart.setDisable(true);
			menuPause.setDisable(false);
		});

		miExit.setOnAction(e ->{
			System.exit(0);
		});
	}

	//update all of the particles inside of threads.
	public void update(){
		Vector<Thread> updateThreads = new Vector<Thread>();
		//create our threads.
		for(Walker w: walkers){
			updateThreads.add(new Thread(w));
		}
		//start our threads.
		for(Thread t: updateThreads){
			t.start();
		}
		//wait for them to join back in.
		for(Thread t: updateThreads){
			try{
				t.join();
			}catch(InterruptedException ie){
				System.out.println("An update thread was interrupted.");
			}
		}

		//remove "dead" walkers so that we don't have to calculate stuff for them.
		//this is an **attempt** at some optimization.
		for(Walker d: deadWalkers){
			walkers.remove(d);
			tree.add(d);
		}
		deadWalkers.clear();

		// System.out.println(""+walkers.get(0).getCenterX());

		//add new walkers when others die.
		if(infiniteWalkers){
			for(int i=walkers.size(); i<concurrentWalkers; i++){
				walkers.add(new Walker((double)rand.nextInt((int)canvas.getWidth()),
							(double)rand.nextInt((int)canvas.getHeight()),
							walkerSize));
			}
		}
		itterations++;
	}

	//Draw all of the things.
	public void draw(GraphicsContext gc){
		//clear our canvas every frame.
		gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
		//set our walkers color.
		gc.setFill(walkerColor);
		//draw our walkers.
		for(Walker w: walkers){
			gc.fillOval(w.getCenterX(), w.getCenterY(),
					w.getRadius(),w.getRadius());
		}

		//set our tree color.
		gc.setFill(treeColor);
		//draw our tree.
		for(Walker t: tree){
			gc.fillOval(t.getCenterX(), t.getCenterY(),
					t.getRadius(),t.getRadius());
		}
	}

	//NOT IMPLEMENTED YET....if ever...
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

	//Our walker class.
	class Walker extends Circle implements Runnable{

		private boolean frozen = false;

		public Walker(double x, double y, double r){
			super(x,y,r);
		}

		//this is only used once, for the initial tree particle.
		public void setFrozen(){
			frozen=true;
		}

		//return if the walker is "dead".
		//this is basically used for some additional gating in other parts of the code.
		private boolean isFrozen(){
			return frozen;
		}

		//set our walker as frozen and mark it for removal in the Main.update().
		private void setFrozenAndChangeState(){
			frozen=true;
			if(this.frozen){
				//tree.add(walkers.remove(walkers.indexOf(this)));
				deadWalkers.add(walkers.get(walkers.indexOf((this))));
			}
		}

		//Check if it collides with a tree particle.
		private boolean collides(Walker otherWalker){
			boolean collides = false;
			//Distance calculations...wow!
			double distance = Math.sqrt(
					(this.getCenterX()-otherWalker.getCenterX())*(this.getCenterX()-otherWalker.getCenterX())+
					(this.getCenterY()-otherWalker.getCenterY())*(this.getCenterY()-otherWalker.getCenterY())
					);

			if((distance < (this.getRadius()+otherWalker.getRadius())/2.0) && otherWalker.isFrozen()){
				collides=true;
			}
			return collides;
		}

		//This is what our thread runs duh.
		@Override
		public void run(){
			this.update();
		}

		//all of our update code.
		private void update(){
			//if it isn't frozen...
			if(!this.frozen){
				//..set the center X value to something random between [-1,1] and scale it by our movementFactor.
				this.setCenterX(rand.nextBoolean()?
						((rand.nextDouble()*movementFactor)*-1)+this.getCenterX():
						(rand.nextDouble()*movementFactor)+this.getCenterX()
						);

				//..set the center Y value to something random between [-1,1] and scale it by our movementFactor.
				this.setCenterY(rand.nextBoolean()?
						((rand.nextDouble()*movementFactor)*-1)+this.getCenterY():
						(rand.nextDouble()*movementFactor)+this.getCenterY()
						);

				//if our particle is off of the canvas, loop it back to the other side.
				if(this.getCenterX() > canvas.getWidth() || this.getCenterX() < 0 ||
						this.getCenterY() > canvas.getHeight() || this.getCenterY() < 0){
					this.setCenterX((this.getCenterX()+canvas.getWidth())%canvas.getHeight());
					this.setCenterY((this.getCenterY()+canvas.getWidth())%canvas.getHeight());
				}
			}

			//Check every walker in the tree, and if it collides with our particle, then change its state.
			for(Walker t: tree){
				if(this.collides(t)){
					this.setFrozenAndChangeState();
					//Main.this.shiftTreeColor(t);
				}
			}
		}
	}
}
