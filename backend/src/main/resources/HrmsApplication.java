package com.hrms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class HrmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrmsApplication.class, args);
        /*
        có 3 cách tạo bean :
        1.@component
        2.@configuration + @bean
        3.xml -> legacy

        nói về 2 cái đầu tiên :
        @Còonfiguration : là cái class mà do em tự tạo
        @Bean : là thư viện của bên t3

         */

        /*
        có bao nhiêu cách để ghi log :
        đầu tiên : sysout -> dùng trong trương hợp em muốn hiển thị value của biến
        t2 : logger slf4j -> sử dụng khác vs sout vì nó có thời gian , logging level , tên class -> vậy khi sử dụng logging
        sẽ sưr dụng cho mục đích giaám sát hệ thống , còn sout chỉ để test local
         */

        /*
        global exception : thay vì mỗi chỗ phải throws , viết 1 nơi sử dụng cho tất cả
         */

        /*
        stateless vs statefull :
        ví dụ em đi ăn 1 quán phở quen : chủ quán quen em là khách víp ko cần thẻ nhưng khi mở thêm chi nhánh mới -> nó ko bt em là th nào -> stateful
        nhưng khi em có thẻ vip thay vì chiỉ nhìn mặt -> em có thê ể đi bất kiì chi nhánh nào -> stateless

        stateless -> ko lưu traạng thái của những gì ở trước đó
        statefull -> lưu trạng thaái của nhữung gì ở trước đó
         */

        /*
        jwt : json web token
        nó có 3 phần
        header.payload.signature

        HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret)

  secret : 1 mật khẩu ngẫu nhiên nên có độ dài tốt thiểu 32 kí tự
         */
    }
}
