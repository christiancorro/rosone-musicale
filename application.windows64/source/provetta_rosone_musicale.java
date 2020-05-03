import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.analysis.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class provetta_rosone_musicale extends PApplet {




Minim minim;
AudioPlayer song;
BeatDetect beat;
BeatListener bl;
float kickSize, snareSize, hatSize;
float circleHeight;
float circleWidth;
int count = 10;
float inc = 0.1f;
boolean draw = false;
float size = 0.2f;
ArrayList<Particle> kick, snare, hat;

float offset = 0.08f;
boolean  kicked, endMusic, toggleText;
float delay;
public void setup()
{
  background(0);
  
  //surface.setResizable(true);
  //fullScreen(P2D);
  kicked = false;
  endMusic = false;
  toggleText = false;
  minim = new Minim(this);
  //song = minim.loadFile("Comfort_Fit_-_03_-_Sorry.mp3", 1024);

  song = minim.loadFile("e2.mp3", 1024);
  song.play();
  delay = song.length() -3500;
  println(song.length());
  //delay = 60000;
  beat = new BeatDetect(song.bufferSize(), song.sampleRate());
  beat.setSensitivity(250);  
  kickSize = snareSize = hatSize = 16;
  bl = new BeatListener(beat, song);  
  circleHeight = circleWidth = height/3;

  textAlign(CENTER);
  textFont(createFont("Fira Code", 30));


  kick = new ArrayList();
  snare = new ArrayList();
  hat = new ArrayList();
}

public void draw() {
  //fill(0, 100);
  //rect(0, 0, width, height);

  if (!draw)
    background(0);
  fill(255);
  if (toggleText)
    text((kick.size()+snare.size()+hat.size()), width/2, height/2);

  if (!endMusic) {
    if ( beat.isKick() ) {
      for (int i = 0; i< count; i++) {
        kick.add(new Particle(width/2, height/2, size));

        kick.get(kick.size()-1).setColor(0, 0, 0);
      }
      float diameter = random(height/4, height/3);
      circleHeight = diameter*1.1f;
      circleWidth = diameter*1.1f;
    }
    if ( beat.isSnare() ) {
      for (int i = 0; i< count; i++) {
        snare.add(new Particle(width/2, height/2, size));
        snare.get(snare.size()-1).setColor(255, 0, 0);
      }
      float diameter = random(height/6, height/4);
      circleHeight = diameter;
      circleWidth = diameter;
    }
    if ( beat.isHat() ) {
      for (int i = 0; i< count; i++) {
        hat.add(new Particle(width/2, height/2, size));
        hat.get(hat.size()-1).setColor(255, 255, 74);
      }
      circleHeight/=1.005f;
      circleWidth/=1.005f;
    }
  }

  if (song.position() >= song.length()/2 && !endMusic) {
    draw = true;
  }
  /*if (song.position() >= song.length()-600) {
   circleHeight = height/100;
   circleWidth = height/100;
   }*/
  showParticles(kick);
  showParticles(snare);
  showParticles(hat);


  if (song.position() >= delay + 2970 && !endMusic) { // if (song.position() >= song.length()-8000) {
    circleHeight/=1.1f;
    circleWidth/=1.1f;
  }

  if (song.position() >= delay+2990 && !endMusic) {
    endMusic = true;
    kicked = true;

    draw = false;
  }

  if (!endMusic) {
    limitaParticellaInCirconferenza(kick);
    limitaParticellaInCirconferenza(snare);
    limitaParticellaInCirconferenza(hat);
    //  removeOldParticles(kick);
    //  removeOldParticles(snare);
    //  removeOldParticles(hat);
  } else if (kicked) {
    kicked = false;
    kickParticles(kick);
    kickParticles(snare);
    kickParticles(hat);
  } else {
    increaseSize(kick, inc);
    increaseSize(snare, inc);
    increaseSize(hat, inc);
  }

  if (endMusic) {
    rimuoviParticelleBound(kick);
    rimuoviParticelleBound(snare);
    rimuoviParticelleBound(hat);
  }
}

public void rimuoviParticelleBound(ArrayList<Particle> particles) {
  for (int i = 0; i < particles.size(); i++) {
    if (i<particles.size()) {
      if (particles.get(i).position.x > width + particles.get(i).diameter || particles.get(i).position.x < - particles.get(i).diameter) {
        particles.remove(particles.get(i));
      }
    }
if (i<particles.size()) {
      if (particles.get(i).position.y > height + particles.get(i).diameter || particles.get(i).position.y < - particles.get(i).diameter) {
        particles.remove(particles.get(i));
      }
    }
  }
}

public void showParticles(ArrayList<Particle> particles) {
  for (Particle particle : particles) {
    particle.update();
    particle.display();
  }
}

public void increaseSize(ArrayList<Particle> particles, float c) {
  for (Particle particle : particles) {
    particle.diameter += c;
  }
}

// limita il movimento delle particelle ad una circonferenza
public void limitaParticellaInCirconferenza(ArrayList<Particle> particles) {
  float angle = radians(360+PApplet.parseFloat(particles.size()+1)%360);
  if (particles.size()>0)
    //particles.get(particles.size()-1).setColor(255, 255, 255);
    for (int i=0; i<particles.size(); i++) {
      float circleX = width/2 + cos(angle*i)*circleHeight; 
      float circleY = height/2 + sin(angle*i)*circleWidth;
      float x = lerp(particles.get(i).position.x, circleX, offset);
      float y = lerp(particles.get(i).position.y, circleY, offset);  
      particles.get(i).position.set(x, y);
    }
}

// allontana le particelle dalla circonferenza
public void kickParticles(ArrayList<Particle> particles) {
  for (Particle  particle : particles) {
    particle.applyForce(new PVector(random(-2, 2), random(-2, 2)));
  }
}

// mantiene le ultime 200 particelle
public void removeOldParticles(ArrayList<Particle> particles) {
  if (particles.size() > 300)
    for (int i = 0; i < 100; i++) {
      particles.remove(i);
    }
}

public void keyPressed() {
  if (keyCode == ENTER) {
    endMusic = true;
    kicked = true;
  } else

    if (key == ' ') {
      song.close();
      setup();
    } else
      if (key == 't') toggleText = !toggleText;
      else if (key == 'd') {
        draw = !draw;
      } else if (key == 'g') {
        count += 10;
      }
}
class BeatListener implements AudioListener{
  private BeatDetect beat;
  private AudioPlayer source;
  
  BeatListener(BeatDetect beat, AudioPlayer source)
  {
    this.source = source;
    this.source.addListener(this);
    this.beat = beat;
  }
  
  public void samples(float[] samps)
  {
    beat.detect(source.mix);
  }
  
  public void samples(float[] sampsL, float[] sampsR)
  {
    beat.detect(source.mix);
  }
}
class Particle {

  final int DEFAULT_FILL_COLOR = color(255);
  int fillColor;
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

  public void display() {
      fill(fillColor, alpha);
      noStroke();
      ellipse(position.x, position.y, diameter, diameter);
      //alpha -= 2;
  }

  public void update() {
    velocity.add(acceleration);
    velocity.limit(velocityLimit);
    position.add(velocity);
    acceleration.mult(0);
  }

  // a = F/m
  public void applyForce(PVector force){
    acceleration.set(force.div(mass));
    acceleration.limit(accelerationLimit);
  }
  public void setColor(float r, float g, float b){
    fillColor = color(r,g,b);
  }
  
  public void setMass(float _mass){
    mass = _mass;
    diameter = mass*20;
  }

}
  public void settings() {  size(800, 800,P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "provetta_rosone_musicale" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
