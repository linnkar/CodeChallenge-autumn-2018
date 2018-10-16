import java.util.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

class Parkkihalli {
  public static void main(String[] args) {
    //System.out.print(new Parkkihalli().toString());
    Parkkihalli PTalo = new Parkkihalli();

    // Start to loop over the tuloJaLahtoAjat
    for (TuloJaLahtoAika customer : tuloJaLahtoAjat) {
       // Check if we have reached the deadline
       if (customer.getArrivalTime().after(deadline)) {
          // Check if there are customers who have left after latest arrival but before deadline
          // Maybe we should stop calculating current fees at deadline. Separated as uncounted fees.
          for (int i=0; i<LahtoAika.size();i++) {
             if (LahtoAika.get(i).before(deadline)) {
                LahtoAika.remove(i);
                i--;
             }
             else {
                movedFees = movedFees + (timeDuration(deadline, LahtoAika.get(i))*price);
             }
          }
         
          // Calculate usage
          double usage = (totalTime/(double)observationPeriod)*100;
          // Show results
          // Number of cars, amount of fees, amount of lost fees, usage percentage
          System.out.println("Status at 15.9.2018 9:00");
          System.out.println("------------------------"); 
          System.out.println("Current number of customers: " + LahtoAika.size()); 
          System.out.format("Total fees: %.2f\u20ac%n",totalFees); 
          System.out.format("Total lost fees: %.2f\u20ac%n", totalLostFees); 
          System.out.format("Usage: %.2f%%%n", usage); 
          System.out.format("Uncounted fees: %.2f\u20ac%n",movedFees); 
          // Jump out of the loop because deadline has been reached
          break;
       }
       else {
          // Check if there are customers who have left before this arrival
          for (int i=0; i<LahtoAika.size();i++) {
             if (LahtoAika.get(i).before(customer.getArrivalTime())) {
                LahtoAika.remove(i);
                i--;
             }
          }
          // Check if there is a free slot, if not calculate lost fee and add it to total
          if (LahtoAika.size() > capacity) {
             totalLostFees = totalLostFees + 
                           (timeDuration(customer.getArrivalTime(), customer.getDepartureTime())*price);
          }
          else {
          // Slot free: calculate fee, add departure to departure list, add time to total parking time
             LahtoAika.add(customer.getDepartureTime());
             Collections.sort (LahtoAika);
             totalFees = totalFees + 
                           (timeDuration(customer.getArrivalTime(), customer.getDepartureTime())*price);
             totalTime = totalTime + timeDuration(customer.getArrivalTime(), customer.getDepartureTime());
          }
       }
    }
  }
  
  private static List<TuloJaLahtoAika> tuloJaLahtoAjat = new ArrayList<>();
  private static List<Date> LahtoAika = new ArrayList<>();   // Array to store departure times, length 
                                                             // tells the number of reserved slots
  private static double price = 0.05;    
  private static int capacity = 3*6*9;         // Three floors, 6 rows and 9 places per row
  private static double totalFees = 0.00;
  private static double totalLostFees = 0.00;
  private static double movedFees = 0.00;      // Fees after deadline from current customers
  private static long totalTime = 0;           // All parking times are stored here
  private static Date deadline = createDateFromString("2018-09-15 09:00:00");
  private static Date startDate = createDateFromString("2018-08-01 00:00:00");
  
  // Total time period to calculate average usage, 1.8 - 15.9
  private static long observationPeriod = timeDuration(startDate, deadline) * capacity;

  public Parkkihalli() {
    for (int i = 0; i < 50000; i++) {
      tuloJaLahtoAjat.add(new TuloJaLahtoAika());
    }
    // Sort the arrival times to ascending order
    Collections.sort(tuloJaLahtoAjat,ArrivalComparator);  
  }

  public List<TuloJaLahtoAika> getTimeStamps() {
    return tuloJaLahtoAjat;
  }
  
  public String toString() {
    return tuloJaLahtoAjat.toString();
  }

  private static long timeDuration (Date pStart, Date pEnd) {
     long MINUTES_FROM_MS = 60*1000;
     long diff = pEnd.getTime() - pStart.getTime();
     long pTime = Math.round(diff/((double)MINUTES_FROM_MS));

     return pTime;
  }

  private static Date createDateFromString (String dateString) {
     long pDate = Timestamp.valueOf(dateString).getTime();
     Date newDate = new Date(pDate);
     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");     
     dateFormat.format(newDate);
     return newDate;
  }
  
  public static Comparator<TuloJaLahtoAika> ArrivalComparator = new Comparator<TuloJaLahtoAika>() {
     public int compare (TuloJaLahtoAika car1, TuloJaLahtoAika car2) {
        Date d1 = car1.getArrivalTime();
        Date d2 = car2.getArrivalTime();
        
        return d1.compareTo(d2);
      }
  };

  public class TuloJaLahtoAika {
    private Date arrival;
    private Date departure;
	
    public TuloJaLahtoAika() {
      Long minArrivalTime = Timestamp.valueOf("2018-08-01 00:00:00").getTime();
      Long maxDepartureTime = Timestamp.valueOf("2018-09-30 00:00:00").getTime();
      Long randomTimeBetweenMaxAndMin = minArrivalTime + (long)(Math.random() * (maxDepartureTime - minArrivalTime + 1));
	  
      arrival = new Date(randomTimeBetweenMaxAndMin);
      departure = new Date(maxDepartureTime);
	  
      while ((departure).after(new Date(maxDepartureTime - 1))) {
        departure = new Date((long)(randomTimeBetweenMaxAndMin + new Random().nextGaussian() * (120 * 60000) + 360 * 60000));
      }
	  
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
      dateFormat.format(arrival);
      dateFormat.format(departure);
    }
	
    private Date getArrivalTime() {
      return arrival;
    }
	
    private Date getDepartureTime() {
      return departure;
    }
	
    public String toString() {
      return "saapumisaika: " + arrival + " lähtöaika: " + departure + System.getProperty("line.separator");
    }
  }
}
                                
