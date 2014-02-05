
void setupIKP() {
  try {
    println( Phidget.getLibraryVersion() );
    ik = new InterfaceKitPhidget();

    ik.addAttachListener(new AttachListener() {
      public void attached(AttachEvent ae) {
        //println("attachment of " + ae);
      }
    }
    );
    ik.addDetachListener(new DetachListener() {
      public void detached(DetachEvent de) {
        //println("detachment of " + de);
      }
    }
    );
    ik.addErrorListener(new ErrorListener() {
      public void error(ErrorEvent ee) {
        //println("error event for " + ee);
      }
    }
    );
    ik.addInputChangeListener(new InputChangeListener() {
      public void inputChanged(InputChangeEvent ie) {
        //println("input event for " + ie);

        if (ie.getState()) {
          shootPressed = true;   //start drawing the arrow
          startTimer = millis();  //timer is used to count how long the button is held down
          elapsed = 0;
          //snd[1].setGain(-10);
          snd[1].trigger(); //HEARTBEAT
          snd[4].trigger(); //BOW DRAW
        }
        if (shootPressed && !ie.getState()) {
          elapsed = millis() - startTimer;   //how many milliseconds was the button held down
          //println(float(elapsed) / 1000 + " seconds elapsed");
          shoot(elapsed);     //shoot the arrow, using elapsed
          reduceCrosshair = 0;
          shootPressed = false;  
          snd[1].stop();
        }
      }
    }
    );
    ik.addOutputChangeListener(new OutputChangeListener() {
      public void outputChanged(OutputChangeEvent oe) {
        //println("output event for " + oe);
      }
    }
    );
    ik.addSensorChangeListener(new SensorChangeListener() {
      public void sensorChanged(SensorChangeEvent se) {
        //println("sensor event for " + se);
        if (se.getIndex() == 6) {  //JOYSTICK Y
          float value = se.getValue(); 
          float n;
         
          //if(!pressed)
          n = map(value, 0, 1000, 0, height);
         //  else
         // n = map(value, 0, 1000, arrowYmap-200, arrowYmap+200);
          arrowY = n;
        }
        if (se.getIndex() == 7) {   //JOYSTICK X
          //println("X: " + se.getValue());
          float value = se.getValue();
          float m;
         
        //  if(!pressed)
          m = map(value, 0, 1000, 0, width);
        //  else
         // m = map(value, 0, 1000, arrowXmap-200, arrowXmap+200);
          arrowX = m;
        }
        if (se.getIndex() == 0) {  //ROTATION SENSOR
          float value = se.getValue();
          float m = map(value, 0, 1000, 0, width);
          indianX = m;
        }
      }
    }
    );

    ik.openAny();
    println("waiting for InterfaceKit attachment...");
    ik.waitForAttachment();

    println(ik.getDeviceName());

    delay(500); //Thread.sleep(500);

    if (false) { //retry init
      print("closing...");
      System.out.flush();
      ik.close();
      println(" ok");
      print("reopening...");
      ik.openAny();
      println(" ok");
      ik.waitForAttachment();
    }

    if (ik.getInputCount() > 8) {
      println("input(7,8) = (" +
        ik.getInputState(7) + "," +
        ik.getInputState(8) + ")");
    }

    if (false) {
      print("turn on outputs (slowly)...");
      for (int i = 0; i < ik.getOutputCount() ; i++) {
        ik.setOutputState(i, true);
        try {
          delay(1000); //Thread.sleep(1000);
        } 
        catch (Exception e) {
        }
      }
      println(" ok");
    }

    if (false) {
      for (;;) {
        try {
          delay(1000); //Thread.sleep(1000);
        } 
        catch (Exception e) {
        }
      }
    }

    for (int j = 0; j < 1000 ; j++) {
      for (int i = 0; i < ik.getOutputCount(); i++) {
        ik.setOutputState(i, true);
      }
      for (int i = 0; i < ik.getOutputCount(); i++) {
        ik.setOutputState(i, false);
      }
    }

    if (false) {
      println("toggling outputs like crazy");
      boolean o[] = new boolean[ik.getOutputCount()];
      for (int i = 0; i < ik.getOutputCount(); i++) {
        o[i] = ik.getOutputState(i);
      }
      for (int i = 0; i < 100000; i++) {
        int n = (int)(Math.random() * ik.getOutputCount());
        ik.setOutputState(n, !o[n]);
        println("setOutputState " + n +
          ": " + !o[n]);
        o[n] = !o[n];
        try {
          delay(1); //Thread.sleep(1);
        } 
        catch (Exception e) {
        }
      }
    }

    //println("Outputting events.  Input to stop.");
    //System.in.read();
  } 
  catch (PhidgetException ex) {
    println(ex);
  }
}


void closeIKP() {
  try {
    print("closing...");
    ik.close();
    ik = null;
    println(" ok");
    if (false) {
      println("wait for finalization...");
      System.gc();
    }
  } 
  catch (PhidgetException ex) {
    println(ex);
  }
}




