class Particle {

  final color DEFAULT_FILL_COLOR = color(255);
  color fillColor;
  float velocityLimit, accelerationLimit;
  PVector position, velocity, acceleration;
  float mass, diameter;
  float alpha;

  Particle(float x, float y, float _mass) {
    position = new PVector(x, y);
    velocity = new PVector(0, 0);
    acceleration = new PVector(0, 0);
    fillColor = DEFAULT_FILL_COLOR;
    mass = _mass;
    diameter = mass*20;
    accelerationLimit = 50;
    velocityLimit = 50;
    alpha = random(40,250);
  }

  void display() {
      fill(fillColor, alpha);
      noStroke();
      ellipse(position.x, position.y, diameter, diameter);
      //alpha -= 2;
  }

  void update() {
    velocity.add(acceleration);
    velocity.limit(velocityLimit);
    position.add(velocity);
    acceleration.mult(0);
  }

  // a = F/m
  void applyForce(PVector force){
    acceleration.set(force.div(mass));
    acceleration.limit(accelerationLimit);
  }
  void setColor(float r, float g, float b){
    fillColor = color(r,g,b);
  }
  
  void setMass(float _mass){
    mass = _mass;
    diameter = mass*20;
  }

}