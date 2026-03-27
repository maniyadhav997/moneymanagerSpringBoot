package in.manikanta.moneymanager.controller;

import in.manikanta.moneymanager.dto.ExpenseDTO;
import in.manikanta.moneymanager.dto.FilterDTO;
import in.manikanta.moneymanager.dto.IncomeDTO;
import in.manikanta.moneymanager.service.ExpenseService;
import in.manikanta.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
public class FilterController {
    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping()
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filter){

        //preparing validation

        log.debug("data", filter);

        System.out.println("asdfghredsfghrfdghjh");


        LocalDate startDate = filter.getStartDate() != null ? filter.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filter.getEndDate() != null ? filter.getEndDate(): LocalDate.now();

        String keyword =filter.getKeyword() != null ? filter.getKeyword() : "";

        String sortFiled = filter.getSorFiled() != null ? filter.getSorFiled() : "date";

        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortFiled);

        if ("income".equals(filter.getType())){
            List<IncomeDTO> incomes =incomeService.filterIncomes(startDate,endDate, keyword, sort);
            return ResponseEntity.ok(incomes);
        }
        else if("expense".equals(filter.getType())){
            List<ExpenseDTO> expense = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expense);
        }
        else {
            return ResponseEntity.badRequest().body("Invalid type. Must be 'income' or 'expense'");
        }
    }

}
