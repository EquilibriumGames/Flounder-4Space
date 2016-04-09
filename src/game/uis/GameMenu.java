package game.uis;

import flounder.engine.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.maths.*;
import flounder.visual.*;

import java.util.*;

public class GameMenu extends GuiComponent {
	public static final Colour TEXT_COLOUR = new Colour(0.85f, 0.85f, 0.85f);
	private static final float TITLE_FONT_SIZE = 4.0f;
	private static final float MAIN_MENU_Y_POS = 0.25f;
	private static final float MAIN_MENU_Y_SIZE = 0.6f;

	private static final float SLIDE_TIME = 0.7f;
	private final MainMenu mainMenu;
	private ValueDriver secondaryDriver = new ConstantDriver(0);
	private ValueDriver mainDriver = new ConstantDriver(0);
	private boolean displayed = true;
	private boolean closeSecondary = false;
	private GuiComponent secondaryScreen;

	public GameMenu(final GameMenuBackground superMenu) {
		mainMenu = new MainMenu(superMenu, this);
		Text text = Text.newText("Flounder 2.0").center().setFontSize(TITLE_FONT_SIZE).create();
		text.setColour(TEXT_COLOUR);
		text.setBorderColour(TEXT_COLOUR.r, TEXT_COLOUR.g, TEXT_COLOUR.b);
		text.setGlowing(new SinWaveDriver(0.075f, 0.150f, 2.320f));
		super.addText(text, 0, 0, 1);
		addComponent(mainMenu, 0, MAIN_MENU_Y_POS, 1, MAIN_MENU_Y_SIZE);
	}

	protected void display(final boolean display) {
		if (display) {
			show(true);
			mainDriver = new SlideDriver(getRelativeX(), 0, SLIDE_TIME); // SinWaveDriver for cool effect :p
			displayed = true;
		} else {
			mainDriver = new SlideDriver(getRelativeX(), 1, SLIDE_TIME);
			displayed = false;
		}
	}

	@Override
	public void show(final boolean visible) {
		super.show(visible);

		if (!visible) {
			removeSecondaryScreen();
			secondaryDriver = new ConstantDriver(0);
		}
	}

	@Override
	protected void updateSelf() {
		float mainValue = mainDriver.update(FlounderEngine.getDelta());
		float value = secondaryDriver.update(FlounderEngine.getDelta());
		mainMenu.setRelativeX(value);

		if (secondaryScreen != null) {
			secondaryScreen.setRelativeX(value - 1);
		}

		super.setRelativeX(mainValue);

		if (!displayed) {
			if (mainValue >= 1) {
				show(false);
			}
		}

		if (closeSecondary) {
			if (secondaryScreen.getRelativeX() <= -1) {
				removeSecondaryScreen();
				closeSecondary = false;
			}
		}
	}

	@Override
	protected void getGuiTextures(final List<GuiTexture> guiTextures) {
	}

	private void removeSecondaryScreen() {
		if (secondaryScreen != null) {
			removeComponent(secondaryScreen);
			secondaryScreen = null;
		}
	}

	protected boolean isDisplayed() {
		return displayed;
	}

	protected void setNewSecondaryScreen(final GuiComponent secondScreen) {
		removeSecondaryScreen();
		secondaryScreen = secondScreen;
		addComponent(secondScreen, mainMenu.getRelativeX() - 1, MAIN_MENU_Y_POS, 1, MAIN_MENU_Y_SIZE);
		secondaryDriver = new SlideDriver(mainMenu.getRelativeX(), 1, SLIDE_TIME);
	}

	protected void closeSecondaryScreen() {
		secondaryDriver = new SlideDriver(mainMenu.getRelativeX(), 0, SLIDE_TIME);
		closeSecondary = true;
	}
}