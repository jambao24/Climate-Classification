import java.io.*;
import java.util.*;

// Griffiths is perhaps best known for his 1966 book Applied Climatology, in which he introduced a new climate classification scheme, among its features being the use of 6°C in the coldest month as the poleward limit of 
// subtropical climates (this line following closely the poleward limit of where the hardiest palm trees can survive, and also the fact that 6°C is the point colder than which photosynthesis becomes impossible), 
// and placing dry climates — arid and semiarid — on the same thermal continuum as other climates, using a separate letter to denote these respective climates (F was used for all dry climates in the original scheme, 
// but X and Z have been used to denote semiarid and arid climates, respectively, in the updated scheme).
// The rules of the Griffiths scheme scheme are as follows:
//
// Temperature: 
// A (tropical) climates: All 12 months with mean monthly temperatures of 18°C or above
// B (subtropical) climates: One or more months with mean monthly temperatures below 18°C, but all 12 months with mean monthly temperatures of 6°C or above
// C (short winter) climates: 7 to 11 months with mean monthly temperatures of 6°C or above
// D (long winter) climates: 3 to 6 months with mean monthly temperatures of 6°C° or above
// E (tundra) climates: Less than 3 months with mean monthly temperatures of 6°C or above, but at least one monthly mean above 0°C. While it is mathematically possible for the warmest month in this climate to average 10°C or above, 
//		it is extremely rare for trees to be found therein, as per Otto Nordenskjöld's formula of W = 9 - 0.1C, with W denoting the warmest monthly mean temperature and C denoting the coldest monthly mean temperature, both in °C — 
// 		the best examples can be found in locations along the Hudson Bay coast of Nunavut, which are treeless despite having a warmest month above 10°C
// F (ice cap) climates: All 12 months with mean monthly temperatures of 0°C or below
//
// Seasonality of Precipitation
// "A" climates only
// A1 (essentially analogous to Af under the Köppen climate classification scheme): 10 or more months with mean monthly precipitation of 50 mm or above (not 60 mm as in the Köppen scheme)
// A2 (essentially analogous to Am in the Köppen scheme): 7-9 months with mean monthly precipitation of 50 mm or above
// A3 (essentially analogous to Aw or As in the Köppen scheme): 6 months or less with mean monthly precipitation of 50 mm or above, but not meeting the standards for designation as a semiarid or arid climate — 
// 		the original formula for determining aridity was R = 160 + 0.9T, with R denoting mean annual precipitation in mm and T the mean annual temperature in °C; however, due to this formula's lack of differentiation between semiarid 
// 		and arid climates, use of either the Köppen or the Trewartha scheme for fixing the aridity thresholds has become customary.
//
// All other climates
// U (uniform precipitation): The three consecutive wettest months do not receive twice as much precipitation as the three consecutive driest months
// S (summer precipitation): The three consecutive wettest months receive more than twice as much precipitation as the three consecutive driest months, and the second of the three consecutive wettest months occurs in the high-sun 
// 		season (April through September in the Northern Hemisphere, or October through March in the Southern Hemisphere). This is seen most commonly in the monsoon regions of southern and eastern Asia, and in the landmass interiors of Asia and North America
// W (winter precipitation — colloquially referred to as the Mediterranean climate, even though it can extend as far north as Provideniya, Russia: The three consecutive wettest months receive more than twice as much precipitation  
// 		as the three consecutive driest months, and the second of the three consecutive wettest months occurs in the low-sun season (October through March in the Northern Hemisphere, or April through October in the Southern Hemisphere)
// Under the original scheme, V for "vernal" (denoting spring maximum precipitation) and A for "autumn" (denoting autumn maximum precipitation) also existed, but these designations have largely fallen out of use.


public class GriffithsClimateClassif {
	public static int months = 12;
	public static String[] monthsList = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("Welcome! Please input the average daily temperature (°C) and precipitation (mm) for each month of a climate station.");

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
      
		// check which of July/January is warmer (true = Jul, false = Jan)
        boolean northHemisphere = (monthTemps[6] - monthTemps[0] > 2);
		boolean southHemisphere = (monthTemps[6] - monthTemps[0] < -2);
      
		// determine if climate has a warm-season or a cool-season precipitation maximum
        double precipSeasonality = getPrecipSeasonIndex(northHemisphere, southHemisphere, monthPrecip, precip);
		// use the above double value to calculate seasonality
        double precipIndex = getAridityThreshold(aveTemp, precipSeasonality);

		// calculate Holdrige biotemperature for the warmest 6 months [not used for classification]
		double WarmTemp = findWarmHoldridgeTemp(monthTemps);
      
        System.out.println("Total Annual Precipitation: " + precip + " mm");
        System.out.println("Precipitation Seasonality: " + (precipSeasonality) );
        System.out.println("Precipitation Threshold: " + precipIndex + " mm");
        System.out.println("Average Annual Temperature: " + aveTemp + " °C");
        System.out.println("Holdridge Warm-Season Biotemperature: " + WarmTemp + " °C");
		String finalString = climateClass(monthTemps, aveTemp, monthPrecip, precip, precipIndex, northHemisphere, southHemisphere);

		System.out.println("Average Mean Monthly Temperatures:  " + Arrays.toString(monthTemps));
		System.out.println("Average Mean Monthly Precipitation:  " + Arrays.toString(monthPrecip));
		System.out.print("Griffiths Climate Classification:  " + finalString);
        //System.out.println(" (" + finalString[1] + ")");
	}


	// construct a double array of entered double values from the scanner input
	public static double[] inputData(Scanner console, String c) {
		System.out.println("Please enter in the monthly data for " + c + ". ");
		double[] newArray = new double[months];

		for (int i = 0; i < months; i++) {
			System.out.println(monthsList[i] + ": ");
			newArray[i] = console.nextDouble();
		}
		System.out.println();
		return newArray;
	}

	/*
	// method returns the temperature for the month with greatest precipitation
    public static double getMaxPrecipMonthTemp(double[] temperature, double[] precip) {
        double maxPrecip = precip[0];
        double selectTemp = temperature[0];
        for (int i = 1; i < months; i++) {
           if (precip[i] > maxPrecip) {
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
   */
   
   // helper method of sorts for getPrecipSeasonIndex for tropical climates with no seasonal temperature difference
   // returns a normalized ratio of the difference between the wettest and driest 6-month periods 
   public static double getPrecipVariance(double[] prec, double precSum) {
	   		double[] vals = new double[months];

			// get the sum of the 6-month precipitation beginning with the selected month
			for (int j = 0; j < months; j++) {
				vals[j] = 0.0;
				vals[j] += prec[j];
				vals[j] += prec[(j+1) % months];
				vals[j] += prec[(j+2) % months];
				vals[j] += prec[(j+3) % months];
				vals[j] += prec[(j+4) % months];
				vals[j] += prec[(j+5) % months];
			}
			
			// get max and min values of vals. Return the value of (max - min)/precSum - this should be between 0.0 and 1.0 
			double maxv = prec[0];
			double minv = prec[0];
			for (int i = 1; i < months; i++) {
			   	if (prec[i] > maxv) 
					maxv = prec[i];
				if (prec[i] < minv)
					minv = prec[i];
			}
			return (maxv-minv)/precSum;
   }


	// calculate seasonality of monthly precipitation relative to the warmer half of the year
	// if both booleans are false, then the climate is super-tropical and it makes no sense to speak of a warmer half of the year
	public static double getPrecipSeasonIndex(boolean julySummer, boolean janSummer, double[] precip, double precipSum) {
		double warmSeasonSum = 0.0;
		if (julySummer){
 			for (int i = 3; i < 9; i++)
		 		warmSeasonSum += precip[i];
		}
		else if (janSummer) {
 			warmSeasonSum = precipSum;
	 		for (int i = 3; i < 9; i++)
			 	warmSeasonSum -= precip[i];
		}

		// Case: no warm season- just measure stdev of precipitation?
		// compare difference between max 6-month precip sum and min 6-month precip sum
		else {
 			double s_val = getPrecipVariance(precip, precipSum);
 			if (s_val < 0.8)
		 		return (280.0-140.0)/0.8*s_val + 140.0;
 			return 280.0;
		}

		// Case: warm season, we actually need to measure precipitation in warmer half of year
		if (warmSeasonSum/precipSum <= 0.1)
 			return 0.0;
		else if (warmSeasonSum/precipSum < 0.9)
 			return 350*(warmSeasonSum/precipSum - 0.1);
		else
	 		return 280.0;
	}

	
	// populate array of 3-month precipitation totals (e.g. Jan is Dec-Jan-Feb)
	public static double[] getThreeMonthTotal(double[] prec) {
		double[] vals = new double[months];
		for (int i = 1; i < months+1; i++) {
			vals[i % months] = 0.0;
			vals[i % months] += prec[(i-1) % months];
			vals[i % months] += prec[(i) % months];
			vals[i % months] += prec[(i+1) % months];
		}
		return vals;
	}

	// find which 3 consecutive months are the wettest -> for non-tropical climates only
	// return the maximum value in the 3-month precipitation totals array
	public static int getMaxThreeMonthTotal(double[] sumvals) {
		int tval = 0;
		double maxv = sumvals[0];
		for (int k = 0; k < sumvals.length; k++) {
			if (sumvals[k] > maxv) {
				maxv = sumvals[k];
				tval = k;
			}
		}
		return tval;
	}

	// find which 3 consecutive months are the driest -> for non-tropical climates only
	// return the maximum value in the 3-month precipitation totals array
	public static int getMinThreeMonthTotal(double[] sumvals) {
		int tval = 0;
		double minv = sumvals[0];
		for (int k = 0; k < sumvals.length; k++) {
			if (sumvals[k] < minv) {
				minv = sumvals[k];
				tval = k;
			}
		}
		return tval;
	}
   
   
	// get the aridity threshold for precipitation
	public static double getAridityThreshold(double aveTemp, double seasonality) {
		double baseline = 0.0;
		if (aveTemp < 10.0)
			baseline = 100.0 + 10*(aveTemp);
		else if (aveTemp < 20.0)
			baseline = 200.0 + 20*(aveTemp - 10);
		else if (aveTemp < 30.0)
			baseline = 400.0 + 30*(aveTemp - 20);
		else
			baseline = 700.0 + 40*(aveTemp - 30);
         
		if (baseline + seasonality <= 0.0)
			return 0.0;
		return (baseline + seasonality);
	}
   
	// find max and min average monthly temperatures/precipitation
	public static double findVal(double[] monthVals, boolean isMax)
	{
		if (isMax) {
			double maxTemp = monthVals[0];
			for (int i = 0; i < months; i++) {
				if (monthVals[i] > maxTemp) 
					maxTemp = monthVals[i];
			}
			return maxTemp;
		}
		else {
			double minTemp = monthVals[0];
			for (int i = 0; i < months; i++) {
				if (monthVals[i] < minTemp) 
					minTemp = monthVals[i];
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
			if (monthVals[i] == val) 
				return i;
		}
		return -1;
	}
	
	// find the Holdridge average biotemperature for the 6 warmest months (average of the highest 6 values in monthTemps)
	public static double findWarmHoldridgeTemp(double[] monthTemps) {
		double[] warmTemps = new double [months];
		for (int k = 0; k < months; k++) {
			warmTemps[k] = (monthTemps[k] < 0.0)
				? 0.0 : monthTemps[k];
		}
		Arrays.sort(warmTemps);
		double sumTemps = 0.0;
		for (int l = warmTemps.length/2; l < warmTemps.length; l++) {
			sumTemps += warmTemps[l];
		}
		return sumTemps/(months/2);
	}


		// climate classification method based on the Koppen-Geiger and Trewartha classification schemes
	// also computes average Holdridge biotemperature for the warmest 6 months
	public static String climateClass(double[] monthTemps, double aveTemp, double[] monthPrecip, double precip, 
	double precipIndex, boolean northHemisphere, boolean southHemisphere) {
		//String[] output = new String[2];
	 	String first = "";
		String second = "";
		String third = "";
		
		if (precip < 0.5*precipIndex) 
			second = "R"; // arid climate
		else if (precip < precipIndex) 
			second = "Z"; // semi-arid climate
		
      
		if (getMonthNum(monthTemps, 18.0, true) == 12) { 
			first = "A"; // Tropical climate- all months above 18.0 C
			
			// Precipitation subclassification
			if (second.length() == 0)
			{
				if (getMonthNum(monthPrecip, 50.0, true) > 9) 
					second = "1"; // rainforest
				else if (getMonthNum(monthPrecip, 50.0, true) > 6) 
					second = "2"; // monsoon
				else 
					second = "3"; // savanna 
			}
		} 
		else if (getMonthNum(monthTemps, 0.0, true) >= 1) { // Non-Tropical/non-Ice Cap Climates
			double maxTemp = findVal(monthTemps, true);

			if (getMonthNum(monthTemps, 6.0, true) == 12) 
				first = "B"; // Subtropical climate- all months above 6.0 C
			else if (getMonthNum(monthTemps, 6.0, true) > 6) 
				first = "C"; // Temperate ("short-winter") climate
			else if (getMonthNum(monthTemps, 6.0, true) > 3) 
				first = "D"; // Continental ("long-winter") climate
			else 
				first = "E"; // Polar/Alpine ("Tundra") climate
			
			// Temperature classification
			if ((getMonthNum(monthTemps, 6.0, true) > 3) && maxTemp > 22.0) 
				third = "a"; // hot-summer
			else if ((getMonthNum(monthTemps, 6.0, true) > 3) && maxTemp < 22.0) 
				third = "b"; // warm-summer
			
			// Precipitation classification
			if (second.length() == 0) {

				double[] threeMoVals = getThreeMonthTotal(monthPrecip);
				int maxPMonth = getMaxThreeMonthTotal(threeMoVals);
				int minPMonth = getMinThreeMonthTotal(threeMoVals);
				double maxminRatio = threeMoVals[maxPMonth]/threeMoVals[minPMonth];

				if ((maxminRatio >= 2.0) && ((northHemisphere && minPMonth < 3 || minPMonth > 8) || (southHemisphere && minPMonth > 2 || minPMonth < 9))) 
					second = "W"; // wet-winter/"Mediterranean"
				else if ((maxminRatio >= 2.0) && ((southHemisphere && minPMonth < 3 || minPMonth > 8) || (northHemisphere && minPMonth > 2 || minPMonth < 9)))
					second = "S"; // wet-summer/"monsoon"
				else 
					second = "U"; // default
			}
		} 
		else // Ice Cap climate- all months below 0.0 C
			first = "F";
	   
		return first + second + third;
	}

}
