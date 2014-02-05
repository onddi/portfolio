class Bird {

  FCircle bird;
  PVector position;
  PVector velocity;

  Bird(PVector origin) { 
    //position = new PVector(origin.x,origin.y);
    position = new PVector(random(0, width), height-110);
    velocity = new PVector(random(-400, 400), random(-400, 0));
    
    setupBirdBody();
    
  }

  void update() {
    position.x = bird.getX();
    position.y = bird.getY();
    bird.addForce(velocity.x, -400);
    constraints();
  }
  
  void setupBirdBody(){
    bird = new FCircle(30);
    //angrybird.resize(0, 30); 
    bird.attachImage(angrybird);
    bird.setPosition(position.x, position.y);
    bird.setVelocity(velocity.x, velocity.y);
    bird.setRestitution(0.5);
    bird.setFriction(0.5);
    bird.setDensity(1);
    bird.setRotation(angle);
    bird.setBullet(true);
    bird.setGrabbable(false);
    world.add(bird);
  }

  FCircle getBird() {
    return bird;
  }

  PVector getPosition() { 
    return position;
  }

  void constraints() {
    //CONSTRAINTS
    if (bird.getX() >= width || bird.getX() <= 0 || bird.getY() < -50) 
      removeBird();
   
  }

  void removeBird() {     
    ArrayList<Bird> b = birdcontainer.getBirds();
    for (int i = 0; i < b.size(); i++)
      if (b.get(i).getBird() == bird)
        b.remove(i);
    world.remove(bird);
  }
}

//BIRDCONTAINER contains/manages all birds

class BirdContainer {

  ArrayList<Bird> birds;
  PVector birdorigin;

  BirdContainer() {
    birds = new ArrayList<Bird>();
    birdorigin = new PVector(width/2, height/2);
    birds.add(new Bird(birdorigin));
  }

  void update() {

    for (int i = 0; i < birds.size(); i++) {
      Bird b = birds.get(i);
      b.update();
      FCircle f = b.getBird();

      if (b.getPosition().y > height) {
        birds.remove(i);
        world.remove(f);
      }
    }
  }

  void addBird() {
    //int size = (int)random(1,5);
    //for(int i = 0; i < size; i++)
    birds.add(new Bird(birdorigin));
  }
  
  //Birds movement can be controlled with this function
  void moveBirds(int direction) {
    for (int i = 0; i < birds.size(); i++) {
      Bird b = birds.get(i);
      FCircle f = b. getBird();
      if (direction == 0)
        f.addForce(0, -1000);
      else if(direction == 1)
        //f.addForce(0,500);
        f.setVelocity(-10, 0);
        //f.adjustVelocity(-100,0);
    }
  }

  ArrayList<Bird> getBirds() { 
    return birds;
  }
  
  void removeAllBirds(){
    for (int i = 0; i < birds.size(); i++){
      FCircle birdBody = birds.get(i).getBird();
      world.remove(birdBody);
    }
    birds.clear();
  }
}

