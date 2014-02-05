// Game scene - the game is divided into three scenes: loading, game and victory
// -------------
// Runs the core gameplay loop
Crafty.scene('Game', function() {

  Crafty.audio.play('taustamusa');

  //Moving background
  var IMAGE_WIDTH = Game.width();
  var IMAGE_HEIGHT = Game.height();
  var bgspeed = 10000; //ms
  var bg = Crafty.e("2D, Canvas, Image, Tween")  
  .attr({x:-IMAGE_WIDTH, w:IMAGE_WIDTH*13, h:IMAGE_HEIGHT})
  .image("assets/stars.png", "repeat-x")
  .tween({x: -IMAGE_WIDTH}, bgspeed)
  .bind("TweenEnd", function() {                
    this.attr({x:0}); 
    this.tween({x: -IMAGE_WIDTH}, bgspeed)
  });

  //Particle options
  var options={
    maxParticles: 150,
    size: 18,
    sizeRandom: 4,
    speed: 1,
    speedRandom: 1.2,
      // Lifespan in frames
      lifeSpan: 29,
      lifeSpanRandom: 7,
      // Angle is calculated clockwise: 12pm is 0deg, 3pm is 90deg etc.
      angle: 65,
      angleRandom: 34,
      startColour: [255, 131, 0, 1],
      startColourRandom: [48, 50, 45, 0],
      endColour: [245, 35, 0, 0],
      endColourRandom: [60, 60, 60, 0],
      // Only applies when fastMode is off, specifies how sharp the gradients are drawn
      sharpness: 20,
      sharpnessRandom: 10,
      // Random spread from origin
      spread: 10,
      // How many frames should this last
      duration: -1,
      // Will draw squares instead of circle gradients
      fastMode: false,
      gravity: { x: 0, y: 0.1 },
      // sensible values are 0-3
      jitter: 0
    }
    //Two particle systems
    var jetpack = Crafty.e("2D,Canvas,Particles").particles(options).bind("EnterFrame",function(frame){}).attr({x:460,y:-100});  
    var explosion = Crafty.e("2D,Canvas,Particles").particles(options).bind("EnterFrame",function(frame){}).attr({x:460,y:-100});

    var instructions = Crafty.e('2D, DOM, Text').textColor('#A6A6A6')

    var Laskuri = Crafty.e('2D, DOM, Text')
    .attr({ x: 0, y: 0, w:300 })
    .text('Victory!')
    .textColor('#A6A6A6')
    .textFont({size: '10px'});

    var theEnd = Crafty.e('2D, DOM, Text')
    .attr({ x: Game.width()*11, y: 400, w:3000 })
    .text('...to Space @ EPSP2013')
    .textColor('#A6A6A6')
    .textFont({size: '1000px'});

  // PLAYER CHARACTER
  //___________________________
  //
  this.player = Crafty.e('PlayerCharacter')
  .attr({x: 400, y:480})
  .requires("Tween")
  .bind('EnterFrame', function(e){

    //Camera focus on player
    Crafty.viewport.x = (Game.width()/2 - this.x);

    //Help-"menu"
    if(Crafty.viewport.x > 240){
      instructions.attr({ x: -840, y: 400, w:600 })
      .text('A,D or left,right = movement ENTER = retry, SPACE = jetpack Q,E = rotation');
    }else{
      instructions.attr({ x: -240, y: 750, w:150 })
      .text('Buttons <------');
    }

    //Point counter, counts points still left
    if(Crafty('Scorepoint').length)
      Laskuri.attr({x: -Crafty.viewport.x, y: Crafty.viewport.y}).text('Still left: ' + Crafty('Scorepoint').length);
    else
      Laskuri.attr({x: -Crafty.viewport.x, y: Crafty.viewport.y, w:500}).text('Almost there...');

    //Ending location pointer, all scorepoints have to be collected to win
    if(-Crafty.viewport.x > Game.width()){
      if(!Crafty('Scorepoint').length)
        uniteText.text('Together ------>');
      else
        uniteText.text('<------ Go Back');
      uniteText.x = Game.width()*10-30;
      uniteText.y = 180;
    }
    
    //No more points left to collect. Readies player for goal  
    if(!Crafty('Scorepoint').length)
      this.scoreCollected = true;
    //If ending animation finished go to victory screen
    if(this.alpha === 0.0)
      Crafty.scene('Victory');

    //Floor and Roof, restart if player falls of the screen
    if(this.y > Game.height()+40 || this.y < -150 & !this.scoreCollected)
      Crafty.scene('Game');


    //PLAYER MOVEMENT
    //__________________________
    //

    //Moving at what direction
    var movX = this._movement.x
    var movY = this._movement.y

    //Direction speed gain
    if(movX > 0)
      this.dx += 0.01;
    else if(movX < 0)
      this.dx -= 0.01;

    this.x += this.dx;
    this.y += this.dy;

    //KEYS________________
    //SPACE for jetpack
    if(this.isDown('SPACE') && !this.dead){
      Crafty.audio.play('thusters');
      //Jetpack x-speed gain
      if(movX > 0)
        if(this.dx < 2)
          this.dx += 0.1;
        if(movX < 0)
          if(this.dx > -2)
            this.dx -= 0.1;

          //Adjust the particle location close to palyer
          jetpack.y = this.y+this.h/2;
          //Side depends on facing direction
          jetpack.x = (this.toRight) ? this.x-10 : this.x+30;
          jetpack.rotation = -this.rotation;
          //Jetpack y-speed gain, also turn gravity upside down for nice feel
          this.y -= 0.2;
          this.gravityConst(-.06);
          this._up = true;
        }else{
          //If not using jetpack, hide the particle of screen and change gravity back to normal
          Crafty.audio.stop('thusters');
          jetpack.y = -100;
          this.gravityConst(.06);
        }
        if(this.isDown('ENTER'))
          Crafty.scene('Game');

        if(this.isDown(Crafty.keys.E)){
          this.rotation +=4;
        }
        if(this.isDown(Crafty.keys.Q)){
          this.rotation -=4;
        }

      });
  //PLAYER CHARACTER END
  //___________________________________

//Player is dead; moves explosion particles to player location, adds rotation and disables movement 
this.explode = this.bind('playerDead', function(e) {
  this.bind('EnterFrame', function(){    
    explosion.x = this.player.x;
    explosion.y = this.player.y;
    e.rotation += 0.05;
  });
  e.disableControl();
  e.removeComponent('Collision');

  if(!e.dead){
    e.animate('Final', 60,1);
    Crafty.audio.play('alarm');
  }
  e.dead = true;
});

//OTHER CREATIONS
//___________________________________
//

  //function to fill the screen with asteroids by a random amount
  function initRocks(lower, upper) {
    var rocks = Crafty.math.randomInt(lower, upper);
    asteroidCount = rocks;
    lastCount = rocks;
    for(var i = 0; i < rocks; i++) {
      Crafty.e("Actor, big, Collision, Asteroid");
    }
  }
  //between 5, 10 asteroids at start
  initRocks(5, 10);

  //function to add asteroids if they run out
  function spawnRock(lower, upper){
    console.log("SPAWNED");
    var amount = Crafty.math.randomInt(lower,upper);
    asteroidCount+=1;
    Crafty.e("Actor, big, Collision, Asteroid");
  }

  //Planet is the final goal
  Crafty.e('Planet').attr({x:Game.width()*10,y:160});
  //A collision platform for the starting point
  Crafty.e('Platform').makePlatform(370,530,160,30);
  //Start text
  var uniteText = Crafty.e('2D, DOM, Text')
  .attr({ x: 380, y: 530, w: 160})
  .text('Unite us ------->')
  .textColor('#A6A6A6')


  // Generate max_scores amount of scorepoints to the map using five different graphics
  var max_scores = 5;
  for(var i = 0; i < max_scores; i++){
    switch(i){
      case 0:
      Crafty.e('Scorepoint').requires('gem0');
      break;
      case 1:
      Crafty.e('Scorepoint').requires('gem1');
      break;
      case 2:
      Crafty.e('Scorepoint').requires('gem2');
      break;
      case 3:
      Crafty.e('Scorepoint').requires('gem3');
      break;
      case 4:
      Crafty.e('Scorepoint').requires('gem4');
      break;
      default:
      var gemi = Crafty.math.randomInt(0,5);
      Crafty.e('Scorepoint').requires("gem"+gemi);
    }
  }
});


// Victory scene
// -------------
// Tells the player when they've won and lets them start a new game
Crafty.scene('Victory', function(ending) {
  // Display some text in celebration of the victory
  Crafty.e('2D, DOM, Text')
  .attr({ x: -Crafty.viewport.x+Game.width()/2-100, y: Crafty.viewport.y+Game.height()/2})
  .text('Victory!')
  .textColor('#A6A6A6');

  // Watch for the player to press a key, then restart the game
  // when a key is pressed
  this.restart_game = this.bind('KeyDown', function() {
    Crafty.scene('Game');
  });
}, function() {
  // Remove our event binding from above so that we don't
  //  end up having multiple redundant event watchers after
  //  multiple restarts of the game
  this.unbind('KeyDown', this.restart_game);
});


// Loading scene
// -------------
// Handles the loading of binary assets such as images and audio files
Crafty.scene('Loading', function(){
  // Draw some text for the player to see in case the file
  //  takes a noticeable amount of time to load
  Crafty.e('2D, DOM, Text')
  .text('Loading...')
  .attr({ x: 0, y: Game.height()/2 - 24, w: Game.width() })
  

  // Load our sprite map image
  Crafty.load(['assets/sprite.png', 'assets/suit3.gif',
   'assets/sounds/bell.mp3',
   'assets/sounds/bell.ogv',
   'assets/sounds/bell.aac','assets/sounds/avaruusbiisi.mp3','assets/sounds/avaruusbiisi.ogg','assets/sounds/thusters.mp3','assets/sounds/thusters.ogg','assets/sounds/alarm.mp3','assets/sounds/alarm.ogg'], function(){
    // Once the image is loaded...

    // Define the PC's sprite to be the first sprite in the third row of the
    //  animation sprite map
    Crafty.sprite(37,53, 'assets/suit4.gif', {
      spr_player:  [0, 0],
    }, 0, 0);

    Crafty.sprite(70,70, 'assets/gems1.gif', {
      gem0:  [0, 0],
      gem1:  [1, 0],
      gem2:  [2, 0],
      gem3:  [3, 0],
      gem4:  [4, 0]

    }, 0, 0);

    Crafty.sprite(400,400, 'assets/earth2.png', {
      planetimg:    [0, 0]
    });

    // Define our sounds for later use
    Crafty.audio.add({
      knock: ['assets/sounds/bell.mp3',
      'assets/sounds/bell.ogg',
      'assets/sounds/bell.aac'],
      taustamusa: ['assets/sounds/avaruusbiisi.mp3', 'assets/sounds/avaruusbiisi.ogg'],//Music by: Stellardrone - Billions And Billions
      thusters: ['assets/sounds/thusters.mp3'],
      alarm: ['assets/sounds/alarm.mp3','assets/sounds/alarm.ogg']
    });

    Crafty.sprite(64, "assets/sprite.png", {
      big: [1,0],
      medium: [2,0],
      small: [3,0]
    });

    // Now that our sprites are ready to draw, start the game
    Crafty.scene('Game');
  })
});