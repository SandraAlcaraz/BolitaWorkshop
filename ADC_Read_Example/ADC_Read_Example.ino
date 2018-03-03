int analogPin = 3;    /* Analog pin 3 is used to read values */
int value = 0;        /* Variable where we save the value from 
                         the ADC */

/* Initial setup */
void setup() {
  Serial.begin(9600);   /* Serial port is initialized */

}

void loop() {
  value = analogRead(analogPin);     /* We read the value from the ADC port */
  Serial.println(value);             /* The value is printed on the Serial Screen */

}
