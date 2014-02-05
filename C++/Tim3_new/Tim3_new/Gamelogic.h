#ifndef tim3_gamelogich
#define tim3_gamelogich

#include "Menu.h"
//#include "objects.h"
//#include "Level.h"
#include "GuiInventory.h"
//#include <SFML/Graphics.hpp>
//#include <SFML/Audio.hpp>
//#include <SFML/Window.hpp>
//#include <Box2D/Box2D.h>
//#include <string>
//#include <list>


class Gamelogic
{
public:
	bool running;
	bool showInventoryList = true;
	bool editorModeOn = false;
	bool showEditorHelp = false;
	Menu::GameState run();
	void reset();
	b2World* world;
	Level* level;
	GuiInventory* guiInventory;
	CollisionListener* collist;
	sf::RenderWindow* window;
	std::vector<sf::Texture*>* texvector;
	std::vector<sf::Sound*> soundvector;
	float camx = 0;//kameran keskipiste
	float camy = 0;
	float ang = 0;
	float sizex = 4;
	float sizey = 1;
	float camxvel = 0;
	float camyvel = 0;
	float camzvel = 0;
	bool mouserightheld = false;
	bool flipped = false;
	bool mouseleftheld = false;
	int lastmousex;
	int lastmousey;
	float zoom = 200;//montako pikseliä per metri

	b2Body *dragbody = 0;
	sf::Font* font;
	sf::Sprite editorhelp;
	sf::Texture helptexture;

	sf::View view1;
	Gamelogic(sf::RenderWindow* window, std::vector<sf::Texture*>* texvector, std::vector<sf::Sound*>& soundvector, sf::Font* font, bool editorModeOn = false);
	~Gamelogic();
};


#endif