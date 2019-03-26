import java.io.*;
import java.util.*;


public class HolridgeClimateClassif {
	public static int months = 12;
   public static String[] monthsList = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

	public static void main(String[] args) throws FileNotFoundException {
		Scanner console = new Scanner(System.in);
		String a = "temperature";
		String b = "precipitation";
		double[] monthTemps = inputData(console, a);
		double[] monthPrecip = inputData(console, b);
	
		double precip = 0.0;
		for (int i = 0; i < months; i++) {
			precip += monthPrecip[i];
		}
      
		double WarmTemp = findWarmHoldridgeTemp(monthTemps);
      
      System.out.println("Total Annual Precipitation: " + precip);
      System.out.println("Holdridge Warm-Season Biotemperature: " + WarmTemp);
		String finalString = climateClassHoldridge(monthTemps, precip, WarmTemp);

		System.out.println("Average Mean Monthly Temperatures:  " + Arrays.toString(monthTemps));
		System.out.println("Average Mean Monthly Precipitation:  " + Arrays.toString(monthPrecip));
		System.out.println("Holdridge Climate Classification:  " + finalString);
	}	


   // construct a double array of entered double values from the scanner input
	public static double[] inputData(Scanner console, String c) {
		System.out.println("Please enter in the monthly data for " + c + ". ");
		double[] newArray = new double[months];

		for (int i = 0; i < months; i++) {
			System.out.print(monthsList[i] + ": ");
			newArray[i] = console.nextDouble();
		}
      System.out.println();
      return newArray;
	}


   // return a climate classification category based on the average monthly temperatures,
   // total annual precipitation, and Holdridge average biotemperature for the warmest 6 months
	public static String climateClassHoldridge(double[] monthTemps, double precip, double WarmTemp) {
		String thermal = "";
		String moisture = "";

		double minTemp = monthTemps[0];
		double maxTemp = monthTemps[0];
		for (int i = 0; i < months; i++) {
			if (monthTemps[i] > maxTemp) {
				maxTemp = monthTemps[i];
			} else if (monthTemps[i] < minTemp) {
				minTemp = monthTemps[i];
			}
		}
	
		if (maxTemp <= 10.0) {
			if (maxTemp - minTemp > 3.0) {
				thermal = "alpine";
			} else {
				thermal = "paramo";
			}
		} else if (minTemp < 0.0) {
			if (WarmTemp < 12.0) {
				thermal = "boreal";
			} else if (WarmTemp < 18.0) {
				thermal = "cold temperate";
			} else {
				thermal = "warm continental";
			}
		} else if (minTemp > 0.0) {
			if (WarmTemp < 12.0) {
				thermal = "cool maritime";
			} else if (WarmTemp < 18.0) {
				thermal = "mild maritime";
			} else if (WarmTemp >= 18.0 && minTemp < 9.0){
				thermal = "warm temperate";
			} else if (WarmTemp >= 18.0 && minTemp < 18.0){
				thermal = "subtropical";
			} else {
				thermal = "tropical";
			}
		}

		double[] HoldridgeTemps = new double [months];
		double calcHoldridge = 0.0;
		for (int j = 0; j < months; j++) {
			if (monthTemps[j] < 0) {
				HoldridgeTemps[j] = 0;
			}
         HoldridgeTemps[j] = monthTemps[j];
			calcHoldridge += HoldridgeTemps[j];
		}
		double aveHoldridge = calcHoldridge/months;
		
		double annualPET = aveHoldridge * 58.93;

	
		if (thermal != "alpine" && thermal != "paramo") {
			if (precip/annualPET > 1) {
				moisture = "humid";
			} else if (precip/annualPET >= 0.5) {
				moisture = "subhumid";
			} else if (precip/annualPET >= 0.25) {
				if (thermal == "cool maritime" || thermal == "boreal") {
					moisture = "dry";
				} else {
					moisture = "semiarid";
				}
			} else {
				moisture = "arid";
			}
		}
	
		return moisture + " " + thermal;
	}



   // find the Holdridge average biotemperature (average of the highest 6 values in monthTemps)
	public static double findWarmHoldridgeTemp(double[] monthTemps) {
		double[] warmTemps = new double [months];
      for (int k = 0; k < months; k++) {
         warmTemps[k] = monthTemps[k];
      }
		Arrays.sort(warmTemps);
      double sumTemps = 0.0;
		for (int l = warmTemps.length/2; l < warmTemps.length; l++) {
			sumTemps += warmTemps[l];
		}
		return sumTemps/(months/2);
	}

}