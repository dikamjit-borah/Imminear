# inhu-tex

import java.util.ArrayList;
import  java.lang.Math;
public class HelloWorld{
    

     public static void main(String []args){
         int i;
         
    ArrayList<Double> latitudes = new ArrayList<>();
    latitudes.add(26.8469737);
    latitudes.add(26.1471576);
    latitudes.add(26.1471577);
    ArrayList<Double> longitudes = new ArrayList<>();
    longitudes.add(80.9136178);
    longitudes.add(91.7917139);
    longitudes.add(91.7917149);
    
    double user_latitude = 26.1471576;
    double user_longitude = 91.7917139;
    double latitude_distance = 0;
    double longitude_distance = 0;
    final int radius = 6371;
    double a = 0;
    double c = 0;
    double dist = 0;
    ArrayList<Double> migrants_near = new ArrayList<>();

        for (i = 0; i<latitudes.size(); i++)
        {
            System.out.println("For" + latitudes.get(i) + ", " + longitudes.get(i) + "\n");
            latitude_distance = (Math.toRadians(user_latitude - latitudes.get(i)));
             System.out.println(latitude_distance);
            longitude_distance = Math.toRadians(user_longitude - longitudes.get(i));
             System.out.println(longitude_distance);

            a = Math.abs(Math.pow(Math.sin(latitude_distance / 2), 2)
                    + Math.cos(user_latitude) * Math.cos(latitudes.get(i))
                    * Math.pow(Math.sin(longitude_distance / 2),2));
            System.out.println("a = " + a + "\n");

             c = 2 * Math.asin(Math.sqrt(a));
             System.out.println("c = " + c + "\n");
            //height = e2 - e1;
            dist = c * radius;
            dist = dist* 1000;//converts to meters

            System.out.println("dist = "+ dist + "\n");
            if (dist > 500) {
                //do nothing

            } else if (dist < 500) {
                migrants_near.add(dist);
            }
        }
        System.out.println(migrants_near);
     }
}
