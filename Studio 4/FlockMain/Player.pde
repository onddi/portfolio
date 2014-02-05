class Player
{
  //The Player class uses follow and easing for moving
  PVector location = new PVector();
  float x = 0;
  float y = 0;
  float angle;

  float[] x_ = new float[60];
  float[] y_ = new float[60];
  float easing = 0.01;
  float segLength = 1;
  
  float colorX,colorY;

  Player() {
  }

  void display() {
    
    colorX = (float)(width/2-mouseX)/1000;
    colorY = (float)(width/2-mouseY)/1000;

    strokeWeight(2);
    stroke(255*colorY, 30, 255*colorX, 255);
    fill(255*colorY, 0, 255*colorX);

    dragSegment(0, mouseX, mouseY);
    for (int i=0; i<this.x_.length-1; i++) {
      dragSegment(i+1, this.x_[i], this.y_[i]);
    }

    drawHead();

    noStroke();
    strokeWeight(1);
    location.x = x;
    location.y = y;
  }

  void dragSegment(int i, float xin, float yin) {
    float dx = xin - this.x_[i];
    float dy = yin - this.y_[i];
    angle = atan2(dy, dx);

    float targetX = xin;
    float tx = targetX - this.x_[i];
    if (abs(tx) > 1) {
      this.x_[i] += tx * easing;
    }

    float targetY = yin;
    float ty = targetY - this.y_[i];
    if (abs(ty) > 1) {
      this.y_[i] += ty * easing;
    }

    segment(this.x_[i], this.y_[i], angle);
  }

  void segment(float x, float y, float a) {
    pushMatrix();
    translate(x, y);
    rotate(a);
    beginShape();
    triangle(-5, -5, 5, 5, -5, 5);
    endShape();
    popMatrix();
  }

  void drawHead() {

    float dx = mouseX - this.x;
    float dy = mouseY - this.y;
    angle = atan2(dy, dx);    

    float tx = mouseX - this.x;
    if (abs(tx) > 1) {
      this.x += tx * easing;
    }

    float ty = mouseY - this.y;
    if (abs(ty) > 1) {
      this.y += ty * easing;
    }
    
    //Dragon head
    pushMatrix();
    translate(x, y);
    rotate(angle);
    beginShape();
    ellipse(10, 0, 40, 10);
    ellipse(30, 3, 5, 3);
    ellipse(30, -3, 5, 3);
    ellipse(0, 0, 5, 5);
    star(9, 0, 0, 30, 30, 0, 0.5); //Star shape
    endShape(CLOSE);
    popMatrix();

    pushMatrix();
    translate(mouseX, mouseY);
    float t = millis()/1000.0f;
    noStroke();
    //CIRCLING BALLS
    ellipse((int)(15*cos(t)), (int)(15*sin(t)), 5, 5);
    ellipse((int)(15*cos(t+PI)), (int)(15*sin(t+PI)), 5, 5);
    ellipse((int)(15*cos(t+PI/4*3)), (int)(15*sin(t+PI/4*3)), 5, 5);
    ellipse((int)(15*cos(t+PI/2)), (int)(15*sin(t+PI/2)), 5, 5);
    ellipse((int)(15*cos(t+PI/4)), (int)(15*sin(t+PI/4)), 5, 5);

    ellipse((int)(15*cos(t-PI/4*3)), (int)(15*sin(t-PI/4*3)), 5, 5);
    ellipse((int)(15*cos(t-PI/2)), (int)(15*sin(t-PI/2)), 5, 5);  
    ellipse((int)(15*cos(t-PI/4)), (int)(15*sin(t-PI/4)), 5, 5);
    popMatrix();
  }

  void star(int n, float cx, float cy, float w, float h, 
  float startAngle, float proportion)
  {
    if (n > 2)
    {
      float angle = TWO_PI/ (2 *n);  // twice as many sides
      float dw; // draw width
      float dh; // draw height

      w = w / 2.0;
      h = h / 2.0;

      beginShape();
      for (int i = 0; i < 2 * n; i++)
      {
        dw = w;
        dh = h;
        if (i % 2 == 1) // for odd vertices, use short radius
        {
          dw = w * proportion;
          dh = h * proportion;
        }
        vertex(cx + dw * cos(startAngle + angle * i), 
        cy + dh * sin(startAngle + angle * i));
      }
      endShape(CLOSE);
    }
  }
}


