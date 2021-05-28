import java.io.*;
import java.util.*; 


// Source: http://www.city-data.com/forum/weather/3273176-climate-classifications-system-i-made.html
// this version uses 0.5 as the summer wetness and winter wetness thresholds for "Mediterranean/dry-summer" and "monsoon/dry-winter"
// thresholds for the Subtropical and Warm Temperate zones. 


public class cherrychips666ClimateClassif {
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
	    for (int i = 0; i < months; i++) {
	        precip += monthPrecip[i];
	    }
      
	    double HBioTemp = findBioTemp(monthTemps);
        double thresh1 = findEvapo(HBioTemp);
        double thresh2 = getIndex(thresh1, precip);
      
        System.out.println("Total Annual Precipitation: " + precip + " mm");
        System.out.println("Holdridge Biotemperature: " + HBioTemp + " °C");
        System.out.println("Calculated Evapotranspiration Rate: " + thresh2 + " mm");
	    String finalString = climateClasscherrychips666(monthTemps, monthPrecip, precip, HBioTemp);
	
	    System.out.println("Average Mean Monthly Temperatures:  " + Arrays.toString(monthTemps));
	    System.out.println("Average Mean Monthly Precipitation:  " + Arrays.toString(monthPrecip));
	    System.out.println("cherrychips666 Climate Classification:  " + finalString);
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

    // find the Holdridge average biotemperature 
    public static double findBioTemp(double[] monthTemps) {
	double[] bioTemps = new double [months];
        for (int k = 0; k < months; k++) {
            bioTemps[k] = monthTemps[k] < 0.0
                ? 0.0
                : monthTemps[k];
        }
        double sumTemps = 0.0;
        for (int l = 0; l < months; l++) {
	        sumTemps += bioTemps[l];
	    }   
	    return sumTemps/months;
    }

    // find the evapotranspiration threshold
    public static double findEvapo(double HBioTemp) {
        return 58.93 * HBioTemp;
    }

    // find the wetness threshold
    public static double getIndex(double transp, double prec) {
        return prec/transp;
    }

    // find the indices of the 3 coldest months
    public static int[] coldestMonths(double[] monthTemps) {
        // loop through the array of 12 values to find the coldest
        double min = monthTemps[0];
        int minv = 0;
        for (int i = 0; i < months; i++) {
            if (monthTemps[i] < min) {
                min = monthTemps[i];
                minv = i;
            }
        }
        // actually on second thought, we can instead assume that the 3 coldest months are consecutive.
        // Which makes sense since this function (Java method) will only be applied to non-equatorial climates that have 
        // seasonal temperature variations based on differences in insolation. 

        // determine if preceding or following month of coldest is colder (2nd coldest)
        int minv2_f = (minv < 11)
            ? minv + 1
            : 0;
        int minv2_p = (minv > 0)
            ? minv - 1
            : 11;

        // determine which month is 3rd coldest
        int minv2, minv3_f, minv3_p = 0;
        if (monthTemps[minv2_f] >= monthTemps[minv2_p]) { // preceding month is colder (2nd coldest)
            minv2 = minv2_p;
            minv3_f = (minv < 11)
                ? minv + 1
                : 0;
            minv3_p = (minv2 > 0)
                ? minv2 - 1
                : 11;
        } else { // following month is colder (2nd coldest)
            minv2 = minv2_f;
            minv3_f = (minv2 < 11)
                ? minv2 + 1
                : 0;
            minv3_p = (minv > 0)
                ? minv - 1
                : 11;
        }
        // the 3rd coldest month is either before or after the 2 coldest
        int minv3 = (monthTemps[minv3_f] >= monthTemps[minv3_p])
            ? minv3_p
            : minv3_f;

        int[] retvals = new int[3];
        retvals[0] = minv; retvals[1] = minv2; retvals[2] = minv3;
        return retvals;
    }

    // find the indices of the 3 hottest months
    public static int[] hottestMonths(double[] monthTemps) {
        // loop through the array of 12 values to find the hottest
        double max = monthTemps[0];
        int maxv = 0;
        for (int i = 0; i < months; i++) {
            if (monthTemps[i] > max) {
                max = monthTemps[i];
                maxv = i;
            }
        }
        // actually on second thought, we can instead assume that the 3 hottest months are consecutive.
        // Which makes sense since this function (Java method) will only be applied to non-equatorial climates that have 
        // seasonal temperature variations based on differences in insolation. 

        // determine if preceding or following month of hottest is hotter (2nd hottest)
        int maxv2_f = (maxv < 11)
            ? maxv + 1
            : 0;
        int maxv2_p = (maxv > 0)
            ? maxv - 1
            : 11;

        // determine which month is 3rd hottest
        int maxv2, maxv3_f, maxv3_p = 0;
        if (monthTemps[maxv2_f] >= monthTemps[maxv2_p]) { // preceding month is hotter (2nd hottest)
            maxv2 = maxv2_f;
            maxv3_f = (maxv < 11)
                ? maxv + 1
                : 0;
            maxv3_p = (maxv2 > 0)
                ? maxv2 - 1
                : 11;
        } else { // following month is hotter (2nd hottest)
            maxv2 = maxv2_p;
            maxv3_f = (maxv2 < 11)
                ? maxv2 + 1
                : 0;
            maxv3_p = (maxv > 0)
                ? maxv - 1
                : 11;
        }
        // the 3rd coldest month is either before or after the 2 hottest
        int maxv3 = (monthTemps[maxv3_f] >= monthTemps[maxv3_p])
            ? maxv3_f
            : maxv3_p;

        int[] retvals = new int[3];
        retvals[0] = maxv; retvals[1] = maxv2; retvals[2] = maxv3;
        return retvals;
    }

    // find the stdev (sample) of a series
    // in practice the series will always be an array of length 12
    public static double getStdev(double[] arr) {
        double mean = 0.0;
        double sum = 0.0;

        //for (int i = 0; i < arr.length; i++) {
        for (int i = 0; i < months; i++) {
            mean += arr[i];
        }
        mean /= arr.length;

        //for (int j = 0; j < arr.length; j++) {
        for (int i = 0; i < months; i++) {
            sum += Math.pow((arr[i]-mean), 2);
        }
        double temp = sum/(months-1);
        double temp2 = Math.sqrt(temp);
        return temp2;
    }

    // find the evapotranspiration threshold for the 3 hottest months
    public static double summerWetness(double[] monthTemps, double[] monthPrecip) {
        int[] hottest3 = hottestMonths(monthTemps);
        double simulEvap = 0.0; double simulPrecip = 0.0;
        for (int i = 0; i < hottest3.length; i++) {
            int t = hottest3[i];
            simulEvap += monthTemps[t];
            simulPrecip += monthPrecip[t];
        }
        simulEvap *= 58.93/3;
        simulPrecip *= 4;
        return simulPrecip/simulEvap;
    }

    // find the evapotranspiration threshold for the 3 coldest months
    public static double winterWetness(double[] monthTemps, double[] monthPrecip) {
        int[] coldest3 = coldestMonths(monthTemps);
        double simulEvap = 0.0; double simulPrecip = 0.0;
        for (int i = 0; i < coldest3.length; i++) {
            int t = coldest3[i];
            simulEvap += monthTemps[t];
            simulPrecip += monthPrecip[t];
        }
        simulEvap *= 58.93/3;
        simulPrecip *= 4;
        return simulPrecip/simulEvap;
    }

    // find temperature continentality (stdev)
    public static double tempCont(double[] monthTemps) {
        double tempSTDEV = getStdev(monthTemps);
        return tempSTDEV * 100;
    }

    // find precipitation seasonality (stdev)
    public static double precipSeas(double[] monthPrecip, double precip) {
        double precipSTDEV = getStdev(monthPrecip);
        return precipSTDEV * 12 / precip;
    }


    // return a climate classification category based on the average monthly temperatures,
    // average monthly precipitation, and Holdridge average biotemperature for the whole year
    public static String climateClasscherrychips666(double[] monthTemps, double[] monthPrecip, double precip, double HBioTemp) {
        String classif = "";
        
        // Polar
	    if (HBioTemp <= 1.5) 
			classif = "7";
        // Subpolar + Boreal + Cool Temperate
        else if (HBioTemp <= 12.0) {
            double tSeasonality = tempCont(monthTemps);
            // Subpolar
            if (HBioTemp <= 3.0) {
                if (tSeasonality > 650)
                    classif = "6c";
                else if (tSeasonality > 300)
                    classif = "6o";
                else
                    classif = "6h";
            }
            // Boreal + Cool Temperate 
            else {
                double annPET = findEvapo(HBioTemp);
                double annWetness = getIndex(annPET, precip);
                
                // Boreal
                if (HBioTemp <= 6.0 && annWetness < 0.7) 
                    classif = "5d";
                else if (HBioTemp <= 6.0 && tSeasonality > 1650)
                    classif = "5x";
                else if (HBioTemp <= 6.0 && tSeasonality > 650)
                    classif = "5c";
                else if (HBioTemp <= 6.0 && tSeasonality > 300)
                    classif = "5o";
                else if (HBioTemp <= 6.0)
                    classif = "5h";
                // Cool Temperate
                else if (annWetness < 0.25)
                    classif = "4d";
                else if (annWetness < 0.7)
                    classif = "4s";
                else if (tSeasonality > 650)
                    classif = "4c";
                else if (tSeasonality > 300)
                    classif = "4o";
                else // if (tSeasonality < 300 && HBioTemp <= 12.0)
                    classif = "4h";
            } 
        }
        // Warm Temperate
        else if (HBioTemp <= 18.0) {
            double tSeasonality = tempCont(monthTemps);
            double annPET = findEvapo(HBioTemp);
            double annWetness = getIndex(annPET, precip);
            double coldWetness = winterWetness(monthTemps, monthPrecip);
            double hotWetness = summerWetness(monthTemps, monthPrecip);

            if (annWetness < 0.25)
                classif = "3d";
            else if (annWetness < 0.7 && coldWetness < 1 && hotWetness < 1)
                classif = "3s";
            else if (coldWetness < 0.5) {
                if (tSeasonality > 650)
                    classif = "3wc";
                else if (tSeasonality > 300)
                    classif = "3wo";
                else 
                    classif = "3wh";
            }
            else if (hotWetness < 0.5) {
                if (tSeasonality > 650)
                    classif = "3mc";
                else if (tSeasonality > 300)
                    classif = "3mo";
                else 
                    classif = "3mh";
            }
            else {
                if (tSeasonality > 650)
                    classif = "3fc";
                else if (tSeasonality > 300)
                    classif = "3fo";
                else 
                    classif = "3fh";
            }
        }
        // Subtropical
        else if (HBioTemp <= 24.0) {
            double annPET = findEvapo(HBioTemp);
            double annWetness = getIndex(annPET, precip);
            double coldWetness = winterWetness(monthTemps, monthPrecip);
            double hotWetness = summerWetness(monthTemps, monthPrecip);
            
            if (annWetness < 0.25)
                classif = "2d";
            else if (annWetness < 0.7 && coldWetness < 1 && hotWetness < 1)
                classif = "2s";
            else if (coldWetness < 0.5)
                classif = "2w";
            else if (hotWetness < 0.5)
                classif = "2m";
            else
                classif = "2f";
        }
        // Tropical
        else {
            double annPET = findEvapo(HBioTemp);
            double annWetness = getIndex(annPET, precip);
            double pSeasonality = precipSeas(monthPrecip, precip);

            if (annWetness < 0.25)
                classif = "1d";
            else if (annWetness < 0.7)
                classif = "1s";
            else if (pSeasonality < 50)
                classif = "1w";
            else
                classif = "1r";
        }

        return classif;
	}

}
