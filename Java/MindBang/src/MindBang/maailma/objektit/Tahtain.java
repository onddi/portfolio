package MindBang.maailma.objektit;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class Tahtain {

	private float x, y; // Tahtaimen sijainti
	private Image tahtain; // Tahtaimen kuva
	private Vector2f head1, head2; // Pelaajien paiden sijaintivektorit
	private Image paa1, paa2; // Pelaajien paiden kuvat
	private Tankki t1, t2; // Pelaajat
	private Color tahtainmenvari = Color.yellow; // Tahtaimen vari 

	/**
	 * Tahtain -luokan luontimetodi. Luodaan ammuksien ampumista varten tahtain,
	 * seka tahtaimen sijainnin mukaan kaantyvat pelaajien paat. Paat ottavat
	 * sijaintinsa tankkien koordinaateista.
	 * 
	 * @param t1
	 *            = tankki1 (pelaaja1)
	 * @param t2
	 *            = tankki2 (pelaaja2)
	 * **/

	public Tahtain(Tankki t1, Tankki t2) throws SlickException {

		this.x = 0;
		this.y = 0;
		this.t1 = t1;
		this.t2 = t2;
		head1 = new Vector2f(t1.getX() + 8, t1.getY() - 20);
		head2 = new Vector2f(t2.getX() + 8, t2.getY() - 20);
		tahtain = new Image("res/tahtain.png");
		paa1 = new Image("res/buddha.png").getScaledCopy(0.1f);
		paa1.rotate(90);
		paa2 = new Image("res/buddha.png").getScaledCopy(0.1f);
		paa2.rotate(-90);

	}

	public void render(GameContainer gc, Graphics g) {

		// Piirretaan tahtain

		g.drawImage(tahtain, x, y,tahtainmenvari);

		// Mikali tankki ovat elossa piirretaan niille paa

		if (t1.getOnElossa()) {

			g.drawImage(paa1, head1.x, head1.y);
		}
		if (t2.getOnElossa()) {

			g.drawImage(paa2, head2.x, head2.y);
		}

	}

	public void update(GameContainer gc) {

		Input i = gc.getInput();

		// Tallennetaan valiaikaismuuttujaan hiiren x ja y koordinaatit

		float mouseX = i.getMouseX();
		float mouseY = i.getMouseY();

		// Koska tahtaimen kuva piirretaan lahtien vasemmasta ylakulmasta,
		// taytyy hiiren x koordinaatista vahentaa puolet tahtainkuvan leveysta
		// ja y koordinaatista puolet tahtainkuvan korkeudesta. Nain tahtainkuva
		// saadaan keskitettya hiiren osoittamaan kohtaan.

		x = mouseX - tahtain.getWidth() / 2;
		y = mouseY - tahtain.getHeight() / 2;
		
		// Muutetaan tahtaimen varia ammuksen mukaan
		if (Ammus.getYkkosase())
			tahtainmenvari = Color.yellow;
		else
			tahtainmenvari = Color.cyan;

		// Pelaajien paiden kaantymista varten lasketaan hiiren ja paiden
		// valiset x- ja y- suuntaiset etaisyydet. Naiden avulla voidaan laskea
		// kulma jonka paan taytyy kaantya.

		// Mikali on tankin1 vuoro niin paa kaantyy
		if (t1.onkoVuoro() == true) {
				
			float xDistance = mouseX - head1.x;
			float yDistance = mouseY - head1.y;
			double angleToTurn = Math.toDegrees(Math
					.atan2(yDistance, xDistance));
			paa1.setRotation((float) angleToTurn);
		}
		// Mikali on tankin2 vuoro niin paa kaantyy
		if (t2.onkoVuoro() == true) {
						
			float xDistance1 = mouseX - head2.x;
			float yDistance1 = mouseY - head2.y;
			double angleToTurn1 = Math.toDegrees(Math.atan2(yDistance1,
					xDistance1));
			paa2.setRotation((float) angleToTurn1);
		}
	}

	public float getX() {
		return x;

	}

	public void setX(int x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
