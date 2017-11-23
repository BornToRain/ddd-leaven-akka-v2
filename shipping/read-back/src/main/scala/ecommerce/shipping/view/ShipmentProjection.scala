package ecommerce.shipping.view

import ecommerce.shipping.ShipmentCreated
import ecommerce.shipping.ShippingStatus.Waiting
import pl.newicom.dddd.messaging.event.OfficeEventMessage
import pl.newicom.dddd.view.sql.Projection
import pl.newicom.dddd.view.sql.Projection.ProjectionAction
import slick.dbio.Effect.Write

import scala.concurrent.ExecutionContext

class ShipmentProjection(dao: ShipmentDao)(implicit ex: ExecutionContext) extends Projection {

  override def consume(eventMessage: OfficeEventMessage): ProjectionAction[Write] = {
    eventMessage.event match {
      case ShipmentCreated(id, orderId) =>
        dao.createOrUpdate(ShipmentView(id.value, orderId, Waiting))
    }
  }
}