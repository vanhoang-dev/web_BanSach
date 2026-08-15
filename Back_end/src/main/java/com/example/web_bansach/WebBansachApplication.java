package com.example.web_bansach;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// Điểm khởi động Spring Boot của toàn bộ hệ thống bán sách.
public class WebBansachApplication {

	// Khởi tạo application context và chạy máy chủ backend.
	public static void main(String[] args) {
		SpringApplication.run(WebBansachApplication.class, args);
	}

}
