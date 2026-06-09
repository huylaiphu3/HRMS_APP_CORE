package com.hrms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class HrmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrmsApplication.class, args);

        /* DI Và IOC
        DI : dependency injection : là một kỹ thuật trong đó một đối tượng nhận các phụ thuộc của nó từ bên ngoài thay vì tự tạo ra chúng
        . Điều này giúp giảm sự phụ thuộc giữa các lớp và làm cho mã dễ bảo trì hơn.
        IOC : inversion of control
        : là một nguyên tắc thiết kế trong đó quyền kiểm soát của việc tạo và quản lý các đối tượng được chuyển từ mã của bạn sang một framework hoặc container.
        Điều này giúp giảm sự phụ thuộc giữa các lớp và làm cho mã dễ bảo trì hơn

        container : giống như khi em tạo 1 string -> pool . thì spring boot nó tạo đối tượng -> dđâẩy vào container -> khi nào cần thì nó sẽ lấy ra ->
        nó quản lý vòng đời của đối tượng đó luôn

        1 cái kiến thức khác : xóa cứng và xóa mềm( soft delete ) : xóa cứng là xóa hẳn khỏi db
        xóa mềm : ví dụ có 1 cái cờ (flag ) coó thẻ là trường deletedBy nếu trường này có giá trị thì nó sẽ coi như là bị xóa
        việc xóa mềm như này -> dễ lưu lại lịch sử , tracking , nhưng cũng có thể gây phình to dữ liệu mà ko cần phải xóa
        có 2 trường hợp :
        xóa user : xóa mềm -> viì còn nhiều dữ liệu nhảy cảm khác
        xóa tin nhắn : xóa cứng -> vì hậu quả để lại ko nhiều , có thể cân nhắc
         */
    }
}
