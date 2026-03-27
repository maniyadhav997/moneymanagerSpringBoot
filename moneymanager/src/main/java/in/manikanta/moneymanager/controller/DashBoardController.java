package in.manikanta.moneymanager.controller;

import in.manikanta.moneymanager.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/dashboard")
@RestController
@RequiredArgsConstructor
public class DashBoardController {
    private final DashBoardService dashBoardService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashBoards(){
        Map<String, Object> dashBoardData = dashBoardService.getDashBoardData();

        return ResponseEntity.ok(dashBoardData);
    }
}
