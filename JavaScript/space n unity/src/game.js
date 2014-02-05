Game = {
  // The total width of the game screen. 
  width: function() {
    return window.innerWidth;
  },
   // The total height of the game screen. 
  height: function() {
    return 800;
  },
   // Initialize and start our game
  start: function() {
    // Start crafty and set a background color so that we can see it's working
    Crafty.init(); //ADD Game.width(), Game.height() if want a certain size
    //Crafty.background("WHITE");
 
    // Simply start the "Loading" scene to get things going
    Crafty.scene('Loading');
  }
}

//LÄHTEET: https://github.com/craftyjs/Crafty/blob/master/demos/asteroids/images/sprite.png 
//http://buildnewgames.com/introduction-to-crafty/
//http://www.last.fm/music/Stellardrone
 
