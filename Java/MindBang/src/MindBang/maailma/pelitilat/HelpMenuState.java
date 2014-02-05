package MindBang.maailma.pelitilat;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import MindBang.MindBangMain;
import MindBang.maailma.Efefktit.Rajahdys;

/**
 * Helpmenu -tila. Tassa tilassa kayttajalle annetaan pelin pelaamiseen
 * liittyvia ohjeita
 * **/

public class HelpMenuState extends BasicGameState {

	private Image shooting;
	private Image energy;
	private Image buttons;
	private int menuX = 100;
	private int menuY = 100;
	private int stateID = 0;
	// Totuusmuuttujat onko hiiri jonkun valikkokuvan ylla
	boolean insideShooting = false;
	boolean insideEnergy = false;
	boolean insideButtons = false;
	private Image shootingohje;
	private Image energyohje;
	private Image buttonsohje;
	private Rajahdys rajahdys;

	/**
	 * Luokan luontimetodissa kerrotaan pelitilalle sen tunnus numero
	 * 
	 * @param stateID
	 *            = pelitilan tunnusnumero
	 * **/

	public HelpMenuState(int stateID) {
		this.stateID = stateID;

	}

	/**
	 * Palauttaa pelitilan tunnusnumeron
	 * 
	 * @return pelitilan tunnusnumero
	 * **/
	public int getID() {
		return stateID;
	}

	/**
	 * Metodi joka ajetaan kun pelitilaan tullaan. Tassa asetetaan hiiren kuvake
	 * nakyvaksi.
	 * **/
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		container.setMouseGrabbed(false);
		rajahdys = new Rajahdys();
	}

	/**
	 * Ladataan tilan taustakuva.
	 * **/
	public void init(GameContainer gc, StateBasedGame arg1)
			throws SlickException {
		shooting = new Image("res/valikot/shooting.png");
		energy = new Image("res/valikot/energy.png");
		buttons = new Image("res/valikot/buttons.png");
		shootingohje = new Image("res/valikot/shootingohje.png");
		energyohje = new Image("res/valikot/energyohje.png");
		buttonsohje = new Image("res/valikot/buttonsohje.png");
	}

	/**
	 * Piirretaan taustakuva.
	 * **/
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		// g.drawImage(background, 0, 0);
		shooting.draw(menuX, menuY);
		if (insideShooting)
			shootingohje.draw(menuX + 250, 0);
		energy.draw(menuX, menuY + 250);
		if (insideEnergy)
			energyohje.draw(menuX + 250, 0);
		buttons.draw(menuX, menuY + 500);
		if (insideButtons)
			buttonsohje.draw(menuX + 250, 0);
		
		rajahdys.render(gc, g);
	}

	/**
	 * Luokan logiikan paivitysmetodi. Mikali ESC tai hiiren vasenta painiketta
	 * painetaan. Siirrytaan pelin paavalikko -tilaan.
	 * **/
	public void update(GameContainer gc, StateBasedGame sb, int delta)
			throws SlickException {
		rajahdys.update(gc, delta);

		Input i = gc.getInput();
		if (i.isKeyPressed(Input.KEY_ESCAPE)) {
			sb.enterState(MindBangMain.MAINMENUSTATE);
		}

		// Tallennetaan hiiren kuuntelija muuttujaan
		Input input = gc.getInput();

		// Tallennetaan hiiren sijainnin x- ja y -koordinaatit
		int mouseX = input.getMouseX();
		int mouseY = input.getMouseY();

		// Totuusmuuttujat onko hiiri jonkun valikkokuvan ylla
		insideShooting = false;
		insideEnergy = false;
		insideButtons = false;

		// Alla tarkastellaan onko hiiri jonkun valikkokuvan koordinaattien
		// ylla.
		if ((mouseX >= menuX && mouseX <= menuX + shooting.getWidth())
				&& (mouseY >= menuY && mouseY <= menuY + shooting.getHeight())) {
			insideShooting = true;
		} else if ((mouseX >= menuX && mouseX <= menuX + energy.getWidth())
				&& (mouseY >= menuY + 250 && mouseY <= menuY + 250
						+ energy.getHeight())) {
			insideEnergy = true;
		} else if ((mouseX >= menuX && mouseX <= menuX + buttons.getWidth())
				&& (mouseY >= menuY + 500 && mouseY <= menuY + 500
						+ buttons.getHeight())) {
			insideButtons = true;
		}

	}
}
