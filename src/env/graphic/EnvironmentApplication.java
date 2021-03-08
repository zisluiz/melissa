package graphic;

import java.util.ArrayList;
import java.util.List;

import graphic.model.HiveGraphic;
import graphic.model.PollenFieldGraphic;
import javafx.application.Application;
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
import model.PollenField;
import model.enumeration.HoneySupply;

public class EnvironmentApplication extends Application {
	public static EnvironmentApplication instance = null;
	private int width;
	private int height;
	private Group ground;

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
	private Color colorBlack = Color.web("black", 1);
	private Color colorRed = Color.web("red", 1);
	private Color colorOrange = Color.web("rgb(255,126,38)", 1);
	private Color colorYellowStrong = Color.web("rgb(255,202,14)", 1);
	private Color colorGreen = Color.web("green", 1);
	private Color colorYellow = Color.web("yellow", 1);
	
	private Font font = new Font(14);
	private Text time;
	private Text day;
	private HiveGraphic hiveGraphic;
	
	private Text extTemp;
    private BooleanProperty stop = new SimpleBooleanProperty(false);
    private int second = 0;
    private int minute = 0;
	public static Color colorBeeFeeder;
	public static Color colorBeeSentinel;
	public static Color colorBeeWorker;
	public static Color colorBeeQueen;
	
	public EnvironmentApplication() {
		System.out.println("Creating BeeEnvironment");
		setStartUpTest(this);
	}

    public static EnvironmentApplication getInstance() {
        return instance;
    }

    public static void setStartUpTest(EnvironmentApplication newInstance) {
    	instance = newInstance;
    }

    @Override
    public void start(Stage stage) throws Exception {
    	System.out.println("Starting graphics");

    	Group root = new Group();
		this.width = Integer.valueOf(getParameters().getRaw().get(0));
		this.height = Integer.valueOf(getParameters().getRaw().get(1));
		Scene scene = new Scene(root,  width+230, height, Color.BLACK);
		stage.setScene(scene);
		
		root.getChildren().add(createHive());
		root.getChildren().add(createPollenFields(Environment.getInstance().getPollenFields()));
		
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
		
		day = new Text(780, 30, "0");
		day.setFill(colorWhite);
		day.setFont(font);
		root.getChildren().add(day);	
		
		Text labelExtTemp = new Text(690, 44, "Temperature: ");
		labelExtTemp.setFill(colorWhite);
		labelExtTemp.setFont(font);
		root.getChildren().add(labelExtTemp);
		
		extTemp = new Text(780, 44, "25℃");
		extTemp.setFill(colorWhite);
		extTemp.setFont(font);
		root.getChildren().add(extTemp);		
		
		Rectangle spaceLabel = createRectangle(20, 600, colorWhite, 810, 0);
		root.getChildren().add(spaceLabel);		
		
		root.getChildren().add(createLabels());
		
		ground = new Group();
		root.getChildren().add(ground);
		
        stage.show();
        
        startTimer();
    }

	private Group createLabels() {
		Group group = new Group();
		Image labels = new Image("file:labels.png");
		ImageView labelsView = new ImageView(labels);
		labelsView.setLayoutX(831);
		labelsView.setLayoutY(20);
		group.getChildren().add(labelsView);
		
		return group;
	}

	private Group createPollenFields(List<PollenField> pollenFields) {
		Group group = new Group();
		PollenFieldResolver pollenFieldResolver = Environment.getInstance().getPollenFieldResolver();
		List<PollenFieldGraphic> pollenFieldGraphics = new ArrayList<>();
		
		for (PollenField pollenField : pollenFields) {
			Rectangle foodSource1 = createRectangle(pollenField.getWidth(), pollenField.getHeight(), pollenField.getStatus().getColor(), pollenField.getPosition().getX(), pollenField.getPosition().getY());
			group.getChildren().add(foodSource1);
			
			PollenFieldGraphic pollenFieldGraphic = new PollenFieldGraphic(foodSource1, pollenField);
			pollenFieldResolver.createPollenField(pollenField.getId(), pollenFieldGraphic);
			pollenFieldGraphics.add(pollenFieldGraphic);
			
		}
		Environment.getInstance().getMapResolver().addContainers(hiveGraphic, pollenFieldGraphics);
		
		return group;
	}

	private Group createHive() {
		Group group = new Group();
		Rectangle hive = createRectangle(artifact.Parameters.HIVE_WIDTH, artifact.Parameters.HIVE_HEIGHT, colorYellow, artifact.Parameters.HIVE_X, artifact.Parameters.HIVE_Y);
		group.getChildren().add(hive);
		hiveGraphic = new HiveGraphic(hive, "hive");
		
		group.getChildren().add(createHiveInformation());
		return group;
	}
	
	private Group createHiveInformation() {
		Group group = new Group();
//		Rectangle rectangleInfo = createRectangle(70, 130, colorBlack, 726, 460);
//		group.getChildren().add(rectangleInfo);
		int newX = 185;
		
		Text labelTemp = new Text(731+newX, 473, "Temp: ");
		labelTemp.setFill(colorWhite);
		labelTemp.setFont(font);
		labelNumberTemp = new Text(771+newX, 473, "0");
		labelNumberTemp.setFill(colorWhite);
		labelNumberTemp.setFont(font);
		Text labelTemp2 = new Text(785+newX, 473, "℃");
		labelTemp2.setFill(colorWhite);
		labelTemp2.setFont(font);
		group.getChildren().add(labelTemp);		
		group.getChildren().add(labelNumberTemp);		
		group.getChildren().add(labelTemp2);		
		
		colorBeeQueen = Color.web("rgb(158,76,158)", 1);
		Circle circleBeeQueen = new Circle(4, colorBeeQueen);
		circleBeeQueen.setLayoutX(736+newX);
		circleBeeQueen.setLayoutY(486);		
		group.getChildren().add(circleBeeQueen);	
		labelNumberBeeQueen = new Text(746+newX, 490, "0");
		labelNumberBeeQueen.setFill(colorWhite);
		labelNumberBeeQueen.setFont(font);	
		group.getChildren().add(labelNumberBeeQueen);	
		
		colorBeeFeeder = Color.web("rgb(156,207,224)", 1);
		Circle circleBeeFeeder = new Circle(4, colorBeeFeeder);
		circleBeeFeeder.setLayoutX(736+newX);
		circleBeeFeeder.setLayoutY(506);		
		group.getChildren().add(circleBeeFeeder);	
		labelNumberBeeFeeder = new Text(746+newX, 510, "0");
		labelNumberBeeFeeder.setFill(colorWhite);
		labelNumberBeeFeeder.setFont(font);	
		group.getChildren().add(labelNumberBeeFeeder);	
		
		colorBeeSentinel = Color.web("rgb(60,61,205)", 1);
		Circle circleBeeSentinel = new Circle(4, colorBeeSentinel);
		circleBeeSentinel.setLayoutX(736+newX);
		circleBeeSentinel.setLayoutY(526);		
		group.getChildren().add(circleBeeSentinel);	
		labelNumberBeeSentinel = new Text(746+newX, 530, "0");
		labelNumberBeeSentinel.setFill(colorWhite);
		labelNumberBeeSentinel.setFont(font);	
		group.getChildren().add(labelNumberBeeSentinel);		
		
		colorBeeWorker = Color.web("rgb(255,128,35)", 1);
		Circle circleBeeWorker = new Circle(4, colorBeeWorker);
		circleBeeWorker.setLayoutX(736+newX);
		circleBeeWorker.setLayoutY(546);		
		group.getChildren().add(circleBeeWorker);	
		labelNumberBeeWorker = new Text(746+newX, 550, "0");
		labelNumberBeeWorker.setFill(colorWhite);
		labelNumberBeeWorker.setFont(font);	
		group.getChildren().add(labelNumberBeeWorker);	
		
		Circle circleBeeLarva = new Circle(4, Color.web("rgb(193,193,193)", 1));
		circleBeeLarva.setLayoutX(736+newX);
		circleBeeLarva.setLayoutY(566);		
		group.getChildren().add(circleBeeLarva);	
		labelNumberBeeLarva = new Text(746+newX, 570, "0");
		labelNumberBeeLarva.setFill(colorWhite);
		labelNumberBeeLarva.setFont(font);	
		group.getChildren().add(labelNumberBeeLarva);
		

		Rectangle rectangleHoney = createRectangle(25, 48,colorBlack, 666+newX, 460);
		rectangleHoney.setStrokeType(StrokeType.OUTSIDE);
		Color greyBorder = Color.web("rgb(137,137,137)", 1);
		rectangleHoney.setStroke(greyBorder);
		rectangleHoney.setStrokeWidth(1);
		group.getChildren().add(rectangleHoney);	
		
		Line line1Honey = new Line(666+newX, 472, 691+newX, 472);
		line1Honey.setStrokeType(StrokeType.OUTSIDE);
		line1Honey.setStroke(greyBorder);
		line1Honey.setStrokeWidth(1);		
		group.getChildren().add(line1Honey);	

		Line line2Honey = new Line(666+newX, 484, 691+newX, 484);
		line2Honey.setStrokeType(StrokeType.OUTSIDE);
		line2Honey.setStroke(greyBorder);
		line2Honey.setStrokeWidth(1);		
		group.getChildren().add(line2Honey);	
		
		Line line3Honey = new Line(666+newX, 496, 691+newX, 496);
		line3Honey.setStrokeType(StrokeType.OUTSIDE);
		line3Honey.setStroke(greyBorder);
		line3Honey.setStrokeWidth(1);		
		group.getChildren().add(line3Honey);	
		
		rectangleHoneyGreen = createRectangle(25, 12, colorBlack, 666+newX, 460);
		group.getChildren().add(rectangleHoneyGreen);	
		
		rectangleHoneyYellow = createRectangle(25, 10, colorBlack, 666+newX, 473);
		group.getChildren().add(rectangleHoneyYellow);
		
		rectangleHoneyOrange = createRectangle(25, 10, colorBlack, 666+newX, 485);
		group.getChildren().add(rectangleHoneyOrange);
		
		rectangleHoneyRed = createRectangle(25, 11, colorBlack, 666+newX, 497);
		group.getChildren().add(rectangleHoneyRed);	
		
		return group;
	}

	private Rectangle createRectangle(int width, int height, Color color, int x, int y) {
		Rectangle rectangle = new Rectangle(width, height, color);
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
	
	public void updateIntTemperature(int ammount) {
		labelNumberTemp.setText(ammount+"℃");
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
	
	public void updateHoneyStatus(HoneySupply newStatus) {
		
		switch (newStatus) {
		case EMPTY:
			rectangleHoneyRed.setFill(colorBlack);
			rectangleHoneyOrange.setFill(colorBlack);
			rectangleHoneyYellow.setFill(colorBlack);
			rectangleHoneyGreen.setFill(colorBlack);
			break;
		case LOW:
			rectangleHoneyRed.setFill(colorRed);
			rectangleHoneyOrange.setFill(colorBlack);
			rectangleHoneyYellow.setFill(colorBlack);
			rectangleHoneyGreen.setFill(colorBlack);
			break;
		case MEDIUM:
			rectangleHoneyRed.setFill(colorRed);
			rectangleHoneyOrange.setFill(colorOrange);
			rectangleHoneyYellow.setFill(colorBlack);
			rectangleHoneyGreen.setFill(colorBlack);
			break;
		case HIGH:
			rectangleHoneyRed.setFill(colorRed);
			rectangleHoneyOrange.setFill(colorOrange);
			rectangleHoneyYellow.setFill(colorYellowStrong);
			rectangleHoneyGreen.setFill(colorBlack);
			break;
		case FULL:
			rectangleHoneyRed.setFill(colorRed);
			rectangleHoneyOrange.setFill(colorOrange);
			rectangleHoneyYellow.setFill(colorYellowStrong);
			rectangleHoneyGreen.setFill(colorGreen);
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

                    JavaFXConcurrent.getInstance().addUpdate(new Runnable() {
						@Override
						public void run() {
							time.setText(min + ":" + sec);
						}
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

	public void updateExtTemp(int newTemp) {
		extTemp.setText(newTemp+"℃");
	}

	public void removeBee(Node node) {
		try {
			ground.getChildren().remove(node);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addBee(Node node) {
		ground.getChildren().add(node);
	}

	public void updatePollenFieldStatus(String pollenFieldId) {
		PollenFieldGraphic pollenField = Environment.getInstance().getPollenFieldResolver().getPollenField(pollenFieldId);
		pollenField.getRectangle().setFill(pollenField.getPollenField().getStatus().getColor());
	}
}