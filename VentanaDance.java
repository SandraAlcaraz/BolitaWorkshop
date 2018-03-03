package poo2da;
import java.io.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;


public class VentanaDance extends JPanel implements Runnable, SerialPortEventListener{
	private Image fondo, logo, star,crosa;
	private int [] pasos, pasos2;
	private int y1,
	sig,
	botonPresionado,
	botonPresionado2,
	puntaje;
	//private BasicPlayer player;

	//private Player player;
	private boolean siguiente, correcto, gano, dibujarBien,n2;
	int flecha;
	//arduino variables
	private SerialPort serialPort;
	private static final String PORT_NAMES="COM10"; // Windows
	private BufferedReader input;
	private OutputStream output;
	private Thread hilo;
	private Calendar fecha;
	private static final int TIME_OUT = 2000;	/** Milliseconds to block while waiting for port open */
	private static final int DATA_RATE = 9600;  /** Default bits per second for COM port. */
	public VentanaDance (){
		super();
		this.setPreferredSize(new Dimension(1400, 700));
		this.pasos = new int [50];
		this.inicializaPasos(this.pasos);
		this.hilo= new Thread(this);
		hilo.start();
		this.fondo=new ImageIcon("disco.jpg").getImage();
		this.star=new ImageIcon("brill.png").getImage();
		this.crosa=new ImageIcon("crosa.png").getImage();	
		this.siguiente=true;
		this.y1=0;
		this.sig=0;
		this.puntaje=0;
		this.gano=false;
		this.n2=false;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		this.fecha = Calendar.getInstance();
		System.out.println(dateFormat.format(this.fecha.getTime()));
		System.out.println(this.fecha.get(Calendar.HOUR));
		//this.fecha
		//arduino variables
		this.initialize();
	}
	
	public void writeArchivo(){
		
		try(BufferedReader br=new BufferedReader(new FileReader("Escritorio.txt"));){
			
			String linea, copy="";
			
			while((linea = br.readLine()) !=null){
				copy+=linea+"\r\n";
				}
	
			FileWriter fw=new FileWriter("Escritorio.txt");//checar direccion
			PrintWriter pw= new PrintWriter(fw);
			pw.println(copy);
			pw.println("Nombre del paciente: José Gómez");
			pw.println("ID: A01621678");
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			pw.println("Fecha: "+this.fecha.getTime());
			//pw.println("Fecha: "+dateFormat.format(this.fecha));
			pw.println("Peso del control: 100 gramos");
			pw.println("Eficacia: "+ this.puntaje/50);
			pw.println("Puntaje: "+this.puntaje);
			pw.close();
			
		}catch(FileNotFoundException|NullPointerException e){
			
			System.out.println("El archivo se encontró "+e);
		}catch(IOException e){
			System.out.println("Error de tipo IOException "+e);
		}
		
		
	}


	public void inicializaPasos(int a []){
		for(int i=0;i<this.pasos.length;i++){
			a[i]=(int) (Math.random()*2+1);	
		}
	}
	public void paintComponent(Graphics g){	
		super.paintComponent(g);
		
			g.drawImage(this.fondo, 0, 0,this.getWidth(),this.getHeight(), this);
			this.dibujaTablero(g);
			this.dibujarCondiciones(g, this.pasos);
			if(this.n2){// modificado estaba como n2
				this.dibujarCondiciones(g, this.pasos2);
			}
			if(this.dibujarBien){
				this.bien(g);
		}
	}
	public void bien(Graphics g){
		g.drawImage(this.star, 900-y1, 350-(y1/2),(this.getWidth()/20)+y1,(this.getHeight()/20)+y1, this);
		g.drawImage(this.star, 200+y1, 350-(y1/2),(this.getWidth()/20)+y1,(this.getHeight()/20)+y1, this);
		Font myFont = new Font ("Showcard Gothic", 1, 45);	
		g.setFont (myFont);
		g.setColor(Color.BLUE);
		if(this.puntaje<10){
			g.drawString("Bien", 500, 350);
		}
		else if(this.puntaje<20){
			g.drawString("Genial", 500, 350);
		}
		else if(this.puntaje<35){
			g.drawString("WOW", 500, 350);
		}
		else if(this.puntaje<51){
			g.drawString("Excelente", 500, 350);
		}
	}

	public void dibujaTablero(Graphics g){
		//g.drawImage(this.fondo, 0, 0,this.getWidth(),this.getHeight(), this);
		g.drawImage(this.logo, 370, 50,500,100, this);
		g.setColor(new Color(255, 0, 178));
		g.drawImage(crosa, 100, 450, 150, 100, this);
		g.drawImage(crosa, 1000, 450, 150, 100, this);
		g.setColor(new Color (0, 144, 255));	
		g.setColor(Color.WHITE);
		int ax[]={150,150,127,150,150,210,210,150};
		int ay[]={480,465,500,535,520,520,480,480};
		g.fillPolygon(ax, ay, 8);
		int dx[]={1040,1100,1100,1120,1100,1100,1040,1040};
		int dy[]={480,480,465,500,535,520,520,480};
		g.fillPolygon(dx, dy, 8);
		g.setColor(Color.WHITE);
		Font myFont = new Font ("Showcard Gothic", 1, 50);	
		g.setFont (myFont);
		g.drawString(String.valueOf(this.puntaje), 70,90 );
		int porcentaje=puntaje/50;
		g.drawString(String.valueOf((porcentaje)+"%"), 170,90 );

	}
	public void validar(int botonPresionado){
		this.botonPresionado= botonPresionado;
		/*if(this.n2 && (this.pasos[this.sig]!=this.pasos2[this.sig])){
			if((this.botonPresionado==this.pasos[this.sig] || this.botonPresionado==this.pasos2[this.sig]) && (this.botonPresionado2==this.pasos2[this.sig] || this.botonPresionado2==this.pasos[this.sig])){
				this.correcto=true;	
			}
			else{
			this.correcto=false;	
			}
		}else{*/
			if(this.botonPresionado==this.pasos[this.sig]){
				this.correcto=true;	
			}
			else{
			this.correcto=false;	
			}
		}
		//	this.botonPresionado=0;
	//}

	public boolean getValidar(){
		return this.correcto;
	}

	public void dibujarCondiciones(Graphics g, int [] pasosCor){
		do{
			if(pasosCor[this.sig]==1){
				this.pintaflechas(g, new Color(255, 0, 178), 130, 20);

			}
			if(pasosCor[this.sig]==2){
				this.pintaflechas(g, new Color(255, 0, 178), 1020, 20);
			}
		}while(this.siguiente && this.sig<pasosCor.length);

	}

	public void pintaflechas(Graphics g, Color color, int xFlecha, int yFlecha){
		g.setColor(color);
		g.fillOval(xFlecha,yFlecha+this.y1,100, 100);
	}
	public void setPuntaje(int puntaje){
		this.puntaje+=puntaje;
	}
	public int getPuntaje(){
		return this.puntaje;
	}

	public static void main(String[] args) throws Exception{

		VentanaDance ven= new VentanaDance();

		JFrame jf= new JFrame();
		jf.setTitle("PXY interactive");
		jf.getContentPane().add(ven);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
	}

	@Override
	public void run() {
			try{
				try {
					while(this.y1<=430){
						this.siguiente=false;
						this.y1+=5;
						this.repaint();
							Thread.sleep(1);	
						if(this.y1>410 && this.y1<430){
							this.siguiente=true;
							this.sig++;
							this.y1=0;			
							if(this.getValidar()){
								int c=1;
								System.out.println("validado correcto");
								this.setPuntaje(c);
								this.dibujarBien=true;
								//	this.correcto=false;
							}else{
								int inc=0;
								System.out.println("validado incorrecto");
								this.setPuntaje(inc);
								this.dibujarBien=false;
								//	this.correcto=false;
							}
						}
						if(this.sig==this.pasos.length-1){
							this.writeArchivo();	
					System.exit(0);
					break;
				}		
						
						else{
							this.siguiente=false;
						}
					}
					}catch(InterruptedException e){
					System.out.println(e);
				}
			} catch(Exception ex) {
				System.out.println("Error with playing sound.");
				// ex.printStackTrace();
			}
		//}
	}

	public void initialize() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			if (currPortId.getName().equals(PORT_NAMES)) {
				portId = currPortId;
				break;
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),TIME_OUT);
			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();
			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}
	//Arduino
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine=input.readLine();
			//	String inputLine2=input.readLine();
					
				if(inputLine.equals("1")){
					this.validar(4);
					this.botonPresionado2=4;
					//this.botonPresionadoMenu=4;
				}
				else if(inputLine.equals("2")){
					this.validar(2);
					this.botonPresionado2=2;
				}
				else if(inputLine.equals("3")){
					this.validar(1);
					this.botonPresionado2=1;
					//this.botonPresionadoMenu=1;
				}
				else if(inputLine.equals("4")){
					this.validar(3);
					this.botonPresionado2=3;
				}
				else if(inputLine.equals("5")){
				//	this.botonStart=true;
				}
				else if(inputLine.equals("0")){
					this.validar(0);
					this.botonPresionado2=0;
				}
				repaint();
			} catch (IOException e) {
				//System.err.println("error en serialEvent: "+e);
			}
		}
	}

}