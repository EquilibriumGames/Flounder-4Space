package game.players;

import flounder.engine.*;
import flounder.inputs.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import game.options.*;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerFocus implements IPlayer {
	private static final float SPEED_BOOST_SCALE = 2.75f;
	private static final float FRONT_SPEED = 30;
	private static final float UP_SPEED = 20;
	private static final float SIDE_SPEED = 30;
	private static final float ROTATE_SPEED = 60;

	private IAxis inputForward;
	private IAxis inputVertical;
	private IAxis inputSide;
	private KeyButton inputSpeedBoost;

	private Vector3f velMove;
	private Vector3f velRoat;

	private Vector3f position;
	private Vector3f rotation;

	//private Entity focusEntity;

	@Override
	public void init() {
		IButton leftKeyButtons = new KeyButton(GLFW_KEY_A, GLFW_KEY_LEFT);
		IButton rightKeyButtons = new KeyButton(GLFW_KEY_D, GLFW_KEY_RIGHT);
		IButton forwardsKeyButtons = new KeyButton(GLFW_KEY_W, GLFW_KEY_UP);
		IButton backwardsKeyButtons = new KeyButton(GLFW_KEY_S, GLFW_KEY_DOWN);
		IButton upKeyButtons = new KeyButton(GLFW_KEY_SPACE);
		IButton downKeyButtons = new KeyButton(GLFW_KEY_LEFT_CONTROL);

		this.inputForward = new CompoundAxis(new ButtonAxis(backwardsKeyButtons, forwardsKeyButtons), new JoystickAxis(OptionsControls.JOYSTICK_PORT, OptionsControls.JOYSTICK_AXIS_Y));
		this.inputVertical = new CompoundAxis(new ButtonAxis(downKeyButtons, upKeyButtons), new ButtonAxis(new JoystickButton(OptionsControls.JOYSTICK_PORT, OptionsControls.JOYSTICK_CAMERA_DOWN), new JoystickButton(OptionsControls.JOYSTICK_PORT, OptionsControls.JOYSTICK_CAMERA_UP)));
		this.inputSide = new CompoundAxis(new ButtonAxis(leftKeyButtons, rightKeyButtons), new JoystickAxis(OptionsControls.JOYSTICK_PORT, OptionsControls.JOYSTICK_AXIS_X));
		this.inputSpeedBoost = new KeyButton(GLFW_KEY_LEFT_SHIFT);

		this.velMove = new Vector3f(0, 0, 0);
		this.velRoat = new Vector3f(0, 0, 0);

		this.position = new Vector3f(0, 0, 0);
		this.rotation = new Vector3f(0, 0, 0);

		//focusEntity = EntityLoader.load("tinyship").createEntity(Environment.getEntities(), new Vector3f(position), new Vector3f(rotation));
		//((ComponentModel) focusEntity.getComponent(ComponentModel.ID)).setScale(0.1f);
	}

	@Override
	public void update(boolean paused) {
		if (!paused) {
			float speedBoost = inputSpeedBoost.isDown() ? SPEED_BOOST_SCALE : 1.0f;
			float distance = speedBoost * FRONT_SPEED * inputForward.getAmount() * FlounderEngine.getDelta();

			velMove.x = (float) (distance * Math.sin(Math.toRadians(rotation.getY())));
			velMove.z = (float) (distance * Math.cos(Math.toRadians(rotation.getY())));
			velMove.y = speedBoost * -UP_SPEED * inputVertical.getAmount() * FlounderEngine.getDelta();

			velRoat.y = speedBoost * -ROTATE_SPEED * FlounderEngine.getDelta() * Maths.deadband(0.05f, inputSide.getAmount());

			//if (Math.abs(velMove.x) > 0 || Math.abs(velMove.y) > 0 || Math.abs(velMove.z) > 0 || Math.abs(velRoat.y) > 0) {
			//	focusEntity.move(velMove, velRoat);
			//	position.set(focusEntity.getPosition());
			//	rotation.set(focusEntity.getRotation());
			//}

			Vector3f.add(position, velMove, position);
			Vector3f.add(rotation, velRoat, rotation);
		}
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public Vector3f getRotation() {
		return rotation;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	@Override
	public void dispose() {
		//	focusEntity.remove();
	}
}