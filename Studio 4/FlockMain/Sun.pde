class Particle {
  PVector location;
  PVector velocity;
  PVector acceleration;
  float lifespan;

  Particle(PVector l) {
    acceleration = new PVector(0, 0);
    velocity = new PVector(random(-0.1, 0.1), random(-0.1, 0.1));
    location = l.get();
    lifespan = 255.0;
  }

  void run() {
    update();
    display();
  }

  // Method to update location
  void update() {
    velocity.add(acceleration);
    location.add(velocity);
    lifespan -= 1;
  }

  // Method to display
  void display() {
    
    noStroke();

    if (location.y > height/2)
      fill(0, lifespan, 215, lifespan);
    else
      fill(255, lifespan, 0, lifespan);

    ellipse(location.x, location.y, 70, 70);
  }

  // Is the particle still useful?
  boolean isDead() {
    if (lifespan < 0.0) {
      return true;
    } 
    else {
      return false;
    }
  }
}

// A class to describe a group of Particles
// An ArrayList is used to manage the list of Particles 

//The Sun uses http://processing.org/examples/movingoncurves.html for moving
class ParticleSystem {
  ArrayList<Particle> particles;
  PVector origin;
  float beginX = 100.0;  // Initial x-coordinate
  float beginY = 100.0;  // Initial y-coordinate
  float endX = 100.0;   // Final x-coordinate
  float endY = 100.0;   // Final y-coordinate
  float distX;          // X-axis distance to move
  float distY;          // Y-axis distance to move
  float exponent = 4;   // Determines the curve
  float x;        // Current x-coordinate
  float y;        // Current y-coordinate
  float step = 0.002;    // Size of each step along the path
  float pct = 0.0;      // Percentage traveled (0.0 to 1.0)

  ParticleSystem(PVector location) {
    origin = location.get();
    x = origin.x;
    y = origin.y;
    distX = endX - beginX;
    distY = endY - beginY;
    //origin = new PVector(100,100);
    particles = new ArrayList<Particle>();
  }

  void addParticle() {
    particles.add(new Particle(origin));
  }

  void run() {
    move();
    for (int i = particles.size()-1; i >= 0; i--) {
      Particle p = particles.get(i);
      p.run();
      if (p.isDead()) {
        particles.remove(i);
      }
    }
  }

  void move() {
    pct += step;
    if (pct < 1.0) {
      x = beginX + (pct * distX);
      y = beginY + (pow(pct, exponent) * distY);
    }
    origin.x = x;
    origin.y = y;
  }
}

//Used to calculate moving on a curve (http://processing.org/examples/movingoncurves.html)
void moveParticle() {
  //PARTICLE MOVE

  ps.pct = 0.0;
  ps.beginX = ps.x;
  ps.beginY = ps.y;
  ps.endX = mouseX;
  ps.endY = mouseY;
  ps.distX = ps.endX - ps.beginX;
  ps.distY = ps.endY - ps.beginY;

  //PARTICLE MOVE END
}

