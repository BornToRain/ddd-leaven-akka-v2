package ecommerce.sales

import akka.actor.Props
import pl.newicom.dddd.actor.PassivationConfig
import pl.newicom.dddd.aggregate.{AggregateRootActorFactory, DefaultConfig, EntityId}
import pl.newicom.dddd.test.support.OfficeSpec

import ReservationSpec._
import pl.newicom.dddd.office.Office

object ReservationSpec {
  implicit def factory: AggregateRootActorFactory[ReservationAggregateRoot] =
    new AggregateRootActorFactory[ReservationAggregateRoot] {
      def props(pc: PassivationConfig): Props = Props(new ReservationAggregateRoot(DefaultConfig(pc)))
    }
}

class ReservationSpec extends OfficeSpec[Event, ReservationAggregateRoot] {

  def reservationOffice: Office = officeUnderTest

  def reservationId: EntityId = aggregateId

  val product = Product("product1", "productName", ProductType.Standard, Some(Money(10)))

  "Reservation office" should {
    "create reservation" in {
      when(
        CreateReservation(reservationId, "client1")
      )
      .expectEvent(
        ReservationCreated(reservationId, "client1")
      )
    }
  }

  "Reservation office" should {
    "reserve product" in {
      given {
        CreateReservation(reservationId, "client1")
      }
      .when {
        ReserveProduct(reservationId, product, quantity = 1)
      }
      .expectEvent {
        ProductReserved(reservationId, product, quantity = 1)
      }
    }
  }

  "Reservation office" should {
    "confirm reservation" in {
      given {
        CreateReservation(reservationId, "client1") &
        ReserveProduct(reservationId, product, quantity = 1)
      }
      .when(
        ConfirmReservation(reservationId)
      )
      .expectEvent {
        ReservationConfirmed(reservationId, "client1", product.price)
      }
    }
  }

  "Reservation office" should {
    "close reservation" in {
      given {
        CreateReservation(reservationId, "client1") &
        ReserveProduct(reservationId, product, quantity = 1)
      }
      .when {
        CloseReservation(reservationId)
      }
      .expectEvent {
        ReservationClosed(reservationId)
      }
    }
  }

}
