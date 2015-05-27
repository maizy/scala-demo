package ru.maizy.scala_demo.demos
/**
 * Copyright (c) Nikita Kovaliov, maizy.ru, 2013
 * See LICENSE.txt for details.
 */
import scala.concurrent.{Await, Future, Promise, ExecutionContext}
import scala.concurrent.duration._
import scala.util.{Try, Failure, Success}
import scala.util.Random

import ru.maizy.scala_demo.{Settings, Demo}
import ru.maizy.scala_demo.demoBlock


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

    val sys = akka.actor.ActorSystem("demo")

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

        def getUsersByGroup(group: String): Future[Users] =
          if (group == "ex") {
            Future.successful(
              List(
                User(s"Vasya ${randGenerator.nextInt()}", "vasya@example.com"),
                User("Masha", "masha@example.com")
              )
            )
          } else {
            Future.failed(new FetchFailed(s"Group $group doesn't exists"))
          }
      }

      class RepoResource extends Resource {

        def getRepos(user: String, limit: Int): Future[Repos] =
          if (limit > 10) {
            Future.failed(new FetchFailed("limit greats than 10"))
          } else {
            Future.successful(
              List(
                Repo(s"$user/some ${randGenerator.nextInt()}}", url = s"example.com/$user/some.git"),
                Repo(s"$user/some_more", url = s"sub.example.com/$user/abcdef.git")
              )
            )
          }

      }

      case class AsyncCache[I, K, R](
          func: I => Future[R],
          computeKey: I => K,
          ttl: Duration = 10.minutes
        ) extends (I => Future[R])
      {
        private val cache = scala.collection.concurrent.TrieMap[K, R]()

        override def apply(x: I): Future[R] = {
          val key = computeKey(x)
          val fromCache: Option[R] = cache.get(key)
          val self = this
          if (fromCache.isDefined) {
            println(s"$key: in cache")
            Future.successful(fromCache.get)
          } else {
            println(s"$key: do request")
            val future = func(x)

            future onComplete {

                case Success(v: R) =>
                  println(s"$key: set data to cache")
                  self.synchronized {
                    cache(key) = v
                  }

                case Failure(e: Throwable) =>
                  println(s"$key: not in cache remove key from cache")
                  self.synchronized {
                    cache.remove(key)
                  }
              }
            future
          }
        }
      }


      class Client {
        lazy val userResource = new UserResource
        lazy val repoResource = new RepoResource
        lazy val cachedGetUsersByGroup = new AsyncCache[String, String, Users](
          userResource.getUsersByGroup,
          computeKey = (x: String) => s"user-group_$x",
          ttl = 60.seconds
        )
        lazy val reposCache = new AsyncCache[(String, Int), String, Repos](
          (repoResource.getRepos _).tupled,
          computeKey = {
            case (group: String, limit: Int) => s"group_$group-limit_$limit"
          },
          ttl = 60.seconds
        )
        lazy val cachedGetReposResource = repoResource.getRepos _

        def getUsers(group: String): Future[Users] =
          cachedGetUsersByGroup(group)

        def getRepos(group: String, limit: Int): Future[Repos] =
          cachedGetReposResource(s"$group/user", limit)
      }

      val printRes: PartialFunction[Try[Any], Unit] = {
        case Success(res: Any) => println(s"Res: $res")
        case Failure(ex: Throwable) => println(s"Error: $ex")
      }

      val client = new Client
      val requestsSeq = List(
          List[Future[Any]](
            client.getUsers("ex"),
            client.getUsers("ex"),
            client.getUsers("ex")
    //        client.getUsers("ex2"),
    //        client.getRepos("gr1", 5),
    //        client.getRepos("gr1", 5),
    //        client.getRepos("gr2", 12),
    //        client.getRepos("gr2", 12)
          ),

          List[Future[Any]](
            client.getUsers("ex")
          )
      )
      for ((seq, i) <- requestsSeq.zipWithIndex) {
        println(s"step $i")
        seq.foreach(_.onComplete(printRes))
        Await.ready(Future.sequence(seq), Duration.Inf)
      }

    }

  }
}
