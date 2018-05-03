import ddf.minim.*;
import ddf.minim.analysis.*;

Minim minim;
AudioPlayer song;
BeatDetect beat;
BeatListener bl;
float kickSize, snareSize, hatSize;
float circleHeight;
float circleWidth;
int count = 50;
boolean draw = false;
float size = 0.2;
ArrayList<Particle> kick, snare, hat;

float offset = 0.08;
boolean  kicked, endMusic, toggleText;
float delay;
void setup()
{
  background(0);
  //size(1000, 1000,P2D);
  //surface.setResizable(true);
  fullScreen(P2D);
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

void draw() {
  //fill(0, 100);
  //rect(0, 0, width, height);

  if (!draw)
    background(0);
  fill(255);
  if (toggleText)
    text((kick.size()+snare.size()+hat.size()), width/2, height/2);

if(!endMusic){
  if ( beat.isKick() ) {
    for (int i = 0; i< count; i++) {
      kick.add(new Particle(width/2, height/2, size));

      kick.get(kick.size()-1).setColor(0, 0, 0);
    }
    float diameter = random(height/4, height/3);
    circleHeight = diameter*1.1;
    circleWidth = diameter*1.1;
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
    circleHeight/=1.005;
    circleWidth/=1.005;
  } 
}

if (song.position() >= song.length()/2 && !endMusic){
 draw = true; 
}
  /*if (song.position() >= song.length()-600) {
   circleHeight = height/100;
   circleWidth = height/100;
   }*/
  showParticles(kick);
  showParticles(snare);
  showParticles(hat);


   if (song.position() >= delay && !endMusic) { // if (song.position() >= song.length()-8000) {

   }

   if (song.position() >= delay+3000 && !endMusic){
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
    increaseSize(kick, 1);
    increaseSize(snare, 1);
    increaseSize(hat, 1);
  }
}

void showParticles(ArrayList<Particle> particles) {
  for (Particle particle : particles) {
    particle.update();
    particle.display();
  }
}

void increaseSize(ArrayList<Particle> particles, float c) {
  for (Particle particle : particles) {
    particle.diameter +=0.1;
  }
}

// limita il movimento delle particelle ad una circonferenza
void limitaParticellaInCirconferenza(ArrayList<Particle> particles) {
  float angle = radians(360+float(particles.size()+1)%360);
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
void kickParticles(ArrayList<Particle> particles) {
  for (Particle  particle : particles) {
    particle.applyForce(new PVector(random(-3, 3), random(-3, 3)));
  }
}

// mantiene le ultime 200 particelle
void removeOldParticles(ArrayList<Particle> particles) {
  if (particles.size() > 300)
    for (int i = 0; i < 100; i++) {
      particles.remove(i);
    }
}

void keyPressed() {
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