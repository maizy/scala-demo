package ru.maizy.scala_demo.demos
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Try, Failure, Success}

import ru.maizy.scala_demo.{Settings, Demo}
import ru.maizy.scala_demo.demoBlock

// based on http://stackoverflow.com/a/16363444/239268
// easily may be replaced by akka.pattern.after
// but here I don't need any additional dependancy
object DelayedFuture {
  import java.util.{Timer, TimerTask}
  import java.util.Date
  import scala.concurrent._

  protected var maybeTimer: Option[Timer] = None

  def startTimer() {
    stopTimer()
    maybeTimer = Some(new Timer)
  }
  
  def timer: Timer =
    maybeTimer.getOrElse {
      startTimer()
      maybeTimer.get
    }

  def stopTimer() {
    maybeTimer foreach {_.cancel()}
    maybeTimer = None
  }

  startTimer()

  private def makeTask[T](body: => T)(schedule: TimerTask => Unit)(implicit ctx: ExecutionContext): Future[T] = {
    val prom = Promise[T]()
    schedule(
      new TimerTask {
        def run() {
          // IMPORTANT: The timer task just starts the execution on the passed
          // ExecutionContext and is thus almost instantaneous (making it
          // practical to use a single  Timer - hence a single background thread).
          ctx.execute(
            new Runnable {
              def run() {
                try {
                  prom.success(body)
                } catch {
                  case ex: Throwable => prom.failure(ex)
                }
              }
            }
          )
        }
      }
    )
    prom.future
  }

  def apply[T](period: Long)(body: => T)(implicit ctx: ExecutionContext): Future[T] =
    makeTask(body)(timer.schedule(_, period))

  def apply[T](date: Date)(body: => T)(implicit ctx: ExecutionContext): Future[T] =
    makeTask(body)(timer.schedule(_, date))

  def apply[T](duration: Duration)(body: => T)(implicit ctx: ExecutionContext): Future[T] =
    // NOTE: will throw IllegalArgumentException for infinite durations
    makeTask(body)(timer.schedule(_, duration.toMillis))
}

class TraitWithTypeParamDemo extends Demo {
  val name: String = "trait with type param"

  def run(settings: Settings) {
    case class User(name: String, email: String)
    case class Repos(name: String, url: String)


    import scala.concurrent.ExecutionContext.Implicits.global

    type Users = Seq[User]
    class FetchFailed(message: String) extends Exception(message)

    demoBlock("cache as mixins") {
      abstract class Resource {
        type R
        def getData: Future[R]
      }

      class UserResource(
          val group: String
        ) extends Resource {
        type R = Users

        def getData: Future[R] = {
          if (group == "ex")
          {
            DelayedFuture(0.5.seconds) {
              List(
                User("Vasya", "vasya@example.com"),
                User("Masha", "masha@example.com")
              )
            }
          } else {
            DelayedFuture(0.8.seconds) {
              throw new FetchFailed(s"Group $group doesn't exists")
            }
          }
        }
      }

      class Client {
        def getUsers(group: String): Future[Users] = {
          //FIXME: statefull resource
          val resource = new UserResource(group)
          resource.getData
        }
      }

      val printRes: PartialFunction[Try[Users], Unit] = {
        case Success(users: Users) => println(users)
        case Failure(ex: Throwable) => println(s"Error: $ex")
      }

      val client = new Client()
      val futures = List(
        client.getUsers("ex"),
        client.getUsers("ex"),
        client.getUsers("ex2")
      )

      futures.foreach(_.onComplete(printRes))
      futures.foreach(Await.ready(_, 3.second))
      DelayedFuture.stopTimer()
    }

  }
}
