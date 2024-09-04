package com.example.jenkinsspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JenkinsSpringApplication {

  public static void main(String[] args) {
    SpringApplication.run(JenkinsSpringApplication.class, args);
    System.out.println(123);
  }

}
