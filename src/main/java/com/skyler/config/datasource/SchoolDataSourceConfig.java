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
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * 核心业务数据源配置类
 *
 * @author skyler
 * @date 2017-03-28
 **/
@Configuration
@MapperScan(basePackages = SchoolDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "schoolSqlSessionFactory")
public class SchoolDataSourceConfig extends AbstractDataSourceConfig {
    static final String PACKAGE = "com.skyler.data.school";
    private static final String MAPPER_LOCATION = "classpath*:mybatis/mapper/school/*.xml";

    @Value("${skyler.datasource.school.type}")
    private Class<? extends DataSource> dataSourceType;

    @Primary
    @ConfigurationProperties(prefix = "skyler.datasource.school")
    @Bean(name = "schoolDataSource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .type(this.dataSourceType)
                .build();
    }

    @Primary
    @Bean(name = "schoolTransactionManager")
    public DataSourceTransactionManager transactionManager(
            @Qualifier("schoolDataSource") final DataSource dataSource) {
        return this.createTransactionManager(dataSource);
    }

    @Primary
    @Bean(name = "schoolSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("schoolDataSource") final DataSource dataSource)
            throws Exception {
        return this.createSqlSessionFactory(dataSource, MAPPER_LOCATION);
    }

    @Primary
    @Bean(name = "schoolSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("schoolSqlSessionFactory") final
                                                 SqlSessionFactory sqlSessionFactory)
            throws Exception {
        return this.createSqlSessionTemplate(sqlSessionFactory);
    }

    @Primary
    @Bean(name = "schoolTransactionTemplate")
    public TransactionTemplate transactionTemplate(@Qualifier("schoolTransactionManager") final
                                                   DataSourceTransactionManager transactionManager)
            throws Exception {
        return this.createTransactionTemplate(transactionManager);
    }

}
