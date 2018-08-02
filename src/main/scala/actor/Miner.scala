package actor

import akka.actor.Status.{Failure, Success}
import akka.actor.{Actor, ActorLogging, Props}
import exception.InvalidProofException
import proof.ProofOfWork

import scala.concurrent.Future

object Miner {
  sealed trait MinerMessage
  case class Validate(hash: String, proof: Long) extends MinerMessage
  case class Mine(hash: String) extends MinerMessage
  case object Ready extends MinerMessage
  case object StopMining extends MinerMessage

  val props: Props = Props(new Miner)
}

class Miner extends Actor with ActorLogging{
  import Miner._
  import context._

  def active: Receive = {
    case Ready => {
      log.info(s"Yes, I'm ready")
    }
    case Mine(hash) => {
      log.info(s"Mining hash $hash...")
      val proof = Future.successful(ProofOfWork.proofOfWork(hash))
      sender() ! proof
    }
    case StopMining => {
      log.info("Stop mining")
      become(idle)
    }
    case Validate(hash, proof) => {
      log.info(s"Validating proof $proof")
      if (ProofOfWork.validProof(hash, proof))
        sender() ! Success
      else
        sender() ! Failure(new InvalidProofException(hash, proof))

    }
  }

  def idle: Receive = {
    case Mine(hash) => {
      log.info("Ok, let's mine!")
      become(active)
      self.tell(Mine(hash), sender())
    }
    case StopMining => {
      log.info("Already idle")
    }
    case Validate(hash, proof) => {
      log.info(s"Validating proof $proof")
      if (ProofOfWork.validProof(hash, proof))
        sender() ! Success
      else
        sender() ! Failure(new InvalidProofException(hash, proof))

    }
  }

  override def receive: Receive = {
    case Ready => become(active)
    case StopMining => become(idle)
  }
}
