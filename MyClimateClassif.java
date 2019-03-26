import java.io.*;
import java.util.*;


public class MyClimateClassif {
	public static int months = 12;
   public static String[] monthsList = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

	public static void main(String[] args) throws FileNotFoundException {
		Scanner console = new Scanner(System.in);
		String a = "temperature";
		String b = "precipitation";
		double[] monthTemps = inputData(console, a);
		double[] monthPrecip = inputData(console, b);
	
		double precip = 0.0;
      double aveTemp = 0.0;
		for (int i = 0; i < months; i++) {
			precip += monthPrecip[i];
         aveTemp += monthTemps[i];
		}
      aveTemp = aveTemp/months;
      
      boolean northHemisphere = (monthTemps[6] - monthTemps[0] > 0);
      
      double precipSeasonality = getPrecipSeasonIndex(northHemisphere, monthPrecip, precip);
      double precipIndex = getAridityThreshold(aveTemp, precipSeasonality);
      boolean maxPrecipWarm = (getMaxPrecipMonthTemp(monthTemps, monthPrecip) >= aveTemp);
      boolean minPrecipWarm = (getMinPrecipMonthTemp(monthTemps, monthPrecip) >= aveTemp);
      
		double WarmTemp = findWarmHoldridgeTemp(monthTemps);
      
      System.out.println("Total Annual Precipitation: " + precip);
      System.out.println("Precipitation Seasonality: " + (precipSeasonality) );
      System.out.println("Precipitation Threshold: " + precipIndex);
      System.out.println("Average Annual Temperature: " + aveTemp);
      System.out.println("Holdridge Warm-Season Biotemperature: " + WarmTemp);
		String[] finalString = climateClass(monthTemps, aveTemp, monthPrecip, precip, 
   precipIndex, maxPrecipWarm, minPrecipWarm, northHemisphere);

		System.out.println("Average Mean Monthly Temperatures:  " + Arrays.toString(monthTemps));
		System.out.println("Average Mean Monthly Precipitation:  " + Arrays.toString(monthPrecip));
		System.out.print("Modified Climate Classification:  " + finalString[0]);
      System.out.println(" (" + finalString[1] + ")");
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
   
   // method returns the temperature for the month with greatest precipitation
   public static double getMaxPrecipMonthTemp(double[] temperature, double[] precip) {
      double maxPrecip = precip[0];
      double selectTemp = temperature[0];
      for (int i = 1; i < months; i++)
      {
         if (precip[i] > maxPrecip)
         {
            maxPrecip = precip[i];
            selectTemp = temperature[i];
         }
      }
      return selectTemp;
   }
   
   // method returns the temperature for the month with least precipitation
   // if there are multiple months with 0 precipitation, the temperatures of those months are averaged
   public static double getMinPrecipMonthTemp(double[] temperature, double[] precip) {
      double minPrecip = precip[0];
      double selectTemp = temperature[0];
      int zeroMonths = 0;
      if (minPrecip == 0.0)
         zeroMonths = 1;
      
      for (int i = 1; i < months; i++) {
         if (precip[i] < minPrecip) {
            if (precip[i] == 0) {
               minPrecip = precip[i];
               selectTemp = temperature[i];
               zeroMonths++;
            }
            minPrecip = precip[i];
            selectTemp = temperature[i];
         }
         else if (precip[i] == minPrecip && zeroMonths > 0) {
            selectTemp += temperature[i];
            zeroMonths++;
         }
      }
      
      if (zeroMonths > 0) {
         return selectTemp/zeroMonths;
      }     
      return selectTemp;
   }
   
   // calculate seasonality of monthly precipitation relative to the warmer half of the year
   public static double getPrecipSeasonIndex(boolean julySummer, double[] precip, double precipSum) {
      double warmSeasonSum = 0.0;
      if (julySummer){
         for (int i = 3; i < 9; i++)
            warmSeasonSum += precip[i];
      }
      else {
         warmSeasonSum = precipSum;
         for (int i = 3; i < 9; i++)
            warmSeasonSum -= precip[i];
      }
      
      if (warmSeasonSum/precipSum <= 0.1)
         return 0.0;
      else if (warmSeasonSum/precipSum < 0.9)
         return 350*(warmSeasonSum/precipSum - 0.1);
      else
         return 280.0;
   }
   
   
   // get the aridity threshold for precipitation
   public static double getAridityThreshold(double aveTemp, double seasonality) {
      double baseline = 0.0;
      if (aveTemp < 10)
         baseline = 100.0 + 10*(aveTemp);
      else if (aveTemp < 20)
         baseline = 200.0 + 20*(aveTemp - 10);
      else if (aveTemp < 30)
         baseline = 400.0 + 30*(aveTemp - 20);
      else
         baseline = 700.0 + 40*(aveTemp - 30);
         
      if (baseline + seasonality <= 0)
         return 0.0;
      return (baseline + seasonality);
   }
   
   // find max and min average monthly temperatures/precipitation
   public static double findVal(double[] monthVals, boolean isMax)
   {
      if (isMax) {
		   double maxTemp = monthVals[0];
		   for (int i = 0; i < months; i++) {
		   	if (monthVals[i] > maxTemp) {
		   		maxTemp = monthVals[i];
		   	}
		   }
         return maxTemp;
      }
      else {
         double minTemp = monthVals[0];
		   for (int i = 0; i < months; i++) {
		   	if (monthVals[i] < minTemp) {
		   		minTemp = monthVals[i];
		   	}
		   }
         return minTemp;
      }
   }
   
   // find number of months where the temperature is above or below a certain threshold
   public static int getMonthNum(double[] monthTemps, double val, boolean isGreater) {
      int monthsNum = 0;
      if (isGreater) {
		   for (int i = 0; i < months; i++) {
		   	if (monthTemps[i] > val) 
		   		monthsNum++;
		   }
      }
      else {
		   for (int i = 0; i < months; i++) {
		   	if (monthTemps[i] <= val) 
		   		monthsNum++;
		   }
      }
      return monthsNum;
   }
   
      // find month in which the min or max value occurs
   public static int returnMonthNum(double[] monthVals, double val) {
      for (int i = 0; i < months; i++) {
		   if (monthVals[i] == val) {
		  		return i;
		  	}
      }
      return -1;
   }
   
   
   // climate classification method based on the Koppen-Geiger and Trewartha classification schemes
   // also computes average Holdridge biotemperature for the warmest 6 months
	public static String[] climateClass(double[] monthTemps, double aveTemp, double[] monthPrecip, double precip, 
   double precipIndex, boolean maxPWarm, boolean minPWarm, boolean northHemisphere) {
      String[] output = new String[2];
		String first = "";
		String second = "";
      String third = "";
      boolean xtrm = false;
      
      double maxTemp = findVal(monthTemps, true);
      double minTemp = findVal(monthTemps, false);
      double maxPrecip = findVal(monthPrecip, true);
      double minPrecip = findVal(monthPrecip, false);
	   
      
      if (precip < 0.5*precipIndex) {
         second = "R"; // arid climate
      } else if (precip < precipIndex) {
         second = "Z"; // semi-arid climate
      }
      
		if (minTemp > 18.0) { 
         first = "A"; // Tropical climate- all months above 18.0 C
         // Temperature subclassification
			if (maxTemp - minTemp < 3.0) { // Equatorial (less than 3.0 C monthly temperature range) 
				if (aveTemp > 28.0){
               third = "E";
            } else {
               third = "e";
            }
			} else if (aveTemp < 22.5) { // Cool/Mild (less than 22.5 C average annual temperature)
				third = "b";
			} else if (aveTemp > 28.0 && minTemp >= 22.5) {
            third = "A";
         } else {
            third = "a";
         }
         if (aveTemp > 28.0 && getMonthNum(monthTemps, 32.0, true) > 6) {
            xtrm = true;
         }
         
         // Precipitation subclassification
         if (second.length() == 0)
         {
            if (minPrecip > 60.0) {
               second = "f"; // rainforest
            } else if (minPrecip > (100 - precip/25)) {
               second = "m"; // monsoon
            } else if (northHemisphere && returnMonthNum(monthPrecip, maxPrecip) > 3
            && returnMonthNum(monthPrecip, maxPrecip) < 10 || !northHemisphere && 
            returnMonthNum(monthPrecip, maxPrecip) < 4 && returnMonthNum(monthPrecip, maxPrecip) > 8) {
               second = "w"; // savanna (normal)
            } else {
               second = "s"; // savanna (dry-summer)
            }
         }
		} else if (getMonthNum(monthTemps, 10.0, true) > 3) { // Non-Tropical/non-Polar Climates
         if (minTemp > 10.0) {
            first = "B"; // Subtropical climate- all months above 10.0 C
            if (getMonthNum(monthTemps, 32.0, true) > 3) {
               xtrm = true;
            }
         } else if (minTemp >= -3.0) {
            first = "C"; // Temperate ("mild-winter") climate
         } else {
            first = "D"; // Continental ("snowy-winter") climate
         }
         // Temperature classification
         if (maxTemp > 22.5) {
            third = "a"; // hot-summer
         } else {
            third = "b"; // warm-summer
         }
         // Precipitation classification
         if (second.length() == 0) {
            if (minPWarm && maxPrecip/minPrecip >= 3.0 && minPrecip < 40.0) {
               second = "s"; // dry-summer/"Mediterranean"
            } else if (maxPWarm && maxPrecip/minPrecip >= 10.0){
               second = "w"; // dry-winter/"monsoon"
            } else {
               second = "f"; // default
            }
         }
		} else if (getMonthNum(monthTemps, 10.0, true) > 0) { 
		   first = "E"; // Subpolar climate- 3 or less months above 10.0 C
         // Temperature classification
         if (minTemp <= -3.0) {
            third = "d"; // "continental (snowy-winter)"
            if (minTemp <= -35.0) {
               xtrm = true;
            }
         } else if (minTemp  <= 6.0) {
            third = "c"; // "temperate (mild-winter")
         } else {
            third = "b"; // "subtropical/low-latitude"
         }
         // Precipitation classification
         if (second.length() == 0) {
            if (minPWarm && maxPrecip/minPrecip >= 3.0 && minPrecip < 40.0) {
               second = "s"; // dry-summer/"Mediterranean"
            } else if (maxPWarm && maxPrecip/minPrecip >= 10.0) {
               second = "w"; // dry-winter/"monsoon"
            } else {
               second = "f"; // default
            }
         }
		} else { // Polar climate- all months below 10.0 C
			if (maxTemp < 0.0) {
				first = "OF";
            if (getMonthNum(monthTemps, -40.0, false) >= 3 && minTemp < -50.0) {
               xtrm = true;
            }
			} else {
				first = "OT";
			}
         if (second.length() > 0)
            first.concat(".");
         if (minTemp > -3.0)
            third = ".m";
		}
	   
      if (xtrm) {
         output[0] = "xtrm " + first + second + third;
         output[1] = nameClimate(first, second, third) + "*";
      } else {
		   output[0] = first + second + third;
         output[1] = nameClimate(first, second, third);
      }
      return output;
	}



   // find the Holdridge average biotemperature (average of the highest 6 values in monthTemps)
	public static double findWarmHoldridgeTemp(double[] monthTemps) {
		double[] warmTemps = new double [months];
      for (int k = 0; k < months; k++) {
         if (monthTemps[k] < 0.0)
            warmTemps[k] = 0.0;
         warmTemps[k] = monthTemps[k];
      }
		Arrays.sort(warmTemps);
      double sumTemps = 0.0;
		for (int l = warmTemps.length/2; l < warmTemps.length; l++) {
			sumTemps += warmTemps[l];
		}
		return sumTemps/(months/2);
	}
   
   // assign a string name to the three-letter classification
   public static String nameClimate(String a, String b, String c) {
      String name = "";
      
      switch(a) {
         case "OT": name = "Polar/Alpine Tundra";
         break;
         case "OF": name = "Polar/Alpine Ice Cap";
         break;
         case "OT.": name = "Polar/Alpine";
         break;
         case "OF.": name = "Polar/Alpine";
         break;
         case "E": name = "Subpolar/Subalpine";
         break;
         case "D": name = "Continental";
            if (b != "Z" && b != "R") {
               if (b == "w") {
                  name += " monsoon";
               } else {
                  name += " humid";
               } 
               switch(c) {
                  case "a": name += " Type I";
                  break;
                  case "b": name += " Type II";
                  break;
               }
            }
         break;
         case "C": name = "Temperate";
            if (b == "s" && c == "a") {
               name = "Mediterranean";
            } else if (b == "f" && c == "b") {
               name += " oceanic";
            } else if (b == "w") {
               name += " monsoon";
            } else if (b != "Z" && b != "R") {
               name += " humid";
            }  
         break;
         case "B": name = "Subtropical";
            if (b == "s") {
               name = "Mediterranean";
            } else if (b == "f" && c == "b") {
               name += " oceanic";
            } else if (b == "w") {
               name += " monsoon";
            } else if (b != "Z" && b != "R") {
               name += " humid";
            }   
         break;
         case "A": name = "Tropical";
            if (c == "e" || c == "E") {
               name = "Equatorial";
            }
            if (b == "f") {
               name += " rainforest";
            } else if (b == "m") {
               name += " monsoon";
            } else if (b == "w" || b == "s") {
               name += " savannah";
            }
         break;
      }
      
      switch(b){
         case "Z": name += " steppe";
         break;
         case "R": name += " desert";
         break;
      }   
      return name;
   }
}
