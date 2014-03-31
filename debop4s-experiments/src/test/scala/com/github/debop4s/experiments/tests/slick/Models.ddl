case class Author(id: Long, firstName: String, lastName: String)

class Authors extends Table[Author]("AUTHOR") {

    def id = column[Long]("ID", 0.PrimaryKey)

    def firstName = column[String]("FIRST_NAME")

    def lastName = column[String]("LAST_NAME")

    def * = id ~ firstName ~ lastName <>(Author.apply _, Author.unapply _)
}