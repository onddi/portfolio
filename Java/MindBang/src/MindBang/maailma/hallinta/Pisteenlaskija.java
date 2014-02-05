package MindBang.maailma.hallinta;

import org.newdawn.slick.GameContainer;
import MindBang.maailma.hallinta.Tormayskasittelija;
import MindBang.maailma.objektit.Tormaysympyra;

/**
 * Pisteenlaskija luokka hallitsee tormayksista syntyvia tapahtumia. Se hakee
 * tormayskasittelijaluokalta tormayspisteita ja niiden lukuarvon perusteella
 * tekee erilaisia asioita kuten kasvattaa tai pienentaa tankkien
 * tormaysalueita.
 * **/

public class Pisteenlaskija {

	private Ohjain ohjain; // Ohjaimen instanssi
	private Tormayskasittelija tk; // Tormayskasittelijan instanssi
	private int tormayspisteet; // Tormayspisteet muuttuja
	private int tankkipisteet; // Tankkipisteet muuttuja
	private Tormaysympyra tormaysympyra; // Tormaysympyran instanssi

	/**
	 * Pisteenlaskija luontimetodi.
	 * 
	 * @param ohjain
	 *            = pelin ohjain -luokka
	 * @param tk
	 *            = tormayskasittelija -luokan instanssi
	 * @param tl
	 *            = tankkien tormaysympyrat
	 * **/

	public Pisteenlaskija(Ohjain ohjain, Tormayskasittelija tk, Tormaysympyra tl) {
		this.ohjain = ohjain;
		this.tk = tk;
		this.tormaysympyra = tl;

	}

	/**
	 * Luokan update metodissa paivitetaan tankkien sateiden arvot ajantasalle.
	 * Tama tapahtuu tarkastamalla jatkuvasti onko tormayskasittelija luokassa
	 * tapahtunut tormays joka vaikuttaisi tankkien elinvoimaa kuvaaviin
	 * tormaysympyroihin.
	 * **/

	public void update(GameContainer gc, int delta) {

		// Haetaan maaston tormayspisteita tormayskasittelijaluokalta
		tormayspisteet = tk.getTormayspisteet();

		// Haetaan tankkien tormayspisteita tormayskasittelijaluokalta
		tankkipisteet = tk.getTankkipisteet();

		// Mikali kyseessa tankin 1 vuoro:
		if (ohjain.tankki1Vuoro() == true) {

			// Jos tormayskasittelijalta saatu tormayspisteiden arvo eroaa
			// nollasta.
			// Tarkoittaa se sita etta maastoa on ammuttu. Talloin sen tankin
			// sade kasvaa joka ampui maastoa.
			if (tormayspisteet != 0) {

				// Kasvatetaan pelaajan1 takin sadetta
				tormaysympyra.setTormaysympyrasize(1, -tormayspisteet);
				// Asetetaan tormayskasittelijan tormayspisteet taas nollaksi
				tk.setTormayspisteet(0);

				// Mikali tankkipisteet ovat erisuuri kuin 0 on kyseessa osuma
				// vastustajan tankkiin. Tormayspisteet ovat silloin ammuksen
				// sateen suuruiset.
			} else if (tankkipisteet != 0) {
				// Vahennetaan vastustajan tormaysympyran sateesta ammuksen
				// suuruutta vastaava maara
				tormaysympyra.setTormaysympyrasize(2, tankkipisteet);
				// Asetetaan tormayskasittelijan tormayspisteet taas nollaksi
				tk.setTankkipisteet(0);
			}
			// Mikali kyseessa tankin 2 vuoro on logiikka sama kuin ylla.
		} else {

			if (tormayspisteet != 0) {

				tormaysympyra.setTormaysympyrasize(2, -tormayspisteet);
				tk.setTormayspisteet(0);

			} else if (tankkipisteet != 0) {
				tormaysympyra.setTormaysympyrasize(1, tankkipisteet);
				tk.setTankkipisteet(0);
			}
		}

	}

}
