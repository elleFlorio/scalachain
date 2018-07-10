import akka.actor.{ActorRef, ActorSystem}

object Scalachain extends App {
  import actor.Node
  import actor.Node._

  val system: ActorSystem = ActorSystem("scalachain")

  val node: ActorRef = system.actorOf(Node.props, "scalaChainNodeActor")

  node ! NewTransaction("A", "B", 1)

  node ! NewBlock(1)

  node ! NewBlock(135)

  node ! GetStatus

  node ! NewTransaction("C", "D", 12)

  node ! MineBlock

  node ! NewTransaction("E", "F", 31)
  node ! NewTransaction("F", "E", 41)
  node ! NewTransaction("G", "H", 81)

  node ! MineBlock

  node ! NewTransaction("H", "I", 100)
  node ! NewTransaction("I", "J", 99)

  node ! GetStatus
}
