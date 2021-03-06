/*
 * Created by Saurabh on 4/11/2017.
 */
package cs455.hadoop.set1;

import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.HashMap;

public class set1Reduce extends Reducer<Text, Text, Text, Text>
{
    private static long [] q2Summary = new long[4];
    private static long [] q3summary = new long[8];
    private static long [] q5summary = new long[20];
    private static HashMap<String, String> q5valueMap = new HashMap<String, String>();
    private static long [] q6summary = new long[16];
    private static HashMap<String, String> q6valueMap = new HashMap<String, String>();

    public void reduce(Text key, Iterable<Text> values, Context context)
        throws IOException, InterruptedException
    {
        // for Q1
        long owned = 0;
        long rented = 0;

        // for Question 2, 3, 5 and 6
        zeroInitialization();

        // for Question 4
        long urban = 0;
        long rural = 0;
        long unDefined = 0;

        // for Question 5
        q5setupHashMap();

        // for Question 6
        q6setupHashMap();

        for (Text value: values)
        {
            String strValue = value.toString();
            String [] byParts = strValue.split("/");

            if (byParts[0].equals("part-2"))
            {
                // Question 1
                String [] Q1 = byParts[1].split(":");

                owned = owned + Long.parseLong(Q1[0]);
                rented = rented + Long.parseLong(Q1[1]);

                // Question 4
                String [] Q4 = byParts[2].split(":");

                urban = urban + Long.parseLong(Q4[0]);
                rural = rural + Long.parseLong(Q4[1]);
                unDefined = unDefined + Long.parseLong(Q4[2]);

                // Question 5
                String [] Q5 = byParts[3].split(":");
                q5addAllValues(Q5);

                // Question 6
                String [] Q6 = byParts[4].split(":");
                q6addAllValues(Q6);

            }
            else if (byParts[0].equals("part-1"))
            {
                System.out.println(byParts[1]);
                String [] Q2 = byParts[1].split(":");
                q2AddToArray(Q2);

                String [] Q3 = byParts[2].split(":");
                q3addToArray(Q3);

            }
        }

        // Question 1
        double rentPercent = rented * 100d/(owned + rented);
        double ownedPercent = owned * 100d/(owned + rented);

        // Question 2
        String q2Results = q2findStats();

        // Question 3
        String [] q3Results = q3getResults();
        String q3WriteString = "Under 18 Male: " + q3Results[0] + " "
                                + "Males From 19 to 29: " + q3Results[1] + " "
                                + "Males From 30 to 39: " + q3Results[2] + " "
                                + "Under 18 Females: " + q3Results[3] + " "
                                + "Females 19 to 29: " + q3Results[4] + " "
                                + "Females 30 to 39: " + q3Results[5];

        // Question 4
        double urbanPercent = urban * 100.0d/ (urban + rural + unDefined);
        double ruralPercent = rural * 100.0d/ (urban + rural + unDefined);

        // Question 5
        long q5total = q5totalForCurrentKey();
        long q5median = q5findMedian(q5total);

        String q5index = q5findRange(q5median);

        String q5range = q5valueMap.get(q5index);

        // Question 6
        long q6total = q6totalForCurrentKey();
        long q6median = q6findMedian(q6total);
        String q6index = q6findRange(q6median);

        String q6range = q6valueMap.get(q6index);

        context.write(key, new Text("Question-1: " + "Owned: " + ownedPercent + " Rented: " + rentPercent +"\n"
                                    + "Question-2: " + q2Results + "\n"
                                    + "Question-3: " + q3WriteString + "\n"
                                    + "Question-4: " + "Urban: " + Double.toString(urbanPercent) + " Rural: "
                                    + Double.toString(ruralPercent) + "\n"
                                    + "Question-5: " + q5range + "\n"
                                    + "Question-6: " + q6range + "\n"));

    }

//-----------Q2 Methods-----------//
    private static String q2findStats()
    {
        double malePercent = q2Summary[2] * 100.0d/q2Summary[0];
        double femalePercent = q2Summary[3] * 100.0d/q2Summary[1];

        String results = "Un-Married Male : " +  Double.toString(malePercent) + "\n"
                + "Un-Married Female: " + Double.toString(femalePercent);

        return results;
    }

    private static void q2AddToArray(String [] parts)
    {
        for (int i = 0; i < parts.length; i++)
        {
            q2Summary[i] = q2Summary[i] + Long.parseLong(parts[i]);
        }
    }

    private static void zeroInitialization()
    {
        for (int i = 0; i < q2Summary.length; i++)
        {
            q2Summary[i] = 0;
        }

        for (int i = 0; i < q3summary.length; i++)
        {
            q3summary[i] = 0;
        }

        for (int i = 0; i < q5summary.length; i++)
        {
            q5summary[i] = 0;
        }

        for (int i = 0; i < q6summary.length; i++)
        {
            q6summary[i] = 0;
        }
    }
//-----------Q2 Methods end-----------//

//-----------Q3 Methods-----------//
    private static void q3addToArray(String [] parts)
    {
        for (int i = 0; i < parts.length; i++)
        {
            q3summary[i] = q3summary[i] + Long.parseLong(parts[i]);
        }
    }

    private static String [] q3getResults()
    {
        String [] results = new String[6];

        double under18MaleD = q3summary[0] * 100.0d/q3summary[6];
        double from19to29MaleD = q3summary[1] * 100.0d /q3summary[6];
        double from30to39MaleD = q3summary[2] * 100.0d/q3summary[6];

        double under18FemaleD = q3summary[3] * 100.0d /q3summary[7];
        double from19to29FemaleD = q3summary[4] * 100.0d /q3summary[7];
        double from30to39FemaleD = q3summary[5] * 100.0d /q3summary[7];

        results[0] = Double.toString(under18MaleD);
        results[1] = Double.toString(from19to29MaleD);
        results[2] = Double.toString(from30to39MaleD);

        results[3] = Double.toString(under18FemaleD);
        results[4] = Double.toString(from19to29FemaleD);
        results[5] = Double.toString(from30to39FemaleD);

        return results;
    }
//-----------Q3 Methods end-----------//

//-----------Q5 Methods-----------//

    private static void q5addAllValues(String [] parts)
    {
        for (int i = 0; i < parts.length; i++)
        {
            long temp = Long.parseLong(parts[i]);
            q5summary[i] = q5summary[i] + temp;
        }
    }

    private static long q5totalForCurrentKey()
    {
        long total = 0;
        for (long aSummary : q5summary) {
            total = total + aSummary;
        }

        return total;
    }

    private static long q5findMedian(long total)
    {
        long median;
        if (total % 2 == 0)
        {
            median = total /2;
        }
        else
        {
            median = 1 + (total/2);
        }
        return median;
    }

    private static String q5findRange(long median)
    {
        long sum = 0;
        for (int i = 0; i<q5summary.length; i++)
        {
            if (sum< median)
            {
                sum = sum + q5summary[i];
            }
            else
            {
                return Integer.toString(i);
            }

        }
        return Integer.toString(q5summary.length -1);
    }

    private static void q5setupHashMap()
    {
        q5valueMap.put("0", "Less than $15,000");
        q5valueMap.put("1", "$15,000 - $19,999");
        q5valueMap.put("2", "$20,000 - $24,999");
        q5valueMap.put("3", "$25,000 - $29,999");
        q5valueMap.put("4", "$30,000 - $34,999");
        q5valueMap.put("5", "$35,000 - $39,999");
        q5valueMap.put("6", "$40,000 - $44,999");
        q5valueMap.put("7", "$45,000 - $49,999");
        q5valueMap.put("8", "$50,000 - $59,999");
        q5valueMap.put("9", "$60,000 - $74,999");
        q5valueMap.put("10", "$75,000 - $99,999");
        q5valueMap.put("11", "$100,000 - $124,999");
        q5valueMap.put("12", "$125,000 - $149,999");
        q5valueMap.put("13", "$150,000 - $174,999");
        q5valueMap.put("14", "$175,000 - $199,999");
        q5valueMap.put("15", "$200,000 - $249,999");
        q5valueMap.put("16", "$250,000 - $299,999");
        q5valueMap.put("17", "$300,000 - $399,999");
        q5valueMap.put("18", "$400,000 - $499,999");
        q5valueMap.put("19", "$500,000 or more");
    }
//-----------Q5 Methods end-----------//

//-----------Q6 Methods-----------//
    private static void q6setupHashMap()
    {
        q6valueMap.put("0", "Less than $100");
        q6valueMap.put("1", "$100 to $149");
        q6valueMap.put("2", "$150 to $199");
        q6valueMap.put("3", "$200 to $249");
        q6valueMap.put("4", "$250 to $299");
        q6valueMap.put("5", "$300 to $349");
        q6valueMap.put("6", "$350 to $399");
        q6valueMap.put("7", "$400 to $449");
        q6valueMap.put("8", "$450 to $499");
        q6valueMap.put("9", "$500 to $549");
        q6valueMap.put("10", "$550 to $ 599");
        q6valueMap.put("11", "$600 to $649");
        q6valueMap.put("12", "$650 to $699");
        q6valueMap.put("13", "$700 to $749");
        q6valueMap.put("14", "$750 to $999");
        q6valueMap.put("15", "$1000 or more");
//        q6valueMap.put("16", "No cash rent");
    }

    private static String q6findRange(long median)
    {
        long sum = 0;
        for (int i = 0; i<q6summary.length; i++)
        {
            if (sum< median)
            {
                sum = sum + q6summary[i];
            }
            else
            {
                return Integer.toString(i);
            }

        }
        return Integer.toString(q6summary.length -1);
    }

    private static long q6findMedian(long total)
    {
        long median;
        if (total % 2 == 0)
        {
            median = total /2;
        }
        else
        {
            median = 1 + (total/2);
        }
        return median;
    }

    private static long q6totalForCurrentKey()
    {
        long total = 0;
        for (long aSummary : q6summary) {
            total = total + aSummary;
        }

        return total;
    }

    private static void q6addAllValues(String [] parts)
    {
        for (int i = 0; i < parts.length; i++)
        {
            long temp = Long.parseLong(parts[i]);
            q6summary[i] = q6summary[i] + temp;
        }
    }


}
