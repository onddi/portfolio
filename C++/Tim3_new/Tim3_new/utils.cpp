#include "utils.h"
#include <SFML/Graphics.hpp>
//#include <SFML/Audio.hpp>
//#include <SFML/Window.hpp>
#include <Box2D/Box2D.h>
//#include <string>
//#include <list>
//#include <iostream>
//#include <fstream>

void render_image(sf::RenderWindow& window, sf::Texture& image, int x, int y, float scale, float rotation, bool center, bool flipped) {
	sf::Sprite sprite;
	sprite.setTexture(image, true);
	sprite.setScale((flipped ? -1 : 1) * scale, scale);
	sprite.setRotation(rotation);
	if (center == false)
	{
		sprite.setOrigin(sf::Vector2f(0, 0));
	}
	else
	{
		sprite.setOrigin(sf::Vector2f(float(image.getSize().x) / 2.0f, float(image.getSize().y) / 2.0f));
	}
	sprite.setPosition(float(x), float(y));
	window.draw(sprite);
}