package ru.maizy.scala_demo.demos
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2014
 * See LICENSE.txt for details.
 */
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

import ru.maizy.scala_demo.{Settings, Demo}
import ru.maizy.scala_demo.demoBlock

trait FuturesDemoData {
  class MyError(msg: String) extends Exception(msg)
  class MyError2(msg: String) extends Exception(msg)

  def sleep(sec: Double): Unit = {Thread.sleep((sec * 1000).toInt)}

  def universalCallbackBuilder(label: String = ""): PartialFunction[Any, Unit] =
    {
      case i: Int if i >= 1 => println(s"universal callback, $label: OK >= 1: $i")
      case e: MyError => println(s"$label: My error catched at universal callback: $e")
    }

  val universalCallback: PartialFunction[Any, Unit] = universalCallbackBuilder()

  def myErrorCallbackBuilder(label: String): PartialFunction[Throwable, Unit] = {
      case e: MyError => println(s"$label: My error catched: $e")
    }

  val intWithConditions: PartialFunction[Int, Unit] = {
    case i if i >= 4 => println(s"Int: >=4 - $i")
  }

  val alwaysMatchCallback: PartialFunction[Any, Unit] = {
    case other => println(s"Ooops catch something unknown: $other")
  }
}

class PartialFunctionsDemo extends Demo {
  val name = "PartialFunction"

  def run(settings: Settings): Unit = {

    new FuturesDemoData {

      import scala.concurrent.ExecutionContext.Implicits.global

      demoBlock("partial functions") {

        println(s"intCallback.isDefinedAt(7)=${intWithConditions.isDefinedAt(7)}") //true
        println(s"intCallback.isDefinedAt(0)=${intWithConditions.isDefinedAt(0)}") //false
        //println(s"intCallback.isDefinedAt(string)=${intWithConditions.isDefinedAt("string")}")  //compile error: type missmatch

        //PartialFunction[Any, Unit] matching everything
        println(s"universalCallback.isDefinedAt(7)=${universalCallback.isDefinedAt(7)}") //true
        println(s"universalCallback.isDefinedAt(0)=${universalCallback.isDefinedAt(0)}") //false
        println(s"universalCallback.isDefinedAt(some string)=${universalCallback.isDefinedAt("some string")}") //false
      }
    }
  }
}

class BasicFutureDemo extends Demo {
  val name = "Future processing"

  def run(settings: Settings): Unit = {

    new FuturesDemoData {

      import scala.concurrent.ExecutionContext.Implicits.global

      demoBlock("simple future callbacks") {

        val simpleFuture: Future[Int] = Future {
          println("simpleFuture compute")
          sleep(sec = 0.2)
          println("simpleFuture finished")
          5
        }

        simpleFuture onSuccess universalCallbackBuilder("simpleFuture #1")
        Await.result(simpleFuture, 2 seconds)

        val otherFuture: Future[Int] = Future {
          println("otherFuture compute")
          sleep(sec = 0.2)
          println("otherFuture finished")
          3
        }

        otherFuture onSuccess universalCallbackBuilder("otherFuture #1") //multiple callbacks allowed
        otherFuture onSuccess universalCallbackBuilder("otherFuture #2") //callback order not garanted
        otherFuture onSuccess universalCallbackBuilder("otherFuture #3")

        Await.result(otherFuture, 2 seconds)

        Future {
          println("some future starts")
          sleep(sec = 0.2)
          println("some future ends")
          3
        } onSuccess {
          case i: Int => println(s"declarative style: $i")
        }
        sleep(sec = 1.5)
      }

      demoBlock("join Seq[Future[Seq[A]]] to Future[Seq[A]]") {

        case class Res(name: String)

        val f1: Future[Seq[Res]] = Future {
          List(Res("red"), Res("green"))
        }

        val f2: Future[Seq[Res]] = Future {
          List(Res("yellow"), Res("blue"))
        }

        val res: Future[Seq[Seq[Res]]] = Future.sequence(List(f1, f2))
        res onSuccess {
          case r => println(s"r: $r, r.flatten: ${r.flatten}")
        }

        val flatRes: Future[Seq[Res]] = res map { case r => r.flatten}
        flatRes onSuccess {
          case r => println(s"flatten res: $r")
        }

        Await.ready(res, 2 seconds)
        sleep(sec = 0.5)

      }
    }
  }
}


class ErrorsInFutureDemo extends Demo {
  val name = "Errors in Future & recovery"

  def run(settings: Settings): Unit = {

    new FuturesDemoData {

      import scala.concurrent.ExecutionContext.Implicits.global

      demoBlock("errors in future") {
        val errorFuture: Future[Int] = Future {
          throw new MyError("zooo")
        }

        errorFuture onSuccess universalCallbackBuilder("errorFuture") //never calls
        errorFuture onFailure myErrorCallbackBuilder("myError #1")
        errorFuture onFailure myErrorCallbackBuilder("myError #2")
        errorFuture onFailure myErrorCallbackBuilder("myError #3") //callback order not garanted

        errorFuture onFailure universalCallbackBuilder("errorFuture onFailure2")

        sleep(sec = 4)

        val unknownErrorFuture: Future[Int] = Future {
          throw new MyError2("bubu 2")
        }

        unknownErrorFuture onFailure universalCallbackBuilder("unknownErrorFuture onFailure")
        unknownErrorFuture onFailure myErrorCallbackBuilder("unknownErrorFuture myErrorCallbackBuilder")
        unknownErrorFuture onFailure {
          myErrorCallbackBuilder("unknownErrorFuture myErrorCallbackBuilder") orElse alwaysMatchCallback
        }

        sleep(sec = 4)
      }

      demoBlock("restore failed future and make some response") {
        class Response(val msg: String)
        class MyException(msg: String) extends Exception(msg)
        def sendResponse(r: Response) = println(r.msg)

        val success: Future[String] = Future.successful {
          "some result"
        }
        val failure: Future[String] = Future.failed(new MyException("something wrong"))
        val unknownFailure: Future[String] = Future.failed(new Exception("all wrong"))

        def processAsyncRes(f: Future[String]) = {
          f map (r => sendResponse(new Response(s"result 1: $r")))
        }

        def processAsyncResAndError(f: Future[String]) = {
          f recover {
            case e: MyException => s"Error: $e"
            case e => s"Unexpected Error: $e"
          } map (r => sendResponse(new Response(s"result 2: $r")))
        }

        processAsyncRes(success)
        processAsyncRes(failure)
        processAsyncRes(failure)

        processAsyncResAndError(success)
        processAsyncResAndError(failure)
        processAsyncResAndError(unknownFailure)

        sleep(sec = 2)

      }

      demoBlock("Orphan futures") {

        val returnUnmatchedInt: Future[Int] = Future {
          0
        }
        returnUnmatchedInt onSuccess universalCallbackBuilder()
        sleep(sec = 2)

        val returnErrorWithoutErrorCallback: Future[Int] = Future {
          throw new MyError(":(")
        }
        returnErrorWithoutErrorCallback onSuccess universalCallbackBuilder()
        sleep(sec = 2)

        val returnUnmatchedError: Future[Int] = Future {
          throw new MyError2(":((((")
        }
        returnUnmatchedError onFailure myErrorCallbackBuilder("hm...")
        sleep(sec = 2)

        val fatalError: Future[Int] = Future {
          throw new RuntimeException("wtf?")
        }
        fatalError onFailure myErrorCallbackBuilder("hm...")

        sleep(sec = 0.5)
        println("oops, nothing fatal happened")
        sleep(sec = 2)

        println("now burn!")
        Await.result(fatalError, 2 seconds)
        println("end (never called)")
      }

      //TODO: create execution context and test catching some errors
    }
  }
}
