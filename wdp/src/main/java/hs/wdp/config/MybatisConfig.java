package hs.wdp.config;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@MapperScan(basePackages="hs.wdp.**.mapper", annotationClass = Mapper.class, sqlSessionFactoryRef = "wdpSqlSessionFactory")
public class MybatisConfig {

	private final ApplicationContext applicationContext;

	@Bean
	SqlSessionFactory wdpSqlSessionFactory(DataSource dataSource) throws Exception, IOException {
		Resource[] resources = null;

		try {
			resources = new PathMatchingResourcePatternResolver().getResources("classpath:mapper/**/*.xml");
		} catch(FileNotFoundException e) {
			log.debug(">> resources(*Maper.xml) does not exist.");
			return null;
		}

		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setVfs(SpringBootVFS.class);
		sessionFactory.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-config.xml"));
		sessionFactory.setMapperLocations(resources);

		return sessionFactory.getObject();
	}

	@Bean
	SqlSessionTemplate wdpSqlSessionTemplate(@Qualifier("wdpSqlSessionFactory") SqlSessionFactory wdpSqlSessionFactory) {
		if(wdpSqlSessionFactory == null) {
			log.debug(">> wdpSqlSessionFactory is null");
			return null;
		}

		return new SqlSessionTemplate(wdpSqlSessionFactory);
	}


}