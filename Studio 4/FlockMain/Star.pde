class Star { 
  float x, y; // location
  float dim; // dimension
  color c; // color

  Star(float x, float y) {
    this.x = x;
    this.y = y;
    dim = random(1, 3);
    c = color(255, random(199, 200));
  }

  void display() {         //Stars are different depending on the y coordinates
    if (this.y < height/2)
      fill(255, random(100, 200));
    else {
      fill(0, random(100, 255), random(100, 255), random(0, 255));
      this.y += random(0.01, 0.5);
      this.x += random(-0.5, 0.5);
      if (this.y > height)
        this.y = height/2;
    }
    ellipse(x, y, dim, dim); // a circle at position xy
  }
}

