//Comentario test
package com.example.cat;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.MotionEvent;
import processing.core.*;

public class MainActivity extends PApplet {
	
	int posX = 1, posY = 1;
	int posXcomida = 0, posYcomida = 0;
	int ancho = 0, alto = 0;
	int puntos = 0, mejorPuntuacion = 0;
	int touchX = 0, touchY = 0;
	int numX = 3, numY = 3;
	int mapa[][] = new int[numX][numY];

	ArrayList<Bola> bolas = new ArrayList<Bola>();

	ControlTiempo thread1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);		
	}
	
	public void setup() 
	{
		 orientation(PORTRAIT);	
		 initMap(numX, numY);
		 mapa[posX][posY] = 1;
		 ancho = ((width / 3) / 3) / 2;
		 alto  = ancho;
		 generarComida();
		 thread1 = new ControlTiempo(5);
		 thread1.start(); 
		 eliminarBolas();
		 generarBola();
	}
	
	public void draw()
	{
		background(204,229,255);
		dibujarCuadricula();
		dibujarComida();
		dibujarPersonaje();
		dibujarBolas();
		verificarSiComio();   
		eliminarBolas();
		texto();
		verificarMuerte();
	}
	
//////////////////////////////Android
public boolean dispatchTouchEvent(MotionEvent event) 
{
	float x = event.getX();                           
	float y = event.getY();
	
	int action = event.getActionMasked();
	
	switch (action)
	{                              // let us know which action code shows up
		case MotionEvent.ACTION_DOWN:
			touchX = (int)x;
			touchY = (int)y;
			break;
		case MotionEvent.ACTION_UP:
			boolean vertical = false;			
			//Verificar si el movimiento es vertical o horizontal
			int difX = touchX - (int)x;
			int difY = touchY - (int)y;
			//Los convertiremos en positivos para ver el movimiento
			int xPositiva = difX>0?difX:difX*-1;
			int yPositiva = difY>0?difY:difY*-1;
			
			if(xPositiva<yPositiva)
				vertical = true;
			
			//Verificar para donde mover
			if(vertical)
			{
				if(difY>0)
				{
					if(posY>0)
				      {
				        mapa[posX][posY] = 0;
				        posY--;
				      }
				      mapa[posX][posY] = 1; 
				}
				else
				{
					if(posY<numY-1)
				      {
				        mapa[posX][posY] = 0;
				        posY++;
				      }
				      mapa[posX][posY] = 1; 
				}
			}
			else
			{
				if(difX>0)
				{
					 if(posX>0)
				      {
				        mapa[posX][posY] = 0;
				        posX--;
				      }
				      mapa[posX][posY] = 1;  
				}
				else
				{
					 if(posX<numX-1)
				      {
				        mapa[posX][posY] = 0;
				        posX++;
				      }
				      mapa[posX][posY] = 1; 
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			break;
	}
	
	return super.dispatchTouchEvent(event);        // pass data along when done!
}
/////////////////////////////////////
/////////////////////////////Android

	void initMap(int numX, int numY)
	{
	  for(int i=0; i<numX; i++)
	    for(int j=0; j<numY; j++)
	      mapa[i][j] = 0;  
	}
	
	public void verificarMuerte()
	{
		int anchoAux = width/3;
		int altoAux  = height/2-(anchoAux/2);
		int xBola = 0;
		int yBola = 0;
		int xPersonaje = 0;
		int yPersonaje = 0;
		
		for(int i=0;i<numY;i++)
			for(int j=0;j<numX;j++)
				for(int k=0; k<bolas.size(); k++)
				{	
					xBola = bolas.get(k).getPosX();
					yBola = bolas.get(k).getPosY();
					xPersonaje = anchoAux+(j*(ancho*2))+(ancho/2); 
					yPersonaje = altoAux+(i*(alto*2))+(alto/2);
					
					if(mapa[j][i]==1)
						if(xBola>xPersonaje&&xBola<xPersonaje+ancho&&
						   yBola>yPersonaje&&yBola<yPersonaje+alto)
							text("Muerto",50,200);
						else
							text("Vivo",50,200);
				}				
	}
	
	void dibujarComida()
	{
	  int anchoAux = width/3;
	  int altoAux  = height/2-(anchoAux/2);
	  fill(51,153,255);
	  rect(anchoAux+(posXcomida*(ancho*2))+(ancho/2), altoAux+(posYcomida*(alto*2))+(alto/2), ancho, alto);
	}
	
	void generarComida()
	{
	  posXcomida = (int)random(0,3);
	  posYcomida = (int)random(0,3);
	  
	  if(posXcomida==posX&&posYcomida==posY)
	    generarComida();    
	}
	
	void movimientoObjetos()
	{
	  for(int i=0; i<bolas.size(); i++)
	  {
	    int lado = bolas.get(i).getLado();
	   
	    switch(lado)
	    {
	      case 1:
	        bolas.get(i).setPosY(bolas.get(i).getPosY()+bolas.get(i).getVel());        
	        break;
	      case 2:
	        bolas.get(i).setPosX(bolas.get(i).getPosX()-bolas.get(i).getVel());   
	        break;
	      case 3:
	        bolas.get(i).setPosY(bolas.get(i).getPosY()-bolas.get(i).getVel());   
	        break;
	      case 4:
	        bolas.get(i).setPosX(bolas.get(i).getPosX()+bolas.get(i).getVel());
	        break;  
	    }
	  } 
	}
	
	void eliminarBolas()
	{
	  for(int i=0; i<bolas.size(); i++)
	  {
	    int lado     = bolas.get(i).getLado();
	    int numBolas = (int)random(1,3);
	    switch(lado)
	    {
	      case 1:
	        if(bolas.get(i).getPosY()>height)
	        {
	          bolas.remove(i);
	          for(int j=0; j<numBolas; j++)
	            if(bolas.size()<4)
	              generarBola();
	        }     
	        break;
	      case 2:
	        if(bolas.get(i).getPosX()<0)
	        {
	          bolas.remove(i);
	          for(int j=0; j<numBolas; j++)
	            if(bolas.size()<4)
	              generarBola();
	        }
	        break;
	      case 3:
	        if(bolas.get(i).getPosY()<0)
	        {
	          bolas.remove(i);
	          for(int j=0; j<numBolas; j++)
	            if(bolas.size()<4)
	              generarBola();
	        }     
	        break;
	      case 4:
	        if(bolas.get(i).getPosX()>width)
	        {
	          bolas.remove(i);
	          for(int j=0; j<numBolas; j++)
	            if(bolas.size()<4)
	              generarBola();
	        }   
	        break;  
	    }   
	  } 
	}
	
	void dibujarBolas()
	{
	  for(int i=0; i<bolas.size(); i++)
	    bolas.get(i).dibujar();    
	}

	void texto()
	{
	  textSize(40);
	  fill(255);
	  text("Best " + mejorPuntuacion,10,35);  
	  textSize(90);
	  text(puntos,10,110); 
	}
	
	void verificarSiComio()
	{
	  if(posXcomida==posX&&posYcomida==posY)
	  {
	    puntos++;
	    generarComida();
	  }    
	}
	
	void generarBola()
	{
	  int anchoAux = width/3;
	  int altoAux  = height/2-(anchoAux/2);
	  int lado      = (int)random(1,5);
	  int velocidad = (int)random(1,3);
	  int pos       = (int)random(1,4);
	  int posXbola  = 0;
	  int posYBola  = 0;
	  
	  switch(lado)
	  {
	    case 1:
	      posYBola = -10;
	      switch(pos)
	      {
	        case 1:
	          posXbola = (anchoAux+(0*(ancho*2))+(ancho/2))+ancho/2;
	          break;
	        case 2:
	          posXbola = (anchoAux+((ancho*2))+(ancho/2))+ancho/2;
	          break;
	        case 3:
	          posXbola = (anchoAux+(2*(ancho*2))+(ancho/2))+ancho/2;
	          break;          
	      }
	      break;
	    case 2:
	      posXbola = width+10;
	      switch(pos)
	      {
	        case 1:
	          posYBola =  (altoAux+(0*(alto*2))+(alto/2))+alto/2;
	          break;
	        case 2:
	          posYBola =  (altoAux+((alto*2))+(alto/2))+alto/2;
	          break;
	        case 3:
	          posYBola =  (altoAux+(2*(alto*2))+(alto/2))+alto/2;
	          break;          
	      }
	      break;
	    case 3:
	      posYBola = height+10;
	      switch(pos)
	      {
	        case 1:
	          posXbola = (anchoAux+(0*(ancho*2))+(ancho/2))+ancho/2;
	          break;
	        case 2:
	          posXbola = (anchoAux+((ancho*2))+(ancho/2))+ancho/2;
	          break;
	        case 3:
	          posXbola = (anchoAux+(2*(ancho*2))+(ancho/2))+ancho/2;
	          break;          
	      }
	      break;
	    case 4:
	      posXbola = -10;
	      switch(pos)
	      {
	        case 1:
	          posYBola =  (altoAux+(0*(alto*2))+(alto/2))+alto/2;
	          break;
	        case 2:
	          posYBola =  (altoAux+((alto*2))+(alto/2))+alto/2;
	          break;
	        case 3:
	          posYBola =  (altoAux+(2*(alto*2))+(alto/2))+alto/2;
	          break;          
	      }
	      break;  
	  }
	  
	  bolas.add(new Bola(posXbola,posYBola,velocidad,lado,ancho));   
	}
	
	void dibujarPersonaje()
	{
	  int anchoAux = width/3;
	  int altoAux  = height/2-(anchoAux/2);
	  for(int i=0; i<mapa.length; i++)
	    for(int j=0; j<mapa.length; j++)
	      if(mapa[i][j]==1)
	      {
	        fill(255,51,51);
	        rect(anchoAux+(i*(ancho*2))+(ancho/2), altoAux+(j*(alto*2))+(alto/2), ancho, alto);
	      }
	}
	
	void dibujarCuadricula()
	{
	  int x = width/3;
	  int y = height/2;
	  stroke(255);
	  strokeWeight (3);
	  line(x,y-(x/3)/2,x*2,y-(x/3)/2);
	  line(x,y+(x/3)/2,x*2,y+(x/3)/2);
	  line(x+(x/3),y-(x/2),x+(x/3),y+(x/2));
	  line(x+((x/3)*2),y-(x/2),x+((x/3)*2),y+(x/2));
	}
	
	class Bola
	{
		int posX, posY, vel, lado, tamano;
	  
		Bola(int posX, int posY, int vel, int lado, int tamano)
		{
		    this.posX  = posX;
		    this.posY  = posY;
		    this.vel   = vel;
		    this.lado  = lado;
		    this.tamano= tamano;
		}
	  
		public void dibujar()
		{
			fill(51,0,25);
			ellipse(posX, posY, tamano, tamano);  
		}
	  
		public void setPosX(int posX)
		{
			this.posX = posX;
		}
	  
		public void setPosY(int posY)
		{
			this.posY = posY;
		}
	  
		public int getPosX()
		{
			return posX;  
		}
	  
		public int getPosY()
		{
			return posY;  
		}
	  
		public int getVel()
		{
			return vel;  
		}
	  
		public int getLado()
		{
			return lado;  
		}
	}
	
	class ControlTiempo extends Thread 
	{	 
		boolean running;           
		int wait;                 
		int count;           
		 		
		ControlTiempo(int w)
		{
			wait = w;
		    running = false;
		    count = 0;
		}
		 
		public void start()
		{
		    running = true;    
		    super.start();
		}
		 
		public void run()
		{
			while(true)
		    {
				try {
		        sleep((long)(wait));
		        movimientoObjetos();
				} catch (Exception e) {
				}      
		    }
		}
		 
		void quit() 
		{
		    running = false;  
		    interrupt();
		}
	}
}	