package com.skyler.config.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * 其他业务数据源配置类
 *
 * @author skyler
 * @date 2017-03-28
 **/
@Configuration
@MapperScan(basePackages = HomeDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "homeSqlSessionFactory")
public class HomeDataSourceConfig extends AbstractDataSourceConfig {
    static final String PACKAGE = "com.skyler.data.home";
    private static final String MAPPER_LOCATION = "classpath*:mybatis/mapper/home/*.xml";

    @Value("${skyler.datasource.home.type}")
    private Class<? extends DataSource> dataSourceType;

    @ConfigurationProperties(prefix = "skyler.datasource.home")
    @Bean(name = "homeDataSource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
            .type(this.dataSourceType)
            .build();
    }

    @Bean(name = "homeTransactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("homeDataSource") final DataSource dataSource) {
        return this.createTransactionManager(dataSource);
    }

    @Bean(name = "homeSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("homeDataSource") final DataSource dataSource)
        throws Exception {
        return this.createSqlSessionFactory(dataSource, MAPPER_LOCATION);
    }

    @Bean(name = "homeSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("homeSqlSessionFactory") final
                                                 SqlSessionFactory sqlSessionFactory)
        throws Exception {
        return this.createSqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "homeTransactionTemplate")
    public TransactionTemplate transactionTemplate(@Qualifier("homeTransactionManager") final
                                                   DataSourceTransactionManager transactionManager)
        throws Exception {
        return this.createTransactionTemplate(transactionManager);
    }

}
