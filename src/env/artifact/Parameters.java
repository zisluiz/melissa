package artifact;

import java.util.ArrayList;
import java.util.List;

import model.PollenField;
import model.Position;

public class Parameters {
	/** Mechanics parameters */
	public static final long DELAY_MOVE_OPERATION = 10;
	public static final int AMMOUNT_BEE_COLLECT_POLLEN = 10;
	public static final int LARVA_AMMOUNT_HONEY_FEED = 2;
	public static final int LARVA_TOTAL_HONEY_TO_EVOLVE = 20;
	
	/** Daily parameters */
	public static final long DELAY_CHANGE_DAY = 10000;
	public static final int DAILY_POLLEN_AMMOUNT_INCREASE = 5;
	
	/** Temperature parameters */
	public static final int AVERAGE_TEMPERATURE = 25;
	public static final int EXTERNAL_TEMPERATURE_START = 25;
	public static final int TERMIC_AMPLITUDE = 10;	
	public static final int TEMPERATURE_START = 25;
	
	/** Start parameters **/
	public static final int POLLEN_START = 100;
	public static final int HONEY_START = 500;
	
	/** Hive Pollen status, in % **/
	public static final int MAX_HIVE_HONEY = 1000;
	public static final double HIVE_SUPPLY_EMPTY = 0.05;
	public static final double HIVE_SUPPLY_LOW = 0.2;
	public static final double HIVE_SUPPLY_MEDIUM = 0.6;
	public static final double HIVE_SUPPLY_HIGH = 0.9;
	
	/** Hive position and dimension **/
	public static final int HIVE_X = 649;
	public static final int HIVE_Y = 449;
	public static final int HIVE_WIDTH = 150;
	public static final int HIVE_HEIGHT = 150;
	
	/** Pollen fields colors, in % **/
	public static final double POLLEN_SUPPLY_OVER = 0;
	public static final double POLLEN_SUPPLY_LOW = 0.3;
	public static final double POLLEN_SUPPLY_MEDIUM = 0.6;
	
	/** Pollen fields to create on map **/
	public static List<PollenField> makePollenFields() {
		List<PollenField> pollenFields = new ArrayList<>();
		
		pollenFields.add(new PollenField("pollenField1", new Position(0, 0), 150, 200, 2000, 2000));
		pollenFields.add(new PollenField("pollenField2", new Position(0, 300), 60, 170, 1000, 1000));
		pollenFields.add(new PollenField("pollenField3", new Position(400, 0), 40, 40, 400, 400));
		pollenFields.add(new PollenField("pollenField4", new Position(759, 230), 40, 40, 400, 400));
		
		return pollenFields;
	}
}
