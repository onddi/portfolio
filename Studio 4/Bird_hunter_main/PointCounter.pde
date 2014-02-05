void drawPoints() {
    //POINT MULTIPLIER
    textSize(20);    
    si =  str(hits);
    fill(0);
    text("Hits " + si, 0, 10);
    
    mipm = str(pointMultiplier*pointMultiplier);
    fill(0);
    text("Multiplied points " + mipm, 0, 25);
    
   
    po = str (points);
    text("Points " + po,0,40);
    
    aw = str(30-arrowsShot);
    text("Arrows left: " + aw, 0,55);
    
    mi = str(pointMultiplier);
    fill(0);
    //text(mi+"x", 0, 25);
  }
