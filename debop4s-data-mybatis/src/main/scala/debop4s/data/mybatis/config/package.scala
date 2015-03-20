package debop4s.data.mybatis

/**
 * package
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
package object config {

  type TransactionFactory = org.apache.ibatis.transaction.TransactionFactory
  type JdbcTransactionFactory = org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
  type ManagedTransactionFactory = org.apache.ibatis.transaction.managed.ManagedTransactionFactory

  type PooledDataSource = org.apache.ibatis.datasource.pooled.PooledDataSource
  type UnpooledDataSource = org.apache.ibatis.datasource.unpooled.UnpooledDataSource
  type JndiDataSourceFactory = org.apache.ibatis.datasource.jndi.JndiDataSourceFactory

  type ObjectFactory = org.apache.ibatis.reflection.factory.ObjectFactory
  type ObjectWrapperFactory = org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory
  type DatabaseIdProvider = org.apache.ibatis.mapping.DatabaseIdProvider
  type LanguageDriver = org.apache.ibatis.scripting.LanguageDriver

  sealed trait LocalCacheScope {
    val unwrap: org.apache.ibatis.session.LocalCacheScope
    case object SESSION extends LocalCacheScope {
      val unwrap = org.apache.ibatis.session.LocalCacheScope.SESSION
    }
    case object STATEMENT extends LocalCacheScope {
      val unwrap = org.apache.ibatis.session.LocalCacheScope.STATEMENT
    }
  }

  sealed trait AutoMappingBehavior {
    val unwrap: org.apache.ibatis.session.AutoMappingBehavior

    case object FULL extends AutoMappingBehavior {
      override val unwrap = org.apache.ibatis.session.AutoMappingBehavior.FULL
    }
    case object NONE extends AutoMappingBehavior {
      override val unwrap = org.apache.ibatis.session.AutoMappingBehavior.NONE
    }
    case object PARTIAL extends AutoMappingBehavior {
      override val unwrap = org.apache.ibatis.session.AutoMappingBehavior.PARTIAL
    }
  }

}
