package db

import anorm._ 
import play.api.db.DB
import play.api.Play.current

// use a case class to model the stuff you save in a table
case class User(id: Long, username: String, first: String, last: String)

object Users {
  // creates the table in the database
  def createTable() {
    DB.withConnection { implicit c => 
      SQL(createStmt).execute()
    } 
  }
  
  def addUser(username: String, first: String, last: String) {
    DB.withConnection { implicit c =>
      SQL(insertStmt).on('username -> username, 'first -> first, 'last -> last).execute()  
    }
  }
  
  val idQuery = "SELECT id, username, first, last FROM users WHERE id={id}"
  def getById(id: Long): Option[User] = {
    val query = SQL(idQuery).on('id -> id)
    DB.withConnection { implicit c =>
      query().map {
        case Row(theId: Long, username: String, Some(first: String), Some(last: String)) => User(theId, username, first, last)
      }.headOption
    }
  }

  // SQL statements
  // Note that these use Scala's multi-line strings and the
  // stripMargin method, so the strings look pretty
  val createStmt = 
    """CREATE TABLE users (
      |  id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
      |  username VARCHAR(20) UNIQUE NOT NULL,
      |  first VARCHAR(30),
      |  last VARCHAR(30),
      |  pwHash VARCHAR(256)
    )""".stripMargin
  
  // In this INSERT statement, we use {varName} for data
  // that will be filled in when we execute
  val insertStmt = 
    "INSERT INTO users (username, first, last) VALUES ({username}, {first}, {last})"
  
    
}