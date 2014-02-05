// A simple Particle class

class Particle {
  PVector location;
  PVector velocity;
  PVector acceleration;
  float lifespan;

  Particle(PVector l) {
    acceleration = new PVector(0, 0);
    velocity = new PVector(random(-.5, 0.5), random(-0.5, 0.5));
    location = l.get();
    lifespan = 50.0;
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
    stroke(255, lifespan);
    fill(255, lifespan);
    ellipse(location.x, location.y, 2, 2);
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

class HitParticle extends Particle
{
  String multipliertext;

  HitParticle(PVector l) {
    super(l);
    //lifespan = 255;
  }
  // Method to display
  void display() {
    stroke(200, 0, 0, lifespan);
    fill(255, 0, 0, lifespan);
    multipliertext = str(pointMultiplier);
    textSize(40);
    text(multipliertext +"x",location.x,location.y);
    //ellipse(location.x, location.y, 16, 16);
    
  }
}

class NoteParticle extends Particle
{  
  PImage noteimg = loadImage("note.png");
  

  NoteParticle(PVector p) {
    super(p);
    velocity = new PVector(random(-1, 1), -2);
    lifespan = 150.0;
  }

  void display() {
    tint(255,0,0, lifespan);
    image(noteimg, location.x, location.y);
    noTint();
    //stroke(200, 0, 0, lifespan);
    //fill(255, 0, 0, lifespan);
    //ellipse(location.x,location.y,15,15);
  }
}

// A class to describe a group of Particles
// An ArrayList is used to manage the list of Particles 

class ParticleSystem {
  ArrayList<Particle> particles;
  PVector origin;
  ArrayList<Particle> hitparticles;
  ArrayList<Particle> noteparticles;

  ParticleSystem(PVector location) {
    origin = location.get();
    particles = new ArrayList<Particle>();
    hitparticles = new ArrayList<Particle>();
    noteparticles = new ArrayList<Particle>();
  }

  void addParticle() {
    for (int i = 0; i < 5; i++)
      particles.add(new Particle(origin));
  }

  void addParticle(PVector position) {
    for (int i = 0; i < 50; i++)
      hitparticles.add(new HitParticle(position));
  }

  void addNoteParticle(PVector position) {
    for (int i = 0; i < 2; i++)
      noteparticles.add(new NoteParticle(position));
  }

  void run() {
    for (int i = particles.size()-1; i >= 0; i--) {
      Particle p = particles.get(i);
      p.run();
      if (p.isDead()) {
        particles.remove(i);
      }
    }
    for (int i = hitparticles.size()-1; i >= 0; i--) {
      Particle p = hitparticles.get(i);
      p.run();
      if (p.isDead()) {
        hitparticles.remove(i);
      }
    }
    for (int i = noteparticles.size()-1; i >= 0; i--) {
      Particle p = noteparticles.get(i);
      p.run();
      if (p.isDead()) {
        noteparticles.remove(i);
      }
    }

    ArrayList<Arrow> arrows_ = arrowContainer.getArrows();
    if (arrows_.size() > 0) {
      for (int i = 0; i < arrows_.size(); i++) {
        Arrow arrow_ = arrows_.get(i);
        origin = arrow_.getPos();
      }
    }
    else
      origin = new PVector(indianX, height-50);
  }
}

