package ru.maizy.scala_demo.demos
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */

import ru.maizy.scala_demo.{Settings, Demo}
import ru.maizy.scala_demo.demoBlock


class StreamsDemo extends Demo {
  val name: String = "streams"
  override val description: String = "Streams and streams combinations"

  def run(settings: Settings): Unit = {
    val list = List(1, 2, 3)

    def concatStreams(list: List[Int],
                      length: Option[Int]): Stream[Int] = {

      def infStream(i: Int, step: Int = 0): Stream[Int] = {
        println(s"Evaluate $i, step: $step")
        if (length.isDefined && step == length.get) Stream.empty
        else i #:: infStream(i, step + 1)
      }

      if (list.isEmpty) Stream.empty
      else {
        val streams = for {
          x <- list.toStream
        } yield infStream(x)
        streams.flatten
      }
    }

    demoBlock("finite stream") {
      val finiteStream = concatStreams(1 :: 2 :: Nil, Some(3))
      println("finite take 2")
      println((finiteStream take 2).toList)
      println("finite take 6")
      println((finiteStream take 6).toList)
      println("finite take 8")
      println((finiteStream take 8).toList)
    }

    demoBlock("inf stream") {
      val infStream = concatStreams(1 :: 2 :: Nil, None)
      println((infStream take 2).toList)
      println((infStream take 10).toList)
    }

  }
}

