class Fire extends Particle
{
  PImage could;
  float size;
  PVector vel;
  PVector off;

  Fire(PVector location) {
    super(location);
    lifespan = 100;
    size = random(3, 5);
    off = new PVector(random(-0.5, 0.5), random(-0.5, 0.5));
    vel = new PVector(mouseX-player.location.x,mouseY-player.location.y); //What location the fire goes towards
    vel.normalize();
    vel.mult(5.0);
    vel.add(off);
    velocity = vel;
  }

  void display() {
    
    if(onScreen(location.x,location.y)){
      noStroke();
      if (location.y > height/2)      //Color the Fire depending on y coordinates
        fill(0, lifespan+125, 215, lifespan);
      else
        fill(255, lifespan+125, 0, lifespan);
      ellipse(location.x, location.y, size, size);
     }
     if(size > 0)
     size+=0.5;  //Size depends on how long the particle has been alive 
  }
}

class FireParticleSystem 
{
  ArrayList<Fire> fires;
  PVector origin;
  float x;
  float y;
  float speed;
  float size;

  FireParticleSystem(PVector location) { 
    origin = location.get();
    x = origin.x;
    y = origin.y;
    fires = new ArrayList<Fire>();
  }

  void addParticle() {
    fires.add(new Fire(origin));
  }

  void run() {
    move();
    for (int i = fires.size()-1; i >= 0; i--) {
      Fire c = fires.get(i);
      c.run();
      if (c.isDead()) {
        fires.remove(i);
      }
    }
  }

  void move() {
    origin.x = player.x; //Fire comes from player
    origin.y = player.y;
  }
  
  ArrayList<Fire> getFires(){ //Used for collision checking against boids
    return fires;
  }
}
