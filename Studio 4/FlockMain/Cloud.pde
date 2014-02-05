class Cloud extends Particle
{
  PImage cloudimg_;
  float size;

  Cloud(PVector location, float size_) {
    super(location);
    lifespan = 100;
    size = size_;
    velocity = new PVector(random(-0.1, 0.1), random(-0.1, 0.1));
    cloudimg_ = cloudimg;
  }

  void display() {
    noStroke();
    tint(blue, lifespan);
    image(cloudimg_, location.x, location.y, size*2, size);
    noTint();
  }
}

//A MANAGER FOR CloudParticleSystems below
class CloudGenerator
{
  ArrayList<CloudParticleSystem> cloudsystem;

  CloudGenerator(int amount)
  {
    cloudsystem = new ArrayList<CloudParticleSystem>();
    for (int i = 0; i < amount; i++)
      cloudsystem.add(new CloudParticleSystem(new PVector(random(0, width-200), random(0, height/4))));
  }
}

class CloudParticleSystem 
{
  ArrayList<Cloud> clouds;
  PVector origin;
  float x;
  float y;
  float speed;
  float size;

  CloudParticleSystem(PVector location) { 
    origin = location.get();
    x = origin.x;
    y = origin.y;
    clouds = new ArrayList<Cloud>();
    size = random(50, 200); //size of the cloud
  }

  void addParticle() {
    clouds.add(new Cloud(origin, size));
  }

  void run() {
    move();
    for (int i = clouds.size()-1; i >= 0; i--) {
      Cloud c = clouds.get(i);
      c.run();
      if (c.isDead()) {
        clouds.remove(i);
      }
    }
  }

  void move() {
    x += 0.1;
    if (x+size*2 > width) { //if off screen place to x = 0
      x = 0;
      origin.y = random(0, height/4);
    }
    origin.x = x;
  }
}











