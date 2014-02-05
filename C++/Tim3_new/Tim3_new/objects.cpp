#include "objects.h"
#include <SFML/Graphics.hpp>
#include <SFML/Audio.hpp>
#include <SFML/Window.hpp>
#include <Box2D/Box2D.h>
#include <iostream>
#include <list>

float32 MyFirstCallback::ReportFixture(b2Fixture* fixture, const b2Vec2& point, const b2Vec2& normal, float32 fraction)
{
	return fraction;
}

void CollisionListener::PreSolve(b2Contact* contact, const b2Manifold* oldManifold)
{
	Baseobject* obj1 = (Baseobject*)contact->GetFixtureA()->GetUserData();
	Baseobject* obj2 = (Baseobject*)contact->GetFixtureB()->GetUserData();
	b2WorldManifold worldManifold;
	contact->GetWorldManifold(&worldManifold);

	float vel = 0;
	if (obj1)
	{
		vel += obj1->conveyorspeed;
	}
	if (obj2)
	{
		vel += obj2->conveyorspeed;
	}
	contact->SetTangentSpeed(vel);
}

void CollisionListener::PostSolve(b2Contact* contact, const b2ContactImpulse* impulse)
{
	b2Vec2 impuls;
	float downhillbonus = 0;
	Baseobject* obj1 = (Baseobject*)contact->GetFixtureA()->GetBody()->GetUserData();
	Baseobject* obj2 = (Baseobject*)contact->GetFixtureB()->GetBody()->GetUserData();
	for (int i = 0; i < contact->GetManifold()->pointCount; i++)
	{
		impuls.x += contact->GetManifold()->points[i].normalImpulse;
		impuls.y += contact->GetManifold()->points[i].tangentImpulse;
	}
	if (obj1)
	{
		obj1->collide(impuls.Length());
	}
	if (obj2)
	{
		obj2->collide(impuls.Length());
	}
	obj1 = (Baseobject*)contact->GetFixtureA()->GetUserData();
	obj2 = (Baseobject*)contact->GetFixtureB()->GetUserData();
	if (obj1)
	{
		if (obj1->isconveyor)
		{
			if (obj1->conveyorspeed * impuls.y > 0)
				downhillbonus = abs(impuls.y);
			else
				downhillbonus = -abs(impuls.y);
			obj1->conveyorstrain -= downhillbonus;
			obj1->conveyorfriction -= 0.1f*impuls.x;
			//std::cout << (140+obj1->conveyorstrain) << std::endl;
		}
	}
	if (obj2)
	{
		if (obj2->isconveyor)
		{
			if (obj2->conveyorspeed * impuls.y > 0.0f)
				downhillbonus = abs(impuls.y);
			else
				downhillbonus = -abs(impuls.y);
			obj2->conveyorstrain -= downhillbonus;
			obj2->conveyorfriction -= 0.1f*impuls.x;
			//std::cout << (140 + obj2->conveyorstrain) << std::endl;
		}
	}
}

Baseobject::~Baseobject()
{
	body->DestroyFixture(body->GetFixtureList());
	body->GetWorld()->DestroyBody(body);
	delete collidesound;
	delete loopsound;
}

void Baseobject::collide(float impulse)
{
	if (impulse > health && destructible)
		timetodie = true;
	if (impulse / body->GetMass() > 1.0f && collidesound && body->GetMass() != 0.0f)
	{
		b2Vec2 deltav = body->GetLinearVelocity() - lastvel;
		float loud = deltav.Length();
		collidesound->setVolume(loud);
		collidesound->play();
	}
}

void Baseobject::render(sf::RenderWindow* win, std::vector<sf::Texture*>* texvector, const bool running)
{
	lastvel = body->GetLinearVelocity();
	bool first = true;
	for (b2Fixture* fix = body->GetFixtureList(); fix; fix = fix->GetNext())
	{
		sf::Vector2f fixpos;
		b2Shape* shape = fix->GetShape();
		b2Vec2 vec;
		b2Vec2 size;
		float ang = body->GetAngle();
		float ang2 = ang + 1.5757f;
		if (shape->m_type == b2Shape::e_circle)
		{
			b2Vec2 pos = static_cast<const b2CircleShape*>(shape)->m_p;
			vec = body->GetPosition() + b2Vec2(pos.x * cos(ang) - pos.y * sin(ang), sin(ang) * pos.x + cos(ang) * pos.y);
			float s = static_cast<const b2CircleShape*>(shape)->m_radius;
			size = b2Vec2(s * 2.0f, s * 2.0f);
			fixpos = sf::Vector2f(vec.x, vec.y);
		}
		else if (shape->m_type == b2Shape::e_polygon)
		{
			const b2PolygonShape* shap = static_cast<const b2PolygonShape*>(shape);
			b2Vec2 pos = shap->m_centroid;
			vec = body->GetPosition() + b2Vec2(pos.x * cos(ang) - pos.y * sin(ang), sin(ang) * pos.x + cos(ang) * pos.y);
			size = shap->m_vertices[2] - shap->m_vertices[0];
			fixpos = sf::Vector2f(vec.x, vec.y);
		}
		b2AABB aabb = fix->GetAABB(0);
		if (drawsize.x == 0.0f && drawsize.y == 0.0f)
		{
			drawshape.setOrigin(size.x * 0.5f, size.y * 0.5f);
			drawshape.setSize(sf::Vector2f(size.x, size.y));
		}
		else
		{
			sf::Vector2f origin = sf::Vector2f(drawsize.x - size.x*.75f, drawsize.y - size.y*.5f);
			drawshape.setOrigin(origin);
			drawshape.setSize(drawsize);
		}
		drawshape.setPosition(vec.x, vec.y);
		drawshape.setRotation(body->GetAngle() * 180.0f / 3.1415f);
		drawshape.setTexture((*texvector)[texindex]);
		if (vec.Length() > 0.001f && first)
			win->draw(drawshape);
		drawshape.setTexture((*texvector)[1]);
		first = false;
		if (conveyorstr != 0.0f && fix->GetUserData())
		{
			float len = 2.0f * size.x + 2.0f * size.y;
			for (float i = 0.0f; i < len; i += 1)
			{
				drawshape.setSize(sf::Vector2f(0.5f, 0.25f));//(0.3, 0.14));
				drawshape.setOrigin(0.25f, 0.12f);
				float posi = conveyorpos + float(i);
				size.y = abs(size.y);
				size.x = abs(size.x);
				float a = -ang - 1.5757f;
				sf::Vector2f pos1 = sf::Vector2f(-size.x * 0.5f * sin(a) - size.y * 0.5f * cos(a), -size.x * 0.5f * cos(a) + size.y * 0.5f * sin(a));
				sf::Vector2f pos2 = sf::Vector2f(-size.x * 0.5f * sin(a) + size.y * 0.5f * cos(a), -size.x * 0.5f * cos(a) - size.y * 0.5f * sin(a));
				sf::Vector2f pos3 = sf::Vector2f(size.x * 0.5f * sin(a) + size.y * 0.5f * cos(a), size.x * 0.5f * cos(a) - size.y * 0.5f * sin(a));
				sf::Vector2f pos4 = sf::Vector2f(size.x * 0.5f * sin(a) - size.y * 0.5f * cos(a), size.x * 0.5f * cos(a) + size.y * 0.5f * sin(a));
				if (posi > len)
					posi -= len;
				if (posi < size.y)
				{
					sf::Vector2f vec = pos2 - pos1;
					float l = sqrt(vec.x * vec.x + vec.y * vec.y);
					drawshape.setPosition(pos1 + (vec / l * float(posi)) + fixpos);
					drawshape.setRotation(ang * 180.0f / 3.1415f - 90.0f);
				}
				else if (posi < size.x + size.y)
				{
					posi -= size.y;
					sf::Vector2f vec = pos3 - pos2;
					float l = sqrt(vec.x * vec.x + vec.y * vec.y);
					drawshape.setPosition(pos2 + (vec / l * float(posi)) + fixpos);
					drawshape.setRotation(ang * 180.0f / 3.1415f + 180.0f);
				}
				else if (posi < size.x + 2 * size.y)
				{
					posi -= size.x + size.y;
					sf::Vector2f vec = pos4 - pos3;
					float l = sqrt(vec.x * vec.x + vec.y * vec.y);
					drawshape.setPosition(pos3 + (vec / l * float(posi)) + fixpos);
					drawshape.setRotation(ang * 180.0f / 3.1415f + 90.0f);
				}
				else
				{
					posi -= size.x + 2.0f * size.y;
					sf::Vector2f vec = pos1 - pos4;
					float l = sqrt(vec.x * vec.x + vec.y * vec.y);
					drawshape.setPosition(pos4 + (vec / l * float(posi)) + fixpos);
					drawshape.setRotation(ang * 180.0f / 3.1415f);
				}

				win->draw(drawshape);
			}
			if (running)
			{
				conveyorpos -= conveyorspeed / 60.0f;
				loopsound->setVolume(abs(conveyorspeed / conveyorstr));
				//std::cout << abs(conveyorspeed / conveyorstr) << std::endl;
				if (conveyorpos > len)
					conveyorpos -= len;
				if (conveyorpos < 0.0f)
					conveyorpos += len;
			}
		}
	}
}

void Baseobject::explode(std::list<Baseobject*>* boxes, b2World* world)
{
	for (int i = 0; i < 160; i++)
	{
		float ang = i / 160.0f * 2.0f * 3.1415f;
		b2Vec2 vel = b2Vec2(explosionvel * cos(ang), explosionvel * sin(ang));
		boxes->push_back(new Particle(exptexindex, world, .3f, body->GetWorldCenter(), vel, explosionmass / 160.0f, 200, 0.4f));
	}
	
}

bool Baseobject::isGoalAreaHit()
{
	if ((this->getPos().y > (GoalAreapos.y - GoalAreasize.y / 2.0f)
		&& this->getPos().y < (GoalAreapos.y + GoalAreasize.y / 2.0f)
		&& this->getPos().x >(GoalAreapos.x - GoalAreasize.x / 2.0f)
		&& this->getPos().x < (GoalAreapos.x + GoalAreasize.x / 2.0f)))
		GoalAreaHit = true;
	else
		GoalAreaHit = false;

	if (GoalAreaHit){
		goalHitNum++;
		resetGoalAreaHit();
	}

	return GoalAreaHit;
}

void Baseobject::setGoalArea(b2Vec2 pos, b2Vec2 size, float angle)
{
	GoalAreapos.x = pos.x;
	GoalAreapos.y = pos.y;
	GoalAreasize.x = size.x;
	GoalAreasize.y = size.y;
	GoalAngle = angle*(180.0f / 3.141592654f);

	GoalAreaRect.setFillColor(sf::Color(0, 0, 250, 100));
	GoalAreaRect.setPosition(pos.x, pos.y);
	GoalAreaRect.setSize(sf::Vector2f(size.x, size.y));
	GoalAreaRect.setRotation(GoalAngle);
	GoalAreaRect.setOrigin(sf::Vector2f(size.x / 2.0f, size.y / 2.0f));
}

void Baseobject::resetGoalAreaHit(){
	GoalAreapos.x = -10000.0f;
	GoalAreaRect.setPosition(-10000.0f, 0.0f);
}

b2Vec2 Baseobject::getGoalAreaPos()
{
	return GoalAreapos;
}

b2Vec2 Baseobject::getGoalAreaSize()
{
	return GoalAreasize;
}

int Baseobject::goalHitAmount()
{
	return goalHitNum;
}

b2Vec2 Baseobject::getPos()
{
	return body->GetPosition();
}

std::string Baseobject::getName()
{
	return name;
}

float Baseobject::getAngle()
{
	return body->GetAngle();
}

b2Vec2 Baseobject::getSize()
{
	return size_;
}

float Baseobject::getDensity()
{
	return density_;
}

Box::Box(const int tex, b2World* world, b2Vec2 size, b2Vec2 pos, float density, float ang, float conveyorvel, sf::Sound* hitsound, sf::Sound* activesound, const bool maketurret, b2Vec2 goalpos, b2Vec2 goalsize, float goalangle)
{
	name = "Tankki:";
	size_ = size;
	density_ = density;
	conveyorstr = conveyorvel;
	setGoalArea(goalpos, goalsize, goalangle);
	destructible = false;
	explodes = false;
	isdrawn = true;
	isconveyor = true;
	texindex = tex;
	collidesound = new sf::Sound(*hitsound);
	loopsound = new sf::Sound(*activesound);
	loopsound->setLoop(true);
	loopsound->play();
	b2BodyDef bodydef;
	if (density > 0.0f)
	bodydef.type = b2_dynamicBody;
	else
	bodydef.type = b2_staticBody;
	bodydef.position = b2Vec2(pos.x, pos.y);
	bodydef.angle = ang;
	bodydef.allowSleep = false;
	body = world->CreateBody(&bodydef);
	body->SetUserData(this);
	b2PolygonShape shape;
	if (maketurret && density > 0)
	{
		texindex = 11;
		shape.SetAsBox(4.0f, 1.0f, b2Vec2(conveyorvel > 0.0f ? 1.5f : -1.5f, 1.1f), 0.0f);
		b2FixtureDef fix;
		fix.shape = &shape;
		fix.density = 7.0f;
		fix.userData = this;
		fix.friction = 0.9f;
		b2PolygonShape shape2;
		shape2.SetAsBox(3.0f, 2.0f, b2Vec2(0.0f, -1.0f), 0.0f);
		b2FixtureDef fix2;
		drawsize = sf::Vector2f(16.5f, 6.0f);
		fix2.shape = &shape2;
		fix2.density = 1.7f;
		fix2.friction = 0.6f;
		if (density > 0.0f)
			body->CreateFixture(&fix2);
		b2PolygonShape shape3;
		shape3.SetAsBox(5.0f, .5f, b2Vec2(conveyorvel > 0.0f ? -3.0f : 3.0f, -1.3f), 0.0f);
		b2FixtureDef fix3;
		fix3.shape = &shape3;
		fix3.density = 0.1f;
		fix3.friction = 0.6f;
		if (density > 0.0f)
			body->CreateFixture(&fix3);
		body->CreateFixture(&fix);
	}
	else
	{
		texindex = 12;
		shape.SetAsBox(size.x, size.y);
		b2FixtureDef fix;
		fix.shape = &shape;
		fix.density = density;
		fix.userData = this;
		fix.friction = 0.9f;
		body->CreateFixture(&fix);
	}
	drawshape.setSize(sf::Vector2f(size.x * 2.0f, size.y * 2.0f));
	drawshape.setOrigin(size.x, size.y);
	if (conveyorvel < 0.0f)
		drawshape.scale(sf::Vector2f(-1.0f, 1.0f));
}

Box::Box(const int tex, const int exptex, b2World* world, b2Vec2 size, b2Vec2 pos, float ang, sf::Sound* hitsound) //räjähtävä boksi
{
	name = "Box1:";
	size_ = size;
	destructible = true;
	health = 10.0f;
	explodes = true;
	isdrawn = true;
	collidesound = new sf::Sound(*hitsound);
	explosionmass = 150.0f;
	explosionvel = 4500.0f;
	texindex = tex;
	exptexindex = exptex;
	b2BodyDef bodydef;
	bodydef.type = b2_dynamicBody;
	bodydef.position = b2Vec2(pos.x, pos.y);
	bodydef.angle = ang;
	body = world->CreateBody(&bodydef);
	body->SetUserData(this);
	b2PolygonShape shape;
	shape.SetAsBox(size.x, size.y);
	b2FixtureDef fix;
	fix.shape = &shape;
	fix.density = 1.0f;
	fix.friction = 0.6f;
	body->CreateFixture(&fix);
	drawshape.setSize(sf::Vector2f(size.x * 2.0f, size.y * 2.0f));
	drawshape.setOrigin(size.x, size.y);
}

Box::Box(const int tex, b2World* world, b2Vec2 size, b2Vec2 pos, float density, float ang,  sf::Sound* hitsound) //boksi(jos density = 0, staattinen, muutoin dynaaminen)
{
	name = "Box2:";
	size_ = size;
	density_ = density;
	destructible = false;
	explodes = false;
	isdrawn = true;
	texindex = tex;
	collidesound = new sf::Sound(*hitsound);
	b2BodyDef bodydef;
	if (density > 0.0f)
		bodydef.type = b2_dynamicBody;
	else
		bodydef.type = b2_staticBody;
	bodydef.position = b2Vec2(pos.x, pos.y);
	bodydef.angle = ang;
	body = world->CreateBody(&bodydef);
	body->SetUserData(this);
	b2PolygonShape shape;
	shape.SetAsBox(size.x, size.y);
	b2FixtureDef fix;
	fix.shape = &shape;
	fix.density = density;
	fix.friction = 0.6f;
	body->CreateFixture(&fix);
	drawshape.setSize(sf::Vector2f(size.x * 2.0f, size.y * 2.0f));
	drawshape.setOrigin(size.x, size.y);
}

Ball::Ball(const int tex, b2World* world, float size, b2Vec2 pos, sf::Sound* hitsound, b2Vec2 goalpos, b2Vec2 goalsize, float goalangle)
{
	name = "Ball:";
	size_ = b2Vec2(size * 2.0f, size * 2.0f);
	setGoalArea(goalpos, goalsize, goalangle / (180.0f / 3.141592654f));
	destructible = false;
	explodes = false;
	isdrawn = true;
	texindex = tex;
	collidesound = new sf::Sound(*hitsound);
	b2BodyDef bodydef;
	bodydef.type = b2_dynamicBody;
	bodydef.position = b2Vec2(pos.x, pos.y);
	body = world->CreateBody(&bodydef);
	body->SetUserData(this);
	b2CircleShape shape;
	shape.m_p.Set(0.0f, 0.0f);
	shape.m_radius = size;
	b2FixtureDef fix;
	fix.shape = &shape;
	fix.density = 1.0f;
	fix.friction = 0.6f;
	fix.restitution = 0.5f;
	body->CreateFixture(&fix);
	drawshape.setSize(sf::Vector2f(size * 2.0f, size * 2.0f));
	drawshape.setOrigin(size, size);

}

BouncingBox::BouncingBox(const int tex, b2World* world, b2Vec2 size, b2Vec2 pos, float density, float ang, float restitution, sf::Sound* hitsound, b2Vec2 goalpos, b2Vec2 goalsize, float goalangle) //Bouncing Staattinen boksi
{
	name = "BouncingBox:";
	size_ = size;
	density_ = density;
	setGoalArea(goalpos, goalsize, goalangle);
	//destructible = false;
	//explodes = false;'destructible = true;
	health = 120.0f;
	explodes = true;
	isdrawn = true;
	collidesound = new sf::Sound(*hitsound);
	explosionmass = 150.0f;
	explosionvel = 4500.0f;
	isdrawn = true;
	texindex = tex;
	collidesound = new sf::Sound(*hitsound);
	b2BodyDef bodydef;
	if (density > 0.0f)
		bodydef.type = b2_dynamicBody;
	else
		bodydef.type = b2_staticBody;
	bodydef.position = b2Vec2(pos.x, pos.y);
	bodydef.angle = ang;
	body = world->CreateBody(&bodydef);
	body->SetUserData(this);
	b2PolygonShape shape;
	shape.SetAsBox(size.x, size.y);
	b2FixtureDef fix;
	fix.shape = &shape;
	fix.density = density;
	fix.friction = 0.6f;
	fix.restitution = restitution;
	body->CreateFixture(&fix);
	drawshape.setSize(sf::Vector2f(size.x * 2.0f, size.y * 2.0f));
	drawshape.setOrigin(size.x, size.y);
}

Particle::Particle(const int tex, b2World* world, float size, b2Vec2 pos, b2Vec2 vel, float density, int lifet, float drag)
{
	destructible = false;
	explodes = false;
	timetodie = false;
	isdrawn = true;
	lifetime = (int)(lifet * rand() / float(RAND_MAX) * 0.3f + 0.7f);
	b2BodyDef bodydef;
	bodydef.type = b2_dynamicBody;
	bodydef.position = b2Vec2(pos.x, pos.y);
	bodydef.bullet = true;
	bodydef.gravityScale = 0.0f;
	bodydef.linearDamping = drag;
	bodydef.fixedRotation = true;
	bodydef.angle = 2.0f * 3.1415f*rand() / float(RAND_MAX);
	bodydef.linearVelocity = b2Vec2(vel.x + 6.0f * (rand() / float(RAND_MAX) - 0.5f), vel.y + 6.0f * (rand() / float(RAND_MAX) - 0.5f));
	body = world->CreateBody(&bodydef);
	body->SetUserData(this);
	b2CircleShape shape;
	shape.m_p.Set(0.0f, 0.0f);
	shape.m_radius = size;
	b2FixtureDef fix;
	fix.shape = &shape;
	fix.density = density;
	fix.filter.groupIndex = -1;
	fix.restitution = 0.999f;
	body->CreateFixture(&fix);
	texindex = tex;
	drawshape.setSize(sf::Vector2f(size * 2.0f, size * 2.0f));
	drawshape.setOrigin(size, size);
}

void Particle::update(std::list<Baseobject*>* boxes, sf::RenderWindow* window, std::vector<sf::Texture*>* texvector)
{
	lifetime--;
	if (lifetime <= 0)
		timetodie = true;
}

Rocket::Rocket(const int tex, const int exptex, b2World* world, b2Vec2 size, b2Vec2 pos, float acc, float density, float ang, sf::Sound* hitsound, sf::Sound* activesound)
{
	name = "Rocket:";
	size_ = size;
	density_ = density;
	active = false;
	thrust = acc;
	collidesound = new sf::Sound(*hitsound);
	loopsound = new sf::Sound(*activesound);
	loopsound->setLoop(false);
	loopsound->play();
	destructible = true;
	health = 50.0f;
	explodes = true;
	isdrawn = true;
	timetodie = false;
	explosionmass = 550.0f;
	explosionvel = 1600.0f;
	texindex = tex;
	exptexindex = exptex;
	b2BodyDef bodydef;
	if (density > 0.0f)
		bodydef.type = b2_dynamicBody;
	else
		bodydef.type = b2_staticBody;
	bodydef.position = b2Vec2(pos.x, pos.y);
	bodydef.bullet = true;
	bodydef.angle = ang;
	body = world->CreateBody(&bodydef);
	body->SetUserData(this);
	b2PolygonShape shape;
	shape.SetAsBox(size.x / 3.0f, size.y);
	b2FixtureDef fix;
	fix.shape = &shape;
	fix.friction = 0.6f;
	fix.density = density;
	body->CreateFixture(&fix);
	drawshape.setSize(sf::Vector2f(size.x * 2.0f, size.y * 2.0f));
	drawshape.setOrigin(size.x, size.y);
}

void Rocket::update(std::list<Baseobject*>* boxes, sf::RenderWindow* window, std::vector<sf::Texture*>* texvector)
{
	if (active)
	{
		health = 10.0f;
		float ang = body->GetAngle();
		b2Vec2 force = b2Vec2(-thrust * cos(ang + 3.1415f * 0.5f), -thrust * sin(ang + 3.1415f * 0.5f));
		b2Vec2 vec = b2Vec2(-cos(ang + 3.1415f * 0.5f), -sin(ang + 3.1415f * 0.5f));
		body->ApplyForce(force, body->GetWorldCenter() + vec, true);
		for (int i = 0; i < 3; i++)
		{
			float ang2 = (i - 1) / 60.0f * 2.0f * 3.1415f;
			b2Vec2 vel = body->GetLinearVelocity();
			Particle* p = new Particle(exptexindex, body->GetWorld(), .3f, body->GetWorldCenter() + b2Vec2(2.5f * cos(ang2 + ang + 3.1415f * 0.5f), 2.5f * sin(ang2 + ang + 3.1415f * 0.5f)), vel, 0, 300, 0.4f);
			p->name = "P";
			p->body->ApplyForceToCenter(b2Vec2(-force.x / 4.0f, -force.y / 4.0f), true);
			boxes->push_back(p);
		}
	}
	else
		health = 1000.0f;
}
Laser::Laser(const int tex, b2World* world, b2Vec2 size, b2Vec2 pos, float density, float ang, sf::Sound* hitsound, sf::Sound* loopsound) //laseremitteri(jos density > 0, dynaaminen, muuten staattinen)
{
	name = "Laser:";
	size_ = size;
	density_ = density;
	islaser = true;
	destructible = false;
	explodes = false;
	isdrawn = true;
	texindex = tex;
	loopsound = new sf::Sound(*loopsound);
	loopsound->setLoop(false);
	loopsound->play();
	collidesound = new sf::Sound(*hitsound);
	b2BodyDef bodydef;
	if (density > 0.0f)
		bodydef.type = b2_dynamicBody;
	else
		bodydef.type = b2_staticBody;
	bodydef.position = b2Vec2(pos.x, pos.y);
	bodydef.angle = ang;
	body = world->CreateBody(&bodydef);
	body->SetUserData(this);
	b2PolygonShape shape;
	shape.SetAsBox(size.x, size.y);
	b2FixtureDef fix;
	fix.shape = &shape;
	fix.density = density;
	fix.friction = 0.6f;
	body->CreateFixture(&fix);
	drawshape.setSize(sf::Vector2f(size.x * 2.0f, size.y * 2.0f));
	drawshape.setOrigin(size.x, size.y);
}

void Laser::update(std::list<Baseobject*>* boxes, sf::RenderWindow* window, std::vector<sf::Texture*>* texvector)
{
	int reflections = 0;
	bool breaker = false;
	float ang2 = body->GetAngle();
	b2Vec2 dir = b2Vec2(cos(ang2), sin(ang2));
	b2Vec2 start = body->GetPosition();
	b2Vec2 stop = start+3000.0f*dir;
	while (reflections < 50.0f && !breaker)
	{
		b2RayCastOutput out;
		b2Vec2 norm;
		b2RayCastInput in = b2RayCastInput();
		in.p1 = start;
		in.p2 = stop;
		in.maxFraction = 1.0f;
		b2Fixture* closest = body->GetFixtureList();
		float len = 1.0f;
		bool found = false;
		for (auto box : *boxes)
		{
			for (b2Fixture* fix = box->body->GetFixtureList(); fix; fix = fix->GetNext())
			{
				in.maxFraction = len;
				if (!fix->RayCast(&out, in, 0))
					continue;
				if (out.fraction < len && len > 0.0f)
				{
					norm = out.normal;
					len = out.fraction;
					closest = fix;
					found = true;
				}
			}
		}
		stop = start + len * 3000.0f * dir;
		drawshape.setOrigin(sf::Vector2f(len*3000.0f, 0.5f)*0.5f);
		drawshape.setSize(sf::Vector2f(len*3000.0f, 0.5f));
		drawshape.setPosition(sf::Vector2f((start.x + stop.x)*.5f, (start.y + stop.y)*.5f));
		drawshape.setRotation(atan2((stop.y - start.y), (stop.x - start.x)) * 180.0f / 3.1415f);
		drawshape.setTexture((*texvector)[20]);
		window->draw(drawshape);
		if (found)
		{
			void* p = closest->GetBody()->GetUserData();
			Baseobject* obj = (Baseobject*)closest->GetBody()->GetUserData();
			if (obj)
			{
				if (obj->destructible || obj->thrust > 0)
				{
					obj->amountoflasering += 1;
					breaker = true;
					if (obj->amountoflasering > 100)
					{
						if (obj->thrust == .0f)
						{
							obj->timetodie = true;
							if (obj->explodes)
								obj->explode(boxes, body->GetWorld());
						}
						else
							obj->active = true;
					}
				}
				else
				{
					reflections++;
					start = stop;
					b2Vec2 projection = b2Dot(dir, norm)*norm;
					dir -= 2.0f * projection;
					dir.Normalize();
					stop = stop + 3000.0f * dir;
				}
			}
		}
		else
			break;
	}
}
