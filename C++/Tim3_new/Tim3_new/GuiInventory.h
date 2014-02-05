#pragma once
//#include "objects.h"
#include "Level.h"
//#include <SFML/Graphics.hpp>
//#include <SFML/Audio.hpp>
//#include <SFML/Window.hpp>
//#include <Box2D/Box2D.h>
//#include <string>
//#include <list>

class GuiInventory
{
public:
	enum CLICKTYPE {
		CLICKTYPE_CLICK,
		CLICKTYPE_ADD,
		CLICKTYPE_SUBSTRACT
	};

	GuiInventory(Level* level, std::vector<sf::Texture*>* texvector, sf::Font* font_);
	bool click(sf::RenderWindow& window, const size_t x, const size_t y, CLICKTYPE clickType = CLICKTYPE::CLICKTYPE_CLICK);
	void render(sf::RenderWindow& window);
	~GuiInventory();
	std::vector<sf::Texture*>* texvector;
	Level* level;
	sf::Sprite background;
	sf::Font* font;
};

