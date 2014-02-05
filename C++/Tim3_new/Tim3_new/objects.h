#ifndef tim3_baseobjecth
#define tim3_baseobjecth

#include <SFML/Graphics.hpp>
#include <SFML/Audio.hpp>
#include <SFML/Window.hpp>
#include <Box2D/Box2D.h>
#include <string>
#include <list>

class MyFirstCallback : public b2RayCastCallback
{
	float32 ReportFixture(b2Fixture* fixture, const b2Vec2& point, const b2Vec2& normal, float32 fraction);
};

class CollisionListener :public b2ContactListener
{
	void PostSolve(b2Contact* contact, const b2ContactImpulse* impulse);
	void PreSolve(b2Contact* contact, const b2Manifold* oldManifold);
};

class Baseobject
{
public:
	bool islaser = false;
	bool isconveyor = false;
	bool destructible = false;
	bool explodes = false;
	bool timetodie = false;
	bool isdrawn = true;
	bool active = false;
	float health;
	float explosionmass;
	float explosionvel;
	float conveyorstr = 0;
	float conveyorspeed = 0;
	float conveyorfriction = 1;
	float conveyorstrain = 1;
	float conveyorpos = 0;
	float thrust = 0;
	int amountoflasering = 0;
	int texindex;
	int exptexindex;
	sf::RectangleShape drawshape;
	sf::Sound* collidesound;
	sf::Sound* loopsound;
	sf::Vector2f drawsize = sf::Vector2f(0, 0);
	b2Body* body;
	b2Vec2 lastvel;
	virtual void update(std::list<Baseobject*>* boxes, sf::RenderWindow* window, std::vector<sf::Texture*>* texvector){}
	virtual void render(sf::RenderWindow* win, std::vector<sf::Texture*>* texvector, const bool running);
	Baseobject(){}
	~Baseobject();
	virtual void collide(float impulse);
	virtual void explode(std::list<Baseobject*>* boxes, b2World* world);
	friend class Level;

	b2Vec2 getPos();
	std::string name;
	std::string getName();
	float ang;
	float getAngle();
	b2Vec2 size_;
	b2Vec2 getSize();
	float density_;
	float getDensity();

	int tex;
	sf::Sound* hitsound;

	//Object GoalAreas
	sf::RectangleShape GoalAreaRect;
	bool GoalAreaHit = false;
	b2Vec2 GoalAreapos;
	b2Vec2 GoalAreasize = b2Vec2(0.0, 0.0);
	float GoalAngle;
	virtual bool isGoalAreaHit();
	virtual void setGoalArea(b2Vec2 pos, b2Vec2 size, float angle);
	virtual void resetGoalAreaHit();

	b2Vec2 getGoalAreaPos();
	b2Vec2 getGoalAreaSize();
	int goalHitNum = 0;
	int goalHitAmount();
};

class Box : public Baseobject
{
public:
	Box(const int tex, b2World* world, b2Vec2 size, b2Vec2 pos, float density, float ang, sf::Sound* hitsound);
	/*Tank*/
	Box(const int tex, b2World* world, b2Vec2 size, b2Vec2 pos, float density, float ang, float conveyorpower, sf::Sound* hitsound, sf::Sound* loopsound, const bool maketurret, b2Vec2 goalpos = b2Vec2(0.0, 0.0), b2Vec2 goalsize = b2Vec2(0.0, 0.0), float angle = 0);
	Box(const int tex, const int exptex, b2World* world, b2Vec2 size, b2Vec2 pos, float ang, sf::Sound* hitsound);
};

class Ball : public Baseobject
{
public:
	Ball(const int tex, b2World* world, float size, b2Vec2 pos, sf::Sound* hitsound, b2Vec2 goalpos = b2Vec2(0.0, 0.0), b2Vec2 goalsize = b2Vec2(0.0, 0.0), float angle = 0);
};

class Particle : public Baseobject
{
public:
	int lifetime;
	Particle(const int tex, b2World* world, float size, b2Vec2 pos, b2Vec2 vel, float density, int lifetime, float drag);
	void update(std::list<Baseobject*>* boxes, sf::RenderWindow* window, std::vector<sf::Texture*>* texvector);
	void collide(float impulse){};
};

class Rocket : public Baseobject
{
public:
	//float thrust;
	Rocket(const int tex, const int exptex, b2World* world, b2Vec2 size, b2Vec2 pos, float thrust, float density, float ang, sf::Sound* hitsound, sf::Sound* loopsound);
	void update(std::list<Baseobject*>* boxes, sf::RenderWindow* window, std::vector<sf::Texture*>* texvector);
};

class BouncingBox : public Baseobject
{
public:
	BouncingBox(const int tex, b2World* world, b2Vec2 size, b2Vec2 pos, float density, float ang, float restitution, sf::Sound* hitsound, b2Vec2 goalpos = b2Vec2(0.0, 0.0), b2Vec2 goalsize = b2Vec2(0.0, 0.0), float angle = 0);
};

class Laser : public Baseobject
{
public:
	Laser(const int tex, b2World* world, b2Vec2 size, b2Vec2 pos, float density, float ang, sf::Sound* hitsound, sf::Sound* loopsound);
	void update(std::list<Baseobject*>* boxes, sf::RenderWindow* window, std::vector<sf::Texture*>* texvector);
};
#endif