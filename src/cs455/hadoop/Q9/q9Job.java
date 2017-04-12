/*
 * Created by Saurabh on 4/12/2017.
 */

package cs455.hadoop.Q9;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class q9Job
{
    public static void main(String [] args)
    {
        try
        {
            // Set all the configuration right. All configuration details are in conf directory
            Configuration conf = new Configuration();

            // Set the configuration and give job a name
            Job job = Job.getInstance(conf, "Rented vs Owned");

            // Current Job
            job.setJarByClass(q9Job.class);

            // Set the Mapper class
            job.setMapperClass(q9Map.class);

            // Set the reducer class
            job.setReducerClass(q9Reduce.class);

            // Here we need to decide key value classes for mapper and reducer
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);

            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);

            // Path input in HDFS
            FileInputFormat.addInputPath(job, new Path(args[0]));

            // Path output in HDFS
            FileOutputFormat.setOutputPath(job, new Path(args[1]));

            //Block until the job is completed
            System.exit(job.waitForCompletion(true) ? 0 : 1);
        }
        catch (IOException e)
        {
            // For job.getInstance
            System.err.println(e.getMessage());
        }
        catch (InterruptedException e)
        {
            // job.waitForCompletion
            System.err.println(e.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            //job.waitForCompletion
            System.err.println(e.getMessage());
        }
    }
}
