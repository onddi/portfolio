class Flock {
  ArrayList<Boid> boids; // An ArrayList for all the boids

    Flock() {
    boids = new ArrayList<Boid>(); 
  }

  void run() {
    
    for(int i = 0; i < boids.size()-1; i++){
      
      if(!breeding && collisionCheck(boids.get(i).location.x,boids.get(i).location.y) ) //If fire is shot and collision is registered remove boid from flock
          boids.remove(i);
    }
    
    for (Boid b : boids) {            
      b.run(boids);  // Passing the entire list of boids to each boid individually
    }
  }

  void addBoid(Boid b) {
    boids.add(b);
  }
  
  boolean collisionCheck(float x_, float y_){ //Check if boid hits fire particles
    ArrayList<Fire> f = fs.getFires(); //Get the fire particles
    
    for(int i = 0; i < f.size(); i++){
      PVector fp = f.get(i).location; //Get particle location
      if(x_ < fp.x + 10 && x_ > fp.x - 10 && y_ < fp.y + 10 && y_ > fp.y - 10) //draw a box around a location for collision
        return true; //if boid location is inside box return true = collision
      else 
        continue;
    }
    return false; //if no collision return false;
  }
 
}

