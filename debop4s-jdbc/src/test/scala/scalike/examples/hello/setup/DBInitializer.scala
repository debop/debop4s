package scalike.examples.hello.setup

import scalikejdbc._

/**
 * DBInitializer
 * @author sunghyouk.bae@gmail.com
 */
object DBInitializer {

  def run() {
    DB readOnly { implicit session =>
      try {
        sql"select 1 from programmer limit 1".map(_.long(1)).single().apply()
      } catch {
        case e: java.sql.SQLException => createSchema()
      }
    }
  }

  def createSchema() {
    DB autoCommit { implicit session =>
      sql"""
            create table programmer(
              id serial not null  primary key,
              name varchar(255) not null,
              company_id bigint,
              created_at timestamp not null,
              deleted_at timestamp
            );

            create table company(
              id serial not null primary key,
              name varchar(255) not null,
              url varchar(255),
              created_at timestamp not null,
              deleted_at timestamp
            );

            create table skill(
              id serial not null primary key,
              name varchar(255) not null,
              created_at timestamp not null,
              deleted_at timestamp
            );

            create table programmer_skill(
              programmer_id bigint not null,
              skill_id bigint not null,
              primary key(programmer_id, skill_id)
            );

            insert into company (name, url, created_at) values ('Typesafe', 'http://typesafe.com/', current_timestamp);
            insert into company (name, url, created_at) values ('Oracle', 'http://www.oracle.com/', current_timestamp);
            insert into company (name, url, created_at) values ('Google', 'http://www.google.com/', current_timestamp);
            insert into company (name, url, created_at) values ('Microsoft', 'http://www.microsoft.com/', current_timestamp);

            insert into skill (name, created_at) values ('Scala', current_timestamp);
            insert into skill (name, created_at) values ('Java', current_timestamp);
            insert into skill (name, created_at) values ('Ruby', current_timestamp);
            insert into skill (name, created_at) values ('MySQL', current_timestamp);
            insert into skill (name, created_at) values ('PostgreSQL', current_timestamp);

            insert into programmer (name, company_id, created_at) values ('Alice', 1, current_timestamp);
            insert into programmer (name, company_id, created_at) values ('Bob', 2, current_timestamp);
            insert into programmer (name, company_id, created_at) values ('Chris', 1, current_timestamp);

            insert into programmer_skill values (1, 1);
            insert into programmer_skill values (1, 2);
            insert into programmer_skill values (2, 2);

    """.execute().apply()
    }
  }
}
