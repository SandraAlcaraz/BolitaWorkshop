#include <Wire.h>

long accelX, accelY, accelZ;
float gForceX, gForceY, gForceZ;

long gyroX, gyroY, gyroZ;
float rotX, rotY, rotZ;
 int c=0;
  int c2=0;
int l=-1;
int r= -1;
int c_l=0;
int c_r=0;

boolean leido0= false;
boolean leido1= false;
boolean leido2= false;

void setup() {
  Serial.begin(9600);
  Wire.begin();
  setupMPU();
}



void loop() {
  recordAccelRegisters();
  recordGyroRegisters();
 leftRight();
  delay(10);
}

void leftRight(){ 

 
  if(rotZ>0){
    l=rotZ;
  //  Serial.println("izq");
  //  Serial.println(l);
    c_l++;
  // Serial.println(c_l);
   }
   if(rotZ<=-2){
     r=rotZ;
    //     Serial.println("der");
    //     Serial.println(r);
     c_r++;
   // Serial.println(c_r);
    }

    //centro
    if(c_l-c_r==0){
      if(!leido0){
    Serial.println(0);
      leido0=true;
      leido1=false;
      leido2=false;
      c_l=0;
      c_r=0;
      }
   }

     //derecha
  if(c_r>=70){
     if(c_l-c_r<0){
       if(!leido1){
    Serial.println(1);
      leido1=true;
      leido0=false;
      leido2=false;
      }
     }
  }

     //izquierda 
    if(c_l>=70){
      if(c_l-c_r>0){
        if(!leido2){
        Serial.println(2);
      leido2=true;
      leido1=false;
      leido0=false;
      }
     }
    }
}


void setupMPU(){
  Wire.beginTransmission(0b1101000); //This is the I2C address of the MPU (b1101000/b1101001 for AC0 low/high datasheet sec. 9.2)
  Wire.write(0x6B); //Accessing the register 6B - Power Management (Sec. 4.28)
  Wire.write(0b00000000); //Setting SLEEP register to 0. (Required; see Note on p. 9)
  Wire.endTransmission();  
  Wire.beginTransmission(0b1101000); //I2C address of the MPU
  Wire.write(0x1B); //Accessing the register 1B - Gyroscope Configuration (Sec. 4.4) 
  Wire.write(0x00000000); //Setting the gyro to full scale +/- 250deg./s 
  Wire.endTransmission(); 
  Wire.beginTransmission(0b1101000); //I2C address of the MPU
  Wire.write(0x1C); //Accessing the register 1C - Acccelerometer Configuration (Sec. 4.5) 
  Wire.write(0b00000000); //Setting the accel to +/- 2g
  Wire.endTransmission(); 
}

void recordAccelRegisters() {
  Wire.beginTransmission(0b1101000); //I2C address of the MPU
  Wire.write(0x3B); //Starting register for Accel Readings
  Wire.endTransmission();
  Wire.requestFrom(0b1101000,6); //Request Accel Registers (3B - 40)
  while(Wire.available() < 6);
  accelX = Wire.read()<<8|Wire.read(); //Store first two bytes into accelX
  accelY = Wire.read()<<8|Wire.read(); //Store middle two bytes into accelY
  accelZ = Wire.read()<<8|Wire.read(); //Store last two bytes into accelZ
  processAccelData();
}

void processAccelData(){
  gForceX = accelX / 16384.0;
  gForceY = accelY / 16384.0; 
  gForceZ = accelZ / 16384.0;
}

void recordGyroRegisters() {
  Wire.beginTransmission(0b1101000); //I2C address of the MPU
  Wire.write(0x43); //Starting register for Gyro Readings
  Wire.endTransmission();
  Wire.requestFrom(0b1101000,6); //Request Gyro Registers (43 - 48)
  while(Wire.available() < 6);
  gyroX = Wire.read()<<8|Wire.read(); //Store first two bytes into accelX
  gyroY = Wire.read()<<8|Wire.read(); //Store middle two bytes into accelY
  gyroZ = Wire.read()<<8|Wire.read(); //Store last two bytes into accelZ
  processGyroData();
}

void processGyroData() {
  rotX = gyroX / 131.0;
  rotY = gyroY / 131.0; 
  rotZ = gyroZ / 131.0;
}

