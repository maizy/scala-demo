package ru.maizy.scala_demo.demos

/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2017
 * See LICENSE.txt for details.
 */

import scala.collection.mutable.{ Publisher, Subscriber }
import ru.maizy.scala_demo.{ Demo, Settings, demoBlock }


// noinspection ReferenceMustBePrefixed
class PubSubDemo extends Demo {
  val name: String = "pub sub demo"

  def run(settings: Settings): Unit = {
    demoBlock("pub sub demo") {
      sealed abstract class Event {
        def uid: String
      }
      case class Fire(uid: String, location: String) extends Event
      case class Flood(uid: String, waterLevel: Int) extends Event

      class PrintUid extends Subscriber[Event, Publisher[Event]] {
        override def notify(pub: Publisher[Event], event: Event): Unit = {
          println("got an event with uid: " + event.uid)
        }
      }

      class FireAlarm extends Subscriber[Event, Publisher[Event]] {
        override def notify(pub: Publisher[Event], event: Event): Unit = {
          event match {
            case Fire(_, location) => println("AAAAALARM! " + location)
            case _ =>
          }
        }
      }

      class MyPublisher extends Publisher[Event] {
        def demoSequence1(): Unit = {
          publish(Fire("fire-2017-01-01", "hell"))
          publish(Flood("flood-2017-03-01", 15))
        }
      }

      val publisher = new MyPublisher()

      publisher.subscribe(new PrintUid)
      publisher.subscribe(new FireAlarm)

      publisher.demoSequence1()
    }
  }

}
