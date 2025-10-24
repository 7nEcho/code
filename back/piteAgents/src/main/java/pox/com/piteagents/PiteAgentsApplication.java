package pox.com.piteagents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("pox.com.piteagents.mapper")
public class PiteAgentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PiteAgentsApplication.class, args);
    }

}
