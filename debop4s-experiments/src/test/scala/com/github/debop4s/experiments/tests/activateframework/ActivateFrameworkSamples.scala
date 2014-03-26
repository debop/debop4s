package com.github.debop4s.experiments.tests.activateframework

import MySqlContext._
import com.github.debop4s.experiments.tests.AbstractExperimentTest
import org.scalatest.Ignore


@Ignore
class ActivateFrameworkSamples extends AbstractExperimentTest {

    test("간단한 ActiveFramework 예제") {
        transactional {
            new Person("John")
        }

        val john = transactional {
            select[Person].where(_.name :== "John").head
        }

        transactional {
            john.name = "John Doe"
        }

        transactional {
            all[Person].foreach(_.delete)
        }
    }
}


class Person(var name: String) extends Entity

class CreatePersonTableMigration extends Migration {
    def timestamp = 201403261605L

    def up = {
        table[Person].createTable(_.column[String]("name"))
    }
}

