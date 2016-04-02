import flounder.devices.*;
import flounder.engine.*;
import flounder.inputs.*;
import flounder.resources.*;
import flounder.sounds.*;

import static org.lwjgl.glfw.GLFW.*;

public class MainGame extends IGame {
	private KeyButton screenshot;
	private KeyButton pauseMusic;
	private KeyButton skipMusic;

	public MainGame() {
	}

	@Override
	public void init() {
		screenshot = new KeyButton(GLFW_KEY_F10);
		pauseMusic = new KeyButton(GLFW_KEY_DOWN);
		skipMusic = new KeyButton(GLFW_KEY_LEFT, GLFW_KEY_RIGHT);

		Playlist playlist = new Playlist();
		playlist.addMusic(Sound.loadSoundInBackground(new MyFile(DeviceSound.SOUND_FOLDER, "era-of-space.wav"), 0.5f));
		playlist.addMusic(Sound.loadSoundInBackground(new MyFile(DeviceSound.SOUND_FOLDER, "spacey-ambient.wav"), 0.5f));
		ManagerDevices.getSound().getMusicPlayer().playMusicPlaylist(playlist, true, 2.25f, 5.82f);

		MainGuis.init();
	}

	@Override
	public void update() {
		MainGuis.update();

		if (screenshot.wasDown()) {
			ManagerDevices.getDisplay().screenshot();
		}

		if (pauseMusic.wasDown()) {
			ManagerDevices.getSound().getMusicPlayer().pauseTrack();
		}

		if (skipMusic.wasDown()) {
			ManagerDevices.getSound().getMusicPlayer().skipTrack();
		}
	}

	@Override
	public void dispose() {

	}
}
