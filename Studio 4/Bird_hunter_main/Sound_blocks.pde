class SoundAnalyzer {

  SoundAnalyzer() {
  }

  void update() {

    fft.forward(mic.mix);

    if ((fft.calcAvg(800, 1200) > 70 || fft.calcAvg(1200, 1600) > 70 || fft.calcAvg(1600, 2000) > 40) && birdcontainer.getBirds().size() < 10){
      //birdcontainer.soundMove();
      particle.addNoteParticle(indian.getPos());
       birdcontainer.addBird();
    }
       
    if(fft.calcAvg(200,400) > 100 || fft.calcAvg(400,800) > 100)
    //birdcontainer.soundMove();
     birdcontainer.moveBirds(1);
    
    //FFT
    /*println("AVG ALL " + fft.calcAvg(0, 3200));
    println("A " + fft.calcAvg(0, 200));
    println("B " + fft.calcAvg(200, 400));
    println("C " + fft.calcAvg(400, 800));
    println("D " + fft.calcAvg(800, 1200));
    println("E " + fft.calcAvg(1200, 1600));
    println("F " + fft.calcAvg(1600, 2000));
    println("G " + fft.calcAvg(2000, 2400));
    println("H " + fft.calcAvg(2400, 2800));
    println("I " + fft.calcAvg(2800, 3200));

    println("LOW " + fft.getBand(75));
    println("HIGH " + fft.getBand(400));
    println(frameRate);*/

    rectMode(CORNERS);
    int w = int(width/fft.avgSize());
    if(showMicInput)
    for (int i = 200; i < fft.avgSize(); i++) //specSize()
    {
      if (i > 350 && i < 450)
        fill(0, 0, 255);
      else if (i > 50 && i < 150)
        fill(0, 255, 0);
      else
        fill(255, 0, 0);
      // draw the line for frequency band i, 
      // scaling it by 4 so we can see it a bit better
      //line(i, height, i, height - fft.getBand(i) * 4);
      rect(i*w, height, i*w+w, height-fft.getAvg(i));
    }
    rectMode(CENTER);
  }
}


