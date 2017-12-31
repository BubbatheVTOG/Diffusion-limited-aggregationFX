import javafx.application.Application;
import javafx.scene.canvas.Canvas;

public class Main extends Application {

	GraphicsContext gc;
	Canvas canvas;
	AnimationTimer at;

	public void main(String[] args){
		lauch(args);
	}

	public void start(Stage window){

		Canvas canvas = new Canvas(window.getWidth(),window.getHeight());
		gc = canvas.GraphicsContext2D();

		BorderPane bpMain = new BorderPane();

		Scene scene = new Scene(bpMain,500,500);

		window.setScene(scene);
		window.show();

	}

	//update
	//clear
	//draw
}
