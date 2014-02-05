#include "GuiInventory.h"
//#include "objects.h"
//#include "Level.h"
#include "utils.h"
//#include <SFML/Graphics.hpp>
#include <SFML/Audio.hpp>
//#include <SFML/Window.hpp>
//#include <Box2D/Box2D.h>
#include <string>
//#include <list>
#include <iostream>
//#include <fstream>

GuiInventory::GuiInventory(Level* level, std::vector<sf::Texture*>* texvector, sf::Font* font_) : level(level), texvector(texvector), font(font_)
{
	background.setTexture(*(*texvector)[16], true);
}

void GuiInventory::render(sf::RenderWindow& window) {
	// Background inventory
	auto size = window.getSize();
	background.setPosition(float(size.x)-200.0f, 0.0f);
	window.draw(background);

	// Inventory Text
	render_image(window, *(*texvector)[17], int(size.x) - 200, 10);

	size_t i = 0;
	for (auto &item : level->getInventory()) {
		size_t y = 100 + i * 100;
		if (i == level->activeInventoryItem) {
			float scale = 100.0f / float((*(*texvector)[19]).getSize().x);
			render_image(window, *(*texvector)[19], int(size.x - 140 - 10), int(y - 10), scale);
		}
		if (item.name == "Ball") {
			float scale = 80.0f/float((*(*texvector)[2]).getSize().x);
			render_image(window, *(*texvector)[2], int(size.x - 140), int(y), scale);
		}
		else if (item.name == "Rocket") {
			float scale = 80.0f / float((*(*texvector)[3]).getSize().x);
			render_image(window, *(*texvector)[3], int(size.x) - 140, int(y), scale);
		}
		else if (item.name == "Brick") {
			float scale = 80.0f / float((*(*texvector)[21]).getSize().x);
			render_image(window, *(*texvector)[21], int(size.x) - 140, int(y), scale);
		}
		else if (item.name == "Tank") {
			float scale = 80.0f / float((*(*texvector)[22]).getSize().x);
			render_image(window, *(*texvector)[22], int(size.x) - 140, int(y), scale);
		}
		else if (item.name == "Convey") {
			float scale = 80.0f / float((*(*texvector)[23]).getSize().x);
			render_image(window, *(*texvector)[23], int(size.x) - 140, int(y), scale);
		}
		else if (item.name == "Laser") {
			float scale = 80.0f / float((*(*texvector)[24]).getSize().x);
			render_image(window, *(*texvector)[24], int(size.x) - 140, int(y), scale);
		}
		else {
			float scale = 80.0f / float((*(*texvector)[18]).getSize().x);
			render_image(window, *(*texvector)[18], int(size.x) - 140, int(y), scale);
		}
		auto textCount = sf::Text(std::to_string(item.count), *font, 30);
		textCount.setColor(sf::Color::Blue);
		textCount.setPosition(float(size.x - 110), float(y + 25));
		window.draw(textCount);
		i += 1;
	}
	
}
bool GuiInventory::click(sf::RenderWindow& window, const size_t mouseX, const size_t mouseY, GuiInventory::CLICKTYPE clickType) {
	// lol apua
	//std::cout << mouseX << "," << mouseY << std::endl;

	size_t i = 0;
	for (auto &item : level->getInventory()) {
		size_t y = 100 + i * 100;
		if (
			mouseX > (window.getSize().x - 200) &&
			mouseY > y &&
			mouseY < y+80
		) {
			std::cout << "Clicked object!" << std::endl;
			if (clickType == CLICKTYPE::CLICKTYPE_CLICK) {
				level->activeInventoryItem = int(i);
			}
			else if (clickType == CLICKTYPE::CLICKTYPE_ADD) {
				level->incInventoryItemCount(i, 1);
			}
			else if (clickType == CLICKTYPE::CLICKTYPE_SUBSTRACT) {
				level->incInventoryItemCount(i, -1);
			}
			return true;
		}
		i += 1;
	}

	if (mouseX > (window.getSize().x - 200)) {
		if (clickType == CLICKTYPE::CLICKTYPE_CLICK) {
			level->activeInventoryItem = -1;
		}
		return true;
	}
	return false;
}

GuiInventory::~GuiInventory()
{
}
