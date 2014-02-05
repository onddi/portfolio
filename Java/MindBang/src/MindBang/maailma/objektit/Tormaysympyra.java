package MindBang.maailma.objektit;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.ShapeRenderer;

import MindBang.maailma.hallinta.Tormayskasittelija;
import MindBang.maailma.objektit.Tankki;

public class Tormaysympyra {

	private Circle tormaysympyra1; // Pelaajaa1 ymparoiva tormaysympyra
	private Circle tormaysympyra2; // Pelaajaa2 ymparoiva tormaysympyra
	private Tormayskasittelija tk;
	private Tankki tankki1;
	private Tankki tankki2;
	private float l1X, l1Y, l2X, l2Y; // Tormaysympyroiden keskipisteiden
										// koordinaatit

	/**
	 * Tormaysympyra luokan luontimetodi.
	 * 
	 * Molemmilla tankeilla (lue pelaajilla) on tormaysalue, joka ymparoi
	 * pelaajaa. Tama alue kuvastaa tankkien jaljella/kaytettavissa olevaa
	 * energiaa.
	 * 
	 * @param tankki1
	 *            = Pelaaja1 hahmo
	 * @param tankki2
	 *            = Pelaaja2 hahmo
	 * @param tk
	 *            = Tormayskasittelija luokka
	 * 
	 **/

	public Tormaysympyra(Tankki tankki1, Tankki tankki2, Tormayskasittelija tk) {

		this.tankki1 = tankki1;
		this.tankki2 = tankki2;
		this.tk = tk;

		luoTormayslaatikot();
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {

		// Mikali pelaaja1 on elossa, piirretaan pelaajan tormaysympyra

		if (tankki1.getOnElossa()) {
			ShapeRenderer.textureFit(getTormaysympyra1(), new Image(
					"res/yellowcircle.png"));
		}

		// Mikali pelaaja2 on elossa, piirretaan pelaajan tormaysympyra

		if (tankki2.getOnElossa()) {
			ShapeRenderer.textureFit(getTormaysympyra2(), new Image(
					"res/bluecircle.png"));
		}

		// Mikali tormaysympyra on suurempi kuin 10, piirretaan ruudun
		// ylareunoihin tankkien elinvoimaa kuvaavat palkit seka numerot.
		// Molemmat ovat pelaajien omilla vareilla piirretty

		if (tormaysympyra1.radius > 10) {
			g.setColor(Color.yellow);
			g.fillRect(30, 5, tormaysympyra1.radius * 2 - 10, 5);
			g.drawString(Integer.toString((int) tormaysympyra1.radius - 10), 1,
					1);
		}
		if (tormaysympyra2.radius > 10) {
			g.setColor(Color.cyan);
			g.fillRect(1249, 5, -tormaysympyra2.radius * 2 + 10, 5);
			g.drawString(Integer.toString((int) tormaysympyra2.radius - 10),
					1254, 1);
		}
		g.setColor(Color.white);

	}

	/**
	 * Luokan update metodissa paivitetaan tormaysympyroiden mukaan tankkien
	 * energiat
	 * **/

	public void update(GameContainer gc, int delta) {
		tankki1.asetaEnergia((int) tormaysympyra1.radius);
		tankki2.asetaEnergia((int) tormaysympyra2.radius);
	}

	/**
	 * Luodaan pelaajia ymparoivat tormaysympyrat. Tormaysympyroiden
	 * keskipisteet ovat tankkien keskipisteissa ja sateet ovat tankkien
	 * leveyden suuruiset. Molemmat ympyrat lisataan tormayskasittelijan
	 * tietoihin.
	 * **/

	public void luoTormayslaatikot() {

		// Luodaan tormaysympyra1: keskipiste tankin keskipisteessa

		tormaysympyra1 = new Circle(tankki1.getX() + tankki1.getL() / 2,
				tankki1.getY() + tankki1.getK() / 2, tankki1.getEnergia());

		// Tormaysympyra1:n keskipisteet x ja y -koordinaatit tallennetaan
		// muuttujiin

		l1X = tormaysympyra1.getCenterX();
		l1Y = tormaysympyra1.getCenterY();
		tk.setTankkialue1(tormaysympyra1);
		
		// Luodaan tormaysympyra2: keskipiste tankin keskipisteessa
		tormaysympyra2 = new Circle(tankki2.getX() + tankki2.getL() / 2,
				tankki2.getY() + tankki2.getK() / 2, tankki2.getEnergia());

		// Tormaysympyra2:n keskipisteet x ja y -koordinaatit tallennetaan
		// muuttujiin

		l2X = tormaysympyra2.getCenterX();
		l2Y = tormaysympyra2.getCenterY();
		tk.setTankkialue2(tormaysympyra2);
	}

	/**
	 * Metodilla muutetaan tormaysympyroiden kokoa.
	 * 
	 * @param kummanvuoro
	 *            = tieto onko kyseessa tankin1 vai tankin2 vuoro
	 * @param vahennys
	 *            = kuinka paljon tormaysympyran sadetta vahennetaan(huom.
	 *            negatiivinen arvo kasvattaa tormaysympyraa)
	 * **/

	public void setTormaysympyrasize(int kummanvuoro, float vahennys) {

		// Jos tankin1 vuoro:

		if (kummanvuoro == 1) {

			// Tormaysympyran minimikoko on 10. Muuten tankki tuhoutuu.

			if (tormaysympyra1.getRadius() < 10) {
				tormaysympyra1.setRadius(10);
			} else {
				tormaysympyra1.setRadius(tormaysympyra1.getRadius() - vahennys);
			}

			// Kun ympyran kokoa muutetaan, muuttuu sen keskipisteen
			// koordinaatit
			// samalla. Jotta ympyra olisi keskittynyt tankin ymparille
			// asetetaan keskipisteeksi luoTormayslaatikot() -metodissa
			// tallennetut keskipisteet. Annetaan tormayskasittelijalle tieto
			// muuttuneesta tormaysympyrasta

			tormaysympyra1.setCenterX(l1X);
			tormaysympyra1.setCenterY(l1Y);
			tk.setTankkialue1(tormaysympyra1);

			// Jos tankin2 vuoro: tehdaan samat asiat tankille2

		} else if (kummanvuoro == 2) {

			if (tormaysympyra2.getRadius() < 10) {
				tormaysympyra2.setRadius(10);
			} else {
				tormaysympyra2.setRadius(tormaysympyra2.getRadius() - vahennys);
			}

			tormaysympyra2.setCenterX(l2X);
			tormaysympyra2.setCenterY(l2Y);
			tk.setTankkialue2(tormaysympyra2);
		}
	}

	/**
	 * Palauttaa tankin1 tormayslaatikon.
	 * 
	 * @return tormaysympyra1
	 * **/

	public Shape getTormaysympyra1() {
		return tormaysympyra1;
	}

	/**
	 * Palauttaa tankin2 tormayslaatikon.
	 * 
	 * @return tormaysympyra2
	 * **/

	public Shape getTormaysympyra2() {
		return tormaysympyra2;
	}

	/**
	 * Poistaa tormayskasittelijan taulukosta annetun tormayslaatikon
	 * 
	 * @param tormaysympyra
	 *            = tormaysalue joka halutaan poistaa
	 * **/

	public void poistaTormaysympyra(Shape tormaysympyra) {
		tk.remove(tormaysympyra);
	}

}
