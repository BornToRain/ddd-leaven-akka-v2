package ecommerce.shipping

import com.typesafe.config.Config
import ecommerce.shipping.view.{ShipmentDao, ShipmentProjection}
import pl.newicom.dddd.view.sql.{SqlViewUpdateConfig, SqlViewUpdateService}
import pl.newicom.eventstore.EventSourceProvider
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

class ShippingViewUpdateService(override val config: Config)(override implicit val profile: JdbcProfile)
  extends SqlViewUpdateService with ShippingReadBackendConfiguration with EventSourceProvider {

  lazy val shipmentDao: ShipmentDao = new ShipmentDao()

  override def vuConfigs: Seq[SqlViewUpdateConfig] = {
    List(
      SqlViewUpdateConfig("shipping-shipments", ShippingOfficeId, new ShipmentProjection(shipmentDao))
    )
  }

  override def viewUpdateInitAction: DBIO[Unit] = {
      super.viewUpdateInitAction >>
        shipmentDao.ensureSchemaCreated
  }
}