// The Boid class - Implemented from http://processing.org/examples/flocking.html with changes to steering and avoiding

class Boid {

  PVector location;
  PVector velocity;
  PVector acceleration;
  float r;
  float maxforce;    // Maximum steering force
  float maxspeed;    // Maximum speed

    float trget = 0;

  Boid(float x, float y) {
    acceleration = new PVector(0, 0);

    // This is a new PVector method not yet implemented in JS
    // velocity = PVector.random2D();

    // Leaving the code temporarily this way so that this example runs in JS
    float angle = random(TWO_PI);
    velocity = new PVector(cos(angle), sin(angle));

    location = new PVector(x, y);
    r = 1.5;
    maxspeed = 1;
    maxforce = 0.08;
  }

  PVector steer(PVector target, boolean arrival)
  {
    PVector steer = new PVector(); //creates vector for steering
    if (!arrival)
    {
      steer.set(PVector.sub(target, location)); //steering vector points towards target (switch target and pos for avoiding)
      steer.limit(maxforce); //limits the steering force to maxSteerForce
    }
    else
    {
      PVector targetOffset = PVector.sub(target, location);
      float distance=targetOffset.mag();
      float rampedSpeed = maxspeed*(distance/100);
      float clippedSpeed = min(rampedSpeed, maxspeed);
      PVector desiredVelocity = PVector.mult(targetOffset, (clippedSpeed/distance));
      steer.set(PVector.sub(desiredVelocity, velocity));
    }
    return steer;
  }

  //avoid. If weight == true avoidance vector is larger the closer the boid is to the target
  PVector avoid(PVector target, boolean weight)
  {
    PVector steer = new PVector(); //creates vector for steering
    steer.set(PVector.sub(location, target)); //steering vector points away from target
    if (weight)
      steer.mult(1/sq(PVector.dist(location, target)));
    //steer.limit(maxSteerForce); //limits the steering force to maxSteerForce
    return steer;
  }

  void run(ArrayList<Boid> boids) {
    
    if (!avoidWalls) 
    {
      acceleration.add(PVector.mult(avoid(new PVector(location.x, height/2), true), 2)); //Middle of the screen
      acceleration.add(PVector.mult(avoid(new PVector(location.x, height), true), 5)); 
      acceleration.add(PVector.mult(avoid(new PVector(location.x, 0), true), 5));
      acceleration.add(PVector.mult(avoid(new PVector(width, location.y), true), 5));
      acceleration.add(PVector.mult(avoid(new PVector(0, location.y), true), 5));
      acceleration.add(PVector.mult(avoid(new PVector(ps.x, ps.y), true), 20)); //Avoid the sun
    }

    if(!breeding) //If spitting fire avoid mouse position
    acceleration.add(PVector.mult(avoid(new PVector(mouseX, mouseY), true), 8));

    flock(boids);
    update();
    borders(); //Check borders
    render();
  }

  void applyForce(PVector force) {
    // We could add mass here if we want A = F / M
    acceleration.add(force);
  }

  // We accumulate a new acceleration each time based on three rules
  void flock(ArrayList<Boid> boids) {
    PVector sep = separate(boids);   // Separation
    PVector ali = align(boids);      // Alignment
    PVector coh = cohesion(boids);   // Cohesion
    // Arbitrarily weight these forces
    sep.mult(1.5);
    ali.mult(1.0);
    coh.mult(1.0);
    // Add the force vectors to acceleration
    applyForce(sep);
    applyForce(ali);
    applyForce(coh);
  }

  // Method to update location
  void update() {
    // Update velocity
    velocity.add(acceleration);
    // Limit speed
    velocity.limit(maxspeed);
    location.add(velocity);

    // Reset accelertion to 0 each cycle
    acceleration.mult(0);
  }

  // A method that calculates and applies a steering force towards a target
  // STEER = DESIRED MINUS VELOCITY
  PVector seek(PVector target) {
    PVector desired = PVector.sub(target, location);  // A vector pointing from the location to the target
    // Scale to maximum speed
    desired.normalize();
    desired.mult(maxspeed);

    // Above two lines of code below could be condensed with new PVector setMag() method
    // Not using this method until Processing.js catches up
    // desired.setMag(maxspeed);

    // Steering = Desired minus Velocity
    PVector steer = PVector.sub(desired, velocity);
    steer.limit(maxforce);  // Limit to maximum steering force
    return steer;
  }


  void render() {
    // Draw a triangle rotated in the direction of velocity
    float theta = velocity.heading2D() + radians(90);
    // heading2D() above is now heading() but leaving old syntax until Processing.js catches up

    fill(200, 100);
    //stroke(255);
    noStroke();
    pushMatrix();
    translate(location.x, location.y);
    rotate(theta);
    float flap = random(r*6, r*16);

    //DRAW FISH OR BIRD SHAPES
    if (location.y > height/2) { //FISHES
      fill(255, blue);
      beginShape(TRIANGLES);
      vertex(0, r);
      vertex(-2*r, 4*r);
      vertex(2*r, 4*r);
      endShape();
      ellipseMode(CENTER);
      ellipse(0, -r, 4*r, 8*r);
      fill(0, 255, 100);
      ellipse(0, 0, 2, 2);
    }
    else if (location.y < height/2) { //BIRDS
      fill(0);
      ellipse(0, 0, flap, r*2*2);
      beginShape();
      vertex(0, -r*2*2);
      vertex(-r*2, r*2*2);
      vertex(r*2, r*2*2);
      endShape();
      fill(255, 50, 0);
      ellipse(0, 0, 2, 2);
    }
    else if (location.y == height/2) {
      fill(255, 0, 0);
      ellipse(location.x, location.y, 50, 50);
    }
    popMatrix();
    noFill();
  }

  // Wraparound
  void borders() {
    if (location.x < -r) location.x = width+r;
    if (location.y < -r) location.y = height+r;
    if (location.x > width+r) location.x = -r;
    if (location.y > height+r) location.y = -r;
  }

  // Separation
  // Method checks for nearby boids and steers away
  PVector separate (ArrayList<Boid> boids) {
    float desiredseparation = 25.0f;
    PVector steer = new PVector(0, 0, 0);
    int count = 0;
    // For every boid in the system, check if it's too close
    for (Boid other : boids) {
      float d = PVector.dist(location, other.location);
      // If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
      if ((d > 0) && (d < desiredseparation)) {
        // Calculate vector pointing away from neighbor
        PVector diff = PVector.sub(location, other.location);
        diff.normalize();
        diff.div(d);        // Weight by distance
        steer.add(diff);
        count++;            // Keep track of how many
      }
    }
    // Average -- divide by how many
    if (count > 0) {
      steer.div((float)count);
    }

    // As long as the vector is greater than 0
    if (steer.mag() > 0) {
      // First two lines of code below could be condensed with new PVector setMag() method
      // Not using this method until Processing.js catches up
      // steer.setMag(maxspeed);

      // Implement Reynolds: Steering = Desired - Velocity
      steer.normalize();
      steer.mult(maxspeed);
      steer.sub(velocity);
      steer.limit(maxforce);
    }
    return steer;
  }

  // Alignment
  // For every nearby boid in the system, calculate the average velocity
  PVector align (ArrayList<Boid> boids) {
    float neighbordist = 50;
    PVector sum = new PVector(0, 0);
    int count = 0;
    for (Boid other : boids) {
      float d = PVector.dist(location, other.location);
      if ((d > 0) && (d < neighbordist)) {
        sum.add(other.velocity);
        count++;
      }
    }
    if (count > 0) {
      sum.div((float)count);
      // First two lines of code below could be condensed with new PVector setMag() method
      // Not using this method until Processing.js catches up
      // sum.setMag(maxspeed);

      // Implement Reynolds: Steering = Desired - Velocity
      sum.normalize();
      sum.mult(maxspeed);
      PVector steer = PVector.sub(sum, velocity);
      steer.limit(maxforce);
      return steer;
    } 
    else {
      return new PVector(0, 0);
    }
  }

  // Cohesion
  // For the average location (i.e. center) of all nearby boids, calculate steering vector towards that location
  PVector cohesion (ArrayList<Boid> boids) {
    float neighbordist = 50;
    PVector sum = new PVector(0, 0);   // Start with empty vector to accumulate all locations
    int count = 0;
    for (Boid other : boids) {
      float d = PVector.dist(location, other.location);
      if ((d > 0) && (d < neighbordist)) {
        sum.add(other.location); // Add location
        count++;
      }
    }
    if (count > 0) {
      sum.div(count);
      return seek(sum);  // Steer towards the location
    } 
    else {
      return new PVector(0, 0);
    }
  }
}


