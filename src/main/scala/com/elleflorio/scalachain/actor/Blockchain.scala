package com.elleflorio.scalachain.actor

import akka.actor.{ActorLogging, Props}
import akka.persistence._
import com.elleflorio.scalachain.blockchain.{Chain, ChainLink, Transaction}
import Blockchain._

object Blockchain {

  sealed trait BlockchainEvent

  case class AddBlockEvent(transactions: List[Transaction], proof: Long, timestamp: Long) extends BlockchainEvent

  sealed trait BlockchainCommand

  case class AddBlockCommand(transactions: List[Transaction], proof: Long, timestamp: Long) extends BlockchainCommand

  case object GetChain extends BlockchainCommand

  case object GetLastHash extends BlockchainCommand

  case object GetLastIndex extends BlockchainCommand

  case class State(chain: Chain)

  def props(chain: Chain, nodeId: String): Props = Props(new Blockchain(chain, nodeId))
}

class Blockchain(chain: Chain, nodeId: String) extends PersistentActor with ActorLogging {

  var state = State(chain)

  override def persistenceId: String = s"chainer-$nodeId"

  override def receiveRecover: Receive = {
    case SnapshotOffer(metadata, snapshot: State) => {
      log.info(s"Recovering from snapshot ${metadata.sequenceNr} at block ${snapshot.chain.index}")
      state = snapshot
    }
    case RecoveryCompleted => log.info("Recovery completed")
    case evt: AddBlockEvent => updateState(evt)
  }

  override def receiveCommand: Receive = {
    case SaveSnapshotSuccess(metadata) => log.info(s"Snapshot ${metadata.sequenceNr} saved successfully")
    case SaveSnapshotFailure(metadata, reason) => log.error(s"Error saving snapshot ${metadata.sequenceNr}: ${reason.getMessage}")
    case AddBlockCommand(transactions: List[Transaction], proof: Long, timestamp: Long) => {
      persist(AddBlockEvent(transactions, proof, timestamp)) { event =>
        updateState(event)
      }

      // This is a workaround to wait until the state is persisted
      deferAsync(Nil) { _ =>
        saveSnapshot(state)
        sender() ! state.chain.index
      }
    }
    case AddBlockCommand(_, _, _) => log.error("invalid add block command")
    case GetChain => sender() ! state.chain
    case GetLastHash => sender() ! state.chain.hash
    case GetLastIndex => sender() ! state.chain.index
  }

  def updateState(event: BlockchainEvent) = event match {
    case AddBlockEvent(transactions, proof, timestamp) => {
      state = State(ChainLink(state.chain.index + 1, proof, transactions, timestamp = timestamp) :: state.chain)
      log.info(s"Added block ${state.chain.index} containing ${transactions.size} transactions")
    }
  }
}
