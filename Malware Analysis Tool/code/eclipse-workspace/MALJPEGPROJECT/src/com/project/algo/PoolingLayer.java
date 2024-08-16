package com.project.algo;

import static com.project.algo.Util.checkPositive;
import static com.project.algo.Util.checkValueInRange;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

import ij.process.ColorProcessor;

/**
 * A plate layer that performs max pooling on a specified window. There is no overlap between different placements of
 * the window.
 * 
 * TODO: This implementation does not let you vary stride. In the future, we may want to add that feature.
 */
public class PoolingLayer {
	
	public static float[][] R;
	public static float[][] G;
	public static float[][] B;
	public static float[][] H;
	public static float[][] S;
	public static float[][] V;
	
	public static int[][] bin;
	static int height;
	static int width;
	public static double[][] allFeatures=new double[1000][66];
	public static double[] features=new double[66];
	public static Calendar cal=Calendar.getInstance();
	public static PrintStream outFile;
	public Boolean flag=Boolean.FALSE;
	
	 public boolean extractAll(String input, String filename) throws IOException {
	    	
	    	try{
	    		
	    		outFile=new PrintStream(filename);
	    	}
	    	catch (Exception e){
	    		e.printStackTrace();
	    	}
	    	
		        BufferedImage image = ImageIO.read(new File(input));
		       
		        ColorProcessor cp=new ColorProcessor(image);
		        
		         /////////////// Color Extraction /////////////////
		        
		        Extraction ce=new Extraction(cp);
		        ce.extract();
		        double[] colors=ce.getColorFeature();

		        ////////////// Edge Detection ///////////////        
		        
		        Detector ced=new Detector();
		        ced.setSourceImage(image);
		        ced.process();
		        BufferedImage edge=ced.getEdgesImage();
		        rgb2bin(edge);
		        
		        ////////////// Texture Extraction ///////////////        
		        
		        MainExtraction te=new MainExtraction(bin, height, width);
		        te.extract();
		        double[] textures=te.getCoocFeatures();
		        
		        for (int i=0;i<colors.length;i++)
		        {
		        	outFile.print(colors[i]+ "\t\n");
		        	flag=true;
		        }
		        
		        //System.out.println("*************");
	        
		        for (int i=0;i<textures.length;i++)
		        {
		        	outFile.print(textures[i]+ "\t\n");
		        	flag=true;
		        }
		        outFile.println();
		        //allFeatures[ind]=features;
				return flag;
		        
		      
	    }
	public static void extractAll1(String input, String test_featurefile) throws IOException {
	    	
	    	try{
	    		
	    		outFile=new PrintStream(test_featurefile);
	    		
	    	}
	    	catch (Exception e){
	    		e.printStackTrace();
	    	}
	    	
		        BufferedImage image = ImageIO.read(new File(input));
		       
		        ColorProcessor cp=new ColorProcessor(image);
		        
		         /////////////// Color Extraction /////////////////
		        
		        Extraction ce=new Extraction(cp);
		        ce.extract();
		        double[] colors=ce.getColorFeature();

		        ////////////// Edge Detection ///////////////        
		        
		        Detector ced=new Detector();
		        ced.setSourceImage(image);
		        ced.process();
		        BufferedImage edge=ced.getEdgesImage();
		        rgb2bin(edge);
		        ////////////// Texture Extraction ///////////////        
		        
		        MainExtraction te=new MainExtraction(bin, height, width);
		        te.extract();
		        double[] textures=te.getCoocFeatures();
		        
		        for (int i=0;i<colors.length;i++)
		        {
		        	outFile.print(colors[i]+ "\t\n");
		        }
		        
		        //System.out.println("*************");
	        
		        for (int i=0;i<textures.length;i++)
		        {
		        	outFile.print(textures[i]+ "\t\n");
		        }
		        outFile.println();
		        //allFeatures[ind]=features;
		        
		      
	    }
	    public static void RGB_HSV_Decompose(ColorProcessor cp){
	    	
	    	R=new float[cp.getHeight()][cp.getWidth()];
	    	G=new float[cp.getHeight()][cp.getWidth()];
	    	B=new float[cp.getHeight()][cp.getWidth()];
	    	H=new float[cp.getHeight()][cp.getWidth()];
	    	S=new float[cp.getHeight()][cp.getWidth()];
	    	V=new float[cp.getHeight()][cp.getWidth()];
	    	int rgb[] =new int[3];
	    	float hsv[]=new float[3];
	    	for (int i=0;i<cp.getHeight();i++){
	    		for (int j=0;j<cp.getWidth();j++){
	    			rgb=cp.getPixel(i, j, rgb);
	    			R[i][j]=rgb[0];
	    			G[i][j]=rgb[1];
	    			B[i][j]=rgb[2];
	    			hsv=java.awt.Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsv);
	    			H[i][j]=hsv[0];
	    			S[i][j]=hsv[1];
	    			V[i][j]=hsv[2];
	    		}
	    	}
	    }
	    
	    public static double[] meanStdSkew ( float[][] data, int h, int w )
	    {
		    double mean = 0;
		    double[] out=new double[3];
		    
		    for (int i=0;i<h;i++){
	        	for (int j=0;j<w;j++){
	        		mean += data[i][j];
	        	}
		    }
		    mean /= (h*w);
		    out[0]=mean;
		    double sum = 0;
		    for (int i=0;i<h;i++){
	        	for (int j=0;j<w;j++){
	        		final double v = data[i][j] - mean;
	        		sum += v * v;
	        	}
		    }
		    out[1]=Math.sqrt( sum / ( h*w - 1 ) );
		    
		    sum = 0;
		    for (int i=0;i<h;i++){
	        	for (int j=0;j<w;j++){
	        		final double v = (data[i][j] - mean)/out[1];
	        		sum += v * v * v;        		
	        	}
		    }
		    
		    out[2]=Math.pow(sum/(h*w-1),1./3);
		    return out;
	    }

	    public static void rgb2bin(BufferedImage in)
	    {
	    	int h=in.getHeight();
	    	int w=in.getWidth();
	    	height=h;
	    	width=w;
	    	int[][] out=new int[h][w];
	    	long rgb;
	    	int r,g,b;
	    	ColorProcessor cp=new ColorProcessor(in);
	    	for (int i=0;i<h;i++){
	    		for (int j=0;j<w;j++){
	    			rgb=cp.getPixel(i, j);
	    	        r   = (int)(rgb % 0x1000000 / 0x10000);  
	    	        g = (int)(rgb % 0x10000 / 0x100);  
	    	        b = (int)(rgb % 0x100);  
	    	        if (r==0 && g==0 && b==0){
	    	        	out[i][j]=0;
	    	        }
	    	        else{
	    	        	out[i][j]=1;
	    	        }
	    		}
	    	}
	    	
	    	bin= out;
	    	
	    }
	// Similar to a plate, except it uses booleans so it's more memory efficient.
	private ArrayList<boolean[][]> maximumOfWindow;


	
	/** Returns the max-pooled plate. No overlap between each pool. */
	public Plate maxPool(Plate plate, boolean[][] maximumOfPlate, int windowHeight, int windowWidth) {
		checkValueInRange(windowHeight, 0, plate.getHeight(), "Max pool window height");
		checkValueInRange(windowWidth, 0, plate.getWidth(), "Max pool window width");
		int resultHeight = plate.getHeight() / windowHeight;
		int resultWidth = plate.getWidth() / windowWidth;
		resultHeight += plate.getHeight() % windowHeight == 0 ? 0 : 1;
		resultWidth += plate.getWidth() % windowWidth == 0 ? 0 : 1;
		
		double[][] result = new double[resultHeight][resultWidth];
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[i].length; j++) {
				int windowStartI = Math.min(i * windowHeight, plate.getHeight() - 1);
				int windowStartJ = Math.min(j * windowWidth, plate.getWidth() - 1);
				result[i][j] =
						maxValInWindow(
								plate,
								maximumOfPlate,
								windowStartI,
								windowStartJ,
								windowHeight,
								windowWidth);
			}
		}
		return new Plate(result);
	}
	
	private double maxValInWindow(
			Plate plate, boolean[][] maximumOfPlate, int windowStartI, int windowStartJ, int windowHeight, int windowWidth) {
		double max = Double.MIN_VALUE;
		int windowEndI = Math.min(windowStartI + windowHeight - 1, plate.getHeight() - 1);
		int windowEndJ = Math.min(windowStartJ + windowWidth - 1, plate.getWidth() - 1);
		int maxI = -1;
		int maxJ = -1;
		for (int i = windowStartI; i <= windowEndI; i++) {
			for (int j = windowStartJ; j <= windowEndJ; j++) {
				double value = plate.getValues()[i][j];
				if (value > max) {
					max = value;
					maxI = i;
					maxJ = j;
				}
			}
		}
		maximumOfPlate[maxI][maxJ] = true;
		return max;
	}

	/** Returns a new builder. */
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * Simple builder pattern for creating PoolingLayers. (Not very helpful now, but later we may want to add other
	 * parameters.)
	 */
	public static class Builder {
		private int windowHeight = 0;
		private int windowWidth = 0;

		private Builder() {
		}

		public Builder setWindowSize(int height, int width) {
			checkPositive(height, "Window height", false);
			checkPositive(width, "Window width", false);
			this.windowHeight = height;
			this.windowWidth = width;
			return this;
		}

		
	}
}
