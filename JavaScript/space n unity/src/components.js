//In components we define what kind of entities will the game feature. Components or so called Crafty.c are the blueprints of entities. Components can have attributes and functions

// An "Actor" is an entity that is drawn in 2D on canvas
Crafty.c('Actor', {
  init: function() {
    this.requires('2D, Canvas');
  },
});
// CollisionClass helps to identify which entities can collide together
Crafty.c('CollisionClass', {
  init: function() {
    this.requires('2D, Canvas');
  },
});

// A Planet is just an Actor with a certain sprite
Crafty.c('Planet', {
  init: function() {
    this.requires('Actor, Solid, Collision, planetimg').collision([0,100],[100,0],[300,0],[400,100],[400,300],[300,400],[100,400],[0,300]);
  },
});

//Platform is used in the start
Crafty.c('Platform', {
  init: function(){
    this.requires('Actor, Solid, Collision');
  },
  makePlatform: function(x,y,w,h){
    this.attr({
      x: x,
      y: y,
      w: w,
      h: h
    });
    this.collision();
    return this;
  }
});

// A Asteroid is just an Actor with a certain sprite. They have speed, acceleration and rotation. Asteroids can break into smaller pieces.
Crafty.c('Asteroid', {
  init: function() {
    this.requires('CollisionClass')
    .attr({
      x: Crafty.math.randomInt(-Crafty.viewport.x+Game.width(), -Crafty.viewport.x+Game.width()+70),
      y: Crafty.math.randomInt(2, Game.height()),
      dX: Crafty.math.randomInt(-2, 2),
      dY: Crafty.math.randomInt(-2, 2),
      rspeed: Crafty.math.randomInt(-.1, .3),
      canBreak: true
    })
    .collision()
    //If hits a member of the CollisionClass, breaks into pieces
    .onHit('CollisionClass', function(){  
      if(this.canBreak){
        this.brkToPieces();
      }
    })
    //On every frame run this function
    .bind('EnterFrame', function() {

                //hit floor or roof, bounces back
                if (this.y <= -70 || this.y >= Game.height()+100){ 
                  this.dY *= -1;
                  this.canBreak = true;
                }
                //If goes too far offscreen, move to a random location at the right side of the screen
                if (this.x <= -Crafty.viewport.x-100 || this.x >= -Crafty.viewport.x+Game.width()+600){ 
                  this.x = Crafty.math.randomInt(-Crafty.viewport.x+Game.width()+100,-Crafty.viewport.x+Game.width()+200);
                  //If there are too many small asteroidpieces turn them into big ones
                  if(this.has("small") && asteroidCount < 30){
                    this.removeComponent("small").addComponent("big");
                    //define hitbox coords.
                    this.collision([0,0],[64,0],[64,64],[0,64]);
                  }
                  this.canBreak = true;
                }
                //Move and rotate the asteroid
                this.y += this.dY;
                this.x += this.dX;
                this.rotation += this.rspeed;

              });


  },
  //A function for breaking asteroids
  brkToPieces: function(){
    //When an asteroid gets hit, it needs to become un-hittable for a certain period of time. Hits would register after another and the player couldnt see any visual breaking
    this.canBreak = false;  
    //Change direction to opposite
    this.dY *=-1;
    this.dX *=-1;
    var size;
          //decide what size to make the asteroid
          if(this.has("big")) {
            this.removeComponent("big").addComponent("medium");
            this.collision([15,15],[50,15],[50,50],[15,50]);
            size = "medium";
          } else if(this.has("medium")) {
            this.removeComponent("medium").addComponent("small");
            this.collision([24,24],[40,24],[40,40],[24,40]);
            size = "small";
          } else if(this.has("small")) { //if the lowest size, delete self
            asteroidCount--;
            this.destroy();
            return;
          }

          var oldxspeed = this.xspeed;
          this.xspeed = -this.yspeed;
          this.yspeed = oldxspeed;

          asteroidCount++;

          //split into two asteroids by creating another asteroid
          Crafty.e("CollisionClass, "+size+", Collision, Asteroid").attr({x: this._x, y: this._y, canBreak: true});

        },


      });

// This is the player-controlled character
Crafty.c('PlayerCharacter', {
  init: function() {
    this.requires('CollisionClass, Twoway, Collision, spr_player, SpriteAnimation, Gravity, Keyboard')
    .twoway(2,3)
    .collision([10,10],[20,10],[20,53],[10,53])
    .attr({ dx: 0, dy: 0, scoreCollected: false, dead: false})
    .onHit('Scorepoint', this.collectScorepoint)
    .gravity('Below')
    .gravityConst(.06)
    //If player hits a platform, falling needs to stop
    .onHit("Platform", function(hit) {
      for (var i = 0; i < hit.length; i++) {
                if (hit[i].normal.y === -1) { // we hit the top of it
                  this._falling = false;
                  this._up = false;
                  this.y = hit[i].obj.y - this.h;
                }
              }
           })
    //If player hits Planet when all the scorepoints are gathered, a final animation begins.
    .onHit("Planet", function(hit) {
      for (var i = 0; i < hit.length; i++) {
                if (hit[i].normal.y === -1) { // we hit thed top of it
                  this._falling = false;
                  this._up = false;
                  this.y = hit[i].obj.y - this.h;
                }
              }
              if(this.scoreCollected){
                this.animate('Final', 50,1).attr({alpha: 1.0,x:this.x,y:Crafty.viewport.y/2,w: 37, h: 53})
                .tween({alpha: 0.0,x:-Crafty.viewport.x,y:Crafty.viewport.y/2,w:600, h:800},1000)
                .antigravity()
                
              }

            })
    //Player dies from asteroid hits
    .onHit("Asteroid", function(){
      this.dx-=0.05;
      Crafty.trigger('playerDead', this);

    })
    //When a scorepoint is hit, a collectScorepoint function is called
    .onHit('Scorepoint', this.collectScorepoint)
      // These next lines define our four animations
      //  each call to .animate specifies:
      //  - the name of the animation
      //  - the x and y coordinates within the sprite
      //     map at which the animation set begins
      //  - the number of animation frames *in addition to* the first one
      .animate('PlayerStillR', 0,0,0)//paikoillaan oikealle
      .animate('PlayerStillL', 0,1,0)//vasemmalle
      .animate('PlayerMovingRight', 1, 0, 1)//lentaa oikealle
      .animate('PlayerMovingLeft',  1, 1, 1)//vasemmalle
      .animate('PlayerWalkingRight', 0, 2, 2)//kavelee oikealle
      .animate('PlayerWalkingLeft', 0, 3, 2)//vasemmalle
      .animate('Final', 0,4,2)//loppuanimaatio
      .attr("toRight", true);//lahtokohtaisesti katsotaan oikealle

    // Watch for a change of direction and switch animations accordingly
    var animation_speed = 32;

    this.bind('NewDirection', function(data) {
      if (data.x > 0 && this._gy !== 0) {
        this.animate('PlayerMovingRight', animation_speed, -1);
        this.toRight = true;
      } else if (data.x < 0 && this._gy !== 0) {
        this.animate('PlayerMovingLeft', animation_speed, -1);
        this.toRight = false;
      } else if (data.x > 0 ){
        this.animate('PlayerWalkingRight', animation_speed, -1);
        this.toRight = true;
      } else if (data.x < 0){
        this.animate('PlayerWalkingLeft', animation_speed, -1);
        this.toRight = false;
      } else if(data.x === 0) {
        this.stop();//stop the animation and check which way facing
        if(this.toRight)
          this.animate('PlayerStillR', animation_speed, -1);
        if(!this.toRight)
          this.animate('PlayerStillL', animation_speed, -1);
      } else {
        this.stop();
      }

    });

  },

  // Respond to this player collecting a scorepoint
  collectScorepoint: function(data) {
    score = data[0].obj;
    score.collect();
  },

});

// A scorepoint is assigned randomly to the game level. Must collect all in-order to win the game
Crafty.c('Scorepoint', {
  init: function() {
    this.requires('Actor')    
    .attr({
      x: Crafty.math.randomInt(0, Game.width()*10),
      y: Crafty.math.randomInt(0, Game.height()-70)
    });
  },

  // Process a collectation of this scorepoint
  collect: function() {
    this.destroy();//destroy the scorepoint
    Crafty.audio.play('knock');
    Crafty.trigger('ScorepointVisited', this);//trigger an event that a score was collected
  }
});