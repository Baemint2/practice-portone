package moz1mozi.practiceportone.controller;

import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moz1mozi.practiceportone.dto.PaymentCallBackRequest;
import moz1mozi.practiceportone.dto.RequestPayDto;
import moz1mozi.practiceportone.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/payment/{id}")
    public String paymentPage(@PathVariable(name = "id", required = false) String id,
                              Model model) {
        RequestPayDto requestDto = paymentService.findRequestDto(id);
        model.addAttribute("requestDto", requestDto);
        return "payment";
    }

    @PostMapping("/payment")
    public ResponseEntity<IamportResponse<Payment>> validationPayment(@RequestBody PaymentCallBackRequest request) {
        IamportResponse<Payment> iamportResponse = paymentService.paymentByCallBack(request);

        log.info("결제 응답={}", iamportResponse.getResponse().toString());

        return ResponseEntity.ok(iamportResponse);
    }

    @GetMapping("/success-payment")
    public String successPaymentPage(){
        return "success-payment";
    }

    @GetMapping("/fail-payment")
    public String failPaymentPage() {
        return "fail-payment";
    }
}
