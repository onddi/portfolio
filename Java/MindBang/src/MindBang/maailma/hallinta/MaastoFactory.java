package MindBang.maailma.hallinta;

import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.GeomUtil;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;

/**
 * MaastoFactory luo maastot pelia varten. Luodut maastot valitetaan Maasto
 * luokalle, joka vastaa Maastojen renderoinnista. Kayttaja valitsee halutun
 * maaston MainMenu -pelitilasta.
 * **/

public class MaastoFactory {

	private List<Shape> maastolista; // Lista johon maastot tallennetaan
	private Polygon polygon; // Polygoni josta voi muokata haluamansa muodon
	private GeomUtil lisaaja; // Slickin muotojen muokkauksen tyokalu
	private int level; // Halutun maaston numero

	/**
	 * Luokan luontimetodi.
	 * 
	 * @param level
	 *            = halutun maaston numero
	 * **/
	public MaastoFactory(int level) {
		this.level = level;
		lisaaja = new GeomUtil();
		maastolista = new ArrayList<Shape>();
	}

	/**
	 * Luo halutun maaston.
	 * 
	 * @return maastolista = maastot sisaltava lista
	 * **/
	public List<Shape> luoMaasto() {
		polygon = new Polygon();
		float[] pp = new float[8]; // Polygonin pisteiden maaritysta varten
									// luodaan taulukkoja
		float[] yp = new float[8];

		if (level == 0) {
			// Powerup on erikoiskenttaa (level 4) varten luotu ominaisuus
			Tormayskasittelija.removePowerup();
		}

		else if (level == 1) {
			Tormayskasittelija.removePowerup();
			pp[0] = 250; // Lisataan alkioihin eri pisteita
			pp[1] = 800;
			pp[2] = 640;
			pp[3] = 400;
			pp[4] = 1030;
			pp[5] = 800;
			polygon.addPoint(pp[0], pp[1]); // Asetetaan polygoni kayttamaan
											// alkioiden pisteita
			polygon.addPoint(pp[2], pp[3]);
			polygon.addPoint(pp[4], pp[5]);
		} else if (level == 2) {
			Tormayskasittelija.removePowerup();
			pp[0] = 400;
			pp[1] = 200;
			pp[2] = 880;
			pp[3] = 200;
			pp[4] = 880;
			pp[5] = 900;
			pp[6] = 400;
			pp[7] = 900;
			polygon.addPoint(pp[0], pp[1]);
			polygon.addPoint(pp[2], pp[3]);
			polygon.addPoint(pp[4], pp[5]);
			polygon.addPoint(pp[6], pp[7]);
		} else if (level == 3) {
			Tormayskasittelija.removePowerup();
			// Koska polygoni ei hyvaksy ympyraa sinallaan, luodaan kaksi
			// ympyraa jotka yhdistetaan GeomUtil tyokalun avulla. Nain saadaan
			// ympyra pakotettua polygoniksi.
			Circle ympyra = new Circle(640, 450, 250);
			Circle ympyra1 = new Circle(640, 450, 200);
			polygon = (Polygon) lisaaja.union(ympyra, ympyra1)[0];
			
			Circle ympyra2 = new Circle(150, 150, 100);
			Circle ympyra3 = new Circle(1130, 150, 100);
			//Lisataan luodut ympyrat maastolistaan
			maastolista.add((Polygon) lisaaja.union(ympyra2, ympyra3)[1]);
			maastolista.add((Polygon) lisaaja.union(ympyra3, ympyra2)[1]);

		} else if (level == 4) {
			Tormayskasittelija.setPowerup();
			//Arvotaan eri x ja y -koordinaatteja for loopin sisalla
			for (int x = 0; x < pp.length; x++) {
				for (int y = 0; y < yp.length; y++) {
					pp[x] = (float) (Math.random() * 900 + 200);
					yp[y] = (float) (Math.random() * 900 + 200);
					polygon.addPoint(pp[x], yp[y]);

				}
			}

		}

		maastolista.add(polygon);
		return maastolista;
	}
}
