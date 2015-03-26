package debop4s.data.slick.examples

import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.profile.createModel
import debop4s.data.slick.SlickExampleDatabase.driver.simple._
import debop4s.data.slick.{AbstractSlickFunSuite, SlickContext}

import scala.slick.ast.ColumnOption
import scala.slick.model.Model
import scala.util.Try

/**
 * DB 스키마에 대한 정보 알 수 있음.
 * @author sunghyouk.bae@gmail.com 15. 3. 25.
 */
class ModelBuilderFunSuite extends AbstractSlickFunSuite {

  lazy val isPostgreSql = SlickContext.isPostgres

  class Categories(tag: Tag) extends Table[(Int, String)](tag, "model_categories") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.DBType("VARCHAR(123)"))
    def * = (id, name)
    def idx = index("ix_model_category_name", name)
  }
  lazy val categories = TableQuery[Categories]

  class Posts(tag: Tag) extends Table[(Int, String, Option[Int], Boolean, String)](tag, "model_posts") {
    def id = column[Int]("id")
    def title = column[String]("title", O.Length(99, false))
    def categoryId = column[Option[Int]]("categoryId")
    def someBool = column[Boolean]("some_bool", O.Default(true))
    def someString = column[String]("some_string", O.Length(111, true))

    def * = (id, title, categoryId, someBool, someString)

    def pk = primaryKey("pk_model_posts", (id, title))
    def categoryFK = foreignKey("fk_model_posts_categories", categoryId, categories)(_.id)
  }
  lazy val posts = TableQuery[Posts]

  class DefaultTest(tag: Tag) extends Table[(Boolean, Boolean, Boolean, Option[Boolean], Option[Boolean], Option[Boolean], String, String, String, Option[String], Option[String], Option[String], Option[String])](tag, "model_default_test") {
    def someBool = column[Boolean]("some_bool")
    def someBoolDefaultTrue = column[Boolean]("some_bool_default_true", O.Default(true))
    def someBoolDefaultFalse = column[Boolean]("some_bool_default_false", O.Default(false))
    def someBoolOption = column[Option[Boolean]]("some_bool_option")
    def someBoolOptionDefaultSome = column[Option[Boolean]]("some_bool_option_default_some", O.Default(Some(true)))
    def someBoolOptionDefaultNone = column[Option[Boolean]]("some_bool_option_default_none", O.Default(None))
    def someString = column[String]("some_string")
    def someStringDefaultNonEmpty = column[String]("some_string_default_non_empty", O.Default("bar"))
    def someStringDefaultEmpty = column[String]("some_string_default_empty", O.Default(""))
    def someStringOption = column[Option[String]]("some_string_option")
    def someStringOptionDefaultEmpty = column[Option[String]]("str_option_default_empty", O.Default(Some("")))
    def someStringOptionDefaultNone = column[Option[String]]("str_option_default_none", O.Default(None))
    def someStringOptionDefaultNonEmpty = column[Option[String]]("str_option_default_non_empty", O.Default(Some("foo")))
    def * = (someBool, someBoolDefaultTrue, someBoolDefaultFalse, someBoolOption, someBoolOptionDefaultSome, someBoolOptionDefaultNone, someString, someStringDefaultNonEmpty, someStringDefaultEmpty, someStringOption, someStringOptionDefaultEmpty, someStringOptionDefaultNonEmpty, someStringOptionDefaultNone)
  }
  val defaultTest = TableQuery[DefaultTest]

  class NoDefaultTest(tag: Tag) extends Table[(Int, Option[String], Option[String])](tag, "model_no_default_test") {
    def int = column[Int]("int")
    def stringOption = column[Option[String]]("stringOption")
    def stringOptionDefaultNone = column[Option[String]]("stringOptionDefaultNone", O.Default(None))
    def * = (int, stringOption, stringOptionDefaultNone)
  }
  val noDefaultTest = TableQuery[NoDefaultTest]

  class TypeTest(tag: Tag) extends Table[(
    String, Boolean, Byte, Short, Int, Long, Float, Double, String, java.sql.Date, java.sql.Time, java.sql.Timestamp, java.sql.Blob //,java.sql.Clob
      , Option[Int]
      , (
      Option[Boolean], Option[Byte], Option[Short], Option[Int], Option[Long], Option[Float], Option[Double], Option[String], Option[java.sql.Date], Option[java.sql.Time], Option[java.sql.Timestamp], Option[java.sql.Blob] //,Option[java.sql.Clob]
      )
    )](tag, "model_type_test") {
    def `type` = column[String]("type")
    // <- test escaping of keywords
    def Boolean = column[Boolean]("Boolean", O.Default(true))
    def Byte = column[Byte]("Byte")
    def Short = column[Short]("Short")
    def Int = column[Int]("Int", O.Default(-5))
    def Long = column[Long]("Long", O.Default(5L))
    //def java_math_BigInteger = column[java.math.BigInteger]("java_math_BigInteger")
    def Float = column[Float]("Float", O.Default(9.999F))
    def Double = column[Double]("Double", O.Default(9.999))
    //def java_math_BigDecimal = column[java.math.BigDecimal]("java_math_BigDecimal")
    def String = column[String]("String", O.Default("someDefaultString"))
    def java_sql_Date = column[java.sql.Date]("java_sql_Date")
    def java_sql_Time = column[java.sql.Time]("java_sql_Time")
    def java_sql_Timestamp = column[java.sql.Timestamp]("java_sql_Timestamp")
    def java_sql_Blob = column[java.sql.Blob]("java_sql_Blob")
    //def java_sql_Clob = column[java.sql.Clob]("java_sql_Clob")

    def None_Int = column[Option[Int]]("None_Int", O.Default(None))

    def Option_Boolean = column[Option[Boolean]]("Option_Boolean", O.Default(Some(true)))
    def Option_Byte = column[Option[Byte]]("Option_Byte")
    def Option_Short = column[Option[Short]]("Option_Short")
    def Option_Int = column[Option[Int]]("Option_Int", O.Default(Some(5)))
    def Option_Long = column[Option[Long]]("Option_Long", O.Default(Some(-5L)))
    //def java_math_BigInteger = column[Option[java.math.BigInteger]]("java_math_BigInteger")
    def Option_Float = column[Option[Float]]("Option_Float", O.Default(Some(9.999F)))
    def Option_Double = column[Option[Double]]("Option_Double", O.Default(Some(9.999)))
    //def java_math_BigDecimal = column[Option[java.math.BigDecimal]]("java_math_BigDecimal")
    def Option_String = column[Option[String]]("Option_String", O.Default(Some("someDefaultString")))
    def Option_java_sql_Date = column[Option[java.sql.Date]]("Option_java_sql_Date")
    def Option_java_sql_Time = column[Option[java.sql.Time]]("Option_java_sql_Time")
    def Option_java_sql_Timestamp = column[Option[java.sql.Timestamp]]("Option_java_sql_Timestamp")

    def Option_java_sql_Blob = column[Option[java.sql.Blob]]("Option_java_sql_Blob")
    def Option_java_sql_Option_Blob = column[Option[Option[java.sql.Blob]]]("Option_java_sql_Blob")

    //def Option_java_sql_Clob = column[Option[java.sql.Clob]]("Option_java_sql_Clob")
    def * = (
      `type`,
      Boolean, Byte, Short, Int, Long, Float, Double, String, java_sql_Date, java_sql_Time, java_sql_Timestamp, java_sql_Blob //,java_sql_Clob
      , None_Int
      , (
      Option_Boolean, Option_Byte, Option_Short, Option_Int, Option_Long, Option_Float, Option_Double, Option_String, Option_java_sql_Date, Option_java_sql_Time, Option_java_sql_Timestamp, Option_java_sql_Blob //,Option_java_sql_Clob
      )
      )
    def pk = primaryKey("PK", (Int, Long))
  }
  val typeTest = TableQuery[TypeTest]

  test("column spec") {
    var ddl = categories.ddl ++ posts.ddl ++ defaultTest.ddl ++ noDefaultTest.ddl
    if (!isPostgreSql) {
      ddl ++= typeTest.ddl
    }

    withSession { implicit session =>
      Try { ddl.drop }
      ddl.create

      // DB Schema 에 중복된 이름이나 잘못된 참조가 있는지 검사합니다.
      createModel(ignoreInvalidDefaults = false).assertConsistency
      val tables = profile.defaultTables
      createModel(Some(tables), ignoreInvalidDefaults = false).assertConsistency

    {
      val model = createModel(Some(tables.filter(_.name.name.toUpperCase == "MODEL_POSTS")), ignoreInvalidDefaults = false)
      model.assertConsistency
      model.tables.map(_.foreignKeys.size).sum shouldEqual 0
    }

      createModel(Some(tables.filter(_.name.name.toUpperCase == "MODEL_CATEGORIES")),
                   ignoreInvalidDefaults = false)
      .assertConsistency

      intercept[AssertionError] {
        Model(
               createModel(Some(tables), ignoreInvalidDefaults = false)
               .tables
               .filter(_.name.table.toUpperCase == "MODEL_POSTS")
             ).assertConsistency
      }

      // Postgres uses lower case and things like int4
      // seen in jtds: int identity
      // seen in oracle: VARCHAR2
      val DBTypePattern = "^[a-zA-Z][a-zA-Z0-9]*$".r

      val model: Model = createModel(ignoreInvalidDefaults = false)
      LOG.debug(s"model: $model")
      model.tables.size should be >= 5

      val categories: slick.model.Table = model.tables.filter(_.name.table.toUpperCase == "MODEL_CATEGORIES").head
      categories.columns.size shouldEqual 2
      categories.primaryKey shouldEqual None
      categories.foreignKeys.size shouldEqual 0
      categories.columns.filter(_.options.contains(ColumnOption.PrimaryKey)).map(_.name) shouldEqual Seq("id")

      categories.columns
      .filter(_.name == "name")
      .head
      .options
      .collect {
        case ColumnOption.Length(length, varying) => (length, varying)
      }.head shouldEqual(123, true)

      categories.columns.foreach {
        _.options.foreach {
          case ColumnOption.Length(length, varying) => length < 256
          case ColumnOption.DBType(DBTypePattern()) =>
          case ColumnOption.DBType(dbType) => assert(false, "invalid DBType:" + dbType)
          case _ =>
        }
      }

      val posts: slick.model.Table = model.tables.filter(_.name.table.toUpperCase == "MODEL_POSTS").head
      LOG.debug(s"posts = $posts")

      // NOTE: MySQL 에서는 foreignKey도 index로 해석한다.
      posts.columns.size shouldEqual 5
      posts.indices.size shouldBe 0
      posts.primaryKey.get.columns.size shouldEqual 2
      posts.foreignKeys.size shouldBe 1

      // 현재 profile 을 알 수 있다!!!
      //      if (SlickContext.driver.profile != slick.driver.SQLiteDriver) {
      //        posts.foreignKeys.head.name.get.toUpperCase shouldEqual "CATEGORY_FK"
      //      }
      def tpe(col: String): String = {
        posts.columns.filter(_.name == col).head
        .options.collect { case ColumnOption.DBType(tpe) => tpe }.head
      }

      Seq("CHAR", "CHARACTER", "BPCHAR") should contain(tpe("title").toUpperCase)
      Seq("VARCHAR", "VARCHAR2") should contain(tpe("some_string").toUpperCase)

      posts.columns.foreach {
        _.options.foreach {
          case ColumnOption.Length(length, varying) => length < 256
          case ColumnOption.DBType(DBTypePattern()) =>
          case ColumnOption.DBType(dbType) => assert(false, "invalid DBType:" + dbType)
          case _ =>
        }
      }

      val defaultTest = model.tables.filter(_.name.table.toUpperCase == "MODEL_DEFAULT_TEST").head

      LOG.debug(s"schema=${ defaultTest.name.schema }, catalog=${ defaultTest.name.catalog }")
      defaultTest.name.schema.map(_.toUpperCase) should not be Some("PUBLIC")
      defaultTest.name.catalog.map(_.toUpperCase) should not be Some("PUBLIC")

      def column(name: String) = defaultTest.columns.filter(_.name == name).head
      def columnDefault(name: String) = column(name).options.collect { case ColumnOption.Default(v) => v }.headOption

      columnDefault("some_bool") shouldEqual None
      ifCap(jcap.booleanMetaData) {
        columnDefault("some_bool_default_true") shouldEqual Some(true)
        columnDefault("some_bool_default_false") shouldEqual Some(false)
      }
      ifNotCap(jcap.booleanMetaData) {
        column("some_bool_default_true").nullable shouldEqual false
        column("some_bool_default_false").nullable shouldEqual false

        columnDefault("some_bool_default_true") shouldEqual Some(1)
        columnDefault("some_bool_default_true").toString shouldEqual Some('1')
        columnDefault("some_bool_default_false") shouldEqual Some(0)
        columnDefault("some_bool_default_false").toString shouldEqual Some('0')
      }

      ifCap(jcap.nullableNoDefault) {
        columnDefault("some_bool_option") shouldEqual None
      }
      ifNotCap(jcap.nullableNoDefault) {
        columnDefault("some_bool_option") shouldEqual Some(None)
      }

      ifCap(jcap.booleanMetaData) {
        columnDefault("some_bool_option_default_some") shouldEqual Some(Some(true))
      }
      ifNotCap(jcap.booleanMetaData) {
        columnDefault("some_bool_option_default_some") shouldEqual Some(Some(1))
      }

      columnDefault("some_bool_option_default_none") shouldEqual Some(None)
      columnDefault("some_string") shouldEqual None
      columnDefault("some_string_default_non_empty") shouldEqual Some("bar")
      columnDefault("some_string_default_empty") shouldEqual Some("")

      ifCap(jcap.nullableNoDefault) {
        columnDefault("some_string_option") shouldEqual None
      }
      ifNotCap(jcap.nullableNoDefault) {
        columnDefault("some_string_option") shouldEqual Some(None)
      }

      columnDefault("str_option_default_empty") shouldEqual Some(Some(""))
      columnDefault("str_option_default_none") shouldEqual Some(None)
      columnDefault("str_option_default_non_empty") shouldEqual Some(Some("foo"))

      if (!isPostgreSql) {
        val typeTest = model.tables.filter(_.name.table.toUpperCase == "MODEL_TYPE_TEST").head

        def column(name: String) = typeTest.columns.filter(_.name.toUpperCase == name.toUpperCase).head
        def columnDefault(name: String) = column(name).options.collect { case ColumnOption.Default(v) => v }.headOption

        ifCap(jcap.booleanMetaData) {
          column("Boolean").tpe shouldEqual "Boolean"
          column("Option_Boolean").tpe shouldEqual "Boolean"
        }
        column("Boolean").nullable shouldEqual false
        column("Option_Boolean").nullable shouldEqual true

        ifCap(jcap.supportsByte) {
          column("Byte").tpe shouldEqual "Byte"
          column("Option_Byte").tpe shouldEqual "Byte"
        }
        column("Byte").nullable shouldEqual false
        column("Option_Byte").nullable shouldEqual true

        ifCap(jcap.distinguishesIntTypes) {
          column("Short").tpe shouldEqual "Short"
          column("Option_Short").tpe shouldEqual "Short"
        }
        column("Short").nullable shouldEqual false
        column("Option_Short").nullable shouldEqual true

        column("Int").nullable shouldEqual false
        column("Option_Int").nullable shouldEqual true
        column("Long").nullable shouldEqual false
        column("Option_Long").nullable shouldEqual true

        if (!SlickContext.isOracle) {
          // FIXME: we should probably solve this somewhat cleaner
          column("Int").tpe shouldEqual "Int"
          column("Option_Int").tpe shouldEqual "Int"

          ifCap(jcap.defaultValueMetaData) {
            columnDefault("Int") shouldEqual Some(-5)
            columnDefault("Option_Int") shouldEqual Some(Some(5))
          }
          ifCap(jcap.distinguishesIntTypes) {
            column("Long").tpe shouldEqual "Long"
            column("Option_Long").tpe shouldEqual "Long"
          }
          ifCap(jcap.defaultValueMetaData) {
            columnDefault("Long") shouldEqual Some(5L)
            columnDefault("Option_Long") shouldEqual Some(Some(-5L))
          }
        }

        /* h2 and hsqldb map this to Double
            assertEquals("Float",column("Float").tpe)
            assertEquals("Float",column("Option_Float").tpe)
            assertEquals(false,column("Float").nullable)
            assertEquals(true,column("Option_Float").nullable)
        */
        column("Double").tpe shouldEqual "Double"
        column("Option_Double").tpe shouldEqual "Double"
        column("Double").nullable shouldEqual false
        column("Option_Double").nullable shouldEqual true

        column("String").tpe shouldEqual "String"
        column("Option_String").tpe shouldEqual "String"
        column("String").nullable shouldEqual false
        column("Option_String").nullable shouldEqual true

        column("java_sql_Date").nullable shouldEqual false
        column("Option_java_sql_Date").nullable shouldEqual true
        column("java_sql_Time").nullable shouldEqual false
        column("Option_java_sql_Time").nullable shouldEqual true
        column("java_sql_Timestamp").nullable shouldEqual false
        column("Option_java_sql_Timestamp").nullable shouldEqual true

        if (!SlickContext.isOracle) {
          // FIXME: we should probably solve this somewhat cleaner
          column("java_sql_Date").tpe shouldEqual "java.sql.Date"
          column("Option_java_sql_Date").tpe shouldEqual "java.sql.Date"
          column("java_sql_Time").tpe shouldEqual "java.sql.Time"
          column("Option_java_sql_Time").tpe shouldEqual "java.sql.Time"
          column("java_sql_Timestamp").tpe shouldEqual "java.sql.Timestamp"
          column("Option_java_sql_Timestamp").tpe shouldEqual "java.sql.Timestamp"
        }

        column("java_sql_Blob").tpe shouldEqual "java.sql.Blob"
        column("Option_java_sql_Blob").tpe shouldEqual "java.sql.Blob"
        column("java_sql_Blob").nullable shouldEqual false
        column("Option_java_sql_Blob").nullable shouldEqual true
      }

      ifCap(jcap.defaultValueMetaData) {
        val typeTest = model.tables.filter(_.name.table.toUpperCase == "MODEL_NO_DEFAULT_TEST").head

        def column(name: String) = typeTest.columns.filter(_.name.toUpperCase == name.toUpperCase).head
        def columnDefault(name: String) = column(name).options.collect { case ColumnOption.Default(v) => v }.headOption

        ifCap(jcap.nullableNoDefault) {
          columnDefault("stringOption") shouldEqual None
        }
        columnDefault("stringOptionDefaultNone") shouldEqual Some(None)
        noDefaultTest.map(_.int).insert(1)
        noDefaultTest.map(_.stringOption).first shouldEqual None
      }
    }
  }
}
