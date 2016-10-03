package ecommerce.sales.app

import akka.actor._
import akka.kernel.Bootable

class SalesFrontApp extends Bootable {

  override def systemName = "sales-front"

  override def startup(): Unit = {
    system.actorOf(SalesFrontAppSupervisor.props, "sales-front-supervisor")
  }

}

object SalesFrontAppSupervisor {
  def props = Props(new SalesFrontAppSupervisor)
}

class SalesFrontAppSupervisor extends Actor with ActorLogging with SalesFrontConfiguration {

  override val supervisorStrategy = SupervisorStrategy.stoppingStrategy

  context.watch(createHttpService())

  override def receive: Receive = {
    case Terminated(ref) =>
      log.warning("Shutting down, because {} has terminated!", ref.path)
      context.system.terminate()
  }

  protected def createHttpService(): ActorRef = {
    import httpService._
    context.actorOf(HttpService.props(interface, port, timeout), "http-service")
  }
}