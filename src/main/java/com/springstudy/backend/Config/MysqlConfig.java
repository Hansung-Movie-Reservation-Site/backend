package com.springstudy.backend.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration // application.yml 환경변수 접근 가능.
@EnableTransactionManagement // 빈 주입된 transactionManager를 사용해서 @transactional 자동 감지해서 트랜젝션 실행.
public class MysqlConfig {

    @Value("${spring.datasource.url}") // @Value: 환경변수 데이터로 초기화하는 어노테이션.
    private String url;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean // @Bean: 필요한 즉시 사용하도록 컴파일할 때 컨테이너에 만듬.
    public DataSourceTransactionManager transactionManager(DataSource datasource){ // Datasource: 데베 연결 역할.
        return new DataSourceTransactionManager(datasource);
    } // transactionManager 생성: 기본적인 트랜젝션 설정.
    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager){
        return new TransactionTemplate(transactionManager);
    } // @Transactional 선언 방식외에 직접 코딩하여 강제 트랜잭션을 실행시킬 때 사용하는 방식.

    @Bean(name = "createUserTransactionManager")
    public PlatformTransactionManager createUserTransactionManager(DataSource dataSource){
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        //todo 필요할 경우 추가적인 커스텀할 것.
        return transactionManager;
    } // 커스텀 트랜잭션 매니저.

}

