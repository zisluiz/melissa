package graphic;

import java.util.concurrent.CountDownLatch;

import graphic.model.HiveGraphic;
import graphic.model.PollenFieldGraphic;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Hive;
import model.enumeration.HoneySupply;
import model.enumeration.PollenSupply;

public class EnvironmentApplication extends Application {
	public static final CountDownLatch latch = new CountDownLatch(1);
	public static EnvironmentApplication instance = null;
	private int width;
	private int height;
	private Group root;

	private Text labelNumberTemp;
	private Text labelNumberBeeQueen;
	private Text labelNumberBeeFeeder;
	private Text labelNumberBeeSentinel;
	private Text labelNumberBeeWorker;
	private Text labelNumberBeeLarva;
	private Rectangle rectangleHoneyGreen;
	private Rectangle rectangleHoneyYellow;
	private Rectangle rectangleHoneyOrange;
	private Rectangle rectangleHoneyRed;
	private Color colorWhite = Color.web("white", 1);
	private Font font = new Font(14);
	private Text time;
	private Text day;
	private HiveGraphic hiveGraphic;
	
    private BooleanProperty stop = new SimpleBooleanProperty(false);
    private int second = 0;
    private int minute = 0;
	
	public EnvironmentApplication() {
		System.out.println("Creating BeeEnvironment");
		setStartUpTest(this);
	}

    public static EnvironmentApplication waitForStartUpTest() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public static void setStartUpTest(EnvironmentApplication newInstance) {
    	instance = newInstance;
        latch.countDown();
    }

    @Override
    public void start(Stage stage) throws Exception {
    	System.out.println("Starting graphics");
    	
		root = new Group();
		this.width = Integer.valueOf(getParameters().getRaw().get(0));
		this.height = Integer.valueOf(getParameters().getRaw().get(1));
		Scene scene = new Scene(root,  width+230, height, Color.BLACK);
		stage.setScene(scene);
		
		createHive();
		createPollenFields();
		
		Text labelTime = new Text(690, 16, "Tempo: ");
		labelTime.setFill(colorWhite);
		labelTime.setFont(font);
		root.getChildren().add(labelTime);
		
		time = new Text(740, 16, "0");
		time.setFill(colorWhite);
		time.setFont(font);
		root.getChildren().add(time);
		
		Text labelDay = new Text(690, 30, "Dia: ");
		labelDay.setFill(colorWhite);
		labelDay.setFont(font);
		root.getChildren().add(labelDay);
		
		day = new Text(740, 30, "0");
		day.setFill(colorWhite);
		day.setFont(font);
		root.getChildren().add(day);		
		
		Rectangle spaceLabel = createRectangle(20, 600, "white", 800, 0);
		root.getChildren().add(spaceLabel);		
		
		createLabels();
		
        stage.show();
        
        startTimer();
    }

	private void createLabels() {
		Image labels = new Image("file:labels.png");
		ImageView labelsView = new ImageView(labels);
		labelsView.setLayoutX(828);
		labelsView.setLayoutY(20);
		root.getChildren().add(labelsView);
	}

	private void createPollenFields() {
		Rectangle foodSource1 = createRectangle(150, 200, PollenSupply.HIGH.getColor(), 0, 0);
		root.getChildren().add(foodSource1);
		
		Rectangle foodSource2 = createRectangle(60, 170, PollenSupply.HIGH.getColor(), 0, 300);
		root.getChildren().add(foodSource2);
		Rectangle foodSource3 = createRectangle(40, 40, PollenSupply.HIGH.getColor(), 400, 0);
		root.getChildren().add(foodSource3);
		Rectangle foodSource4 = createRectangle(40, 40, PollenSupply.HIGH.getColor(), 759, 230);
		root.getChildren().add(foodSource4);
		
		PollenFieldGraphic pollenField1 = new PollenFieldGraphic(foodSource1, "pollenField1", 2000, 2000);
		PollenFieldGraphic pollenField2 = new PollenFieldGraphic(foodSource2, "pollenField2", 1000, 1000);
		PollenFieldGraphic pollenField3 = new PollenFieldGraphic(foodSource3, "pollenField3", 400, 400);
		PollenFieldGraphic pollenField4 = new PollenFieldGraphic(foodSource4, "pollenField4", 400, 400);
		
		PollenFieldResolver pollenFieldResolver = Environment.getInstance().getPollenFieldResolver();
		pollenFieldResolver.createPollenField("pollenField1", pollenField1);
		pollenFieldResolver.createPollenField("pollenField2", pollenField2);
		pollenFieldResolver.createPollenField("pollenField3", pollenField3);
		pollenFieldResolver.createPollenField("pollenField4", pollenField4);
		Environment.getInstance().getMapResolver().addContainers(hiveGraphic, pollenField1, pollenField2, pollenField3, pollenField4);
	}

	private void createHive() {
		Rectangle hive = createRectangle(150, 150, "yellow", 649, 449);
		root.getChildren().add(hive);
		hiveGraphic = new HiveGraphic(hive, "hive");
		createHiveInformation();
	}
	
	private void createHiveInformation() {
		Rectangle rectangleInfo = createRectangle(70, 130, "black", 726, 460);
		root.getChildren().add(rectangleInfo);		
		
		Text labelTemp = new Text(731, 473, "Temp: ");
		labelTemp.setFill(colorWhite);
		labelTemp.setFont(font);
		labelNumberTemp = new Text(771, 473, "0");
		labelNumberTemp.setFill(colorWhite);
		labelNumberTemp.setFont(font);
		Text labelTemp2 = new Text(785, 473, "º");
		labelTemp2.setFill(colorWhite);
		labelTemp2.setFont(font);
		root.getChildren().add(labelTemp);		
		root.getChildren().add(labelNumberTemp);		
		root.getChildren().add(labelTemp2);		
		
		Circle circleBeeQueen = new Circle(4, Color.web("rgb(158,76,158)", 1));
		circleBeeQueen.setLayoutX(736);
		circleBeeQueen.setLayoutY(486);		
		root.getChildren().add(circleBeeQueen);	
		labelNumberBeeQueen = new Text(746, 490, "0");
		labelNumberBeeQueen.setFill(colorWhite);
		labelNumberBeeQueen.setFont(font);	
		root.getChildren().add(labelNumberBeeQueen);	
		
		Circle circleBeeFeeder = new Circle(4, Color.web("rgb(156,207,224)", 1));
		circleBeeFeeder.setLayoutX(736);
		circleBeeFeeder.setLayoutY(506);		
		root.getChildren().add(circleBeeFeeder);	
		labelNumberBeeFeeder = new Text(746, 510, "0");
		labelNumberBeeFeeder.setFill(colorWhite);
		labelNumberBeeFeeder.setFont(font);	
		root.getChildren().add(labelNumberBeeFeeder);	
		
		Circle circleBeeSentinel = new Circle(4, Color.web("rgb(60,61,205)", 1));
		circleBeeSentinel.setLayoutX(736);
		circleBeeSentinel.setLayoutY(526);		
		root.getChildren().add(circleBeeSentinel);	
		labelNumberBeeSentinel = new Text(746, 530, "0");
		labelNumberBeeSentinel.setFill(colorWhite);
		labelNumberBeeSentinel.setFont(font);	
		root.getChildren().add(labelNumberBeeSentinel);		
		
		Circle circleBeeWorker = new Circle(4, Color.web("rgb(255,128,35)", 1));
		circleBeeWorker.setLayoutX(736);
		circleBeeWorker.setLayoutY(546);		
		root.getChildren().add(circleBeeWorker);	
		labelNumberBeeWorker = new Text(746, 550, "0");
		labelNumberBeeWorker.setFill(colorWhite);
		labelNumberBeeWorker.setFont(font);	
		root.getChildren().add(labelNumberBeeWorker);	
		
		Circle circleBeeLarva = new Circle(4, Color.web("rgb(193,193,193)", 1));
		circleBeeLarva.setLayoutX(736);
		circleBeeLarva.setLayoutY(566);		
		root.getChildren().add(circleBeeLarva);	
		labelNumberBeeLarva = new Text(746, 570, "0");
		labelNumberBeeLarva.setFill(colorWhite);
		labelNumberBeeLarva.setFont(font);	
		root.getChildren().add(labelNumberBeeLarva);
		

		Rectangle rectangleHoney = createRectangle(25, 48, "black", 666, 460);
		rectangleHoney.setStrokeType(StrokeType.OUTSIDE);
		Color greyBorder = Color.web("rgb(137,137,137)", 1);
		rectangleHoney.setStroke(greyBorder);
		rectangleHoney.setStrokeWidth(1);
		root.getChildren().add(rectangleHoney);	
		
		Line line1Honey = new Line(666, 472, 691, 472);
		line1Honey.setStrokeType(StrokeType.OUTSIDE);
		line1Honey.setStroke(greyBorder);
		line1Honey.setStrokeWidth(1);		
		root.getChildren().add(line1Honey);	

		Line line2Honey = new Line(666, 484, 691, 484);
		line2Honey.setStrokeType(StrokeType.OUTSIDE);
		line2Honey.setStroke(greyBorder);
		line2Honey.setStrokeWidth(1);		
		root.getChildren().add(line2Honey);	
		
		Line line3Honey = new Line(666, 496, 691, 496);
		line3Honey.setStrokeType(StrokeType.OUTSIDE);
		line3Honey.setStroke(greyBorder);
		line3Honey.setStrokeWidth(1);		
		root.getChildren().add(line3Honey);	
		
		rectangleHoneyGreen = createRectangle(25, 12, "green", 666, 460);
//		root.getChildren().add(rectangleHoneyGreen);	
		
		rectangleHoneyYellow = createRectangle(25, 10, "rgb(255,202,14)", 666, 473);
//		root.getChildren().add(rectangleHoneyYellow);
		
		rectangleHoneyOrange = createRectangle(25, 10, "rgb(255,126,38)", 666, 485);
//		root.getChildren().add(rectangleHoneyOrange);
		
		rectangleHoneyRed = createRectangle(25, 11, "red", 666, 497);
//		root.getChildren().add(rectangleHoneyRed);			
	}

	private Rectangle createRectangle(int width, int height, String color, int x, int y) {
		Rectangle rectangle = new Rectangle(width, height, Color.web(color, 1));
		rectangle.setLayoutX(x);
		rectangle.setLayoutY(y);
		return rectangle;
	}

	public void run(String args[]) {
		launch(args);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}

	public void updateLarvaCount() {
		labelNumberBeeLarva.setText(Hive.getInstance().getLarvas().size()+"");
	}

	public void updateBeeCount() {
		labelNumberBeeQueen.setText(Hive.getInstance().getQueens().size()+"");
		labelNumberBeeFeeder.setText(Hive.getInstance().getFeeders().size()+"");
		labelNumberBeeSentinel.setText(Hive.getInstance().getSentinels().size()+"");
		labelNumberBeeWorker.setText(Hive.getInstance().getWorkers().size()+"");
	}
	
	public void updateHoneyStatus() {
		HoneySupply status = Hive.getInstance().getStatus();
		root.getChildren().removeAll(rectangleHoneyGreen, rectangleHoneyOrange, rectangleHoneyRed, rectangleHoneyYellow);
		
		switch (status) {
		case EMPTY:
			break;
		case LOW:
			root.getChildren().add(rectangleHoneyRed);
			break;
		case MEDIUM:
			root.getChildren().addAll(rectangleHoneyOrange, rectangleHoneyRed);
			break;
		case HIGH:
			root.getChildren().addAll(rectangleHoneyOrange, rectangleHoneyRed, rectangleHoneyYellow);
			break;
		case FULL:
			root.getChildren().addAll(rectangleHoneyGreen, rectangleHoneyOrange, rectangleHoneyRed, rectangleHoneyYellow);
			break;			
		}
	}
	
	private void startTimer() {
        Task t = new Task() {

            @Override
            protected Object call() throws Exception {
                while (!stop.get()) {
                    second++;

                    if (second == 60) {
                        minute++;
                        second = 0;
                    }

                    String min = minute <= 9 ? "0" + minute : minute + "";
                    String sec = second <= 9 ? "0" + second : second + "";

                    Platform.runLater(() -> {
                        time.setText(min + ":" + sec);
                    });
                    Thread.sleep(1000);

                }
                return null;
            }
        };
        new Thread(t).start();

    }	

	public void updateDay(int newDay) {
		day.setText(newDay+"");
	}

	public void removeNode(Node node) {
		try {
			root.getChildren().remove(node);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addNode(Node node) {
		root.getChildren().add(node);
	}

	public void updatePollenFieldStatus(String pollenFieldId) {
		PollenFieldGraphic pollenField = Environment.getInstance().getPollenFieldResolver().getPollenField(pollenFieldId);
		pollenField.getRectangle().setFill(Color.web(pollenField.getPollenField().getStatus().getColor(), 1));
	}
}