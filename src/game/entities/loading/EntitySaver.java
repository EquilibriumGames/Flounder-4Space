package game.entities.loading;

import flounder.engine.*;
import flounder.helpers.*;
import game.entities.*;

import java.io.*;
import java.util.*;

/**
 * Class capable of saving engine.entities to a .entity file.
 */
public class EntitySaver {
	/**
	 * Saves the entity components to a .entity file.
	 *
	 * @param entity The entity to save.
	 * @param name The nave for the entity and file.
	 */
	public static void save(Entity entity, String name) {
		try {
			// Creates the save folder
			File saveFolder = new File("entities");

			if (!saveFolder.exists()) {
				saveFolder.mkdir();
			}

			// The save file and the writers.
			File saveFile = new File(saveFolder.getPath() + "/" + name + ".entity");
			saveFile.createNewFile();
			FileWriter fileWriter = new FileWriter(saveFile);
			FlounderFileWriter flounderFileWriter = new FlounderFileWriter(fileWriter);

			// Date and save info.
			String savedDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "." + Calendar.getInstance().get(Calendar.YEAR) + " - " + Calendar.getInstance().get(Calendar.HOUR) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
			FlounderEngine.getLogger().log("Entity " + name + " is being saved at: " + savedDate);
			flounderFileWriter.addComment("Date Generated: " + savedDate, "Created By: " + System.getProperty("user.classpath"));

			// Entity General Data.
			flounderFileWriter.beginNewSegment("EntityData");
			{
				flounderFileWriter.writeSegmentData("Name: " + name + ";", true);
			}
			flounderFileWriter.endSegment(false);

			// Components.
			flounderFileWriter.beginNewSegment("Components");
			{
				for (int i = 0; i < entity.getComponents().size(); i++) {
					flounderFileWriter.beginNewSegment(entity.getComponents().get(i).getClass().getName());

					Pair<String[], EntitySaverFunction[]> saveableValues = entity.getComponents().get(i).getSavableValues();

					// Individual data components.
					for (String s : saveableValues.getFirst()) {
						flounderFileWriter.writeSegmentData(s + ";", true);
					}

					// Blank area between both sections.
					if (saveableValues.getSecond().length > 0) {
						flounderFileWriter.enterBlankLine();
						flounderFileWriter.enterBlankLine();
					}

					// Segmented data components.
					int fi = 0;

					for (EntitySaverFunction f : saveableValues.getSecond()) {
						flounderFileWriter.beginNewSegment(f.getSectionName());
						{
							f.writeIntoSection(flounderFileWriter);
						}
						flounderFileWriter.endSegment(fi++ == saveableValues.getSecond().length - 1);
					}

					flounderFileWriter.endSegment(i == entity.getComponents().size() - 1);
				}
			}
			flounderFileWriter.endSegment(false);

			// Closes the file for writing.
			fileWriter.close();
		} catch (IOException e) {
			FlounderEngine.getLogger().error("File saver for entity " + name + " did not execute successfully!");
			FlounderEngine.getLogger().exception(e);
		}
	}
}
