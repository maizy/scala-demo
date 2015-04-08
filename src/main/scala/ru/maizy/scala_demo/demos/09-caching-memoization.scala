package ru.maizy.scala_demo.demos
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Try, Failure, Success}
import scala.util.Random

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
  var randGenerator = new Random(777)

  def run(settings: Settings) {
    case class User(name: String, email: String)
    case class Repo(name: String, url: String)


    import scala.concurrent.ExecutionContext.Implicits.global

    type Users = Seq[User]
    type Repos = Seq[Repo]
    class FetchFailed(message: String) extends Exception(message)


    demoBlock("based on Memo from https://github.com/pathikrit/scalgos") {

      case class Memo[I, K, O](kf: I => K, f: I => O) extends (I => O) {
        import scala.collection.mutable.{Map => Dict}
        val cache = Dict.empty[K, O]
        override def apply(x: I) = cache getOrElseUpdate (kf(x), f(x))
      }

      val myFunc = (group: String, limit: Int) => {
        List(
          s"group: $group: ${randGenerator.nextInt()}}",
          s"Limit: $limit"
        )
      }

      println(myFunc("ex", 23))

      val memoized = new Memo[(String, Int), String, List[String]](
        {
          case (group: String, limit: Int) => s"gr-${group}_lm-$limit"
        },
        myFunc.tupled
      )
      println(memoized("s", 5))
      println(memoized("s", 5))

    }



    demoBlock("AsyncCache wrapper") {
      trait Resource

      class UserResource extends Resource {

        def getUsersByGroup(group: String): Future[Users] = {
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

      class RepoResource extends Resource {
        def getRepos(user: String, limit: Int): Future[Repos] = {
            DelayedFuture(0.5.seconds) {
              if (limit > 10) {
                throw new FetchFailed("limit greats than 10")
              } else {
                List(
                  Repo(s"$user/some", url = s"example.com/$user/some.git"),
                  Repo(s"$user/some_more", url = s"sub.example.com/$user/abcdef.git")
                )
              }
          }
        }
      }

      case class AsyncCache[I, K, R](
          func: I => Future[R],
          computeKey: I => K,
          ttl: Duration = 10.minutes
        ) extends (I => Future[R])
      {
        private val cache = scala.collection.mutable.Map[K, R]()

        override def apply(x: I): Future[R] = {
          val key = computeKey(x)
          println(s"Computed key: $x = $key")
          func(x)
        }
      }
      class Client {
        lazy val userResource = new UserResource
        lazy val repoResource = new RepoResource
        lazy val userCache = new AsyncCache[String, String, Users](
          userResource.getUsersByGroup,
          computeKey = (x: String) => s"user-group_$x",
          ttl = 5.seconds
        )
//        lazy val reposCache = new AsyncCache[[String, Int], String, Repos](
//          repoResource.getRepos,
//          computeKey = (group: String, limit: Int) => s"group_$group-limit_$limit",
//          ttl = 5.seconds
//        )
        lazy val cachedGetReposResource = repoResource.getRepos _

        def getUsers(group: String): Future[Users] =
          userCache(group)

        def getRepos(group: String, limit: Int): Future[Repos] =
          cachedGetReposResource(s"$group/user", limit)
      }

      val printRes: PartialFunction[Try[Any], Unit] = {
        case Success(res: Any) => println(res)
        case Failure(ex: Throwable) => println(s"Error: $ex")
      }

      val client = new Client
      val futures = List[Future[Any]](
        client.getUsers("ex"),
        client.getUsers("ex"),
        client.getUsers("ex"),
        client.getUsers("ex2"),
        client.getRepos("gr1", 5),
        client.getRepos("gr2", 15)
      )

      futures.foreach(_.onComplete(printRes))
      futures.foreach(Await.ready(_, 3.second))
      DelayedFuture.stopTimer()
    }

  }
}
