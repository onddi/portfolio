#ifndef tim3_guielement
#define tim3_guielement

#include <SFML/Graphics.hpp>
#include <SFML/Audio.hpp>

class Guielement
{
public:

	Guielement(int sizex, int sizey, int posx, int posy, sf::Texture* non_clicked, sf::Texture* clicked, sf::Sound* sound, sf::Text text, std::string type);
	Guielement(int sizex, int sizey, int posx, int posy, sf::Texture* non_clicked, sf::Texture* clicked, sf::Sound* sound, std::string type); //konstruktori guielementille ilman tekstia
	Guielement(int sizex, int sizey, int posx, int posy, sf::Texture* non_clicked, sf::Text text, std::string type); //konstruktori ei-klikattavalle elementille
	Guielement(int sizex, int sizey, int posx, int posy, std::string type); //näkymättömien klikattavien alueiden tekoon
	bool contains(sf::Vector2i position);
	void render(sf::RenderWindow *window);
	void setClicked();
	void set_not_Clicked();
	void changeText(sf::Text teksti);
	void changePosition(int posx, int posy);
	bool isClicked = false;
	std::string objecttype;

private:

	sf::RectangleShape buttonarea;
	sf::Texture clicked_texture;			
	sf::Texture non_clicked_texture;
	sf::Sound* clicksound;
	sf::Text text;
	bool drawable = true;
	bool clickable = true;
	

};
















#endif