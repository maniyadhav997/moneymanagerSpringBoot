package in.manikanta.moneymanager.service;

import in.manikanta.moneymanager.dto.ExpenseDTO;
import in.manikanta.moneymanager.entity.CategoryEntity;
import in.manikanta.moneymanager.entity.ExpenseEntity;
import in.manikanta.moneymanager.entity.ProfileEntity;
import in.manikanta.moneymanager.repository.CategoryRepository;
import in.manikanta.moneymanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.analysis.function.Exp;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class ExpenseService {

    private  final CategoryService categoryService;

    private final ExpenseRepository expenseRepository;

    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    // add a new expense to database
    public ExpenseDTO addExpense(ExpenseDTO dto){
        ProfileEntity profile=profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not Found"));
        ExpenseEntity newExpense = toEnity(dto,profile,category);
        newExpense=expenseRepository.save(newExpense);
        return toDTO(newExpense);
    }

    //Retrive all expenses for current month/based on the start date and end date

    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentuser(){
        ProfileEntity profile= profileService.getCurrentProfile();
        LocalDate now= LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);

        return list.stream().map(this::toDTO).toList();
    }

    //Get latest 5 expenses of current user

    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<ExpenseEntity> list =    expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    //Get totoal expenses of current user
    public BigDecimal getTotalExpenseForCurrentUset(){
        ProfileEntity profile = profileService.getCurrentProfile();
       BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
       return total != null ? total: BigDecimal.ZERO;
    }


    //delete expenses by current user id

    public void deleteExpense(Long expenseId){
        ProfileEntity profile = profileService.getCurrentProfile();

        ExpenseEntity entity = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized access");
        }
        expenseRepository.delete(entity);
    }

    //filter expenses
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){

        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);

        return list.stream().map(this :: toDTO).toList();
    }

    //Notifications

    public List<ExpenseDTO> getExpenseUserOnDate(Long profileId, LocalDate date){

        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDate(profileId, date);

        return list.stream().map(this:: toDTO).toList();

    }

    private ExpenseEntity toEnity(ExpenseDTO expenseDTO, ProfileEntity profile, CategoryEntity category) {
        return ExpenseEntity.builder()
                .name(expenseDTO.getName())
                .icon(expenseDTO.getIcon())
                .amount(expenseDTO.getAmount())
                .date(expenseDTO.getDate())
                .profile(profile)
                .category(category)
                .build();


    }

    private ExpenseDTO toDTO(ExpenseEntity entity){
        return ExpenseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId(): null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName(): "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

    }


}
