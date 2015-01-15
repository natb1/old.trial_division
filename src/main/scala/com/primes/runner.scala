import akka.actor.{Actor, ActorSystem, Props, Terminated, PoisonPill}
import akka.routing.{RoundRobinRouter, Broadcast}
import scala.util.control.Breaks._

object PrimesRunner extends App {
  val n = args(0).toInt

  val system = ActorSystem("PrimesActors")
  val dealer = system.actorOf(Dealer.props, "dealer")
  dealer ! Dealer.Initialize(n)

  Console.println("do World: " + (n))
  system.awaitTermination()
}

class Dealer extends Actor {
  import Dealer._
  
  val worker_count = 2
  val partitions = worker_count * 2
  val workers = context.actorOf(Worker.props.withRouter(
    RoundRobinRouter(nrOfInstances = worker_count)))

  override def preStart {
    context.watch(workers)
  }

  def receive = {
    case Dealer.Initialize(n) => 
      val min_part_size = math.floor((n-1)/partitions.toFloat).toInt 
      val large_parts = (n-1) % partitions; 
      var from = 2; 
      breakable { /**I think this means I don't get scala yet*/
        for (i <- 0 until partitions) { 
          if (from > n) break
          val partition_size = min_part_size + (if (i < large_parts) 1 else 0) 
          val end = from + partition_size - 1
          workers ! IsPrime(from, end)
          from = from + partition_size
        }
      }
      workers ! Broadcast(PoisonPill)
    case Terminated(`workers`) =>
      context.system.shutdown()
  }  
}

object Dealer {
  val props = Props[Dealer]
  case class Initialize(n: Int)
  case class IsPrime(from: Int, end: Int)
}

class Worker extends Actor {
  import Worker._

  def receive = {
    case Dealer.IsPrime(from, end) =>
      for (candidate <- from to end){
        var sqrt = math.sqrt(candidate).toInt
        var prime = true
        breakable {
          for (divisor <- 2 to math.max(sqrt, 2)){
            if (candidate % divisor == 0){
              prime = false
              break
            }
          }
        }
        if (prime) Console.println(candidate)
        /* treat 2 as a special case because scala is hard */
        if (candidate == 2) Console.println(candidate)
      }
  }
}

object Worker {
  val props = Props[Worker]
}
