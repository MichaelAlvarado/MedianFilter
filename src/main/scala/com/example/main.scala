import java.awt.Color
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import java.util.Arrays
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.collection.parallel.CollectionConverters._
import scala.collection.mutable.ListBuffer

case class Data (inputImage : BufferedImage)  //Used to give the image to the Servers
case class Result(endTime:Long, image:BufferedImage) //Used to return the results of each Server (execution time and filtered image)

//Serial Server
class serialServer extends Actor{
  def receive : PartialFunction[Any, Unit]  = {
    case Data(inputImage)  => {
      val startTime = System.currentTimeMillis()
      val filteredImage = serialFilter(inputImage)
      val endTime = System.currentTimeMillis() - startTime
      sender() ! Result(endTime, filteredImage)
    }
  }
  //Median Filter in Serial
  def serialFilter(inputImage: BufferedImage) : BufferedImage = {
    val result = new BufferedImage(inputImage.getWidth,inputImage.getHeight,BufferedImage.TYPE_3BYTE_BGR)
    val width = inputImage.getWidth-1
    val height = inputImage.getHeight-1
    for (x <- 0 to width){
      for(y <- 0 to height){        
        //Take neighbors pixels
        val pixels = new ListBuffer[Int]()
        pixels+=inputImage.getRGB(x,y) //Current Pixel
        if(x-1 > 0){
          pixels+=inputImage.getRGB(x-1,y)
          if(y+1 < height){pixels+=inputImage.getRGB(x-1,y+1)}
          if(y-1 > 0){pixels+=inputImage.getRGB(x-1,y-1)}
        }
        if(x+1 < width){
          pixels+=inputImage.getRGB(x+1,y)
          if(y+1 < height){pixels+=inputImage.getRGB(x+1,y+1)}
          if(y-1 > 0){pixels+=inputImage.getRGB(x+1,y-1)}
        }
        if(y+1 < height){pixels+=inputImage.getRGB(x,y+1)}
        if(y-1 > 0){pixels+=inputImage.getRGB(x,y-1)}
        //Calculate Median
        var pixelsSorted = pixels.toList.sorted 
        result.setRGB(x,y,pixelsSorted(pixels.size/2)) //Apply the RGB value
      }
    }
    return result; 
  }
}

//ParallelServer
class parallelServer extends Actor{
 def receive : PartialFunction[Any, Unit]  = {
    case Data(inputImage)  => {
      val startTime = System.currentTimeMillis()
      val filteredImage:BufferedImage = parallelFilter(inputImage)
      val endTime = System.currentTimeMillis() - startTime
      sender() ! new Result(endTime, filteredImage)
    }
 }
 //Median Filter in Parallel
  def parallelFilter(inputImage: BufferedImage) : BufferedImage = {
    val result = new BufferedImage(inputImage.getWidth,inputImage.getHeight,BufferedImage.TYPE_3BYTE_BGR)
    val width = inputImage.getWidth-1
    val height = inputImage.getHeight-1
    for (x <- (0 to width).par){  //Range in Parallel
      for(y <- (0 to height).par){ //Range in Parallel
        //Take neighbors pixels
        val pixels = new ListBuffer[Int]() 
        pixels+=inputImage.getRGB(x,y) //Current Pixel
        if(x-1 > 0){
          pixels+=inputImage.getRGB(x-1,y)
          if(y+1 < height){pixels+=inputImage.getRGB(x-1,y+1)}
          if(y-1 > 0){pixels+=inputImage.getRGB(x-1,y-1)}
        }
        if(x+1 < width){
          pixels+=inputImage.getRGB(x+1,y)
          if(y+1 < height){pixels+=inputImage.getRGB(x+1,y+1)}
          if(y-1 > 0){pixels+=inputImage.getRGB(x+1,y-1)}
        }
        if(y+1 < height){pixels+=inputImage.getRGB(x,y+1)}
        if(y-1 > 0){pixels+=inputImage.getRGB(x,y-1)}
        //Calculate Median
        var pixelsSorted = pixels.toList.sorted 
        result.setRGB(x,y,pixelsSorted(pixels.size/2)) //Apply the RGB value
      }
    }
    return result;
  }
}

//Client
object MedianFilter extends App{ 
  //Load Sample Image
  val imagePath = scala.io.StdIn.readLine("Enter the path of the image you want to filter:  ")
  val inputImage = ImageIO.read(new File(imagePath)) 
  implicit val timeout: Timeout = 20.seconds
  
  // System 1 Serial
  val actorSystem = ActorSystem("ActorSystem")
  val serialActor = actorSystem.actorOf(Props[serialServer], name = "serialActor")
  val serialFuture = serialActor ? Data(inputImage)

  // System 2 Parallel
  val actorSystem2 = ActorSystem("ActorSystem")
  val parallelActor = actorSystem2.actorOf(Props[parallelServer], name = "parallelActor")
  val parallelFuture = parallelActor ? Data(inputImage)

  // Wait for results of the servers
  val serialWait = Await.result(serialFuture, timeout.duration) //Wait for response
  val parallelWait = Await.result(parallelFuture, timeout.duration) //Wait for response

  // Get the Results values from server it contains execution time and output image
  var serialResult : Result = serialWait.asInstanceOf[Result] 
  var parallelResult : Result = parallelWait.asInstanceOf[Result]
  
  // Execution Time Print  
  println("\nSERIAL Execution Time: " + serialResult.endTime + " ms")
  println("PARALLEL Execution Time: " + parallelResult.endTime + " ms\n")

  // Save Output images
  println("Saving Output Images...")
  ImageIO.write(serialResult.image, "png", new File("serialOutput.png"))
  ImageIO.write(parallelResult.image, "png", new File("parallelOutput.png"))
  println("Images Saved")

  // Terminate Servers
  actorSystem.terminate()
  actorSystem2.terminate()
}

