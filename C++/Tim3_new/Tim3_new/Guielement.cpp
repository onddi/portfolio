#include <iostream>

#include "Guielement.h"

Guielement::Guielement(int sizex, int sizey, int posx, int posy, sf::Texture* non_clicked, sf::Texture* clicked, sf::Sound* sound, sf::Text teksti, std::string type)
	{
		buttonarea = sf::RectangleShape(sf::Vector2f((float)sizex, (float)sizey));
		non_clicked_texture = sf::Texture(*non_clicked);
		clicked_texture = sf::Texture(*clicked);
		buttonarea.setPosition((float)posx, (float)posy);
		buttonarea.setTexture(&non_clicked_texture, true);
		clicksound = sound;
		text = teksti;
		sf::FloatRect textRect = text.getLocalBounds();
		text.setOrigin(textRect.left + textRect.width / 2.0f, textRect.top + textRect.height / 2.0f);
		text.setPosition((float)posx + (float)sizex / 2, (float)posy + (float)sizey / 2);
		text.setColor(sf::Color::Black);
		objecttype = type;
	}


Guielement::Guielement(int sizex, int sizey, int posx, int posy, sf::Texture* non_clicked, sf::Texture* clicked, sf::Sound* sound, std::string type) //konstruktori guielementille ilman tekstia
{
	buttonarea = sf::RectangleShape(sf::Vector2f((float)sizex, (float)sizey));
	non_clicked_texture = sf::Texture(*non_clicked);
	clicked_texture = sf::Texture(*clicked);
	buttonarea.setPosition((float)posx, (float)posy);
	buttonarea.setTexture(&non_clicked_texture, true);
	clicksound = sound;
	objecttype = type;
}

Guielement::Guielement(int sizex, int sizey, int posx, int posy, sf::Texture* non_clicked, sf::Text teksti, std::string type) //konstruktori ei-klikattavalle elementille
{
	buttonarea = sf::RectangleShape(sf::Vector2f((float)sizex, (float)sizey));
	non_clicked_texture = sf::Texture(*non_clicked);
	buttonarea.setPosition((float)posx, (float)posy);
	buttonarea.setTexture(&non_clicked_texture, true);
	text = teksti;
	sf::FloatRect textRect = text.getLocalBounds();
	text.setOrigin(textRect.left + textRect.width / 2.0f, textRect.top + textRect.height / 2.0f);
	text.setPosition((float)posx + (float)sizex / 2, (float)posy + (float)sizey / 2);
	text.setColor(sf::Color::Black);
	clickable = false;
	objecttype = type;
}

Guielement::Guielement(int sizex, int sizey, int posx, int posy, std::string type) //konstruktori näkymättömälle klikattavalle alueelle
{
	buttonarea = sf::RectangleShape(sf::Vector2f((float)sizex, (float)sizey));
	buttonarea.setPosition((float)posx, (float)posy);
	drawable = false;
	objecttype = type;
}


bool Guielement::contains(sf::Vector2i position)
{

if (position.x >= buttonarea.getPosition().x && position.x <= (buttonarea.getPosition().x + buttonarea.getSize().x))
	{
	if (position.y >= buttonarea.getPosition().y && position.y <= (buttonarea.getPosition().y + buttonarea.getSize().y))
			return true;
	}
return false;
}

void Guielement::render(sf::RenderWindow *window)
{
	if (drawable)
	{
		window->draw(buttonarea);
		window->draw(text);
	}
	
}

void Guielement::setClicked()
{
	if (clickable)
	{
		clicksound->play();
		text.setCharacterSize(text.getCharacterSize() - 1);
		buttonarea.setTexture(&clicked_texture, true);
		isClicked = true;
	}
}

void Guielement::set_not_Clicked()
{
	text.setCharacterSize(text.getCharacterSize() + 1);
	buttonarea.setTexture(&non_clicked_texture, true);
	isClicked = false;
}

void Guielement::changeText(sf::Text teksti)
{
	int posx = (int)buttonarea.getPosition().x;
	int posy = (int)buttonarea.getPosition().y;
	int sizex = (int)buttonarea.getSize().x;
	int sizey = (int)buttonarea.getSize().y;
	text = teksti;
	sf::FloatRect textRect = text.getLocalBounds();
	text.setOrigin(textRect.left + textRect.width / 2.0f, textRect.top + textRect.height / 2.0f);
	text.setPosition((float)posx + (float)sizex / 2, (float)posy + (float)sizey / 2);
	text.setColor(sf::Color::Black);
}
void Guielement::changePosition(int posx, int posy)
{
	buttonarea.setPosition(float(posx), float(posy));
	int sizex = int(buttonarea.getSize().x);
	int sizey = int(buttonarea.getSize().y);
	sf::FloatRect textRect = text.getLocalBounds();
	text.setOrigin(textRect.left + textRect.width / 2.0f, textRect.top + textRect.height / 2.0f);
	text.setPosition(float(posx + sizex / 2), float(posy + sizey / 2));
}