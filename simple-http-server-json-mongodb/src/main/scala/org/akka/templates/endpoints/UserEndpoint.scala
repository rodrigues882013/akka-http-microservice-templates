package org.akka.templates.endpoints

import akka.http.scaladsl.server.Route
import org.akka.templates.model.{User, UserRepository, UserRequest}
import org.akka.templates.response._
import org.akka.templates.validators._

import de.heikoseeberger.akkahttpjackson.JacksonSupport._

/**
  * @author Gabriel Francisco <gabfssilva@gmail.com>
  */
trait UserEndpoint extends Endpoint {
  val userRepository: UserRepository

  val apiRoute: Route = {
    pathPrefix("api" / "users") {
      (get & validateAndExtract(path(Segment).as(UserRequest))) { request: UserRequest =>
        complete {
          userRepository
            .findById(request.id)
            .flatMap {
              case Some(user) => ok(Envelop(user))
              case None => notFound(Envelop(messages = Set(DefaultMessage(s"user with id=${request.id} not found"))))
            }
        }
      } ~ (post & validateAndExtract(entity(as[User]))) { user =>
        complete {
          userRepository
            .save(user)
            .flatMap { id => created(s"/api/users/$id") }
        }
      }
    }
  }
}
