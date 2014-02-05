package MindBang.maailma.objektit;

import java.util.List;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.ShapeRenderer;

import MindBang.maailma.hallinta.Tormayskasittelija;

/**
 * Maasto -luokka pit‰‰ sis‰ll‰‰n pelin muokkautuvaa maastoa.
 * **/

public class Maasto {

	private Tormayskasittelija tk;
	private Image lion = new Image("res/maastot/lion.png");
	private Image psychedelic = new Image("res/maastot/psychedelic.png");
	private Image sun = new Image("res/maastot/sun.png");
	private Image abstrakti = new Image("res/maastot/abstrakti.png");
	private Image heimo = new Image("res/maastot/heimo.png");
	private Image lintu = new Image("res/maastot/lintu.png");
	private Image psyche = new Image("res/maastot/psyche.jpg");
	private Image psycat = new Image("res/maastot/psycat.png");
	private Image silma = new Image("res/maastot/silma.jpg");
	private Image faces = new Image("res/maastot/faces.png");
	private Image kuva; // Maaston renderoinnissa kaytettava kuva

	/**
	 * Maasto -luokan luontimetodi.
	 * 
	 * @param tk
	 *            = tormayskasittelija luokan instanssi
	 * @param shape
	 *            = MaastoFactory -luokan luoma muoto
	 * **/

	public Maasto(Tormayskasittelija tk, List<Shape> shape)
			throws SlickException {

		this.tk = tk;
		// Lisataan tormayskasittelijan listaan annettu muoto
		lisaaMaastot(shape);
		// Asetetaan maaston kuvaksi arvoKuva() -metodin palauttama kuva
		kuva = arvoKuva();

	}

	/**
	 * Lisaa Maastoihin tuodut maastot tormayskasittelijan maastot -listaan.
	 * 
	 * @param shape
	 *            = maastot sisaltava lista
	 * **/

	private void lisaaMaastot(List<Shape> shape) {
		for (int i = 0; i < shape.size(); i++) {
			tk.add(shape.get(i));
		}

	}

	public void render(GameContainer gc, Graphics g) {

		// Haetaan tormayskasittelijalta maasto ja piirretaan se. Asetetaan
		// maaston tekstuuriksi kuva muuttuja. Maasto haetaan
		// tormayskasittelijalta, koska maastoon saattaa tulla jatkuvasti
		// muutoksia tormaysten takia.

		for (int i = 0; i < tk.getMaastot().size(); i++) {
			ShapeRenderer.textureFit(tk.getMaastot().get(i), kuva);

		}
	}

	/**
	 * Arpoo annettujen kuvien joukosta satunnaisesti yhden ja palauttaa sen.
	 * 
	 * @return satunnaisesti arvottu kuva
	 **/

	public Image arvoKuva() {

		int luku = (int) (Math.random() * 11);
		System.out.println("MIKALUKU " + luku);
		switch (luku) {
		case 1:
			kuva = psychedelic;
			break;
		case 2:
			kuva = lion;
			break;
		case 3:
			kuva = sun;
			break;
		case 4:
			kuva = abstrakti;
			break;
		case 5:
			kuva = heimo;
			break;
		case 6:
			kuva = lintu;
			break;
		case 7:
			kuva = psyche;
			break;
		case 8:
			kuva = silma;
			break;
		case 9:
			kuva = psycat;
			break;
		case 10:
			kuva = faces;
			break;
		default:
			kuva = sun;
			break;

		}
		return kuva;
	}

}
